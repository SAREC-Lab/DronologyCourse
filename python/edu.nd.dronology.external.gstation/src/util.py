import logging
import logging.config
import sys
import shutil
import subprocess
import signal
import numpy as np
import nvector as nv
import yaml
from common import *
from threading import Timer


def get_logger(name='default_file', p2cfg='../cfg/logging.conf'):
    with open(p2cfg, 'r') as f:
        cfg = yaml.load(f)

    logging.config.dictConfig(cfg)

    return logging.getLogger(name)


_LOG = get_logger('default_file')

arr = np.array

SEMI_MAJOR = np.float64(6378137.0)
SEMI_MINOR = np.float64(6356752.31)

NV_A = SEMI_MAJOR
NV_F = 1 - (SEMI_MINOR / SEMI_MAJOR)


def _distance_2d(p0, p1):
    return np.sqrt(np.sum((p1 - p0) **2))


def _get_search_path_spiral(start, vertices):
    _LOG.warn('Spiral search not implemented yet.')

    return vertices


def _get_search_path_zigzag(start, vertices):
    start_ = arr(start.to_pvector().as_array()[:-1])
    grid = arr([v.to_pvector().as_array()[:-1] for v in vertices])
    dists = [_distance_2d(start_, p1) for p1 in grid]

    path = [grid[np.argmin(dists)]]
    

    return vertices

_search_strats = {
    SEARCH_ZIGZAG: _get_search_path_zigzag,
    SEARCH_SPIRAL: _get_search_path_spiral
}


def get_search_path(start, vertices, strat=SEARCH_ZIGZAG, last_known_loc=None):
    """

    :param start: the starting location of the drone (Position)
    :param vertices: the vertices of the search space (List<Position>)
    :param strat: the search strategy (choose from search strategies in common.py)
    :param last_known_loc: the last known location of the target (optional)
    :return:
    """
    if len(vertices) < 3:
        raise ValueError('invalid search area, must have 3 vertices.')

    if strat not in _search_strats:
        raise ValueError('invalid search strategy specified {}'.format(strat))

    return _search_strats[strat](start, vertices)


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
    def __getitem__(self, item):
        return self.as_array()[item]

    def as_array(self, flat=True):
        a = self._as_array()
        if not flat:
            a = a.reshape(-1, 1)

        return a

    # noinspection PyTypeChecker
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

    def __repr__(self):
        return '{}'.format(self.as_array())

    def __str__(self):
        return repr(self)

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
    n_EM_E = nv.mean_horizontal_position(nvecs[:-1, :]).ravel()
    m_Z = np.mean(nvecs[-1, :])

    return Nvector(n_EM_E[0], n_EM_E[1], n_EM_E[2], m_Z)


class RepeatedTimer(object):
    def __init__(self, interval, function, *args, **kwargs):
        self._timer = None
        self.interval = interval
        self.function = function
        self.args = args
        self.kwargs = kwargs
        self.is_running = False
        self.start()

    def _run(self):
        self.is_running = False
        self.start()
        self.function(*self.args, **self.kwargs)

    def start(self):
        if not self.is_running:
            self._timer = Timer(self.interval, self._run)
            self._timer.start()
            self.is_running = True

    def set_interval(self, interval):
        self._timer.cancel()
        self.interval = interval
        self.start()

    def stop(self):
        self._timer.cancel()
        self.is_running = False


def clean_up_run():
    if os.path.exists('.sitl_temp'):
        _LOG.info('Deleting temporary sitl directory')
        shutil.rmtree('.sitl_temp')

    try:
        pids = map(int, subprocess.check_output(['pgrep', 'arducopter']).split())
        for pid in pids:
            os.kill(pid, signal.SIGINT)
        _LOG.warn('ArduCopter processes failed to shut down gracefully.')
    except subprocess.CalledProcessError:
        _LOG.debug('No ArduCopter processes found')
    #
    try:
        pids = map(int, subprocess.check_output(['pgrep', '-f', '/usr/local/bin/mavproxy.py']).split())
        for pid in pids:
            os.kill(pid, signal.SIGINT)
        _LOG.warn('MavProxy processes failed to shut down gracefully.')
    except subprocess.CalledProcessError:
        _LOG.debug('No MavProxy processes found')


if __name__ == '__main__':
    bounds = [Lla(*pos) for pos in DEFAULT_SAR_BOUNDS]
    start = mean_position([bounds[0], bounds[1]]).to_lla()

    get_search_path(start, bounds)
