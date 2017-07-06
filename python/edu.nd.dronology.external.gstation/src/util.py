import dronekit
import numpy as np
import nvector as nv
import matplotlib.path as mpl_path


arr = np.array

SEMI_MAJOR = np.float64(6378137.0)
SEMI_MINOR = np.float64(6356752.31)

NV_A = SEMI_MAJOR
NV_F = 1 - (SEMI_MINOR / SEMI_MAJOR)


class Position(object):
    def distance(self, other):
        p1 = self.to_pvector().as_array()
        p2 = other.to_pvector().as_array()

        resid = p1 - p2
        resid_sq = resid ** 2
        resid_sq_sum = resid_sq.sum()
        dist = np.sqrt(resid_sq_sum)

        return dist

    def to_lla(self):
        pass

    def to_nvector(self):
        pass

    def to_pvector(self):
        pass

    def as_array(self):
        pass

    def coerce(self, other):
        if isinstance(other, Position):
            if isinstance(self, LlaCoordinate):
                return other.to_lla()
            elif isinstance(self, NVector):
                return other.to_nvector()
            else:
                return other.to_pvector()
        else:
            return other


class LlaCoordinate(Position):
    def __init__(self, latitude, longitude, altitude):
        self.lla = arr([latitude, longitude, altitude]).astype(np.float64)

    def to_nvector(self):
        x, y, z = nv.lat_lon2n_E(*map(lambda a: np.float64(np.deg2rad(a)), self.lla[:-1])).ravel()

        return NVector(x, y, z, -self.lla[-1])

    def to_pvector(self):
        return self.to_nvector().to_pvector()

    def to_lla(self):
        return self

    def as_array(self):
        return self.lla


class NVector(Position):
    def __init__(self, x, y, z, depth):
        self.n_EB_E = arr([x, y, z]).astype(np.float64).reshape(-1, 1)
        self.depth = depth

    def to_nvector(self):
        return self

    def to_pvector(self):
        x, y, z = nv.n_EB_E2p_EB_E(self.n_EB_E, depth=self.depth, a=NV_A, f=NV_F).ravel()

        return PVector(x, y, z)

    def to_lla(self):
        lat, lon = nv.n_E2lat_lon(self.n_EB_E)

        return LlaCoordinate(np.rad2deg(lat), np.rad2deg(lon), -self.depth)

    def as_array(self):
        x, y, z = self.n_EB_E.ravel()
        return arr([x, y, z, self.depth])


class PVector(Position):
    def __init__(self, x, y, z):
        self.p_EB_E = arr([x, y, z]).astype(np.float64).reshape(-1, 1)

    def to_nvector(self):
        (x, y, z), depth = nv.p_EB_E2n_EB_E(self.p_EB_E, a=NV_A, f=NV_F)

        return NVector(x, y, z, depth)

    def to_pvector(self):
        return self

    def to_lla(self):
        return self.to_nvector().to_lla()

    def as_array(self):
        return self.p_EB_E.ravel()


def get_search_path(vertices):
    if len(vertices) < 3:
        raise ValueError('invalid search area, must have 3 vertices.')

    return mpl_path.Path(vertices)
