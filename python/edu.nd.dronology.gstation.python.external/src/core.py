import socket
import threading

import mathutil
import util
import dronekit
import dronekit_sitl
import Queue
from pymavlink import mavutil
from common import *

_LOG = util.get_logger()

_CMD_LOCK = threading.Lock()
_CMD_DICT = {}


def put_command(target, command):
    """
    Store a command from Dronology to a vehicle.

    :param target:
    :param command:
    :return:
    """
    with _CMD_LOCK:
        if target not in _CMD_DICT:
            _CMD_DICT[target] = Queue.Queue()
        _CMD_DICT[target].put(command)


def get_commands(target):
    """
    Get all the commands for a vehicle.

    :param target:
    :return:
    """
    commands = []
    with _CMD_LOCK:
        if target in _CMD_DICT:
            while not _CMD_DICT[target].empty():
                commands.append(_CMD_DICT[target].get_nowait())

    return commands


def make_mavlink_command(command, trg_sys=0, trg_component=0, seq=0,
                         frame=mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT,
                         param1=0, param2=0, param3=0, param4=0,
                         lat_or_param5=0, lon_or_param6=0, alt_or_param7=0):
    """
    Make a new mavlink command.

    :param command:
    :param trg_sys:
    :param trg_component:
    :param seq:
    :param frame:
    :param param1:
    :param param2:
    :param param3:
    :param param4:
    :param lat_or_param5:
    :param lon_or_param6:
    :param alt_or_param7:
    :return:
    """
    cmd_args = [trg_sys, trg_component,
                seq,
                frame,
                command,
                0, 0,
                param1, param2, param3, param4,
                lat_or_param5, lon_or_param6, alt_or_param7]

    return dronekit.Command(*cmd_args)


class VehicleControl(object):
    @staticmethod
    def connect_vehicle(**kwargs):
        raise NotImplementedError

    @staticmethod
    def set_armed(vehicle, **kwargs):
        raise NotImplementedError

    @staticmethod
    def takeoff(vehicle, **kwargs):
        raise NotImplementedError

    @staticmethod
    def set_mode(vehicle, **kwargs):
        raise NotImplementedError

    @staticmethod
    def goto_lla(vehicle, **kwargs):
        raise NotImplementedError

    @staticmethod
    def is_lla_reached(vehicle, **kwargs):
        raise NotImplementedError

    @staticmethod
    def goto_lla_sequential(vehicle, **kwargs):
        raise NotImplementedError


class ArduPilot(VehicleControl):
    @staticmethod
    def connect_vehicle(vehicle_type=None, vehicle_id=None, ip=None, instance=0, ardupath=ARDUPATH, rate=10,
                        home=(41.732955, -86.180886, 0, 0), baud=115200):
        """
        Connect to a SITL vehicle or a real vehicle.

        :param vehicle_type:
        :param vehicle_id:
        :param ip:
        :param instance:
        :param ardupath:
        :param speed:
        :param rate:
        :param home:
        :param baud:
        :return:
        """
        def _sitl_shutdown_cb(m_vehicle, m_sitl):
            m_vehicle.close()
            m_sitl.stop()

        vehicle = None
        shutdown_cb = lambda: 0

        if vehicle_type == DRONE_TYPE_PHYS:
            vehicle = dronekit.connect(ip, wait_ready=True, baud=baud)
            shutdown_cb = lambda: vehicle.close()
        elif vehicle_type == DRONE_TYPE_SITL_VRTL:
            sitl_args = [
                '-S',
                '-I{}'.format(instance),
                '--model', '+',
                '--home', ','.join(map(str, home)),
                '--rate', str(rate),
                '--defaults', os.path.join(ardupath, 'Tools', 'autotest', 'default_params', 'copter.parm')
            ]
            _LOG.debug('Trying to launch SITL instance {} on tcp:127.0.0.1:{}'.format(instance, 5760 + instance * 10))
            sitl = dronekit_sitl.SITL(path=os.path.join(ardupath, 'build', 'sitl', 'bin', 'arducopter'))
            sitl.launch(sitl_args, await_ready=True)
            tcp, ip, port = sitl.connection_string().split(':')
            port = str(int(port) + instance * 10)
            conn_string = ':'.join([tcp, ip, port])
            _LOG.debug('SITL instance {} launched on: {}'.format(instance, conn_string))
            vehicle = dronekit.connect(conn_string, wait_ready=True, baud=baud)
            _LOG.info('Vehicle {} connected on {}'.format(vehicle_id, conn_string))
            shutdown_cb = lambda: _sitl_shutdown_cb(vehicle, sitl)
        else:
            _LOG.warn('vehicle type {} not supported!'.format(vehicle_type))

        return vehicle, shutdown_cb

    @staticmethod
    def set_armed(vehicle, armed=True):
        """

        :param vehicle:
        :param armed:
        :return:
        """
        if vehicle.armed != armed:
            if armed:
                while not vehicle.is_armable:
                    time.sleep(1)

            vehicle.armed = armed

            while vehicle.armed != armed:
                time.sleep(1)

    @staticmethod
    def takeoff(vehicle, alt=10):
        """

        :param vehicle:
        :param alt:
        :return:
        """
        cur_alt = 0
        vehicle.simple_takeoff(alt=alt)

        while abs(alt - cur_alt) > 3:
            cur_alt = vehicle.location.global_frame.alt
            time.sleep(1)

    @staticmethod
    def land(vehicle):
        """

        :param vehicle:
        :return:
        """
        vehicle.mode = dronekit.VehicleMode("LAND")

        while vehicle.location.global_frame.alt:
            time.sleep(2)

    @staticmethod
    def set_mode(vehicle, mode):
        if mode == 'LAND':
            ArduPilot.land(vehicle)
        else:
            _LOG.warn('unsupported mode ({})'.format(mode))

    @staticmethod
    def goto_lla_and_wait(vehicle, lat, lon, alt, groundspeed=None):
        ArduPilot.goto_lla(vehicle, lat, lon, alt, groundspeed=groundspeed)

        while not ArduPilot.is_lla_reached(vehicle, lat, lon, alt):
            time.sleep(2)

    @staticmethod
    def goto_lla(vehicle, lat, lon, alt, groundspeed=None):
        """

        :param vehicle:
        :param lat:
        :param lon:
        :param alt:
        :param groundspeed:
        :return:
        """
        vehicle.simple_goto(dronekit.LocationGlobal(lat, lon, alt), airspeed=groundspeed)

    @staticmethod
    def is_lla_reached(vehicle, lat, lon, alt, threshold=1):
        """

        :param vehicle:
        :param lat:
        :param lon:
        :param alt:
        :param threshold:
        :return:
        """
        return ArduPilot.vehicle_to_lla(vehicle).distance(mathutil.Lla(lat, lon, alt)) <= threshold

    @staticmethod
    def _goto_sequential(vehicle, waypoints):
        """

        :param vehicle:
        :param waypoints:
        :return:
        """
        is_complete = False
        waypoints = list(waypoints)
        cur_wp = waypoints.pop(0)
        ArduPilot.goto_lla(vehicle, *cur_wp.get_lla(), groundspeed=cur_wp.get_groundspeed())

        while not is_complete and vehicle.mode.name == 'GUIDED':
            if ArduPilot.is_lla_reached(vehicle, *cur_wp.get_lla()):
                _LOG.info('Vehicle reached ({}, {}, {})'.format(*cur_wp.get_lla()))
                if waypoints:
                    cur_wp = waypoints.pop(0)
                    _LOG.info('Vehicle en route ({}, {}, {}) at {} m/s'.format(*cur_wp.as_array()))
                    ArduPilot.goto_lla(vehicle, *cur_wp.get_lla(), groundspeed=cur_wp.get_groundspeed())
                else:
                    is_complete = True

    @staticmethod
    def goto_lla_sequential(vehicle, waypoints, block=False):
        """
        Execute a series of "goto commands":
            (lat, lon, alt, groundspeed)
        :param vehicle:
        :param waypoints:
        :param block:
        :return:
        """
        if block:
            ArduPilot._goto_sequential(vehicle, waypoints)
        else:
            worker = threading.Thread(target=ArduPilot._goto_sequential, args=[vehicle, waypoints])
            worker.start()
            return worker

    @staticmethod
    def vehicle_to_lla(vehicle):
        lla = vehicle.location.global_frame
        return mathutil.Lla(lla.lat, lla.lon, lla.alt)


class Connection:
    _WAITING = 1
    _CONNECTED = 2
    _DEAD = -1

    def __init__(self, conn):
        self.conn = conn


class Host:
    _WAITING = 1
    _CONNECTED = 2
    _DEAD = -1

    def __init__(self, addr='', port=1234, accept_timeout=15.0):
        self._addr = addr
        self._port = port
        self._accept_timeout = accept_timeout
        self._socket = None
        self._conn = None
        self._conn_lock = threading.Lock()
        self._status = Host._WAITING
        self._status_lock = threading.Lock()
        self._msg_buffer = ''

    def get_status(self):
        with self._status_lock:
            return self._status

    def set_status(self, status):
        with self._status_lock:
            self._status = status

    def is_connected(self):
        return self.get_status() == Host._CONNECTED

    def start(self):
        threading.Thread(target=self._work).start()

    def stop(self):
        self.set_status(Host._DEAD)

    def send(self, msg):
        success = False
        with self._conn_lock:
            if self._status == Host._CONNECTED:
                try:
                    self._conn.send(msg)
                    self._conn.send(os.linesep)
                    success = True
                except Exception as e:
                    _LOG.warn('failed to send message! ({})'.format(e))

        return success

    def _work(self):
        cont = True
        while cont:
            status = self.get_status()
            if status == Host._DEAD:
                cont = False
                _LOG.info('shutting down connection...')
            elif status == Host._WAITING:
                try:
                    self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    self._socket.settimeout(self._accept_timeout)
                    self._socket.bind((self._addr, self._port))
                    self._socket.listen(0)
                    _LOG.info('Waiting for Dronology connection.')
                    while self.get_status() == Host._WAITING:
                        try:
                            conn, addr = self._socket.accept()
                            self._conn = conn
                            self._conn.settimeout(5.0)
                            self.set_status(Host._CONNECTED)
                            _LOG.info('Established Dronology connection.')
                            time.sleep(1.0)
                        except socket.timeout:
                            _LOG.debug('No connection attempted')
                except socket.error as e:
                    _LOG.info('Socket error ({})'.format(e))
                    time.sleep(5.0)
            else:
                try:
                    # msg = ''
                    msg = self._conn.recv(2048)
                    _LOG.info('Message received: {}'.format(msg))
                    if os.linesep in msg:
                        toks = msg.split(os.linesep)
                        msg_end = toks[0]
                        new_msgs = [self._msg_buffer + msg_end]

                        for msg_ in toks[1:-1]:
                            new_msgs.append(msg_)

                        for msg_ in new_msgs:
                            _LOG.info('Command received: {}'.format(msg_))
                            cmd = CommandFactory.get_command(msg_)
                            if isinstance(cmd, (SetMonitorFrequency,)):
                                put_command(cmd.get_target(), cmd)

                        msg = toks[-1]
                        self._msg_buffer = ''
                    self._msg_buffer += msg
                except socket.timeout:
                    pass
                except socket.error as e:
                    _LOG.warn('connection interrupted! ({})'.format(e))
                    # self._conn.shutdown(socket.SHUT_RD)
                    # self._conn.close()
                    self._conn = None
                    self._socket.shutdown(socket.SHUT_RDWR)
                    self._socket.close()
                    self._socket = None
                    self.set_status(Host._WAITING)
                    time.sleep(20.0)
        if self._conn is not None:
            self._conn.shutdown(socket.SHUT_RD)
            self._conn.close()
