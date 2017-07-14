import core
import argparse
import util
from core import connect_vehicle, make_mavlink_command, deploy_vehicle
from pymavlink.mavutil import mavlink
from common import *

_LOG = util.get_logger()


def mission_single_uav_sar(connection, v_type, v_id, bounds, last_known_loc=None, ip=None,
                           instance=0, ardupath=ARDUPATH,
                           speed=1, rate=10, home=(41.732955, -86.180886, 0, 0), baud=115200):
    vehicle, shutdown_cb = connect_vehicle(v_type, vehicle_id=v_id, ip=ip, instance=instance, ardupath=ardupath,
                                           speed=speed, rate=rate, home=home, baud=baud)

    vehicle.commands.clear()
    vehicle.commands.upload()

    vehicle.commands.add(core.make_mavlink_command(mavlink.MAV_CMD_DO_SET_HOME))
    vehicle.commands.add(core.make_mavlink_command(mavlink.MAV_CMD_MISSION_START))
    # TODO: if last known location is not none, we should start there

    for lat, lon, alt in bounds:
        # TODO: do some sort of search instead of just iterating the boundaries
        cmd = make_mavlink_command(mavlink.MAV_CMD_NAV_WAYPOINT,
                                   frame=mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT,
                                   latitude=lat, longitude=lon, altitude=alt)
        vehicle.commands.add(cmd)

    @vehicle.on_attribute('mode')
    def mode_listener(_, name, value):
        # should stop doing whatever we are doing if mode changes
        pass

    vehicle.commands.upload()

    _LOG.info('Commands uploaded successfully to vehicle {}.'.format(v_id))
    v_worker = deploy_vehicle(connection, vehicle, v_id, mode='AUTO')
    v_worker.join()


def main(host, port, vehicle_type, vehicle_id, ardupath, bounds=DEFAULT_SAR_BOUNDS):
    _LOG.info('STARTING NEW MISSION.')
    try:
        # start a thread to monitor dronology connection
        connection = core.Connection(host=host, port=port)
        connection.start()

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
