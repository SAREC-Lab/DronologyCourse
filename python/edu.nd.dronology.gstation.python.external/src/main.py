import core
import argparse
import util
import time
import importlib


_LOG = util.get_logger()


def _parse_mission(mission_str):
    """
    e.g. missions.sar.SingleUAVSAR [... [...]]
    """
    toks = mission_str.split()
    module_id = '.'.join(toks[0].split('.')[:2])
    clazz = toks[0].split('.')[2]
    mission = getattr(importlib.import_module(module_id), clazz)
    kwargs = mission.parse_args(' '.join(toks[1:]))

    return mission, kwargs


def main(addr, port, mission, **kwargs):
    _LOG.info('STARTING NEW MISSION.')
    connection = core.Host(addr=addr, port=port)
    connection.start()
    time.sleep(1.0)
    _LOG.info('Accepting connection on tcp:{}:{}'.format(addr, port))

    mission.start(connection, **kwargs)
    connection.stop()
    _LOG.info('MISSION ENDED.')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-addr', '--address',
                        type=str, default='')
    parser.add_argument('-p', '--port',
                        type=int, default=1234)
    parser.add_argument('-m', '--mission',
                        type=_parse_mission, default='missions.sar.SaR', help=_parse_mission.__doc__)
    args = parser.parse_args()
    main(args.address, args.port, args.mission[0], **args.mission[1])
