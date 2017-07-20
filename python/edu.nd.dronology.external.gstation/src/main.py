import core
import argparse
import util
import dronekit
from common import *

_LOG = util.get_logger()


def mission_single_uav_sar(connection, v_type, v_id, bounds, last_known_loc=None, ip=None, instance=0,
                           ardupath=ARDUPATH, speed=1, rate=10, home=(41.519508, -86.239996, 0, 0), baud=115200):
    vehicle, shutdown_cb = core.connect_vehicle(v_type, vehicle_id=v_id, ip=ip, instance=instance,
                                                ardupath=ardupath, speed=speed, rate=rate, home=home, baud=baud)

    # SET UP CALLBACKS
    @vehicle.on_attribute('mode')
    def mode_listener(_, name, value):
        # should stop doing whatever we are doing if mode changes
        pass

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

    # TODO: make the search better (if we actually care)
    waypoints = []
    if last_known_loc:
        waypoints.append(Waypoint(*last_known_loc, groundpseed=10))
    for lat, lon, alt in bounds:
        waypoints.append(Waypoint(lat, lon, alt, groundpseed=10))

    home = vehicle.home_location
    waypoints.append(Waypoint(home.lat, home.lon, bounds[-1][-1], groundpseed=10))

    # SEND HANDSHAKE
    dronology_handshake_complete = connection.send(str(DronologyHandshakeMessage.from_vehicle(vehicle, v_id)))

    # START MESSAGE TIMERS
    send_state_message_timer = util.RepeatedTimer(1.0, gen_state_message, vehicle)
    send_monitor_message_timer = util.RepeatedTimer(5.0, gen_monitor_message, vehicle)

    # TAKEOFF
    core.takeoff(vehicle, alt=10)
    _LOG.info('Vehicle {} takeoff complete.'.format(v_id))

    # FLY
    worker = core.goto_sequential(vehicle, waypoints, block=False)

    # HANDLE INCOMING MESSAGES
    while worker.isAlive():
        if not dronology_handshake_complete:
            dronology_handshake_complete = connection.send(str(DronologyHandshakeMessage.from_vehicle(vehicle, v_id)))

        cmds = core.get_commands(v_id)
        if cmds:
            for cmd in cmds:
                # TODO: decide how to respond to this command
                pass

    worker.join()
    
    core.land(vehicle)
    _LOG.info('Vehicle {} landed.'.format(v_id))
    core.set_armed(vehicle, armed=False)
    _LOG.info('Vehicle {} disarmed.'.format(v_id))
    shutdown_cb()


def main(host, port, vehicle_type, vehicle_id, ardupath, bounds=DEFAULT_SAR_BOUNDS):
    _LOG.info('STARTING NEW MISSION.')
    try:
        # start a thread to monitor dronology connection
        connection = core.Connection(host=host, port=port)
        connection.start()
        _LOG.info('Accepting connection on tcp:{}:{}'.format(host, port))

        # TODO: make this configurable
        mission_single_uav_sar(connection, vehicle_type, vehicle_id, bounds, ardupath=ardupath)
        connection.stop()
    except KeyboardInterrupt:
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
