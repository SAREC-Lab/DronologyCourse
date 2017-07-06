import os

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
DRONE_ATTRS = ['location', 'attitude', 'velocity', 'gimbalRotation', 'battery', 'home', 'status', 'heading', 'armable',
               'airspeed', 'groundspeed', 'armed', 'mode', 'id']

ARDUPATH = os.path.join('/Users', 'seanbayley', 'Desktop', 'git', 'ardupilot')
DRONE_TYPE_SITL_PHYS = 'sitl_physical'
DRONE_TYPE_SITL_VRTL = 'sitl_sim'
DRONE_TYPE_HTTP = 'http'
DRONE_TYPE_SIM = 'simulated'

SITL_PORT = 5760
