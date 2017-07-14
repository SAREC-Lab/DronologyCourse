import socket
import threading
import util
import dronekit
import dronekit_sitl
import time
import Queue
from pymavlink import mavutil
from common import *

_LOG = util.get_logger('default_file')

_CMD_LOCK = threading.Lock()
_CMD_DICT = {}


def put_command(target, command):
    with _CMD_LOCK:
        if target not in _CMD_DICT:
            _CMD_DICT[target] = Queue.Queue()
        _CMD_DICT[target].put(command)


def get_commands(target):
    commands = []
    with _CMD_LOCK:
        if target in _CMD_DICT:
            while not _CMD_DICT[target].empty():
                commands.append(_CMD_DICT[target].get_nowait())

    return commands


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
                    success = True
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
                self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                self._socket.settimeout(self._accept_timeout)
                self._socket.bind((self._host, self._port))
                self._socket.listen(0)
                conn = None
                while conn is None:
                    try:
                        conn, addr = self._socket.accept()
                        self._conn = conn
                        self.set_status(Connection._CONNECTED)
                    except socket.timeout:
                        _LOG.debug('no connection attempted')
            else:
                try:
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
                except socket.error as e:
                    _LOG.warn('connection interrupted! ({})'.format(e))
                    self._socket.close()
                    self._socket = None
                    self._conn.close()
                    self._conn = None
                    time.sleep(3)
                    self.set_status(Connection._WAITING)


def make_mavlink_command(command, trg_sys=0, trg_component=0, seq=0,
                         frame=mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT,
                         param1=0, param2=0, param3=0, param4=0,
                         latitude=0, longitude=0, altitude=0):
    cmd_args = [trg_sys, trg_component,
                seq,
                frame,
                command,
                0, 0,
                param1, param2, param3, param4,
                latitude, longitude, altitude]

    return dronekit.Command(*cmd_args)


def connect_vehicle(vehicle_type, vehicle_id=None, ip=None, instance=0, ardupath=ARDUPATH, speed=1, rate=10,
                    home=(41.732955, -86.180886, 0, 0), baud=115200):
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
        _LOG.info('SITL launched on: {}'.format(conn_string))
        vehicle = dronekit.connect(conn_string, wait_ready=True, baud=baud)
        _LOG.info('Vehicle connected'.format(conn_string))
        shutdown_cb = lambda: _sitl_shutdown_cb(vehicle, sitl)
    else:
        _LOG.warn('vehicle type {} not supported!'.format(vehicle_type))

    if vehicle is not None:
        if vehicle_id is None:
            vehicle_id = int(vehicle.parameters['SYSID_THISMAV'])
        vehicle.parameters['SYSID_THISMAV'] = vehicle_id

    return vehicle, shutdown_cb


def _deploy_vehicle_auto(connection, vehicle, v_id):
    # these are going to be timers
    num_commands = vehicle.count()

    vehicle.mode = dronekit.VehicleMode('AUTO')
    is_connected = False
    send_state_message_timer = None
    send_monitor_message_timer = None

    while vehicle.next != num_commands:
        if not is_connected:
            is_connected = connection.send('placeholder')
        else:
            for cmd in get_commands(v_id):
                # TODO: check what the command is and maybe do something
                # send_location_beacon = util.RepeatedTimer(some interval, some func)
                pass


def deploy_vehicle(connection, vehicle, vehicle_id, mode='AUTO'):
    if mode == 'AUTO':
        worker = threading.Thread(target=_deploy_vehicle_auto, args=[connection, vehicle, vehicle_id])
    else:
        _LOG.warn('Mode {} is not currently supported'.format(mode))
        worker = threading.Thread(target=lambda: 0)
    worker.start()
    return worker



