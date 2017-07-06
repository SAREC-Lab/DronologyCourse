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

    def connect(self):
        raise NotImplementedError

    def disconnect(self):
        raise NotImplementedError

    def on_command(self, cmd, **kwargs):
        self.strategy.respond(cmd, self.vehicle, **kwargs)

    def get_location(self):
        return {'x': 0, 'y': 0, 'z': 0}

    def get_attitude(self):
        return {'x': 0, 'y': 0, 'z': 0}

    def get_velocity(self):
        return {'x': 0, 'y': 0, 'z': 0}

    def get_gimbal_rotation(self):
        return {'x': 0, 'y': 0, 'z': 0}

    def get_battery_status(self):
        return {'voltage': 12.5, 'current': 0, 'level': 100}

    def get_home_location(self):
        return {'x': 0, 'y': 0, 'z': 0}

    def get_current_status(self):
        return 'STANDBY'

    def get_heading(self):
        return '0'

    def get_is_armable(self):
        return False

    def get_is_armed(self):
        return False

    def get_air_speed(self):
        return 0

    def get_ground_speed(self):
        return 0

    def get_mode(self):
        return 'GUIDED'

    def set_id(self, d_id):
        self.id = d_id

    def get_id(self):
        return self.id

    def set_strategy(self, strategy):
        self.strategy = strategy

    def report(self):
        return {
            D_ATTR_LOC: self.get_location(),
            D_ATTR_ATTITUDE: self.get_attitude(),
            D_ATTR_VEL: self.get_velocity(),
            D_ATTR_GMBL_ROT: self.get_gimbal_rotation(),
            D_ATTR_BTRY: self.get_battery_status(),
            D_ATTR_HOME_LOC: self.get_home_location(),
            D_ATTR_STATUS: self.get_current_status(),
            D_ATTR_HEADING: self.get_heading(),
            D_ATTR_IS_ARMABLE: self.get_is_armable(),
            D_ATTR_GRNDSPEED: self.get_ground_speed(),
            D_ATTR_AIRSPEED: self.get_air_speed(),
            D_ATTR_IS_ARMED: self.get_is_armed(),
            D_ATTR_MODE: self.get_mode(),
            D_ATTR_ID: self.get_id()
        }


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
        self.connect_port = '127.0.0.1:' + str(14550 + 10 * instance)
        self.baud = baud
        self.home = home
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
        self.vehicle = dronekit.connect(self.connect_port, wait_ready=True, baud=self.baud)
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

    try:
        pids = map(int, subprocess.check_output(['pgrep', 'arducopter']).split())
        for pid in pids:
            os.kill(pid, signal.SIGINT)
    except subprocess.CalledProcessError:
        print('no sitl links found, exiting...')


if __name__ == '__main__':
    close_sitl_connections()