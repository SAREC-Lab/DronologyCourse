import subprocess
import os
import signal
import dronekit
import time
import shutil
from common import *


class Drone(object):
    def __init__(self):
        self.id = None
        self.vehicle = None
        self.strategy = None

        for k in DRONE_ATTRS:
            if k not in self.__dict__:
                self.__dict__[k] = 0

    def connect(self):
        raise NotImplementedError

    def disconnect(self):
        raise NotImplementedError

    def on_command(self, cmd, **kwargs):
        self.strategy.respond(cmd, self.vehicle, **kwargs)

    def set_id(self, d_id):
        self.id = d_id

    def get_id(self):
        return self.id

    def set_strategy(self, strategy):
        self.strategy = strategy

    def report(self):
        return {k: self.__dict__[k] for k in DRONE_ATTRS}


class SITLDrone(Drone):
    def __init__(self):
        super(SITLDrone, self).__init__()

    def connect(self):
        pass

    def disconnect(self):
        self.vehicle.close()


class VirtualSITL(SITLDrone):
    def __init__(self,
                 ip='tcp:127.0.0.1',
                 instance=0,
                 ardupath=ARDUPATH,
                 speed=1, rate=10, home=(41.732955, -86.180886, 0, 0), baud=115200):
        port = SITL_PORT + instance * 10
        self.ip = ip
        self.port = port
        self.baud = baud
        self.sitl_args = ['../resources/startSITL.sh',
                          ip,
                          str(self.port),
                          str(instance),
                          ardupath,
                          str(speed),
                          str(rate),
                          ','.join(map(str, home))]

        super(VirtualSITL, self).__init__()

    def connect(self):
        subprocess.call(self.sitl_args)
        time.sleep(1)
        self.vehicle = dronekit.connect('{}:{}'.format(self.ip, self.port), wait_ready=True, baud=self.baud)
        self.set_id('VRTL_{}'.format(int(self.vehicle.parameters['SYSID_THISMAV'])))


class PhysicalSITL(SITLDrone):
    def __init__(self, ip='/dev/ttyUSB0', baud=57600):
        self.ip = ip
        self.baud = baud
        super(PhysicalSITL, self).__init__()

    def connect(self):
        self.vehicle = dronekit.connect(self.ip, wait_ready=True, baud=self.baud)
        self.set_id('PHYS_{}'.format(int(self.vehicle.parameters['SYSID_THISMAV'])))


def make_drone_link(drone_type, **kwargs):
    if drone_type == DRONE_TYPE_SITL_VRTL:
        return VirtualSITL(**kwargs)
    elif drone_type == DRONE_TYPE_SITL_PHYS:
        return PhysicalSITL(**kwargs)
    else:
        pass  # warn


def close_sitl_connections():
    if os.path.exists('.sitl_temp'):
        shutil.rmtree('.sitl_temp')

    pids = map(int, subprocess.check_output(['pgrep', 'arducopter']).split())
    for pid in pids:
        os.kill(pid, signal.SIGINT)
