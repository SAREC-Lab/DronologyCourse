import control
import communication
import argparse
import util
import signal
import time

_LOG = util.get_logger()


def main(gid, addr, port, drone_config_path, global_cfg_path):
    dronology_in_msg_queue = communication.core.MessageQueue()
    dronology_handshake_out_msg_queue = communication.core.MessageQueue()
    dronology_state_out_msg_queue = communication.core.MessageQueue()
    new_vehicle_msg_queue = communication.core.MessageQueue()
    connection = communication.core.Connection(dronology_in_msg_queue, addr=addr, port=port, g_id=gid)
    ctrl_station = control.ControlStation(connection,
                                          dronology_in_msg_queue,
                                          dronology_handshake_out_msg_queue, dronology_state_out_msg_queue,
                                          new_vehicle_msg_queue)

    # Ensure everything is shutdown cleanly in case process is killed.
    def shutdown(*args):
        ctrl_station.stop()
        connection.stop()

    signal.signal(signal.SIGINT, shutdown)
    signal.signal(signal.SIGTERM, shutdown)

    global_cfg = util.load_json(global_cfg_path)

    if 'ardupath' not in global_cfg:
        _LOG.error('You must specify \"ardupath\" in the global config file at {}'.format(global_cfg_path))
        exit(1)

    ap = global_cfg['ardupath']

    connection.start()
    ctrl_station.start()
    time.sleep(1.0)

    # Register vehicles that are specified in a config path (if any)
    for dc in util.load_json(drone_config_path):
        dc['ardupath'] = ap
        new_vehicle_msg_queue.put_message(dc)
        time.sleep(2.0)

    # Loop while control station is still operating
    while ctrl_station.is_alive():
        time.sleep(5.0)

    # Cleanup
    shutdown()


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-gid', '--gcs_id',
                        type=str, default='default_ground_station')
    parser.add_argument('-addr', '--address',
                        type=str, default='localhost')
    parser.add_argument('-p', '--port',
                        type=int, default=1234)
    parser.add_argument('-d', '--drone_config', type=str, default='../cfg/drone_cfgs/default.json')
    parser.add_argument('-c', '--config', type=str, default='../cfg/global_cfg.json')
    args = parser.parse_args()
    main(args.gcs_id, args.address, args.port, args.drone_config, args.config)
