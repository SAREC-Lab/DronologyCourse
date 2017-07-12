import Queue
import socket
import threading
import time
import drone_link as dl
import log_util
import mission
import util
from common import *

_LOG = log_util.get_logger('default_file')


class ControlStation:
    _WAITING = 1
    _IN_PROGRESS = 2
    _DISCONNECTED = 3
    _EXIT_FAIL = -1
    _EXIT_SUCCESS = 0

    def __init__(self, host='127.0.0.1', port=1234, report_freq=1.0, ardupath=ARDUPATH,
                 drone_specs=DEFAULT_DRONE_SPECS, mission_type=mission.SAR, **kwargs):
        self.host = host
        self.port = port
        self.sock = None
        self.conn = None
        self.recv_worker = None
        self.send_worker = None
        self.buffer = None
        self.ardupath = ardupath
        self.drone_specs = drone_specs
        self.drones = {}
        self.drone_lock = threading.Lock()
        self.in_queue = Queue.Queue()
        # self.out_queue = Queue.Queue()
        self.mission = mission_type(**kwargs)
        self.mission.set_in_queue(self.in_queue)
        self.report_freq = report_freq
        self._status = self._WAITING
        self._status_lock = threading.Lock()

    def get_status(self):
        with self._status_lock:
            return self._status

    def set_status(self, status):
        with self._status_lock:
            self._status = status

    def is_waiting(self):
        return ControlStation._WAITING == self.get_status()

    def is_disconnected(self):
        return ControlStation._DISCONNECTED == self.get_status()

    def is_in_progress(self):
        return ControlStation._IN_PROGRESS == self.get_status()

    def work(self):
        while True:
            if self.is_waiting():
                if self._wait_for_connection():
                    _LOG.info('ControlStation: established connection.')
                    self.drones = self._make_drones()
                    self.mission.do_mission(self.drones)
                    self.set_status(ControlStation._IN_PROGRESS)
                    self._start_workers()
            elif self.is_disconnected():
                _LOG.info('ControlStation: connection broken, attempting to reset.')
                _LOG.info('ControlStation: joining send and receive workers.')
                self._join_workers()
                self.sock.close()
                self.conn.close()
                _LOG.info('ControlStation: sending drones to home locations.')
                self._send_drones_home()
                _LOG.info('ControlStation: waiting for connection.')
                self._wait_for_connection()
                _LOG.info('ControlStation: restarting send and receive workers.')
                self.set_status(ControlStation._IN_PROGRESS)
                self._start_workers()
                _LOG.info('ControlStation: mission has been successfully reset.')
            elif self.is_in_progress():
                # self.mission.do_mission(self.drones)
                pass
            else:
                # do shutdown
                _LOG.info('ControlStation exiting with status {}.'.format(self.get_status()))
                self._join_workers()
                self.shutdown()
                return

    def shutdown(self):
        _LOG.info('ControlStation: closing connection.')
        self.conn.close()
        _LOG.info('ControlStation: closing socket.')
        self.sock.close()
        # blocking call, hopefully we made sure all the messages were received by the mission
        _LOG.info('ControlStation: joining message queue.')
        self.in_queue.join()
        _LOG.info('ControlStation: shutting down SITL and MAVLINK connections.')
        util.clean_up_run()

    def _wait_for_connection(self, accept_timeout=10, conn_timeout=0.25):
        success = 1
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.settimeout(accept_timeout)
        self.sock.bind((self.host, self.port))
        self.sock.listen(0)
        try:
            conn, addr = self.sock.accept()
            self.conn = conn
            self.conn.settimeout(conn_timeout)
            self.sock.settimeout(None)
            self.buffer = ''
        except socket.timeout:
            # _LOG.debug('connection timed out, starting over.')
            self.sock.close()
            success = 0

        return success

    def _start_workers(self):
        self.recv_worker = threading.Thread(target=self._recv)
        self.send_worker = threading.Thread(target=self._send)
        self.recv_worker.start()
        self.send_worker.start()

    def _join_workers(self):
        self.recv_worker.join()
        self.send_worker.join()

    def _handle_disconnection(self, e):
        with self._status_lock:
            if self.is_in_progress():
                _LOG.info('ControlStation: connection interrupted: {}'.format(e))
                if e.errno in (socket.errno.ECONNRESET, socket.errno.EPIPE):
                    # TODO: handle reconnection
                    self._status = self._DISCONNECTED
                else:
                    self._status = self._EXIT_FAIL

    def _recv(self):
        while self.is_in_progress():
            try:
                msg = self.conn.recv(4096)
                if os.linesep in msg:
                    msg_end, msg = msg.split(os.linesep)
                    self.in_queue.put(DronologyCommand.from_string(self.buffer + msg_end))
                    self.buffer = ''
                self.buffer += msg
            except socket.timeout:
                # _LOG.debug('socket timeout when receiving message from dronology.')
                pass
            except socket.error as e:
                self._handle_disconnection(e)

    def _send(self, send_rate=0.25):
        while self.is_in_progress():
            try:
                self.conn.send(self._get_drone_update())
                self.conn.send(os.linesep)
                time.sleep(send_rate)
            except socket.error as e:
                self._handle_disconnection(e)

    def _make_drones(self):
        drones = {}
        for i, (d_type, d_kwargs) in enumerate(self.drone_specs):
            drone = dl.make_drone_link(d_type, ardupath=self.ardupath, **d_kwargs)
            drone.connect()

            # TODO: figure out why SYSID_THISMAV is not unique.
            d_id = '{}{}'.format(d_type, i + 1)
            drone.set_id(d_id)
            drones[d_id] = drone

        return drones

    def _get_drone_update(self):
        drone_list = {}
        with self.drone_lock:
            for drone_id, drone in self.drones.items():
                drone_list[str(drone_id)] = drone.report()

        return json.dumps({'type': 'drone_list', 'data': drone_list})

    def _send_drones_home(self):
        for drone_id, drone in self.drones.items():
            pass
            # drone.send_to_home()
            # while not drone.is_home():
            #     time.sleep(3)

