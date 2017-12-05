import runner
import argparse
import util
import signal

_LOG = util.get_logger()


def main(gid, addr, port, drone_config_path, global_cfg_path):
    gcs_runner = runner.GCSRunner(gid, addr, port, global_cfg_path, drone_cfg_path=drone_config_path)

    # register shutdown handlers
    signal.signal(signal.SIGINT, gcs_runner.stop)
    signal.signal(signal.SIGTERM, gcs_runner.stop)
    gcs_runner.start()
    gcs_runner.wait()


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
