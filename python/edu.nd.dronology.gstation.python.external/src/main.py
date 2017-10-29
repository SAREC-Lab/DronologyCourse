import control
import comms
import argparse
import util
import atexit
import time

_LOG = util.get_logger()


def main(gid, addr, port, drone_configs):
    dronology_in_msg_queue = comms.MessageQueue()
    dronology_out_msg_queue = comms.MessageQueue()
    new_vehicle_msg_queue = comms.MessageQueue()
    connection = comms.Connection(dronology_in_msg_queue, addr=addr, port=port, g_id=gid)
    ctrl_station = control.ControlStation(connection,
                                          dronology_in_msg_queue, dronology_out_msg_queue, new_vehicle_msg_queue)

    @atexit.register
    def shutdown():
        connection.stop()
        ctrl_station.stop()

    connection.start()
    ctrl_station.start()
    time.sleep(1.0)

    for dc in util.load_drone_configs(drone_configs):
        new_vehicle_msg_queue.put_message(dc)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-gid', '--gcs_id',
                        type=str, default='default_ground_station')
    parser.add_argument('-addr', '--address',
                        type=str, default='')
    parser.add_argument('-p', '--port',
                        type=int, default=1234)
    parser.add_argument('-d', '--drone_configs', type=str, default='../cfg/drone_cfgs/default.json')
    args = parser.parse_args()
    main(args.gcs_id, args.address, args.port, args.drone_configs)
