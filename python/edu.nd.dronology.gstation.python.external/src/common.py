import os
import json
import time

SEARCH_DEFAULT = 'search_default'

ARDUPATH = os.path.join('/', 'Users', 'seanbayley', 'Desktop', 'git', 'ardupilot')
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

# SEARCH AND RESCUE
URBAN_SAR_BOUNDS = ((41.681070, -86.249625),
                    (41.679557, -86.249625),
                    (41.679557, -86.247696),
                    (41.681070, -86.247696))
DEFAULT_SAR_BOUNDS_STR = '|'.join(map(lambda tup: ','.join(map(str, tup)), URBAN_SAR_BOUNDS))

# HOBBIEST
DEFAULT_NB_BOUNDS = [[41.6855319096, -86.25961], [41.6859820875, -86.25961], [41.685982086, -86.2590094488],
                     [41.685531908, -86.259009453]]
DEFAULT_NB_BOUNDS_STR = '|'.join(map(lambda tup: ','.join(map(str, tup)), DEFAULT_NB_BOUNDS))


# NEWS
DEFAULT_NEWS_CFG = '../cfg/default_news_config.json'


class Waypoint:
    def __init__(self, lat, lon, alt, groundspeed=None):
        self.lat = lat
        self.lon = lon
        self.alt = alt
        self.gs = groundspeed

    def get_lla(self):
        return self.lat, self.lon, self.alt

    def get_groundspeed(self):
        return self.gs

    def as_array(self):
        return [self.lat, self.lon, self.alt, self.gs]


