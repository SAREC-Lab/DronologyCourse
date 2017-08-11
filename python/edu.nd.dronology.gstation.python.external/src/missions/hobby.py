import mathutil as mu
import argparse
import core
import numpy as np
import random
import string
import threading
import util
import dronekit
import time
from common import *
from missions import Mission

_LOG = util.get_logger()


def gen_neighborhoods(bounds, n_neighborhoods):
    grid_lla = list(map(lambda (lat, lon): mu.Lla(lat, lon, 0), bounds))
    grid_geo = mu.GeoPoly(grid_lla)

    p_EA_E = grid_geo.sw_vertex()
    p_EB_E = grid_geo.nw_vertex()
    p_EC_E = grid_geo.se_vertex()

    p_AB_N = p_EA_E.distance_ned(p_EB_E)
    p_AC_N = p_EA_E.distance_ned(p_EC_E)

    dist_n = p_AB_N[0]
    dist_e = p_AC_N[1]

    step_n = dist_n / n_neighborhoods
    step_e = dist_e / n_neighborhoods

    neighborhoods = []

    for i in range(n_neighborhoods):
        x = i * step_e
        y = random.uniform(0, dist_n - step_n)

        nb = mu.make_grid(p_EA_E.move_ned(y, x, 0), 100, 100)
        verts = nb.get_lla()

        neighborhoods.append([tuple(p.to_lla()[:-1]) for p in verts])

    return neighborhoods


class Neighborhoods(Mission):
    @staticmethod
    def start(connection, control=core.ArduPilot, bounds=SOUTH_BEND_BOUNDS, ardupath=ARDUPATH,
              n_neighborhoods=10, n_drones=3):
        workers = []
        nb_bounds = gen_neighborhoods(bounds, n_neighborhoods)

        for i, nb_bound in enumerate(nb_bounds):
            worker = threading.Thread(target=Neighborhood.start,
                                      args=[connection],
                                      kwargs={'control': control, 'ardupath': ardupath, 'bounds': nb_bound,
                                              'inst_offset': n_drones * i})
            workers.append(worker)

        for worker in workers:
            worker.start()
            time.sleep(2.0)

        for worker in workers:
            worker.join()

    @staticmethod
    def parse_args(cla):
        parser = argparse.ArgumentParser()
        parser.add_argument('-c', '--control',
                            type=Mission._parse_controller, default='core.ArduPilot',
                            help=Mission._parse_controller.__doc__)
        parser.add_argument('-b', '--bounds',
                            type=Mission._parse_sar_bounds, default=SOUTH_BEND_BOUNDS_STR,
                            help=Mission._parse_sar_bounds.__doc__)
        parser.add_argument('-n', '--n_neighborhoods',
                            type=int, default=10, help='the number of neighborhoods to create')
        parser.add_argument('-ap', '--ardupath',
                            type=str, default=ARDUPATH, help='the path to ardupilot static resources')

        # TODO: add support to customize Neighborhood params.
        args = parser.parse_args(cla.split())
        return vars(args)


class Neighborhood(Mission):
    @staticmethod
    def start(connection, control=core.ArduPilot, ardupath=ARDUPATH, bounds=DEFAULT_NB_BOUNDS, n_drones=3, duration=30,
              inst_offset=0):
        """

        :param control:
        :param ardupath:
        :param connection:
        :param bounds:
        :param n_drones:
        :param duration:
        :param inst_offset:
        :return:
        """
        workers = []
        duration_s = duration * 60
        grid_lla = map(lambda (lat, lon): mu.Lla(lat, lon, 0), bounds)
        grid_geo = mu.GeoPoly(grid_lla)
        origin = grid_geo.sw_vertex()
        dist_n = grid_geo.distance_north()
        dist_e = grid_geo.distance_east()

        for i in range(n_drones):
            temp_n = dist_n / (i + 1)
            temp_e = dist_e / (i + 1)
            offset_n = random.uniform(temp_n, temp_n + (dist_n / n_drones))
            offset_e = random.uniform(temp_e, temp_e + (dist_e / n_drones))
            d_origin = origin.move_ned(offset_n, offset_e, 0).to_lla()[:-1]
            vid = 'UAV{:02d}{}{:02d}'.format(random.randint(1, 100),
                                             ''.join(random.sample(string.letters, 3)),
                                             random.randint(1, 50))
            args = [connection, control, bounds, duration_s, d_origin, vid,
                    inst_offset + i, ardupath]
            worker = threading.Thread(target=Neighborhood._start, args=args)
            workers.append(worker)

        for worker in workers:
            worker.start()
            time.sleep(0.2)

        for worker in workers:
            worker.join()

    @staticmethod
    def _start(connection, control, bounds, duration, home, v_id, inst, ardu):
        home_ = tuple(home) + (0, 0)
        vehicle, shutdown_cb = control.connect_vehicle(DRONE_TYPE_SITL_VRTL,
                                                       vehicle_id=v_id, instance=inst, ardupath=ardu, home=home_)

        grid_lla = map(lambda (lat, lon): mu.Lla(lat, lon, 0), bounds)
        grid_geo = mu.GeoPoly(grid_lla)
        max_move = np.sqrt(99)

        while not connection.is_connected():
            time.sleep(3.0)

        handshake_complete = False
        # WAIT FOR HANDSHAKE BEFORE STARTING
        while not handshake_complete:
            handshake_complete = connection.send(str(HandshakeMessage.from_vehicle(vehicle, v_id)))
            time.sleep(3.0)

        battery_dur = 40 * 60
        mission_start = time.time()

        # SET UP TIMERS
        def gen_state_message(m_vehicle):
            msg = StateMessage.from_vehicle(m_vehicle, v_id)
            connection.send(str(msg))

        def gen_monitor_message(m_vehicle):
            battery_level = (battery_dur - (time.time() - mission_start)) / battery_dur
            battery_level *= 100
            msg = MonitorMessage.from_vehicle(m_vehicle, v_id, battery_level=battery_level)
            connection.send(str(msg))

        # ARM & READY
        control.set_armed(vehicle, armed=True)
        _LOG.info('Vehicle {} armed.'.format(v_id))
        vehicle.mode = dronekit.VehicleMode('GUIDED')

        # TAKEOFF
        control.takeoff(vehicle, alt=random.uniform(10, 20))

        # START MESSAGE TIMERS
        util.RepeatedTimer(1.0, gen_state_message, vehicle)
        monitor_msg_timer = util.RepeatedTimer(5.0, gen_monitor_message, vehicle)
        _LOG.info('Vehicle {} takeoff complete.'.format(v_id))

        start_time = time.time()
        # loop for some specified number of seconds
        while time.time() - start_time < duration:
            north = random.uniform(0, max_move)
            proposed = control.vehicle_to_lla(vehicle).move_ned(north, 0, 0)

            if not grid_geo.point_in_rectangle(proposed):
                north *= -1

            east = np.sqrt(99 - north ** 2)
            proposed = control.vehicle_to_lla(vehicle).move_ned(north, east, 0)

            if not grid_geo.point_in_rectangle(proposed):
                east *= -1

            target = control.vehicle_to_lla(vehicle).move_ned(north, east, random.uniform(-1, 1))
            speed = random.uniform(10, 20)

            _LOG.debug('Vehicle {} heading at {} m/s to ({}, {}, {}) '.format(v_id, speed, *target.as_array()))

            control.goto_lla_and_wait(vehicle, *target.as_array(), airspeed=speed)

            _LOG.debug('Vehicle {} reached ({}, {}, {})'.format(v_id, *target.as_array()))

            cmds = core.get_commands(v_id)
            for cmd in cmds:
                if isinstance(cmd, (SetMonitorFrequency,)):
                    freq = cmd.get_monitor_frequency() / 1000
                    _LOG.info('Setting vehicle {} monitoring period to {}'.format(v_id, freq))
                    # acknowledge
                    connection.send(str(AcknowledgeMessage.from_vehicle(vehicle, v_id, msg_id=cmd.get_msg_id())))
                    # stop the timer, reset interval, restart timer
                    monitor_msg_timer.stop()
                    monitor_msg_timer.set_interval(freq)
                    monitor_msg_timer.start()

    @staticmethod
    def parse_args(cla):
        parser = argparse.ArgumentParser()
        parser.add_argument('-c', '--control',
                            type=Mission._parse_controller, default='core.ArduPilot',
                            help=Mission._parse_controller.__doc__)
        parser.add_argument('-b', '--bounds',
                            type=Mission._parse_sar_bounds, default=DEFAULT_NB_BOUNDS_STR,
                            help=Mission._parse_sar_bounds.__doc__)
        parser.add_argument('-n', '--n_drones',
                            type=int, default=3, help='the number of drones to fly in the neighborhood.')
        parser.add_argument('-d', '--duration',
                            type=int, default=10, help='the number of minutes each drone should fly.')
        parser.add_argument('-ap', '--ardupath',
                            type=str, default=ARDUPATH, help='the path to ardupilot static resources')

        args = parser.parse_args(cla.split())
        return vars(args)
