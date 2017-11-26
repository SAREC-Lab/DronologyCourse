import json
import util
from util import Lla

_LOG = util.get_logger()


class Command(object):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        self._vid = vehicle_id
        self._timestamp = timestamp
        self._data = data
        self._msg_id = msg_id

    def __str__(self):
        return 'Vehicle {} {}: {}'.format(self._vid, self.__class__.__name__, json.dumps(self._data))

    def __repr__(self):
        return str(self)

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
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(SetMonitorFrequency, self).__init__(vehicle_id, timestamp, data, msg_id)

    def get_monitor_frequency(self):
        return self._data['frequency']


class SetStateFrequency(Command):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(SetStateFrequency, self).__init__(vehicle_id, timestamp, data, msg_id)

    def get_monitor_frequency(self):
        return self._data['frequency']


class GotoLocation(Command):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(GotoLocation, self).__init__(vehicle_id, timestamp, data, msg_id)
        self._lla = Lla(self._data['x'], self._data['y'], self._data['z'])

    def get_lla(self):
        return self._lla


class Takeoff(Command):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(Takeoff, self).__init__(vehicle_id, timestamp, data, msg_id)

    def get_altitude(self):
        return self._data['altitude']


class SetVelocity(Command):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(SetVelocity, self).__init__(vehicle_id, timestamp, data, msg_id)
        self._n = self._data['x']
        self._e = self._data['y']
        self._d = self._data['z']

    def get_ned(self):
        return self._n, self._e, self._d


class SetHome(Command):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(SetHome, self).__init__(vehicle_id, timestamp, data, msg_id)
        self._lla = Lla(self._data['x'], self._data['y'], self._data['z'])

    def get_lla(self):
        return self._lla


class SetGroundSpeed(Command):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(SetGroundSpeed, self).__init__(vehicle_id, timestamp, data, msg_id)

    def get_speed(self):
        return self._data['speed']


class SetMode(Command):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(SetMode, self).__init__(vehicle_id, timestamp, data, msg_id)

    def get_mode(self):
        return self._data['mode']


class SetArmed(Command):
    def __init__(self, vehicle_id, timestamp, data, msg_id):
        super(SetArmed, self).__init__(vehicle_id, timestamp, data, msg_id)

    def get_armed(self):
        return self._data['armed']


class CommandFactory(object):
    _parsers = {
        'setMonitorFrequency': SetMonitorFrequency,
        'setStateFrequency': SetStateFrequency,
        'gotoLocation': GotoLocation,
        'takeoff': Takeoff,
        'setVelocity': SetVelocity,
        'setHome': SetHome,
        'setGroundspeed': SetGroundSpeed,
        'setMode': SetMode,
        'setArmed': SetArmed

    }

    @staticmethod
    def get_command(msg):
        cmd = json.loads(msg)
        args = [cmd[s] if s in cmd else '' for s in ('uavid', 'sendtimestamp', 'data', 'msgid')]

        try:
            return CommandFactory._parsers[cmd['command']](*args)
        except Exception as e:
            _LOG.warn('Unrecognized command: {}'.format(e))