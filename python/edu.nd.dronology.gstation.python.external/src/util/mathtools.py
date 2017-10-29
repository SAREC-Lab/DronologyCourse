import numpy as np
import nvector as nv


arr = np.array

SEMI_MAJOR = np.float64(6378137.0)
SEMI_MINOR = np.float64(6356752.31)

NV_A = SEMI_MAJOR
NV_F = 1 - (SEMI_MINOR / SEMI_MAJOR)


def mean_position(positions):
    nvecs = arr([pos.to_nvector().as_array() for pos in positions]).T
    n_EM_E = nv.mean_horizontal_position(nvecs[:-1, :]).ravel()
    m_Z = np.mean(nvecs[-1, :])

    return positions[0].coerce(Nvector(n_EM_E[0], n_EM_E[1], n_EM_E[2], m_Z))


def column_vector(a):
    if not isinstance(a, (list, tuple, np.ndarray)):
        raise ValueError('invalid type')

    return arr(a).reshape(-1, 1)


def make_grid(a, north, east):
    """

    :param a: lower left (sw) corner
    :param north: distance north
    :param east: distance east
    :return:
    """
    grid = [
        a,
        a.move_ned(north, 0, 0),
        a.move_ned(north, east, 0),
        a.move_ned(0, east, 0)
    ]

    return GeoPoly(grid)


class GeoShape(object):
    def __init__(self, vertices):
        self._verts = vertices
        self._grid_xyz = arr([v.to_pvector() for v in vertices])
        self._grid_lla = arr([v.to_lla() for v in vertices])


# noinspection PyNoneFunctionAssignment
class GeoPoly(GeoShape):
    def __init__(self, vertices):
        super(GeoPoly, self).__init__(vertices)
        self._sw = None
        self._nw = None
        self._se = None
        self._ne = None
        self._n_max = None
        self._e_max = None
        self._s_max = None
        self._w_max = None

    def __str__(self):
        return str(self._grid_lla)

    def __repr__(self):
        return str(self)

    def get_lla(self):
        return self._grid_lla

    def sw_vertex(self):
        if self._sw is None:
            self._sw = min(self._grid_xyz, key=lambda l: (l[0] + l[2]))
        return self._sw

    def nw_vertex(self):
        if self._nw is None:
            self._nw = min(self._grid_xyz, key=lambda l: (l[0] - l[2]))
        return self._nw

    def se_vertex(self):
        if self._se is None:
            self._se = max(self._grid_xyz, key=lambda l: (l[0] - l[2]))
        return self._se

    def ne_vertex(self):
        if self._ne is None:
            self._ne = max(self._grid_xyz, key=lambda l: (l[0] + l[2]))
        return self._ne

    def furthest_north(self):
        if not self._n_max:
            self._n_max = max(self._grid_xyz, key=lambda l: l[2])
        return self._n_max

    def furthest_east(self):
        if not self._e_max:
            self._e_max = max(self._grid_xyz, key=lambda l: l[0])
        return self._e_max

    def furthest_west(self):
        if not self._w_max:
            self._w_max = min(self._grid_xyz, key=lambda l: l[0])
        return self._w_max

    def furthest_south(self):
        if not self._s_max:
            self._s_max = min(self._grid_xyz, key=lambda l: l[2])
        return self._s_max

    def cpa(self, pos):
        dists = [pos.distance(a) for a in self._verts]
        return self._verts[np.argmin(dists)]

    def distance_east(self):
        p_EA_A = self.sw_vertex()
        p_EB_A = self.se_vertex()

        dist_ned = p_EA_A.distance_ned(p_EB_A)

        return dist_ned[1]

    def distance_north(self):
        p_EA_A = self.sw_vertex()
        p_EB_A = self.nw_vertex()

        dist_ned = p_EA_A.distance_ned(p_EB_A)

        return dist_ned[0]

    def point_in_rectangle(self, p):
        p_lla = p.to_lla()
        lat = p_lla.get_latitude()
        lon = p_lla.get_longitude()

        contained = False
        if self.furthest_south().to_lla().get_latitude() < lat < self.furthest_north().to_lla().get_latitude():
            contained = self.furthest_west().to_lla().get_longitude() < lon < self.furthest_east().to_lla().get_longitude()

        return contained

    def contains(self, p):
        # ll = p.to_pvector()[:]
        # p_ = Point(*ll)
        # return p_.within(self._poly) or p_.touches(self._poly)
        pass

    def mean_position(self):
        return mean_position(self._verts)


class Earth(object):
    radius_m = 6371E3
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

    def n_E2R_EN(self):
        n_E = self.to_nvector().get_xyz(shape=(3, 1))
        R_EN = nv.n_E2R_EN(n_E)

        return R_EN

    def move_ned(self, north, east, down):
        p_EA_E = self.to_pvector().get_xyz()

        R_NE = self.n_E2R_EN()
        p_delta_E = R_NE.dot([north, east, down])

        p_EA_E_delta = p_EA_E + p_delta_E

        return self.coerce(Pvector(*p_EA_E_delta))

    def distance_ned(self, other):
        R_NE = self.n_E2R_EN().T

        p_AB_E = other.to_pvector().as_array() - self.to_pvector().as_array()
        p_AB_N = R_NE.dot(p_AB_E)

        return p_AB_N

    def __repr__(self):
        return '{}'.format(','.join(self.as_array().astype(str)))

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

    def get_xyz(self, shape=(3,)):
        return self.n_EB_E.ravel().reshape(shape)

    def get_depth(self):
        return self.depth

    def to_nvector(self):
        return self

    def to_pvector(self):
        x, y, z = nv.n_EB_E2p_EB_E(self.n_EB_E, depth=self.depth, a=NV_A, f=NV_F).ravel()

        return Pvector(x, y, z)

    def to_lla(self):
        lat, lon = nv.n_E2lat_lon(self.n_EB_E)

        return Lla(np.rad2deg(lat[0]), np.rad2deg(lon[0]), -self.depth)

    def _as_array(self):
        x, y, z = self.n_EB_E.ravel()
        return arr([x, y, z, self.depth])


class Pvector(Position):
    def __init__(self, x, y, z):
        self.p_EB_E = arr([x, y, z]).astype(np.float64).reshape(-1, 1)

    def __sub__(self, other):
        return self.p_EB_E.ravel() - other.p_EB_E.ravel()

    def get_x(self):
        return self.p_EB_E[0, 0]

    def get_y(self):
        return self.p_EB_E[1, 0]

    def get_z(self):
        return self.p_EB_E[2, 0]

    def get_xyz(self, shape=(3,)):
        xyz = self.p_EB_E.ravel().reshape(shape)
        return xyz

    def to_nvector(self):
        (x, y, z), depth = nv.p_EB_E2n_EB_E(self.p_EB_E, a=NV_A, f=NV_F)

        return Nvector(x, y, z, depth)

    def to_pvector(self):
        return self

    def to_lla(self):
        return self.to_nvector().to_lla()

    def _as_array(self):
        return self.p_EB_E.ravel()



if __name__ == '__main__':
    origin = Lla(41.679945, -86.245484, 210)

    sep = 3
    for i in range(-1, 1):
        for j in range(8):
            print('"home": [{}],'.format(','.join(map(str, origin.move_ned(sep * j, sep * i, 0).as_array()[:2]))))
