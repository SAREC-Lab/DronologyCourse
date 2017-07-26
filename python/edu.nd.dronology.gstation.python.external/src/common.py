import os
import json
import time


SEARCH_DEFAULT = 'search_default'



ARDUPATH = os.path.join('/', 'Users', 'seanbayley', 'Desktop', 'git', 'ardupilot')
DRONE_TYPE_SITL_PHYS = 'PHYS'
DRONE_TYPE_SITL_VRTL = 'VRTL'

DRONE_1 = (DRONE_TYPE_SITL_VRTL, {'instance': 0, 'home': (41.519408, -86.239996, 0, 0)})
DRONE_2 = (DRONE_TYPE_SITL_VRTL, {'instance': 1, 'home': (41.519408, -86.239496, 0, 0)})

DEFAULT_DRONE_SPECS = (DRONE_1,)
DEFAULT_SAR_START = (41.519412, -86.239830, 0)
DEFAULT_SAR_BOUNDS = ((41.519362, -86.240411, 0),
                      (41.519391, -86.239414, 0),
                      (41.519028, -86.239411, 0),
                      (41.519007, -86.240396, 0))


class Waypoint:
    def __init__(self, lat, lon, alt, groundpseed=None):
        self.lat = lat
        self.lon = lon
        self.alt = alt
        self.gs = groundpseed

    def get_lla(self):
        return self.lat, self.lon, self.alt

    def get_groundspeed(self):
        return self.gs


class DronologyMessage(object):
    def __init__(self, m_type, uav_id, data):
        self.m_type = m_type
        self.uav_id = uav_id
        self.data = data

    def __str__(self):
        return json.dumps({'type': self.m_type, 'sendtimestamp': long(round(time.time() * 1000)),
                           'uavid': str(self.uav_id), 'data': self.data})

    def __repr__(self):
        return str(self)

    @classmethod
    def from_vehicle(cls, vehicle, v_id):
        raise NotImplementedError


class DronologyHandshakeMessage(DronologyMessage):
    def __init__(self, uav_id, data):
        super(DronologyHandshakeMessage, self).__init__('handshake', uav_id, data)

    @classmethod
    def from_vehicle(cls, vehicle, v_id):
        battery = {
            'voltage': vehicle.battery.voltage,
            'current': vehicle.battery.current,
            'level'	: vehicle.battery.level,
        }
        lla = vehicle.location.global_frame
        data = {'batterystatus': battery, 'location': {'x': lla.lat, 'y': lla.lon, 'z': lla.alt}}
        return cls(v_id, data)


class DronologyStateMessage(DronologyMessage):
    def __init__(self, uav_id, data):
        super(DronologyStateMessage, self).__init__('state', uav_id, data)

    @classmethod
    def from_vehicle(cls, vehicle, v_id):
        lla = vehicle.location.global_frame
        att = vehicle.attitude
        vel = vehicle.velocity
        battery = {
            'voltage': vehicle.battery.voltage,
            'current': vehicle.battery.current,
            'level'	: vehicle.battery.level,
        }
        data = {
            'location': {'x': lla.lat, 'y': lla.lon, 'z': lla.alt},
            'attitude': {'x': att.roll, 'y': att.pitch, 'z': att.yaw},
            'velocity': {'x': vel[0], 'y': vel[1], 'z': vel[2]},
            'status': vehicle.system_status.state,
            'heading': vehicle.heading,
            'armable': vehicle.is_armable,
            'groundspeed': vehicle.groundspeed,
            'armed': vehicle.armed,
            'mode': vehicle.mode.name,
            'batterystatus': battery
        }

        return cls(v_id, data)


class DronologyMonitorMessage(DronologyMessage):
    def __init__(self, uav_id, data):
        super(DronologyMonitorMessage, self).__init__('monitoring', uav_id, data)

    @classmethod
    def from_vehicle(cls, vehicle, v_id):
        # TODO: put some stuff in here
        data = {}

        return cls(v_id, data)


class Command(object):
    def __init__(self, vehicle_id, time_stamp, data):
        self._vid = vehicle_id
        self._timestamp = time_stamp
        self._data = data

    def get_target(self):
        return self._vid

    def get_timestamp(self):
        return self._timestamp


class SetMonitorFrequency(Command):
    def __init__(self, *args):
        super(SetMonitorFrequency, self).__init__(*args)

    def get_monitor_frequency(self):
        return self._data['frequency']


class CommandFactory:
    @staticmethod
    def get_command(msg):
        cmd = json.loads(msg)
        args = [cmd[s] for s in ['uavid', 'sendtimestamp', 'data']]

        if cmd['command'] == 'setMonitorFrequency':
            return SetMonitorFrequency(*args)