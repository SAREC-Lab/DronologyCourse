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
            status = self.get_status()
            if status == ControlStation._WAITING:
                self._wait_for_connection()
                _LOG.info('Established connection.')
                self.set_status(ControlStation._IN_PROGRESS)
                if not self.mission.is_in_progress():
                    self.drones = self._make_drones()
                    self.mission.do_mission(self.drones)
                _LOG.info('Starting send and receive workers')
                self._start_workers()
            elif status == ControlStation._DISCONNECTED:
                _LOG.info('Connection broken, attempting to reset.')
                self._join_workers()
                self.sock.close()
                self.conn.close()
                _LOG.info('Sending drones to home locations.')
                self._send_drones_home()
                time.sleep(3)
                _LOG.info('Waiting for connection.')
                self.set_status(ControlStation._WAITING)
            elif status == ControlStation._IN_PROGRESS:
                # _LOG.info('mission in progress')
                # self.mission.do_mission(self.drones)
                pass
            elif status in (ControlStation._EXIT_FAIL, ControlStation._EXIT_SUCCESS):
                # do shutdown
                self.shutdown(status)
                return

            time.sleep(0.1)

    def shutdown(self, status):
        _LOG.info('Exiting with status {}.'.format(status))
        if not self.is_waiting():
            self.set_status(status)
            _LOG.info('Closing connection.')
            self.conn.close()
            _LOG.info('Closing socket.')
            self.sock.close()
            # blocking call, hopefully we made sure all the messages were received by the mission
            _LOG.info('Joining message queue.')
            self.in_queue.join()
            self._join_workers()
            _LOG.info('shutting down SITL and MAVLINK connections.')
            self.mission.stop(status)
            util.clean_up_run()

    def _wait_for_connection(self, accept_timeout=5, conn_timeout=0.25):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.settimeout(accept_timeout)
        self.sock.bind((self.host, self.port))
        self.sock.listen(0)

        while True:
            try:
                conn, addr = self.sock.accept()
                self.conn = conn
                self.conn.settimeout(conn_timeout)
                self.sock.settimeout(None)
                self.buffer = ''
                return
            except socket.timeout:
                _LOG.debug('no connection attempted in {} seconds, starting over.'.format(accept_timeout))

    def _start_workers(self):
        self.recv_worker = threading.Thread(target=self._recv)
        self.send_worker = threading.Thread(target=self._send, args=[self.report_freq])
        self.recv_worker.start()
        self.send_worker.start()

    def _join_workers(self):
        self.recv_worker.join()
        self.send_worker.join()

    def _handle_disconnection(self, e):
        with self._status_lock:
            if self._status == ControlStation._IN_PROGRESS:
                _LOG.info('Connection interrupted: {}'.format(e))
                self._status = ControlStation._DISCONNECTED
                # if e.errno in (socket.errno.ECONNRESET, socket.errno.EPIPE):
                #     pass
                # else:
                #     self._status = ControlStation._EXIT_FAIL

    def _recv(self):
        while self.is_in_progress():
            try:
                msg = self.conn.recv(4096)
                if os.linesep in msg:
                    msg_end, msg = msg.split(os.linesep)
                    _LOG.info('Message received: {}'.format(self.buffer + msg_end))
                    self.in_queue.put(DronologyCommand.from_string(self.buffer + msg_end))
                    self.buffer = ''
                self.buffer += msg
            except socket.timeout:
                # _LOG.debug('socket timeout when receiving message from dronology.')
                pass
            except socket.error as e:
                self._handle_disconnection(e)

        _LOG.info('Receive worker exiting.')

    def _send(self, send_rate):
        while self.is_in_progress():
            try:
                self.conn.send(self._get_drone_update())
                self.conn.send(os.linesep)
                time.sleep(send_rate)
            except socket.error as e:
                self._handle_disconnection(e)

        _LOG.info('Send worker exiting.')

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

