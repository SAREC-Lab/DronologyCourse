import argparse
import importlib
import log_util
import mission
from control import ControlStation


_LOG = log_util.get_logger('default_file')


def parse_mission_type(arg):
    """
    example usage:
        python control.py -m "mission.SAR -n 4"
    """
    toks = arg.split('.')
    mod_name, mission_type = toks[:2]
    mod = importlib.import_module(mod_name)

    mission_ = getattr(mod, mission_type)

    return mission_


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('-ap', dest='ardu_path', required=True, type=str, help='path to ardupilot folder')
    ap.add_argument('-p', '--port', default=1234, type=int, help='port to connect to dronology')
    ap.add_argument('-m', '--mission', default=mission.SAR, type=parse_mission_type, help=parse_mission_type.__doc__)
    ap.add_argument('-rf', '--report_freq', default=1.0, type=float, help='how frequently drone updates should be sent')
    args = ap.parse_args()

    _LOG.info('STARTING NEW MISSION.')

    ctrl = ControlStation(mission_type=args.mission, ardupath=args.ardu_path, port=args.port)
    try:
        ctrl.work()
    except KeyboardInterrupt:
        _LOG.warn('keyboard interrupt, shutting down.')
        ctrl.shutdown()

    _LOG.info('MISSION ENDED.')


if __name__ == "__main__":
    main()
