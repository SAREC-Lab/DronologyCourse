import mathutil as mu
import argparse
import core
import numpy as np
import random
import string
from common import *
from missions import Mission


class Neighborhood(Mission):
    @staticmethod
    def start(connection, control=core.ArduPilot, ardupath=ARDUPATH, bounds=None, n_drones=3, duration=10, **kwargs):
        """

        :param control:
        :param ardupath:
        :param connection:
        :param bounds:
        :param n_drones:
        :param duration:
        :param kwargs:
        :return:
        """
        workers = []
        if bounds is not None:
            duration_s = duration * 60
            grid_lla = map(lambda (lat, lon): mu.Lla(lat, lon, 0), bounds)
            grid_geo = mu.GeoPoly(grid_lla)
            origin = grid_geo.sw_vertex()
            dist_n = grid_geo.distance_north()
            dist_e = grid_geo.distance_east()

            for i in range(n_drones):
                offset_n = random.uniform(0, dist_n)
                offset_e = random.uniform(0, dist_e)
                d_origin = origin.move_ned(offset_n, offset_e, 0)[:-1]
                vid = 'UAV{}{}{}'.format(random.randint(1, 1000),
                                         random.sample(string.letters, 2),
                                         random.randint(1, 50))
                args = [connection, control, bounds, duration_s, d_origin, vid, DRONE_TYPE_SITL_VRTL,
                        i, ardupath, None]


    @staticmethod
    def _start(connection, control, bounds, duration, home, v_id, v_type, inst, ardu, ip, baud=115200, rate=10):
        home_ = tuple(home) + (0,0)

    @staticmethod
    def parse_args(cla):
        parser = argparse.ArgumentParser()
        parser.add_argument('-c', '--control',
                            type=Mission._parse_controller, default='core.ArduPilot',
                            help=Mission._parse_controller.__doc__)
        parser.add_argument('-b', '--bounds',
                            type=Mission._parse_sar_bounds, default=DEFAULT_SAR_BOUNDS_STR,
                            help=Mission._parse_sar_bounds.__doc__)
        parser.add_argument('-n', '--n_drones',
                            type=int, default=3, help='the number of drones to fly in the neighborhood.')
        parser.add_argument('-d', '--duration',
                            type=int, default=10, help='the number of minutes each drone should fly.')
        parser.add_argument('-ap', '--ardupath',
                            type=str, default=ARDUPATH, help='the path to ardupilot static resources')

        args = parser.parse_args(cla.split())
        return vars(args)
