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

DRONE_1 = (DRONE_TYPE_SITL_VRTL, {'instance': 0, D_ATTR_HOME_LOC: (41.519408, -86.239996, 0, 0)})
DRONE_2 = (DRONE_TYPE_SITL_VRTL, {'instance': 1, D_ATTR_HOME_LOC: (41.519408, -86.239496, 0, 0)})

DEFAULT_DRONE_SPECS = (DRONE_1,)
DEFAULT_SAR_BOUNDS = ((41.519367, -86.240419, 0),
                      (41.519277, -86.240405, 0),
                      (41.519395, -86.239418, 0),
                      (41.519313, -86.239417, 0))



class DronologyMessage:
    def __init__(self):
        pass



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



