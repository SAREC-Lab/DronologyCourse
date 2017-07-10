import signal
import sys
import threading
import time
import mission
import json
import argparse
import importlib
import util
import atexit
import Queue
import log_util
from common import *
from comms.dronology_link import DronologyLink


_LOG = log_util.get_logger('default_file')


class ControlStation:
    _STATUS_ALIVE = 0
    _STATUS_DEAD = 1
    _STATUS_WAITING = 2

    def __init__(self, mission_type=mission.SAR, port=1234, ardupath=ARDUPATH, report_freq=1.0, **kwargs):
        self.report_freq = report_freq
        self.in_msg_queue = Queue.Queue()
        self.mission = mission_type(self.in_msg_queue, ardupath=ardupath, **kwargs)
        self.dronology_link = DronologyLink(self.in_msg_queue, port=port)
        self._worker = threading.Thread(target=self._work)
        self._status = self._STATUS_WAITING

    def is_alive(self):
        return self._status != self._STATUS_DEAD

    def start(self):
        self.dronology_link.start()
        _LOG.info('control waiting for dronology connection.')
        self.mission.start()
        _LOG.info('mission loaded, starting workers.')
        self._status = self._STATUS_ALIVE
        self._worker.start()

    def _work(self):
        while self._status == self._STATUS_ALIVE:
            drone_list = {}
            for drone_id, drone in self.mission.get_drones().items():
                drone_list[str(drone_id)] = drone.report()

            out_msg = json.dumps({'type': 'drone_list', 'data': drone_list})
            self.dronology_link.send(out_msg)

            try:
                in_msg = self.in_msg_queue.get(timeout=0.25)
                self.in_msg_queue.task_done()

                if not isinstance(in_msg, Command):
                    _LOG.warn('invalid command received: {}'.format(in_msg))
                elif isinstance(in_msg, ExitCommand):
                    _LOG.warn('received an exit command from {}'.format(in_msg.get_origin()))
                    self._status = self._STATUS_DEAD
                else:
                    if in_msg.get_destination() == MISSION:
                        self.mission.on_command(in_msg)

            except Queue.Empty:
                pass

        _LOG.info('shutting down...')

    def stop(self):
        if self._status != self._STATUS_WAITING:
            self._worker.join()

        self._status = self._STATUS_DEAD
        _LOG.info('control worker joined.')
        self.dronology_link.stop()
        _LOG.info('dronology link stopped.')
        self.mission.stop()
        _LOG.info('mission link stopped.')


def shutdown(control_station):
    control_station.stop()


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
    # drones = [(DRONE_TYPE_SITL_VRTL, {'instance': 0, 'home': (41.519408, -86.239996, 0, 0)}),
    #           (DRONE_TYPE_SITL_VRTL, {'instance': 1, 'home': (41.514408, -86.239996, 0, 0)})]
    # drones = [{'type': 'physical', 'ConnectionData': {'ConnectionString': '/dev/ttyUSB0', 'BaudRate': 57600, }, }, ]
    ap = argparse.ArgumentParser()
    ap.add_argument('-ap', dest='ardu_path', required=True, type=str, help='path to ardupilot folder')
    ap.add_argument('-p', '--port', default=1234, type=int, help='port to connect to dronology')
    ap.add_argument('-m', '--mission', default=mission.SAR, type=parse_mission_type, help=parse_mission_type.__doc__)
    ap.add_argument('-rf', '--report_freq', default=1.0, type=float, help='how frequently drone updates should be sent')
    args = ap.parse_args()

    _LOG.info('STARTING NEW MISSION.')
    ctrl = ControlStation(mission_type=args.mission, ardupath=args.ardu_path, port=args.port)
    atexit.register(shutdown, control_station=ctrl)
    try:
        ctrl.start()
        while ctrl.is_alive():
            time.sleep(5)
    except KeyboardInterrupt:
        _LOG.warn('keyboard interrupt, shutting down.')
    _LOG.info('control station is shutting down all modules.')
    ctrl.stop()
    _LOG.info('MISSION ENDED.')


if __name__ == "__main__":
    main()
