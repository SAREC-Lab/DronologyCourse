import mathutil as mu
import json
import argparse
import core
import numpy as np
import random
import string
import threading
import util
import dronekit
import time
from copy import deepcopy
from common import *
from missions import Mission

_LOG = util.get_logger()


class NewsStations(Mission):
    @staticmethod
    def start(connection, control=core.ArduPilot, n_drones=3, config=DEFAULT_NEWS_CFG, ardupath=ARDUPATH):
        workers = []

        with open(config) as f:
            cfg = json.load(f)

        crash_site = map(lambda (lat, lon): mu.Lla(lat, lon, 0), cfg['crash_site'])

        no_fly_zones = []
        for nfz in cfg['no_fly_zones']:
            vertices = map(lambda (lat, lon): mu.Lla(lat, lon, 0), nfz)
            no_fly_zones.append(mu.GeoPoly(vertices))

        inst = 0
        for station in cfg['stations']:
            for i in range(n_drones):
                crash_site_geo = mu.GeoPoly(crash_site[:])
                vid = 'UAV{}{:05d}'.format(''.join(random.sample(string.letters, 2)),
                                           random.randint(1, 100000))
                # some drones should try to cheat
                is_cheater = random.uniform(0, 1) <= 0.4
                home = station['lat'], station['lon']
                args = [connection, control, crash_site_geo, deepcopy(no_fly_zones), vid, inst, home, is_cheater,
                        ardupath]
                workers.append(threading.Thread(target=NewsStations._start, args=args))
                inst += 1

        for worker in workers:
            worker.start()
            time.sleep(0.2)

        for worker in workers:
            worker.join()

    @staticmethod
    def _start(connection, control, crash_site_geo, no_fly_zones_geo, v_id, inst, home, is_cheater, ap):
        home_ = home + (0, 0)
        vehicle, cb = control.connect_vehicle(DRONE_TYPE_SITL_VRTL,
                                              vehicle_id=v_id, instance=inst, ardupath=ap, home=home_)

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

        mission_complete = False

        # ARM & READY
        control.set_armed(vehicle, armed=True)
        _LOG.info('Vehicle {} armed.'.format(v_id))
        vehicle.mode = dronekit.VehicleMode('GUIDED')
        # TAKEOFF
        altitude = random.uniform(30, 60)
        control.takeoff(vehicle, alt=altitude)
        _LOG.info('Vehicle {} takeoff complete at ({}, {}, {}).'.format(v_id, home[0], home[1], altitude))

        # START MESSAGE TIMERS
        util.RepeatedTimer(1.0, gen_state_message, vehicle)
        monitor_msg_timer = util.RepeatedTimer(5.0, gen_monitor_message, vehicle)

        def work():
            # HANDLE INCOMING MESSAGES
            while not mission_complete:
                cmds = core.get_commands(v_id)
                for cmd in cmds:
                    if isinstance(cmd, (SetMonitorFrequency,)):
                        freq = cmd.get_monitor_frequency() / 1000
                        _LOG.debug('Setting vehicle {} monitoring period to {}'.format(v_id, freq))
                        # acknowledge
                        connection.send(
                            str(AcknowledgeMessage.from_vehicle(vehicle, v_id, msg_id=cmd.get_msg_id())))
                        # stop the timer, reset interval, restart timer
                        monitor_msg_timer.stop()
                        monitor_msg_timer.set_interval(freq)
                        monitor_msg_timer.start()

        threading.Thread(target=work).start()
        nfz = random.choice(no_fly_zones_geo)
        nfz_cpa = nfz.cpa(control.vehicle_to_lla(vehicle)).to_lla()
        lat, lon = nfz_cpa[:2]
        _LOG.info('Vehicle {} heading to the no fly zone at ({}, {})'.format(v_id, lat, lon))
        control.goto_lla_and_wait(vehicle, lat, lon, altitude, airspeed=random.uniform(15, 25))

        if is_cheater:
            _LOG.info('Vehicle {} decided to go into the no fly zone'.format(v_id))
            nfz_mp = nfz.mean_position().to_lla()
            lat, lon = nfz_mp[:2]
            control.goto_lla_and_wait(vehicle, lat, lon, altitude, airspeed=random.uniform(15, 25))

        crash_site_cpa = crash_site_geo.cpa(control.vehicle_to_lla(vehicle)).to_lla()
        lat, lon = crash_site_cpa[:2]
        _LOG.info('Vehicle {} heading to the crash site at ({}, {})'.format(v_id, lat, lon))
        control.goto_lla_and_wait(vehicle, lat, lon, altitude, airspeed=random.uniform(15, 25))
        _LOG.info('Vehicle {} arrived at the crash site.'.format(v_id))
        # move randomly for 20 minutes
        duration = 20 * 60
        start_time = time.time()
        while time.time() - start_time < duration:
            dist = random.uniform(10, 25)
            max_move = dist ** 2
            north = random.uniform(0, dist)
            proposed = control.vehicle_to_lla(vehicle).move_ned(north, 0, 0)

            if not crash_site_geo.point_in_rectangle(proposed):
                north *= -1

            east = np.sqrt(max_move - north ** 2)
            proposed = control.vehicle_to_lla(vehicle).move_ned(north, east, 0)

            if not crash_site_geo.point_in_rectangle(proposed):
                east *= -1

            target = control.vehicle_to_lla(vehicle).move_ned(north, east, random.uniform(-2, 2))
            speed = random.uniform(1, 5)

            _LOG.debug('Vehicle {} heading at {} m/s to ({}, {}, {}) '.format(v_id, speed, *target.as_array()))

            control.goto_lla_and_wait(vehicle, *target.as_array(), airspeed=speed)

        mission_complete = True
        cb()

    @staticmethod
    def parse_args(cla):
        parser = argparse.ArgumentParser()
        parser.add_argument('-c', '--control',
                            type=Mission._parse_controller, default='core.ArduPilot',
                            help=Mission._parse_controller.__doc__)
        parser.add_argument('-n', '--n_drones',
                            type=int, default=4, help='number of drones per new station')
        parser.add_argument('-cfg', '--config',
                            type=str, default=DEFAULT_NEWS_CFG, help='path to config file')
        parser.add_argument('-ap', '--ardupath',
                            type=str, default=ARDUPATH, help='the path to ardupilot static resources')

        # TODO: add support to customize Neighborhood params.
        args = parser.parse_args(cla.split())
        return vars(args)
