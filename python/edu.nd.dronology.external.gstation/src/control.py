import signal
import sys
import threading
import time
import core
import json
from common import *
from comms.dronology_link_ import DronologyLink
from comms import drone_link



class ControlStation:
    def __init__(self, port, drone_specs, ardupath=ARDUPATH):
        self.java_link = DronologyLink(port=port)
        # self.java.setDataHandler(self.on_receive)

        self.drones = {}
        for d_type, d_strat, d_kwargs in drone_specs:
            drone = drone_link.make_drone_link(d_type, ardupath=ardupath, **d_kwargs)
            drone.connect()
            drone.set_strategy(d_strat)
            self.drones[drone.get_id()] = drone

        self.java_comm_worker = threading.Thread(target=self.send_drone_list_cont)
        self.java_comm_worker_is_running = False

    def start(self):
        self.java_link.start()
        self.java_comm_worker_is_running = True
        self.java_comm_worker.start()

    def stop(self):
        # self.java.close() # need to clean this up somehow
        self.java_link.stop()
        for drone in self.drones.values():
            drone.disconnect()

        self.java_comm_worker_is_running = False
        self.java_comm_worker.join()

        drone_link.close_sitl_connections()
        sys.exit(0)

    def send_drone_list_cont(self, delay=1.0):
        """
        Send information about each drone to dronology every second
        :param delay:
        :return:
        """
        while self.java_comm_worker_is_running:
            drone_list = {}
            for drone_id, drone in self.drones.items():
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
            self.on_command(data["id"], data["command"], data["data"])
        else:
            print "Unrecognized data type:"
            print "Type: {type}".format(type=c_type)
            print "Data: {data}".format(data=data)

    def on_command(self, d_id, command, data):
        """
        Handle directives sent from dronology
        :param d_id:
        :param command:
        :param data:
        :return:
        """
        if d_id in self.drones:
            self.drones[d_id].on_command(command, **data)
        else:
            pass  # warn
        # drone = self.drones[d_id]
        # if command == "gotoLocation":
        #     drone.simple_goto(data['x'], data['y'], data['z'])
        # elif command == "takeoff":
        #     drone.takeoff(data['altitude'])
        # elif command == "setVelocity":
        #     drone.set_ned(**data)
        # elif command == "setGimbalRotation":
        #     pass
        # elif command == "setGimbalTarget":
        #     pass
        # elif command == "setHome":
        #     pass
        # elif command == "setHeading":
        #     pass
        # elif command == "setAirspeed":
        #     pass
        # elif command == "setGroundspeed":
        #     pass
        # elif command == "setArmed":
        #     pass
        # elif command == "setMode":
        #     pass
        # else:
        #     pass


def main():
    drones = [(DRONE_TYPE_SITL_VRTL, core.ResponsiveDrone, {'instance': 0, 'home': (41.519408, -86.239996, 0, 0)})]
    # drones = [{'type': 'physical', 'ConnectionData': {'ConnectionString': '/dev/ttyUSB0', 'BaudRate': 57600, }, }, ]
    comm = ControlStation(1234, drones)
    signal.signal(signal.SIGTERM, lambda: comm.stop())
    comm.start()


if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        drone_link.close_sitl_connections()
        sys.exit(0)
