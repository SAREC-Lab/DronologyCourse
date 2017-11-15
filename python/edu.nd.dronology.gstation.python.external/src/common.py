import os
import json
import time


ARDUPATH = os.path.join('/', 'home', 'sean', 'git', 'ardupilot')
DRONE_TYPE_PHYS = 'PHYS'
DRONE_TYPE_SITL_VRTL = 'VRTL'

MODE_LAND = 'LAND'
MODE_GUIDED = 'GUIDED'
MODE_RTL = 'RTL'
MODE_LOITER = 'LOITER'
