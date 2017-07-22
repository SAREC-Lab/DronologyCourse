import numpy as np
import util
import mathutil
from common import *

_LOG = util.get_logger()

arr = np.array



def _distance_2d(p0, p1):
    return np.sqrt(np.sum((p1 - p0) **2))


def tsp_greedy(start, points):
    to_visit = points
    path = [start]

    while to_visit.size:
        dists = np.sqrt(np.sum((to_visit - path[-1]) ** 2, axis=1))
        i = np.argmin(dists)
        path.append(to_visit[i])
        to_visit = np.delete(to_visit, i, axis=0)

    return arr(path)


def _get_search_path_default(start, vertices, step=10):
    start_2d = start.to_pvector()[:-1]
    grid_2d = arr([v.to_pvector()[:-1] for v in vertices])
    z = start.to_pvector()[-1]

    x_min, x_max = [f(grid_2d[:, 0]) for f in (np.min, np.max)]
    y_min, y_max = [f(grid_2d[:, 1]) for f in (np.min, np.max)]

    x = np.linspace(x_min, x_max, num=max((x_max - x_min) / step, 3))
    y = np.linspace(y_min, y_max, num=max((y_max - y_min) / step, 3))

    xx, yy = np.meshgrid(x, y)
    S = np.c_[xx.ravel(), yy.ravel()]

    path_2d = tsp_greedy(start_2d, S)

    return [mathutil.Pvector(x, y, z).to_lla() for x, y in path_2d]




_search_strats = {
    SEARCH_DEFAULT: _get_search_path_default,
}


def get_search_path(start, vertices, strat=SEARCH_DEFAULT, point_last_seen=None):
    """

    :param start: the starting location of the drone (mathutil.Position)
    :param vertices: the vertices of the search space ([mathutil.Position, ... ])
    :param strat: the search strategy (choose from search strategies in common.py)
    :param point_last_seen: optional, the last known location of the target (lat, lon, alt)
    :return:
    """
    if len(vertices) < 3:
        raise ValueError('invalid search area, must have at least 3 vertices.')

    if strat not in _search_strats:
        raise ValueError('invalid search strategy specified {}'.format(strat))

    return _search_strats[strat](start, vertices)


if __name__ == '__main__':
    bounds = DEFAULT_SAR_BOUNDS
    s = mathutil.Lla(*bounds[0])
    v = [mathutil.Lla(*loc) for loc in bounds]

    p = get_search_path(s, v)
    print(p)