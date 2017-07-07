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
from common import *
from comms.dronology_link import DronologyLink


class ControlStation:
    def __init__(self, mission_type=mission.SAR, port=1234, ardupath=ARDUPATH, report_freq=1.0, **kwargs):
        self.mission = mission_type(ardupath=ardupath, **kwargs)
        self.report_freq = report_freq
        self.in_msg_queue = Queue.Queue()
        self.dronology_link = DronologyLink(self.in_msg_queue, port=port)
        self.send_worker = None
        self.recv_worker = None
        self.monitor_worker = None

    def start(self):
        self.dronology_link.start()
        print('dronology link waiting for connection...')
        self.mission.start()
        print('mission loaded, starting workers...')
        self.monitor_worker = util.RepeatedTimer(0.1, self._monitor, self, self.mission, self.dronology_link)
        self.send_worker = util.RepeatedTimer(self.report_freq, self._send, self.mission, self.dronology_link)
        self.recv_worker = util.RepeatedTimer(0.1, self._recv, self.mission, self.in_msg_queue)

    def stop(self):
        self.recv_worker.stop()
        self.send_worker.stop()
        self.monitor_worker.stop()
        self.dronology_link.stop()
        self.mission.stop()

    @staticmethod
    def _monitor(control_station, m_mission, m_dr_link):
        if not m_dr_link.is_alive or not m_mission.is_alive:
            if m_dr_link.exit_status == STATUS_EXIT:
                print('dronology link closed successfully, exiting...')
                ControlStation.stop(control_station)
            elif m_dr_link.exit_status == STATUS_RESET:
                print('dronology link closed due to an error, resetting...')
                # print('waiting for sockets to close...')
                # m_dr_link.stop()
                # m_dr_link.start()
                #
                # time.sleep(10)
                # m_mission.on_command({'type': })
                # control_station.monitor_worker.start()
                ControlStation.stop(control_station)
            else:
                print('dronology link closed for an unexpected reason...')
                ControlStation.stop(control_station)

    @staticmethod
    def _send(m_mission, m_dr_link):
        drone_list = {}
        for drone_id, drone in m_mission.get_drones().items():
            drone_list[str(drone_id)] = drone.report()

        out_msg = json.dumps({'type': 'drone_list', 'data': drone_list})
        m_dr_link.send(out_msg)

    @staticmethod
    def _recv(m_mission, m_msg_queue):
        try:
            in_msg = m_msg_queue.get_nowait()
            cmd = json.loads(in_msg)
            m_mission.on_command(cmd)
        except Queue.Empty:
            pass


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

    ctrl = ControlStation(mission_type=args.mission, ardupath=args.ardu_path, port=args.port)
    atexit.register(shutdown, control_station=ctrl)
    ctrl.start()


if __name__ == "__main__":
    main()
