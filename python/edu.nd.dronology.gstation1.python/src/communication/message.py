import json
import time
import util

_LOG = util.get_logger()


class InternalMessage(object):
    def __init__(self, m_type, data):
        self._m_type = m_type
        self._data = data

    def get_data(self):
        return self._data


class VehicleConnectedMessage(InternalMessage):
    def __init__(self, data):
        super(VehicleConnectedMessage, self).__init__('vehicle_connected', data)

    def get_vehicle(self):
        return self._data['vehicle']


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

    def get_data(self):
        return self.data


class DroneHandshakeMessage(DronologyMessage):
    def __init__(self, uav_id, data, p2sac='../cfg/sac.json'):
        super(DroneHandshakeMessage, self).__init__('handshake', uav_id, data)
        self.p2sac = p2sac

    @classmethod
    def from_vehicle(cls, vehicle, v_id, p2sac='../cfg/sac.json'):
        battery = {
            'voltage': vehicle.battery.voltage,
            'current': vehicle.battery.current,
            'level': vehicle.battery.level,
        }

        try:
            with open(p2sac) as f:
                sac = json.load(f)
        except IOError as e:
            _LOG.warn(e)
            sac = {}

        lla = vehicle.location.global_relative_frame
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
        lla = vehicle.location.global_relative_frame
        att = vehicle.attitude
        vel = vehicle.velocity
        battery = {
            'voltage': vehicle.battery.voltage,
            'current': vehicle.battery.current,
            'level': vehicle.battery.level,
        }
        data = {
            'location': {'x': lla.lat, 'y': lla.lon, 'z': lla.alt},
            'attitude': {'x': att.roll, 'y': att.pitch, 'z': att.yaw},
            'velocity': {'x': vel[0], 'y': vel[1], 'z': vel[2]},
            'status': vehicle.system_status.state,
            'heading': vehicle.heading,
            'armable': vehicle.is_armable,
            'airspeed': vehicle.airspeed,
            'groundspeed': vehicle.airspeed,
            'armed': vehicle.armed,
            'mode': vehicle.mode.name,
            'batterystatus': battery
        }

        return cls(v_id, data)


class MonitorMessage(DronologyMessage):
    def __init__(self, uav_id, data):
        super(MonitorMessage, self).__init__('monitoring', uav_id, data)

    @classmethod
    def from_vehicle(cls, vehicle, v_id, battery_level=None, **kwargs):
        if battery_level is None:
            battery_level = vehicle.battery.level

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
            'level': battery_level,
            'eph': vehicle.gps_0.eph,
            'epv': vehicle.gps_0.epv,
            'n_satellites': vehicle.gps_0.satellites_visible,
            'roll': att.roll,
            'pitch': att.pitch,
            'yaw': att.yaw,
            'airspeed': vehicle.airspeed,
            'groundspeed': vehicle.airspeed,
        }

        return cls(v_id, data)


class AcknowledgeMessage(DronologyMessage):
    def __init__(self, uav_id, data):
        super(AcknowledgeMessage, self).__init__('ack', uav_id, data)

    @classmethod
    def from_vehicle(cls, vehicle, v_id, msg_id=''):
        return cls(v_id, {'msgid': msg_id})