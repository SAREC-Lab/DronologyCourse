import socket
import threading
import util
import dronekit
import dronekit_sitl
import Queue
from pymavlink import mavutil
from common import *

_LOG = util.get_logger('default_file')

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


def connect_vehicle(vehicle_type, vehicle_id=None, ip=None, instance=0, ardupath=ARDUPATH, speed=1, rate=10,
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

    if vehicle_type == DRONE_TYPE_SITL_PHYS:
        vehicle = dronekit.connect(ip, wait_ready=True, baud=baud)
        shutdown_cb = lambda: vehicle.close()
    elif vehicle_type == DRONE_TYPE_SITL_VRTL:
        sitl_args = [
            '-S',
            '-I{}'.format(instance),
            '--model', '+',
            '--home', ','.join(map(str, home)),
            '--speedup', str(speed),
            '--rate', str(rate),
            '--defaults', os.path.join(ardupath, 'Tools', 'autotest', 'default_params', 'copter.parm')
        ]
        sitl = dronekit_sitl.SITL(path=os.path.join(ardupath, 'build', 'sitl', 'bin', 'arducopter'))
        sitl.launch(sitl_args, await_ready=True, verbose=True)
        tcp, ip, port = sitl.connection_string().split(':')
        port = str(int(port) + instance * 10)
        conn_string = ':'.join([tcp, ip, port])
        _LOG.info('SITL instance {} launched on: {}'.format(instance ,conn_string))
        vehicle = dronekit.connect(conn_string, wait_ready=True, baud=baud)
        _LOG.info('Vehicle {} connected on {}'.format(vehicle_id, conn_string))
        shutdown_cb = lambda: _sitl_shutdown_cb(vehicle, sitl)
    else:
        _LOG.warn('vehicle type {} not supported!'.format(vehicle_type))

    if vehicle is not None:
        if vehicle_id is None:
            vehicle_id = int(vehicle.parameters['SYSID_THISMAV'])
        vehicle.parameters['SYSID_THISMAV'] = vehicle_id

    return vehicle, shutdown_cb


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


def takeoff(vehicle, alt):
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


def land(vehicle):
    """

    :param vehicle:
    :return:
    """
    vehicle.mode = dronekit.VehicleMode("LAND")

    while vehicle.location.global_frame.alt:
        time.sleep(1)


def goto_lla(vehicle, lat, lon, alt, groundspeed=None):
    """

    :param vehicle:
    :param lat:
    :param lon:
    :param alt:
    :param groundspeed:
    :return:
    """
    vehicle.simple_goto(dronekit.LocationGlobal(lat, lon, alt), groundspeed=groundspeed)


def is_lla_reached(vehicle, lat, lon, alt, threshold=1):
    """

    :param vehicle:
    :param lat:
    :param lon:
    :param alt:
    :param threshold:
    :return:
    """
    return vehicle_to_lla(vehicle).distance(util.Lla(lat, lon, alt)) <= threshold


def _goto_sequential(vehicle, waypoints):
    """

    :param vehicle:
    :param waypoints:
    :return:
    """
    is_complete = False
    waypoints = list(waypoints)
    cur_wp = waypoints.pop(0)
    goto_lla(vehicle, *cur_wp.get_lla(), groundspeed=cur_wp.get_groundspeed())

    while not is_complete:
        if is_lla_reached(vehicle, *cur_wp.get_lla()):
            _LOG.info('Vehicle reached ({}, {}, {})'.format(*cur_wp.get_lla()))
            if waypoints:
                cur_wp = waypoints.pop(0)
                goto_lla(vehicle, *cur_wp.get_lla(), groundspeed=cur_wp.get_groundspeed())
            else:
                is_complete = True


def goto_sequential(vehicle, waypoints, block=False):
    """
    Execute a series of "goto commands":
        (lat, lon, alt, groundspeed)
    :param vehicle:
    :param waypoints:
    :param block:
    :return:
    """
    if block:
        _goto_sequential(vehicle, waypoints)
    else:
        worker = threading.Thread(target=_goto_sequential, args=[vehicle, waypoints])
        worker.start()
        return worker


def vehicle_to_lla(vehicle):
    lla = vehicle.location.global_frame
    return util.Lla(lla.lat, lla.lon, lla.alt)


class Connection:
    _WAITING = 1
    _CONNECTED = 2
    _DEAD = -1

    def __init__(self, host='127.0.0.1', port=1234, accept_timeout=5.0):
        self._host = host
        self._port = port
        self._accept_timeout = accept_timeout
        self._socket = None
        self._conn = None
        self._conn_lock = threading.Lock()
        self._status = Connection._WAITING
        self._status_lock = threading.Lock()
        self._msg_buffer = ''

    def get_status(self):
        with self._status_lock:
            return self._status

    def set_status(self, status):
        with self._status_lock:
            self._status = status

    def is_connected(self):
        return self.get_status() == Connection._CONNECTED

    def start(self):
        threading.Thread(target=self._work).start()

    def stop(self):
        self.set_status(Connection._DEAD)

    def send(self, msg):
        success = False
        if self._conn is not None:
            with self._conn_lock:
                try:
                    self._conn.send(msg)
                    self._conn.send(os.linesep)
                    success = True
                    _LOG.info(msg)
                except Exception as e:
                    _LOG.warn('failed to send message! ({})'.format(e))

        return success

    def _work(self):
        cont = True
        while cont:
            status = self.get_status()
            if status == Connection._DEAD:
                cont = False
            elif status == Connection._WAITING:
                try:
                    self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    self._socket.settimeout(self._accept_timeout)
                    self._socket.bind((self._host, self._port))
                    self._socket.listen(0)
                    conn = None
                    while conn is None and self.get_status() == Connection._WAITING:
                        _LOG.info('Waiting for Dronology connection.')
                        try:
                            conn, addr = self._socket.accept()
                            self._conn = conn
                            self._conn.settimeout(0.5)
                            self.set_status(Connection._CONNECTED)
                            _LOG.info('Established Dronology connection.')
                            time.sleep(1.0)
                        except socket.timeout:
                            _LOG.debug('No connection attempted')
                except socket.error as e:
                    _LOG.debug('Socket error ({})'.format(e))
                    if e.errno == socket.errno.EADDRINUSE:
                        time.sleep(3.0)
            else:
                try:
                    # msg = ''
                    msg = self._conn.recv(2048)
                    if os.linesep in msg:
                        toks = msg.split(os.linesep)
                        msg_end = toks[0]
                        _LOG.info('Command received: {}'.format(self._msg_buffer + msg_end))
                        cmds = [SetMonitorFrequency.from_string(self._msg_buffer + msg_end)]

                        for msg in toks[1:-1]:
                            _LOG.info('Command received: {}'.format(msg))
                            cmds.append(SetMonitorFrequency.from_string(msg))

                        # TODO: put the commands in the cmd dict

                        msg = toks[-1]
                        self._msg_buffer = ''
                    self._msg_buffer += msg
                except socket.timeout:
                    pass
                except socket.error as e:
                    _LOG.warn('connection interrupted! ({})'.format(e))
                    self._conn.close()
                    self._conn = None
                    time.sleep(1)
                    self._socket.close()
                    self.set_status(Connection._WAITING)

