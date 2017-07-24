import numpy as np
import util
from mathutil import Lla, GeoPoly
from common import *

_LOG = util.get_logger()

arr = np.array


def tsp_greedy(start, points, point_last_seen=None):
    to_visit = arr(points)
    path = [start]

    if point_last_seen is not None:
        path.append(point_last_seen)

    while to_visit.size:
        dists = [path[-1].distance(b) for b in to_visit]
        i = np.argmin(dists)
        path.append(to_visit[i])
        to_visit = np.delete(to_visit, i, axis=0)

    return arr(path)


def _get_search_path_default(start, vertices, step=5, point_last_seen=None):
    p_ES_E = start.to_pvector()
    grid = GeoPoly(vertices)

    p_EA_E = grid.sw_vertex()
    p_EC_E = grid.ne_vertex()
    p_ED_E = grid.se_vertex()
    p_furthest_north_E = grid.furthest_north()

    R_EN = p_EA_E.n_E2R_EN()
    p_AD_N = R_EN.T.dot(p_ED_E - p_EA_E)
    p_AC_N = R_EN.T.dot(p_EC_E - p_EA_E)
    e_dist = int(max(p_AD_N[1], p_AC_N[1]))
    azimuth = np.rad2deg(np.arctan2(p_AD_N[1], p_AD_N[0]))

    S = []

    for x in range(0, e_dist + step, step):
        p_EA1_E = p_EA_E.move_azimuth_distance(azimuth, x)
        p_A1FN_E = p_furthest_north_E - p_EA1_E
        p_A1FN_N = p_EA1_E.n_E2R_EN().T.dot(p_A1FN_E)
        n_dist = int(p_A1FN_N[0])
        for y in range(0, n_dist + step, step):
            p_new = p_EA1_E.move_azimuth_distance(0, y)
            S.append(p_new)

    # TODO: implement a filter to remove all points in S that do not intersect with the GeoPoly

    path = tsp_greedy(p_ES_E, S, point_last_seen=point_last_seen)
    return [pos.to_lla() for pos in path]


_search_strats = {
    SEARCH_DEFAULT: _get_search_path_default,
}


def get_search_path(start, vertices, strat=SEARCH_DEFAULT, step=10, point_last_seen=None):
    """

    :param start: the starting location of the drone (Position)
    :param vertices: the vertices of the search space ([Position, ... ])
    :param strat: the search strategy (choose from search strategies in common.py)
    :param step: the number of meters between each waypoint
    :param point_last_seen: optional, the last known location of the target (lat, lon, alt)
    :return:
    """
    if strat not in _search_strats:
        raise ValueError('invalid search strategy specified {}'.format(strat))

    return _search_strats[strat](start, vertices, step=step, point_last_seen=point_last_seen)


def main():
    bounds = DEFAULT_SAR_BOUNDS
    s = Lla(*DEFAULT_SAR_START)
    v = [Lla(*loc) for loc in bounds]

    p = get_search_path(s, v)
    print('\n'.join([','.join(x[:-1].astype(str)) for x in p]))


if __name__ == '__main__':
    main()
