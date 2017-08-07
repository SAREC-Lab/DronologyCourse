import numpy as np
import util
import networkx as nx
import itertools
import core
import dronekit
import argparse
import threading
from missions import Mission
import matplotlib.pyplot as plt
from mathutil import Lla, GeoPoly
from common import *

_LOG = util.get_logger()

arr = np.array


def _init_tsp(start, point_last_seen):
    path = [start]

    if point_last_seen is not None:
        path.append(point_last_seen)

    return path


def tsp_christofides(start, points, point_last_seen=None):
    path = _init_tsp(start, point_last_seen)

    g = nx.Graph()
    for a, b in itertools.combinations(points, 2):
        g.add_edge(a, b, weight=a.distance(b))

    T = nx.minimum_spanning_tree(g)

    return path


def tsp_greedy(start, points, point_last_seen=None):
    path = _init_tsp(start, point_last_seen)
    to_visit = arr(points)
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
    _LOG.debug('SW SAR corner: {}'.format(p_EA_E.to_lla()))
    p_EC_E = grid.ne_vertex()
    _LOG.debug('NE SAR corner: {}'.format(p_EC_E.to_lla()))
    p_ED_E = grid.se_vertex()
    _LOG.debug('SE SAR corner: {}'.format(p_ED_E.to_lla()))
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

    # TODO: fix the filter
    # S = [s for s in S if grid.contains(s)]
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


class SingleUAVSAR(Mission):
    @staticmethod
    def start(connection, v_type=DRONE_TYPE_SITL_VRTL, v_id='VRTL_0', bounds=DEFAULT_SAR_BOUNDS,
              point_last_seen=None, altitude=10, groundspeed=10, ip=None, instance=0, ardupath=ARDUPATH, baud=115200,
              speed=1, rate=10, home=(41.519412, -86.239830, 0, 0), control=core.ArduPilot, **kwargs):
        """
        Conduct a search and rescue mission with a single UAV using waypoint navigation.

        :param connection: core.Connection object used to talk to Dronology
        :param v_type: vehicle type (PHYS or VRTL)
        :param v_id: vehicle id
        :param bounds: the bounds of the search space ([(lat, lon, alt), ...])
                       NOTE: alt will be ignored but still must be provided
        :param point_last_seen: the last point the target was seen (lat, lon, alt)
        :param altitude: the altitude at which the uav should fly during the search (meters)
        :param groundspeed: the speed at which the uav should fly (m/s)
        :param ip: the ip used to connect to the vehicle (only necessary for a physical drone)
        :param instance: the SITL instance (only necessary if multiple simulated uavs are required)
        :param ardupath: the path to the ardupilot repository
        :param baud: internal SITL parameter (see documentation)
        :param speed: internal SITL parameter (see documentation)
        :param rate: internal SITL parameter (see documentation)
        :param home: internal SITL parameter (see documentation)
        :param control:
        """
        SingleUAVSAR._start(connection, v_type, v_id, bounds, point_last_seen, altitude, groundspeed,
                            ip, instance, ardupath, baud, speed, rate, home, control)

    @staticmethod
    def _start(connection, v_type, v_id, bounds, pls, alt, gs, ip, inst, ardu, baud, speed, rate, home, control):
        vehicle, shutdown_cb = control.connect_vehicle(v_type, vehicle_id=v_id, ip=ip,
                                                       instance=inst, ardupath=ardu, speed=speed,
                                                       rate=rate, home=home, baud=baud)

        # SET UP CALLBACKS
        # @vehicle.on_attribute('mode')
        # def mode_listener(_, name, value):
        #     # should stop doing whatever we are doing if mode changes
        #     pass

        # SET UP TIMERS
        def gen_state_message(m_vehicle):
            msg = DronologyStateMessage.from_vehicle(m_vehicle, v_id)
            _LOG.info(str(msg))
            connection.send(str(msg))

        def gen_monitor_message(m_vehicle):
            msg = DronologyMonitorMessage.from_vehicle(m_vehicle, v_id)
            _LOG.info(str(msg))
            connection.send(str(msg))

        # ARM & READY
        control.set_armed(vehicle, armed=True)
        _LOG.info('Vehicle {} armed.'.format(v_id))
        vehicle.mode = dronekit.VehicleMode('GUIDED')

        home = vehicle.home_location
        start = Lla(home.lat, home.lon, 0)
        vertices = [Lla(lat, lon, 0) for lat, lon, _ in bounds]
        path = get_search_path(start, vertices, point_last_seen=pls)

        waypoints = []
        for lat, lon, _ in path:
            waypoints.append(Waypoint(lat, lon, alt, groundpseed=gs))

        # SEND HANDSHAKE
        dronology_handshake_complete = connection.send(str(DronologyHandshakeMessage.from_vehicle(vehicle,
                                                                                                  v_id)))

        # START MESSAGE TIMERS
        send_state_message_timer = util.RepeatedTimer(1.0, gen_state_message, vehicle)
        send_monitor_message_timer = util.RepeatedTimer(5.0, gen_monitor_message, vehicle)

        # TAKEOFF
        control.takeoff(vehicle, alt=alt)
        _LOG.info('Vehicle {} takeoff complete.'.format(v_id))

        # FLY
        worker = control.goto_lla_sequential(vehicle, waypoints, block=False)

        try:
            # HANDLE INCOMING MESSAGES
            while worker.isAlive():
                if not connection.is_connected():
                    dronology_handshake_complete = False

                if not dronology_handshake_complete:
                    dronology_handshake_complete = connection.send(
                        str(DronologyHandshakeMessage.from_vehicle(vehicle, v_id)))

                cmds = core.get_commands(v_id)
                for cmd in cmds:
                    if isinstance(cmd, (SetMonitorFrequency,)):
                        # stop the timer, send message, reset interval, restart timer
                        send_monitor_message_timer.stop()
                        gen_monitor_message(vehicle)
                        send_monitor_message_timer.set_interval(cmd.get_monitor_frequency() / 1000)
                        send_monitor_message_timer.start()

            worker.join()
            control.goto_lla_and_wait(vehicle, *start.as_array(), groundspeed=gs)
            _LOG.info('Vehicle {} landed.'.format(v_id))
            control.set_armed(vehicle, armed=False)
            _LOG.info('Vehicle {} disarmed.'.format(v_id))
        except KeyboardInterrupt:
            vehicle.mode = dronekit.VehicleMode('LOITER')

        worker.join()
        shutdown_cb()



def main():
    v1 = 41.5190146513, -86.2400358089, 0
    v2 = 41.5192946477, -86.239555554, 0
    v3 = 41.5190274009, -86.2394354903, 0
    bounds = DEFAULT_SAR_BOUNDS
    # bounds = [v1, v2, v3]
    s = Lla(*DEFAULT_SAR_START)
    v = [Lla(*loc) for loc in bounds]

    p = get_search_path(s, v)
    print('\n'.join([','.join(x[:-1].astype(str)) for x in p]))


if __name__ == '__main__':
    main()
