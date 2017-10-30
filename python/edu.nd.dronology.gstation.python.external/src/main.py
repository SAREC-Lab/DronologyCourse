import control
import communication
import argparse
import util
import signal
import time

_LOG = util.get_logger()


def main(gid, addr, port, drone_configs):
    dronology_in_msg_queue = communication.MessageQueue()
    dronology_handshake_out_msg_queue = communication.MessageQueue()
    dronology_state_out_msg_queue = communication.MessageQueue()
    new_vehicle_msg_queue = communication.MessageQueue()
    connection = communication.Connection(dronology_in_msg_queue, addr=addr, port=port, g_id=gid)
    ctrl_station = control.ControlStation(connection,
                                          dronology_in_msg_queue,
                                          dronology_handshake_out_msg_queue, dronology_state_out_msg_queue,
                                          new_vehicle_msg_queue)

    def shutdown(*args):
        ctrl_station.stop()
        connection.stop()

    signal.signal(signal.SIGINT, shutdown)
    signal.signal(signal.SIGTERM, shutdown)

    connection.start()
    ctrl_station.start()
    time.sleep(1.0)

    for dc in util.load_drone_configs(drone_configs):
        new_vehicle_msg_queue.put_message(dc)

    while ctrl_station.is_alive():
        time.sleep(5.0)

    if ctrl_station.is_alive():
        shutdown(None, None)


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
