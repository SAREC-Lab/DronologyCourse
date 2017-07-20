import os
import json
import time

MISSION = 'mission'
DRONOLOGY_LINK = 'dronology'
CONTROL_STATION = 'control'

D_ATTR_LOC = 'location'
D_ATTR_ATTITUDE = 'attitude'
D_ATTR_VEL = 'velocity'
D_ATTR_GMBL_ROT = 'gimbalRotation'
D_ATTR_BTRY = 'battery'
D_ATTR_HOME_LOC = 'home'
D_ATTR_STATUS = 'status'
D_ATTR_HEADING = 'heading'
D_ATTR_IS_ARMABLE = 'armable'
D_ATTR_AIRSPEED = 'airspeed'
D_ATTR_GRNDSPEED = 'groundspeed'
D_ATTR_IS_ARMED = 'armed'
D_ATTR_MODE = 'mode'
D_ATTR_ID = 'id'

# TODO: use the definitions above
DRONE_ATTRS = [D_ATTR_LOC, D_ATTR_ATTITUDE, D_ATTR_VEL, D_ATTR_GMBL_ROT, D_ATTR_BTRY, D_ATTR_HOME_LOC,
               D_ATTR_STATUS, D_ATTR_HEADING, D_ATTR_IS_ARMABLE, D_ATTR_AIRSPEED, D_ATTR_GRNDSPEED, D_ATTR_IS_ARMED,
               D_ATTR_MODE, D_ATTR_ID]

ARDUPATH = os.path.join('/', 'Users', 'seanbayley', 'Desktop', 'git', 'ardupilot')
DRONE_TYPE_SITL_PHYS = 'PHYS'
DRONE_TYPE_SITL_VRTL = 'VRTL'

DRONE_1 = (DRONE_TYPE_SITL_VRTL, {'instance': 0, D_ATTR_HOME_LOC: (41.519408, -86.239996, 0, 0)})
DRONE_2 = (DRONE_TYPE_SITL_VRTL, {'instance': 1, D_ATTR_HOME_LOC: (41.519408, -86.239496, 0, 0)})

DEFAULT_DRONE_SPECS = (DRONE_1,)
DEFAULT_SAR_BOUNDS = ((41.519367, -86.240419, 30),
                      (41.519277, -86.240405, 30),
                      (41.519395, -86.239418, 30),
                      (41.519313, -86.239417, 30))


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
    def __init__(self, m_type, uav_id):
        self.m_type = m_type
        self.uav_id = uav_id
        self.data = {}

    def __str__(self):
        return json.dumps({'type': self.m_type, 'sendtimestamp': int(round(time.time() * 1000)),
                           'uavid': str(self.uav_id), 'data': self.data})

    def __repr__(self):
        return str(self)

    @classmethod
    def from_vehicle(cls, vehicle, v_id):
        raise NotImplementedError


class DronologyHandshakeMessage(DronologyMessage):
    def __init__(self, uav_id, battery):
        super(DronologyHandshakeMessage, self).__init__('handshake', uav_id)
        self.data = {'batterystatus': battery}

    @classmethod
    def from_vehicle(cls, vehicle, v_id):
        battery = {
            'voltage': vehicle.battery.voltage,
            'current': vehicle.battery.current,
            'level'	: vehicle.battery.level,
        }
        return cls(v_id, battery)


class DronologyStateMessage(DronologyMessage):
    def __init__(self,
                 uav_id,
                 lat, lon, alt,
                 roll, pitch, yaw,
                 heading,
                 north, east, down,
                 status,
                 armable,
                 groundpseed,
                 armed,
                 mode,
                 battery):
        super(DronologyStateMessage, self).__init__('state', uav_id)
        self.location = {'x': lat, 'y': lon, 'z': alt}
        self.attitude = {'x': roll, 'y': pitch, 'z': yaw}
        # pretty sure heading is the same thing as yaw
        self.heading = heading
        self.velocity = {'x': north, 'y': east, 'z': down}
        self.status = status
        self.armable = armable
        self.groundspeed = groundpseed
        self.armed = armed
        self.mode = mode
        self.battery = battery
        self.data = {'location': self.location, 'attitude': self.attitude, 'velocity': self.velocity,
                     'status': self.status, 'heading': self.heading, 'armable': self.armable,
                     'groundspeed': self.groundspeed, 'armed': self.armed, 'mode': self.mode,
                     'batterystatus': self.battery}

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

        return cls(v_id,
                   lla.lat, lla.lon, lla.alt,
                   att.roll, att.pitch, att.yaw,
                   vehicle.heading,
                   vel[0], vel[1], vel[2],
                   vehicle.system_status.state,
                   vehicle.is_armable,
                   vehicle.groundspeed,
                   vehicle.armed,
                   vehicle.mode.name,
                   battery)


class Command(object):
    def __init__(self, vehicle_id, time_stamp, command_type):
        self._vid = vehicle_id
        self._timestamp = time_stamp
        self._c_type = command_type

    @classmethod
    def from_string(cls, msg):
        pass


class SetMonitorFrequency(Command):
    def __init__(self, data, *args):
        self.data = data
        super(SetMonitorFrequency, self).__init__(*args)

    @classmethod
    def from_string(cls, msg):
        d_msg = json.loads(msg)
        cls(d_msg['data'])
