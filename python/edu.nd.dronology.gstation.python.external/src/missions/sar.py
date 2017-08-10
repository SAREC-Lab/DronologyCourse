import numpy as np
import util
import networkx as nx
import itertools
import core
import dronekit
import argparse
import threading
import string
import random
from missions import Mission
import matplotlib.pyplot as plt
from mathutil import Lla, GeoPoly
from common import *

_LOG = util.get_logger()

arr = np.array


def _init_tsp(start, points, point_last_seen):
    path = []

    if point_last_seen is not None:
        path.append(point_last_seen)
    else:
        dists = [start.distance(a) for a in points]
        i = np.argmin(dists)
        path.append(points[i])
        points.pop(i)

    return path


def tsp_christofides(start, points, point_last_seen=None):
    path = _init_tsp(start, points, point_last_seen)

    g = nx.Graph()
    for a, b in itertools.combinations(points, 2):
        g.add_edge(a, b, weight=a.distance(b))

    T = nx.minimum_spanning_tree(g)

    return path


def tsp_greedy(start, points, point_last_seen=None):
    path = _init_tsp(start, points, point_last_seen)
    to_visit = arr(points)
    while to_visit.size:
        dists = [path[-1].distance(b) for b in to_visit]
        i = np.argmin(dists)
        path.append(to_visit[i])
        to_visit = np.delete(to_visit, i, axis=0)

    return arr(path)


def _get_search_path_default(start, vertices, N=32, point_last_seen=None):
    m = int(np.ceil(np.sqrt(N)))
    p_ES_E = start.to_pvector()
    grid = GeoPoly(vertices)

    p_EA_E = grid.sw_vertex()
    p_EB_E = grid.ne_vertex()
    p_EC_E = grid.se_vertex()

    R_NE = p_EA_E.n_E2R_EN().T
    p_AB_N = R_NE.dot(p_EB_E - p_EA_E)
    p_AC_N = R_NE.dot(p_EC_E - p_EA_E)

    n_dist = p_AB_N[0]
    e_dist = p_AC_N[1]

    n_step = n_dist / m
    e_step = e_dist / m

    S = []

    for x in np.arange(0, e_dist - e_step, e_step):
        for y in np.arange(0, n_dist - n_step, n_step):
            S.append(p_EA_E.move_ned(y, x, 0).to_lla())

    # TODO: fix the filter
    # S = [s for s in S if grid.contains(s)]
    path = tsp_greedy(p_ES_E, S, point_last_seen=point_last_seen)
    return [pos.to_lla() for pos in path]


def _partition_grid(bounds, N):
    m = int(np.ceil(np.sqrt(N)))
    grid = GeoPoly(bounds)
    p_EA_E = grid.sw_vertex()
    p_EB_E = grid.nw_vertex()
    p_EC_E = grid.se_vertex()

    R_NE = p_EA_E.n_E2R_EN().T
    p_AB_N = R_NE.dot(p_EB_E - p_EA_E)
    p_AC_N = R_NE.dot(p_EC_E - p_EA_E)

    n_dist = p_AB_N[0]
    e_dist = p_AC_N[1]

    n_step = n_dist / m
    e_step = e_dist / m

    quads = []
    for i in np.arange(0, e_dist, e_step):
        for j in np.arange(0, n_dist, n_step):
            sw = p_EA_E.move_ned(j, i, 0)
            nw = p_EA_E.move_ned(j + n_step, i, 0)
            ne = p_EA_E.move_ned(j + n_step, i + e_step, 0)
            se = p_EA_E.move_ned(j, i + e_step, 0)
            quads.append(list(map(lambda pos: pos.to_lla(), [sw, nw, ne, se])))

    return quads


_search_strats = {
    SEARCH_DEFAULT: _get_search_path_default,
}


def get_search_path(start, vertices, strat=SEARCH_DEFAULT, N=36, point_last_seen=None):
    """

    :param start: the starting location of the drone (Position)
    :param vertices: the vertices of the search space ([Position, ... ])
    :param strat: the search strategy (choose from search strategies in common.py)
    :param N: number of sectors
    :param point_last_seen: optional, the last known location of the target (lat, lon, alt)
    :return:
    """
    if strat not in _search_strats:
        raise ValueError('invalid search strategy specified {}'.format(strat))

    return _search_strats[strat](start, vertices, N=N, point_last_seen=point_last_seen)


class SaR(Mission):
    @staticmethod
    def parse_args(cla):
        parser = argparse.ArgumentParser()
        parser.add_argument('-c', '--control',
                            type=Mission._parse_controller, default='core.ArduPilot',
                            help=Mission._parse_controller.__doc__)
        parser.add_argument('-p', '--partition_grid',
                            action='store_true',
                            help='flag to indicate that the search space needs to be partitioned'
                                 '\notherwise it is assumed each drone has its own grid')
        parser.add_argument('-b', '--bounds',
                            type=Mission._parse_sar_bounds, default=DEFAULT_SAR_BOUNDS_STR,
                            help=Mission._parse_sar_bounds.__doc__)
        parser.add_argument('-pls', '--point_last_seen',
                            type=SaR._parse_coord, default='', help=SaR._parse_coord.__doc__)
        parser.add_argument('-cfg', '--drone_configs',
                            type=Mission._parse_drone_cfg, default='../cfg/drone_cfgs/16_drone_SAR.json',
                            help=Mission._parse_drone_cfg.__doc__)
        parser.add_argument('-ap', '--ardupath',
                            type=str, default=ARDUPATH, help='the path to ardupilot static resources')

        parser.set_defaults(partition_grid=False)
        args = parser.parse_args(cla.split())
        return vars(args)

    @staticmethod
    def start(connection, drone_configs=None, ardupath=ARDUPATH, control=core.ArduPilot,
              bounds=None, point_last_seen=None, partition_grid=False):
        """
        Conduct a search and rescue mission with a single UAV using waypoint navigation.


        :param control: the UAV controller to use
        :param connection: core.Connection object used to talk to Dronology
        :param drone_configs:
        :param ardupath:
        :param control:
        :param bounds: the bounds of the search space, defaults to None and assumes each drone has its own bounds
        :param point_last_seen:
        :param partition_grid: if True then the grid should be partitioned and each partition assigned to a drone
        :return:
        """
        workers = []
        if drone_configs is not None:
            n_drones = len(drone_configs)
            n_vrtl = 0
            n_phys = 0

            if partition_grid:
                _LOG.info('Attempting to automatically partition the grid.')
                bounds = map(lambda tup: Lla(tup[0], tup[1], 0), bounds)
                quadrants = _partition_grid(bounds, n_drones)
                for i, dc in enumerate(drone_configs):
                    dc['bounds'] = quadrants[i]

            workers = []
            # np.random.shuffle(drone_configs)
            for i, dc in enumerate(drone_configs):
                clazz = dc['class']
                vid = 'UAV{:03d}{}{:02d}'.format(random.randint(1, 1000),
                                                 ''.join(random.sample(string.letters, 2)),
                                                 random.randint(1, 50))

                dc_bounds = map(lambda tup: Lla(tup[0], tup[1], 0), dc['bounds'])
                args = [connection, control, dc_bounds, point_last_seen,
                        vid, clazz, dc['altitude'], dc['groundspeed'], n_vrtl - 1, ardupath,
                        dc['baud'], dc['rate'], tuple(dc['home']), dc['ip']]

                if clazz == DRONE_TYPE_SITL_VRTL:
                    n_vrtl += 1
                else:
                    n_phys += 1

                worker = threading.Thread(target=SaR._start, args=args)
                workers.append(worker)

        np.random.shuffle(workers)

        while not connection.is_connected():
            time.sleep(3.0)

        for worker in workers:
            worker.start()
            time.sleep(1.5)

        for worker in workers:
            worker.join()

    @staticmethod
    def _start(connection, control, bounds, pls, v_id, v_type, alt, gs, inst, ardu, baud, rate, home, ip):
        vehicle, shutdown_cb = control.connect_vehicle(v_type, vehicle_id=v_id, ip=ip,
                                                       instance=inst, ardupath=ardu,
                                                       rate=rate, home=home + (0, 0,), baud=baud)

        handshake_complete = False

        # SET UP TIMERS
        def gen_state_message(m_vehicle):
            msg = StateMessage.from_vehicle(m_vehicle, v_id)
            connection.send(str(msg))

        def gen_monitor_message(m_vehicle):
            msg = MonitorMessage.from_vehicle(m_vehicle, v_id)
            connection.send(str(msg))

        start = Lla(home[0], home[1], 0)
        path = get_search_path(start, bounds, point_last_seen=pls)

        # ARM & READY
        control.set_armed(vehicle, armed=True)
        _LOG.info('Vehicle {} armed.'.format(v_id))
        vehicle.mode = dronekit.VehicleMode('GUIDED')

        # WAIT FOR HANDSHAKE BEFORE STARTING
        while not handshake_complete:
            handshake_complete = connection.send(str(HandshakeMessage.from_vehicle(vehicle, v_id)))
            time.sleep(3)

        # START MESSAGE TIMERS
        util.RepeatedTimer(1.0, gen_state_message, vehicle)
        monitor_msg_timer = util.RepeatedTimer(5.0, gen_monitor_message, vehicle)

        for i in range(10):
            # log the expected route
            _LOG.debug(
                'vehicle {} dispatched to {}'.format(v_id, '|'.join([','.join(x[:-1].astype(str)) for x in path])))

            waypoints = [Waypoint(path[0][0], path[0][1], alt, groundspeed=gs)]
            for lat, lon, _ in path[1:]:
                waypoints.append(Waypoint(lat, lon, np.random.uniform(alt - 3, alt + 3),
                                          groundspeed=np.random.uniform(1, 3)))
            waypoints.append(Waypoint(home[0], home[1], alt, groundspeed=gs))

            # TAKEOFF
            control.takeoff(vehicle, alt=alt)
            _LOG.info('Vehicle {} takeoff complete.'.format(v_id))

            # FLY
            _LOG.info('Vehicle {} en route!'.format(v_id))
            worker = control.goto_lla_sequential(vehicle, v_id, waypoints, block=False)
            try:
                # HANDLE INCOMING MESSAGES
                while worker.isAlive():
                    cmds = core.get_commands(v_id)
                    for cmd in cmds:
                        if isinstance(cmd, (SetMonitorFrequency,)):
                            # acknowledge
                            connection.send(str(AcknowledgeMessage.from_vehicle(vehicle, v_id, msg_id=cmd.get_msg_id())))
                            # stop the timer, send message, reset interval, restart timer
                            monitor_msg_timer.stop()
                            gen_monitor_message(vehicle)
                            monitor_msg_timer.set_interval(cmd.get_monitor_frequency() / 1000)
                            monitor_msg_timer.start()
                worker.join()
            except KeyboardInterrupt:
                vehicle.mode = dronekit.VehicleMode('LOITER')

            worker.join()

        control.land(vehicle)
        _LOG.info('Vehicle {} landed.'.format(v_id))
        control.set_armed(vehicle, armed=False)
        _LOG.info('Vehicle {} disarmed.'.format(v_id))
        shutdown_cb()


class SaRLoop(SaR):
    @staticmethod
    def start(connection, drone_configs=None, ardupath=ARDUPATH, control=core.ArduPilot,
              bounds=None, point_last_seen=None, partition_grid=False):
        pass


def main():
    v1 = 41.5190146513, -86.2400358089, 0
    v2 = 41.5192946477, -86.239555554, 0
    v3 = 41.5190274009, -86.2394354903, 0
    # bounds = URBAN_SAR_BOUNDS
    # # bounds = [v1, v2, v3]
    # # s = Lla(DEFAULT_SAR_START[0], DEFAULT_SAR_START[1], 0)
    # s = Lla(41.683202, -86.250413, 0)
    # v = [Lla(loc[0], loc[1], 0) for loc in bounds]
    #
    # quads = _partition_grid(v, 16)
    # # print(len(quads))
    # # for quad in quads:
    # #     print('{}\n'.format('\n'.join(map(lambda pos: ','.join(map(str, pos[:2])), quad))))
    # p = get_search_path(s, quads[1])
    # print('\n'.join([','.join(x[:-1].astype(str)) for x in p]))
    dcs = Mission._parse_drone_cfg('../cfg/drone_cfgs/16_drone_SAR.json')

    for dc in dcs:
        path = get_search_path(Lla(dc['home'][0], dc['home'][1], 0),
                               map(lambda tup: Lla(tup[0], tup[1], 0), dc['bounds']))
        print(path)


if __name__ == '__main__':
    main()
