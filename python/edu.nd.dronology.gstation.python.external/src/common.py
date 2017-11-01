import os
import json
import time


ARDUPATH = os.path.join('/', 'home', 'sean', 'git', 'ardupilot')
DRONE_TYPE_PHYS = 'PHYS'
DRONE_TYPE_SITL_VRTL = 'VRTL'


SOUTH_BEND_BOUNDS = [[41.68832, -86.24319], [41.68831, -86.25979], [41.67302, -86.25961], [41.67326, -86.24268]]
DEFAULT_SB_ALT = 210
SOUTH_BEND_BOUNDS_STR = '|'.join(map(lambda tup: ','.join(map(str, tup)), SOUTH_BEND_BOUNDS))

# FLY FIELD
FLY_FIELD_START = (41.519412, -86.239830)
FLY_FIELD_BOUNDS = ((41.519362, -86.240411),
                    (41.519391, -86.239414),
                    (41.519028, -86.239411),
                    (41.519007, -86.240396))

