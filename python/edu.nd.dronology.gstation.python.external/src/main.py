import core
import argparse
import util
from missions import sar
from common import *

_LOG = util.get_logger()


def main(host, port, vehicle_type, vehicle_id, ardupath, bounds=DEFAULT_SAR_BOUNDS):
    _LOG.info('STARTING NEW MISSION.')
    connection = core.Connection(host=host, port=port)
    # start a thread to monitor dronology connection
    connection.start()
    _LOG.info('Accepting connection on tcp:{}:{}'.format(host, port))

    # TODO: make this configurable
    sar.SingleUAVSAR.start(connection, vehicle_type, vehicle_id, bounds, ardupath=ardupath)
    # mission_single_uav_sar(connection, vehicle_type, vehicle_id, bounds, ardupath=ardupath)
    connection.stop()
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
