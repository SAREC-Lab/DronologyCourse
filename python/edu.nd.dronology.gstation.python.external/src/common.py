import os
import json
import time


SEARCH_DEFAULT = 'search_default'


ARDUPATH = os.path.join('/', 'Users', 'seanbayley', 'Desktop', 'git', 'ardupilot')
DRONE_TYPE_PHYS = 'PHYS'
DRONE_TYPE_SITL_VRTL = 'VRTL'

DRONE_1 = (DRONE_TYPE_SITL_VRTL, {'instance': 0, 'home': (41.519408, -86.239996, 0, 0)})
DRONE_2 = (DRONE_TYPE_SITL_VRTL, {'instance': 1, 'home': (41.519408, -86.239496, 0, 0)})

DEFAULT_DRONE_SPECS = (DRONE_1,)
DEFAULT_SAR_START = (41.519412, -86.239830)
DEFAULT_SAR_BOUNDS = ((41.519362, -86.240411),
                      (41.519391, -86.239414),
                      (41.519028, -86.239411),
                      (41.519007, -86.240396))

SAR_BOUNDS_SIM = ((41.680370, -86.251986),
                  (41.671081, -86.251939),
                  (41.670975, -86.241677),
                  (41.680476, -86.242008))

DEFAULT_SAR_BOUNDS_STR = '|'.join(map(lambda tup: ','.join(map(str, tup)), DEFAULT_SAR_BOUNDS))


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

    def as_array(self):
        return [self.lat, self.lon, self.alt, self.gs]


class DronologyMessage(object):
    def __init__(self, m_type, uav_id, data):
        self.m_type = m_type
        self.uav_id = uav_id
        self.data = data

    def __str__(self):
        return json.dumps({'type': self.m_type,
                           'sendtimestamp': long(round(time.time() * 1000)),
                           'uavid': str(self.uav_id),
                           'data': self.data})

    def __repr__(self):
        return str(self)

    @classmethod
    def from_vehicle(cls, vehicle, v_id, **kwargs):
        raise NotImplementedError


class HandshakeMessage(DronologyMessage):
    def __init__(self, uav_id, data, p2sac='../resources/sac.json'):
        super(HandshakeMessage, self).__init__('handshake', uav_id, data)
        self.p2sac = p2sac

    @classmethod
    def from_vehicle(cls, vehicle, v_id, p2sac='../resources/sac.json'):
        battery = {
            'voltage': vehicle.battery.voltage,
            'current': vehicle.battery.current,
            'level'	: vehicle.battery.level,
        }

        with open(p2sac) as f:
            sac = json.load(f)

        lla = vehicle.location.global_frame
        data = {
                'home': {'x': lla.lat,
                         'y': lla.lon,
                         'z': lla.alt},
                'safetycase': json.dumps(sac)}
        return cls(v_id, data)


class StateMessage(DronologyMessage):
    def __init__(self, uav_id, data):
        super(StateMessage, self).__init__('state', uav_id, data)

    @classmethod
    def from_vehicle(cls, vehicle, v_id, **kwargs):
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


class MonitorMessage(DronologyMessage):
    def __init__(self, uav_id, data):
        super(MonitorMessage, self).__init__('monitoring', uav_id, data)

    @classmethod
    def from_vehicle(cls, vehicle, v_id, **kwargs):
        lla = vehicle.location.global_frame
        att = vehicle.attitude
        vel = vehicle.velocity
        data = {
            'lat': lla.lat,
            'lon': lla.lon,
            'alt': lla.alt,
            'north': vel[0],
            'east': vel[1],
            'down': vel[2],
            'voltage': vehicle.battery.voltage,
            'current': vehicle.battery.current,
            'level'	: vehicle.battery.level,
            'eph': vehicle.gps_0.eph,
            'epv': vehicle.gps_0.epv,
            'n_satellites': vehicle.gps_0.satellites_visible,
            'roll': att.roll,
            'pitch': att.pitch,
            'yaw': att.yaw,
            'groundspeed': vehicle.groundspeed
        }

        return cls(v_id, data)


class AcknowledgeMessage(DronologyMessage):
    def __init__(self, uav_id, data):
        super(AcknowledgeMessage, self).__init__('ack', uav_id, data)

    @classmethod
    def from_vehicle(cls, vehicle, v_id, msg_id=None):
        return cls(v_id, {'msgid': msg_id})


class Command(object):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        self._vid = vehicle_id
        self._timestamp = timestamp
        self._data = data
        self._msg_id = msg_id

    def __getitem__(self, item):
        if item in self.__dict__:
            return self.__dict__[item]
        elif item in self._data:
            return self._data[item]

    def get_target(self):
        return self._vid

    def get_timestamp(self):
        return self._timestamp

    def get_msg_id(self):
        return self._msg_id


class SetMonitorFrequency(Command):
    def __init__(self, *args):
        super(SetMonitorFrequency, self).__init__(*args)

    def get_monitor_frequency(self):
        return self._data['frequency']


class CommandFactory(object):
    @staticmethod
    def get_command(msg):
        cmd = json.loads(msg)
        args = [cmd[s] if s in cmd else '' for s in ['uavid', 'sendtimestamp', 'data', 'msgid']]

        if cmd['command'] == 'setMonitorFrequency':
            return SetMonitorFrequency(*args)