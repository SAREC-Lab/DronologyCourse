import os
import json
import time
import socket
import threading
from util import get_logger, Lla
from boltons import socketutils


_LOG = get_logger()


class MessageQueue:
    def __init__(self):
        self._lock = threading.Lock()
        self._messages = []

    def put_message(self, msg):
        with self._lock:
            self._messages.append(msg)

    def get_messages(self):
        msgs = []
        with self._lock:
            while self._messages:
                msgs.append(self._messages.pop(0))

        return msgs


class Connection:
    _WAITING = 1
    _CONNECTED = 2
    _DEAD = -1

    def __init__(self, msg_queue, addr='', port=1234, g_id='default_groundstation'):
        self._g_id = g_id
        self._msgs = msg_queue
        self._addr = addr
        self._port = port
        self._sock = None
        self._conn_lock = threading.Lock()
        self._status = Connection._WAITING
        self._status_lock = threading.Lock()
        self._msg_buffer = ''

    def get_status(self):
        with self._status_lock:
            return self._status

    def set_status(self, status):
        with self._status_lock:
            self._status = status

    def is_connected(self):
        return self.get_status() == Connection._CONNECTED

    def start(self):
        threading.Thread(target=self._work).start()

    def stop(self):
        self.set_status(Connection._DEAD)

    def send(self, msg):
        success = False
        with self._conn_lock:
            if self._status == Connection._CONNECTED:
                try:
                    self._sock.send(msg)
                    self._sock.send(os.linesep)
                    success = True
                except Exception as e:
                    _LOG.warn('failed to send message! ({})'.format(e))

        return success

    def get_messages(self, vid):
        return self._msgs.get_messages(vid)

    def _work(self):
        """
        Main loop.
            1. Wait for a connection
            2. Once connected, wait for commands from dronology
            3. If connection interrupted, wait for another connection again.
            4. Shut down when status is set to DEAD
        :return:
        """
        cont = True
        while cont:
            status = self.get_status()
            if status == Connection._DEAD:
                # Shut down
                cont = False
                _LOG.info('shutting down connection...')
            elif status == Connection._WAITING:
                # Try to connect, timeout after 10 seconds.
                try:
                    sock = socket.create_connection((self._addr, self._port), timeout=5.0)
                    self._sock = socketutils.BufferedSocket(sock)
                    handshake = json.dumps({'type': 'connect', 'uavid': self._g_id})
                    self._sock.send(handshake)
                    self._sock.send(os.linesep)
                    self.set_status(Connection._CONNECTED)
                except socket.error as e:
                    _LOG.info('Socket error ({})'.format(e))
                    time.sleep(10.0)
            else:
                # Receive messages
                try:
                    msg = self._sock.recv_until(os.linesep, timeout=0.1)
                    _LOG.debug(r'Message received: {}'.format(msg))
                    cmd = CommandFactory.get_command(msg)
                    if isinstance(cmd, (SetMonitorFrequency,)):
                        self._msgs.put_message(cmd)
                except socket.timeout:
                    pass
                except socket.error as e:
                    _LOG.warn('connection interrupted! ({})'.format(e))
                    self._sock.shutdown(socket.SHUT_RDWR)
                    self._sock.close()
                    self._sock = None
                    self.set_status(Connection._WAITING)
                    time.sleep(20.0)

        if self._sock is not None:
            self._sock.shutdown(socket.SHUT_RDWR)
            self._sock.close()


class InternalMessage(object):
    def __init__(self, m_type, data):
        self._m_type = m_type
        self._data = data


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


class SetStateFrequency(Command):
    def __init__(self, *args):
        super(SetStateFrequency, self).__init__(*args)

    def get_monitor_frequency(self):
        return self._data['frequency']


class GotoLocation(Command):
    def __init__(self, *args):
        super(GotoLocation, self).__init__(*args)
        self._lla = Lla(self._data['x'], self._data['y'], self._data['z'])

    def get_lla(self):
        return self._lla


class Takeoff(Command):
    def __init__(self, *args):
        super(Takeoff, self).__init__(*args)

    def get_altitude(self):
        return self._data['altitude']


class SetVelocity(Command):
    def __init__(self, *args):
        super(SetVelocity, self).__init__(*args)
        self._n = self._data['x']
        self._e = self._data['y']
        self._d = self._data['z']

    def get_ned(self):
        return self._n, self._e, self._d


class SetHome(Command):
    def __init__(self, *args):
        super(SetHome, self).__init__(*args)
        self._lla = Lla(self._data['x'], self._data['y'], self._data['z'])

    def get_lla(self):
        return self._lla


class SetGroundSpeed(Command):
    def __init__(self, *args):
        super(SetGroundSpeed, self).__init__(*args)

    def get_speed(self):
        return self._data['speed']


class SetMode(Command):
    def __init__(self, *args):
        super(SetMode, self).__init__(*args)

    def get_mode(self):
        return self._data['mode']


class CommandFactory(object):
    _parsers = {
        'setMonitorFrequency': SetMonitorFrequency,
        'setStateFrequency': SetStateFrequency,
        'gotoLocation': GotoLocation,
        'takeoff': Takeoff,
        'setVelocity': SetVelocity,
        'setHome': SetHome,
        'setGroundspeed': SetGroundSpeed,
        'setMode': SetMode

    }

    @staticmethod
    def get_command(msg):
        cmd = json.loads(msg)
        args = [cmd[s] if s in cmd else '' for s in ('uavid', 'sendtimestamp', 'data', 'msgid')]

        try:
            return CommandFactory._parsers[cmd['command']](*args)
        except Exception as e:
            _LOG.warn(e)
