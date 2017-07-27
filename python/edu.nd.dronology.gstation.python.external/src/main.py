import core
import argparse
import mathutil
import sar
import util
import dronekit
from common import *

_LOG = util.get_logger()


def mission_single_uav_sar(connection, v_type, v_id, bounds, point_last_seen=None, altitude=10, groundspeed=10,
                           ip=None, instance=0, ardupath=ARDUPATH, baud=115200,
                           speed=1, rate=10, home=(41.519412, -86.239830, 0, 0)):
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

    """
    vehicle, shutdown_cb = core.connect_vehicle(v_type, vehicle_id=v_id, ip=ip, instance=instance,
                                                ardupath=ardupath, speed=speed, rate=rate, home=home, baud=baud)

    # SET UP CALLBACKS
    # @vehicle.on_attribute('mode')
    # def mode_listener(_, name, value):
    #     # should stop doing whatever we are doing if mode changes
    #     pass

    # SET UP TIMERS
    def gen_state_message(m_vehicle):
        msg = DronologyStateMessage.from_vehicle(m_vehicle, v_id)
        _LOG.debug(str(msg))
        connection.send(str(msg))

    def gen_monitor_message(m_vehicle):
        msg = DronologyMonitorMessage.from_vehicle(m_vehicle, v_id)
        _LOG.debug(str(msg))
        connection.send(str(msg))

    # ARM & READY
    core.set_armed(vehicle, armed=True)
    _LOG.info('Vehicle {} armed.'.format(v_id))
    vehicle.mode = dronekit.VehicleMode('GUIDED')

    home = vehicle.home_location
    start = mathutil.Lla(home.lat, home.lon, 0)
    vertices = [mathutil.Lla(lat, lon, 0) for lat, lon, _ in bounds]
    path = sar.get_search_path(start, vertices, point_last_seen=point_last_seen)

    waypoints = []
    if point_last_seen:
        waypoints.append(Waypoint(*point_last_seen, groundpseed=groundspeed))
    for lat, lon, _ in path:
        waypoints.append(Waypoint(lat, lon, altitude, groundpseed=groundspeed))

    # SEND HANDSHAKE
    dronology_handshake_complete = connection.send(str(DronologyHandshakeMessage.from_vehicle(vehicle, v_id)))

    # START MESSAGE TIMERS
    send_state_message_timer = util.RepeatedTimer(1.0, gen_state_message, vehicle)
    send_monitor_message_timer = util.RepeatedTimer(5.0, gen_monitor_message, vehicle)

    # TAKEOFF
    core.takeoff(vehicle, alt=altitude)
    _LOG.info('Vehicle {} takeoff complete.'.format(v_id))

    # FLY
    worker = core.goto_sequential(vehicle, waypoints, block=False)

    try:
        # HANDLE INCOMING MESSAGES
        while worker.isAlive():
            if not connection.is_connected():
                dronology_handshake_complete = False

            if not dronology_handshake_complete:
                dronology_handshake_complete = connection.send(str(DronologyHandshakeMessage.from_vehicle(vehicle, v_id)))

            cmds = core.get_commands(v_id)
            for cmd in cmds:
                if isinstance(cmd, (SetMonitorFrequency,)):
                    # stop the timer, send message, reset interval, restart timer
                    send_monitor_message_timer.stop()
                    gen_monitor_message(vehicle)
                    send_monitor_message_timer.set_interval(cmd.get_monitor_frequency() / 1000)
                    send_monitor_message_timer.start()

        worker.join()
        core.return_to_launch(vehicle)
        _LOG.info('Vehicle {} landed.'.format(v_id))
        core.set_armed(vehicle, armed=False)
        _LOG.info('Vehicle {} disarmed.'.format(v_id))
    except KeyboardInterrupt:
        vehicle.mode = dronekit.VehicleMode('Loiter')

    worker.join()
    shutdown_cb()


def main(host, port, vehicle_type, vehicle_id, ardupath, bounds=DEFAULT_SAR_BOUNDS):
    _LOG.info('STARTING NEW MISSION.')
    connection = core.Connection(host=host, port=port)
    try:
        # start a thread to monitor dronology connection
        connection.start()
        _LOG.info('Accepting connection on tcp:{}:{}'.format(host, port))

        # TODO: make this configurable
        mission_single_uav_sar(connection, vehicle_type, vehicle_id, bounds, ardupath=ardupath)
        connection.stop()
    except KeyboardInterrupt:
        connection.stop()
        exit(-1)
    _LOG.info('MISSION ENDED.')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-ap', '--ardupath', type=str, required=True)
    parser.add_argument('-host', '--host', type=str, default='127.0.0.1')
    parser.add_argument('-p', '--port', type=int, default=1234)
    parser.add_argument('-vtype', '--vehicle_type', type=str, default=DRONE_TYPE_SITL_VRTL)
    parser.add_argument('-vid', '--vehicle_id', type=float, default=1.0)
    args = parser.parse_args()

    main(args.host, args.port, args.vehicle_type, args.vehicle_id, args.ardupath)
