import dronekit
import numpy as np
import nvector as nv
import matplotlib.path as mpl_path


arr = np.array


class Position(object):
    def distance(self, other):
        x1, y1, z1, depth1 = self.to_nvector().as_array()
        x2, y2, z2, depth2 = other.to_nvector().as_array()

        n1 = arr([x1, y1, z1]).reshape(-1, 1)
        n2 = arr([x2, y2, z2]).reshape(-1, 1)

        dist = nv.n_EA_E_and_n_EB_E2p_AB_E(n1, n2, z_EA=depth1, z_EB=depth2).ravel()
        dist = np.sum(np.sqrt(dist ** 2))

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
        self.lat = latitude
        self.lon = longitude
        self.alt = altitude

    def to_nvector(self):
        x, y, z = nv.lat_lon2n_E(np.deg2rad(self.lat), np.deg2rad(self.lon)).ravel()

        return NVector(x, y, z, -self.alt)

    def to_pvector(self):
        return self.to_nvector().to_pvector()

    def to_lla(self):
        return self

    def as_array(self):
        return arr([self.lat, self.lon, self.alt])


class NVector(Position):
    def __init__(self, x, y, z, depth):
        self.x = x
        self.y = y
        self.z = z
        self.depth = depth

    def to_nvector(self):
        return self

    def to_pvector(self):
        x, y, z = nv.n_EB_E2p_EB_E(arr([self.x, self.y, self.z]).reshape(-1, 1), depth=self.depth).ravel()

        return PVector(x, y, z)

    def to_lla(self):
        lat, lon = nv.n_E2lat_lon(arr([self.x, self.y, self.z]).reshape(-1, 1))

        return LlaCoordinate(np.rad2deg(lat), np.rad2deg(lon), -self.depth)

    def as_array(self):
        return arr([self.x, self.y, self.z, self.depth])


class PVector(Position):
    def __init__(self, x, y, z):
        self.x = x
        self.y = y
        self.z = z

    def to_nvector(self):
        (x, y, z), depth = nv.p_EB_E2n_EB_E(arr([self.x, self.y, self.z]).reshape(-1, 1))

        return NVector(x, y, z, depth)

    def to_pvector(self):
        return self

    def to_lla(self):
        return self.to_nvector().to_lla()

    def as_array(self):
        return arr([self.x, self.y, self.z])



def get_search_path(vertices):
    if len(vertices) < 3:
        raise ValueError('invalid search area, must have 3 vertices.')

    return mpl_path.Path(vertices)
