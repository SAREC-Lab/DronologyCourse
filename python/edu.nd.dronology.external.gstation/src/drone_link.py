import subprocess
import os
import signal
import dronekit
import dronekit_sitl
import time
import threading
import util
import log_util
from common import *


_LOG = log_util.get_logger('default_file')


class Drone(object):
    def __init__(self):
        self.id = None
        self.vehicle = None
        self.attrs = []

    def is_attribute(self, attr_id):
        return attr_id in self.attrs

    def get_attribute(self, attr_id):
        if self.is_attribute(attr_id):
            return self.attrs[attr_id]
        else:
            _LOG.info('Invalid attribute: {}'.format(attr_id))

    def connect(self):
        raise NotImplementedError

    def disconnect(self):
        raise NotImplementedError

    def simple_goto(self, lat, lon, alt, **kwargs):
        raise NotImplementedError

    def _get_attribute(self):
        raise NotImplementedError

    def get_location(self, as_array=False):
        if as_array:
            return [0, 0, 0]
        else:
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

    def is_home(self, threshold=5):
        tmp = self.get_home_location()
        home_lla = util.Lla(tmp['x'], tmp['y'], tmp['z'])
        tmp = self.get_location()
        cur_lla = util.Lla(tmp['x'], tmp['y'], tmp['z'])

        return home_lla.distance(cur_lla) <= threshold

    def send_to_home(self):
        home = self.get_home_location()
        self.simple_goto(home['x'], home['y'], home['z'])

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
        raise NotImplementedError

    def disconnect(self):
        self.vehicle.close()

    def simple_goto(self, lat, lon, alt, airspeed=None, groundspeed=None):
        location = dronekit.LocationGlobal(lat, lon, alt=alt)
        self.vehicle.simple_goto(location, airspeed=airspeed, groundspeed=groundspeed)


class VirtualSITL(SITLDrone):
    def __init__(self,
                 ip='tcp:127.0.0.1',
                 instance=0,
                 ardupath=ARDUPATH,
                 speed=1, rate=10, home=(41.732955, -86.180886, 0, 0), baud=115200):
        self.ip = ip
        self.instance = instance
        self.ardupath = ardupath
        self.speed = speed
        self.rate = rate
        self.home = home
        self.baud = baud
        self.sitl = dronekit_sitl.SITL(path=os.path.join(ardupath, 'build', 'sitl', 'bin', 'arducopter'))
        super(VirtualSITL, self).__init__()

    def connect(self, verbose=False):
        # self.sitl.download('copter', '3.3')
        threading.Thread(target=self._connect()).start()

    def _connect(self):
        sitl_args = [
            '-S',
            '-I{}'.format(self.instance),
            '--model', '+',
            '--home', ','.join(map(str, self.home)),
            '--speedup', str(self.speed),
            '--rate', str(self.rate),
            '--defaults', os.path.join(self.ardupath, 'Tools', 'autotest', 'default_params', 'copter.parm')
        ]
        self.sitl.launch(sitl_args, await_ready=True, verbose=True)
        tcp, ip, port = self.sitl.connection_string().split(':')
        port = str(int(port) + self.instance * 10)
        conn_string = ':'.join([tcp, ip, port])
        _LOG.info('SITL launched on: {}'.format(conn_string))
        self.vehicle = dronekit.connect(conn_string, wait_ready=True, baud=self.baud)
        _LOG.info('Vehicle connected'.format(conn_string))
        # self.set_id('VRTL_{}'.format(int(self.vehicle.parameters['SYSID_THISMAV'])))

    def disconnect(self):
        self.vehicle.close()
        self.sitl.stop()


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


if __name__ == '__main__':
    VirtualSITL().connect(verbose=True)