import os
import json

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
DRONE_TYPE_HTTP = 'http'
DRONE_TYPE_SIM = 'simulated'

RESPOND_ALL = 'all'
RESPOND_CRITICAL = 'critical'

SITL_PORT = 5760


CMD_TYPE_ERROR = 'error'
CMD_RESET = 'reset'

ERROR_CONN_RESET = {'type': CMD_TYPE_ERROR, 'data': {'id': None, 'command': CMD_RESET, 'data': None}}


class Command(object):

    def __init__(self, origin, destination, payload):
        self.orig = origin
        self.dest = destination
        self.payload = payload

    def get_origin(self):
        return self.orig

    def get_destination(self):
        return self.dest

    def get_payload(self):
        return self.payload


class DronologyCommand(Command):

    def __init__(self, data):
        super(DronologyCommand, self).__init__(DRONOLOGY_LINK, MISSION, data)

    @classmethod
    def from_string(cls, msg):
        d_msg = json.loads(msg)
        cls(d_msg['data'])


class ExitCommand(Command):

    def __init__(self, origin, destination):
        super(ExitCommand, self).__init__(origin, destination, None)
