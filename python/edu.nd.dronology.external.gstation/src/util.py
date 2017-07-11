import dronekit
import sys
import os
import shutil
import subprocess
import signal
import numpy as np
import nvector as nv
import matplotlib.path as mpl_path
from threading import Timer
import log_util

_LOG = log_util.get_logger('default_file')

arr = np.array

SEMI_MAJOR = np.float64(6378137.0)
SEMI_MINOR = np.float64(6356752.31)

NV_A = SEMI_MAJOR
NV_F = 1 - (SEMI_MINOR / SEMI_MAJOR)


def get_search_path(vertices):
    if len(vertices) < 3:
        raise ValueError('invalid search area, must have 3 vertices.')

    return mpl_path.Path(vertices)


def column_vector(a):
    if not isinstance(a, (list, tuple, np.ndarray)):
        raise ValueError('invalid type')

    return arr(a).reshape(-1, 1)


class Earth:
    @staticmethod
    def meridional_radius_curvature(latitude, a=SEMI_MAJOR, b=SEMI_MINOR):
        """
        https://en.wikipedia.org/wiki/Earth_radius#Location-dependent_radii

        :param latitude: geodetic latitude
        :param a: length (m) of the equatorial radius
        :param b: length (m) of the polar radius
        :return:
        """
        lat = np.deg2rad(latitude)
        n = (a * b) ** 2
        dl = (a * np.cos(lat)) ** 2
        dr = (b * np.sin(lat)) ** 2
        d = np.power(dl + dr, 1.5)
        M = n / d

        return M

    @staticmethod
    def transverse_radius_curvature(latitude, a=SEMI_MAJOR, b=SEMI_MINOR):
        """
        https://en.wikipedia.org/wiki/Earth_radius#Location-dependent_radii

        :param latitude: geodetic latitude
        :param a: length (m) of the equatorial radius
        :param b: length (m) of the polar radius
        :return:
        """
        lat = np.deg2rad(latitude)
        n = a ** 2
        d_sq = (a * np.cos(lat)) ** 2 + (b * np.sin(lat)) ** 2
        d = np.sqrt(d_sq)
        N = n / d

        return N


# noinspection PyPep8Naming
class Position(object):
    def as_array(self, flat=True):
        a = self._as_array()
        if not flat:
            a = a.reshape(-1, 1)

        return a

    def update(self, v_EB_N, t=0.1):
        """
        Given a position and NED, we *might* be able to use equations 12, 14, 15 to determine an exact solution
        for an updated position at some point in the future.
            Gade, Kenneth. "A non-singular horizontal position representation."
            The journal of navigation 63.3 (2010): 395-417.

        This would be great for simulation. Not sure if we can do it though.

        :param v_EB_N: north east down (3x1 ndarray)
        :param t: time step for update
        :return:
        """
        n_EB_E_and_h = self.to_nvector().as_array(flat=False)
        n_EB_E = n_EB_E_and_h[:-1, :]
        h_B = n_EB_E_and_h[-1, 0]

        R_EN = nv.n_E2R_EN(n_EB_E)
        # we want to go from N to E, so need to transpose
        R_NE = np.transpose(R_EN)
        v_EB_E = np.dot(R_NE, v_EB_N)

        # TODO: the next two equations are broken... cross products are hard.
        temp = column_vector([0, 0, 1])
        # temp = [0, 0, 1]
        # Equation 9
        v_EB_E_east = np.cross(v_EB_E.ravel(), temp.ravel())
        # Equation 10
        v_EB_E_north = np.cross(v_EB_E_east, temp.ravel())

        lla = self.to_lla()
        lat = lla.get_latitude()

        v_EB_E_north_scaled = v_EB_E_north / Earth.meridional_radius_curvature(lat)
        v_EB_E_east_scaled = v_EB_E_east / Earth.transverse_radius_curvature(lat)
        # Equation 12: the angular velocity in E
        omega_EL_E = np.cross(n_EB_E.ravel(), (v_EB_E_north_scaled + v_EB_E_east_scaled))

        # Equation 14: derivative wrt time
        n_EB_E_prime = np.cross(omega_EL_E, n_EB_E.ravel())
        h_B_prime = np.dot(n_EB_E.ravel(), v_EB_E)

        update_n_EB_E = n_EB_E_prime * t
        update_h_B = h_B_prime * t

        new_x, new_y, new_z = n_EB_E.ravel() + update_n_EB_E
        new_depth = h_B + update_h_B[0]

        new_self = Nvector(new_x, new_y, new_z, new_depth)

        return self.coerce(new_self)

    def distance(self, other):
        p1 = self.to_pvector().as_array()
        p2 = other.to_pvector().as_array()

        resid = p1 - p2
        resid_sq = resid ** 2
        resid_sum_sq = resid_sq.sum()
        dist = np.sqrt(resid_sum_sq)

        return dist

    def coerce(self, other):
        if isinstance(other, Position):
            if isinstance(self, Lla):
                return other.to_lla()
            elif isinstance(self, Nvector):
                return other.to_nvector()
            else:
                return other.to_pvector()
        else:
            return other

    # noinspection PyTypeChecker
    def __eq__(self, other):
        other_ = self.coerce(other)
        if isinstance(other_, self.__class__):
            return np.isclose(self.as_array(), other_.as_array()).all()
        return False

    def to_lla(self):
        raise NotImplementedError

    def to_nvector(self):
        raise NotImplementedError

    def to_pvector(self):
        raise NotImplementedError

    def _as_array(self):
        raise NotImplementedError


# noinspection PyPep8Naming
class Lla(Position):
    def __init__(self, latitude, longitude, altitude):
        self.lla = arr([latitude, longitude, altitude]).astype(np.float64)

    def get_latitude(self, as_rad=False):
        lat = self.lla[0]
        if as_rad:
            lat = np.deg2rad(lat)

        return lat

    def get_longitude(self, as_rad=False):
        lon = self.lla[1]
        if as_rad:
            lon = np.deg2rad(lon)

        return lon

    def get_altitude(self):
        return self.lla[-1]

    def to_nvector(self):
        lat = self.get_latitude(as_rad=True)
        lon = self.get_longitude(as_rad=True)
        alt = self.get_altitude()
        n_EB_E = nv.lat_lon2n_E(lat, lon)
        x, y, z = n_EB_E.ravel()

        return Nvector(x, y, z, -alt)

    def to_pvector(self):
        return self.to_nvector().to_pvector()

    def to_lla(self):
        return self

    def _as_array(self):
        return self.lla


# noinspection PyPep8Naming
class Nvector(Position):
    def __init__(self, x, y, z, depth):
        self.n_EB_E = arr([x, y, z]).astype(np.float64).reshape(-1, 1)
        self.depth = depth

    def get_x(self):
        return self.n_EB_E[0, 0]

    def get_y(self):
        return self.n_EB_E[1, 0]

    def get_z(self):
        return self.n_EB_E[2, 0]

    def get_depth(self):
        return self.depth

    def to_nvector(self):
        return self

    def to_pvector(self):
        x, y, z = nv.n_EB_E2p_EB_E(self.n_EB_E, depth=self.depth, a=NV_A, f=NV_F).ravel()

        return Pvector(x, y, z)

    def to_lla(self):
        lat, lon = nv.n_E2lat_lon(self.n_EB_E)

        return Lla(np.rad2deg(lat), np.rad2deg(lon), -self.depth)

    def _as_array(self):
        x, y, z = self.n_EB_E.ravel()
        return arr([x, y, z, self.depth])


# noinspection PyPep8Naming
class Pvector(Position):
    def __init__(self, x, y, z):
        self.p_EB_E = arr([x, y, z]).astype(np.float64).reshape(-1, 1)

    def get_x(self):
        return self.p_EB_E[0, 0]

    def get_y(self):
        return self.p_EB_E[1, 0]

    def get_z(self):
        return self.p_EB_E[2, 0]

    def to_nvector(self):
        (x, y, z), depth = nv.p_EB_E2n_EB_E(self.p_EB_E, a=NV_A, f=NV_F)

        return Nvector(x, y, z, depth)

    def to_pvector(self):
        return self

    def to_lla(self):
        return self.to_nvector().to_lla()

    def _as_array(self):
        return self.p_EB_E.ravel()


def mean_position(positions):
    nvecs = arr([pos.to_nvector().as_array() for pos in positions]).T
    p_EM_E = nv.mean_horizontal_position(nvecs)
    x, y, z = p_EM_E.ravel()

    return Pvector(x, y, z)


def clean_up_run():
    if os.path.exists('.sitl_temp'):
        _LOG.info('deleting temporary sitl directory')
        shutil.rmtree('.sitl_temp')

    try:
        pids = map(int, subprocess.check_output(['pgrep', 'arducopter']).split())
        for pid in pids:
            os.kill(pid, signal.SIGINT)
        _LOG.warn('ArduCopter processes failed to shut down gracefully.')
    except subprocess.CalledProcessError:
        _LOG.debug('no sitl links found')
    #
    try:
        pids = map(int, subprocess.check_output(['pgrep', '-f', '/usr/local/bin/mavproxy.py']).split())
        for pid in pids:
            os.kill(pid, signal.SIGINT)
        _LOG.warn('MavProxy processes failed to shut down gracefully.')
    except subprocess.CalledProcessError:
        _LOG.debug('no python processes found')


if __name__ == '__main__':
    args = sys.argv
    if len(args) > 1:
        if args[1] == 'kill':
            clean_up_run()
