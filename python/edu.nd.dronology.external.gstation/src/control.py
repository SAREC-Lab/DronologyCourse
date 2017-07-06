import signal
import sys
import threading
import time
import mission
import json
import argparse
import importlib
from common import *
from comms.dronology_link import DronologyLink
from comms import drone_link



class ControlStation:
    def __init__(self, mission_type=mission.SAR, port=1234, ardupath=ARDUPATH, **kwargs):
        self.mission = mission_type(ardupath=ardupath, **kwargs)
        self.java_link = DronologyLink(port=port)
        # self.java.setDataHandler(self.on_receive)
        self.java_worker = threading.Thread(target=self.send_drone_list_cont)
        self.java_worker_is_running = False

    def start(self):
        self.java_link.start()
        self.java_worker_is_running = True
        self.java_worker.start()

    def stop(self):
        # self.java.close() # need to clean this up somehow
        self.java_link.stop()
        for drone in self.mission.get_drones().values():
            drone.disconnect()

        self.java_worker_is_running = False
        self.java_worker.join()

        drone_link.close_sitl_connections()
        sys.exit(0)

    def send_drone_list_cont(self, delay=1.0):
        """
        Send information about each drone to dronology every second
        :param delay:
        :return:
        """
        while self.java_worker_is_running:
            drone_list = {}
            for drone_id, drone in self.mission.get_drones().items():
                drone_list[str(drone_id)] = drone.report()

            self.java_link.send(json.dumps({'type': 'drone_list', 'data': drone_list}))
            time.sleep(delay)

    def on_receive(self, d_id, c_type, data):
        """
        Unpack a message from dronology
        :param d_id:
        :param c_type:
        :param data:
        :return:
        """
        if c_type == "command":
            self.mission.on_command(data)
        else:
            print "Unrecognized data type:"
            print "Type: {type}".format(type=c_type)
            print "Data: {data}".format(data=data)


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
    ap.add_argument('-ap', dest='ardu_path', required=True, type=str)
    ap.add_argument('-p', '--port', default=1234, type=int)
    ap.add_argument('-m', '--mission', default=mission.SAR, type=parse_mission_type, help=parse_mission_type.__doc__)
    args = ap.parse_args()

    ctrl = ControlStation(mission_type=args.mission, ardupath=args.ardu_path, port=args.port)
    signal.signal(signal.SIGTERM, lambda: ctrl.stop())
    ctrl.start()


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        drone_link.close_sitl_connections()
        sys.exit(0)
