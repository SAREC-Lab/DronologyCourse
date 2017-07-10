import multiprocessing as mp
import threading
import socket
import Queue
import errno
import log_util
import time
from common import *


_LOG = log_util.get_logger('default_file')


class StopException(Exception):
    pass


class DronologyLink:
    _STATUS_WAITING = 0
    _STATUS_ALIVE = 1
    _STATUS_CONNECTED = 2
    _STATUS_STOPPED = 3

    def __init__(self, in_queue, host='127.0.0.1', port=1234):
        self.host = host
        self.port = port
        self.sock = None
        self.conn = None
        self.in_queue = in_queue
        self.out_queue = None
        self.recv_message_buffer = ''
        self.worker = None
        self._status = self._STATUS_WAITING
        self._status_lock = threading.Lock()

    def start(self):
        self.worker = threading.Thread(target=self._work)
        self.worker.start()
        self._status = self._STATUS_ALIVE

    def stop(self):
        if self._status == self._STATUS_ALIVE:
            socket.socket(socket.AF_INET,
                          socket.SOCK_STREAM).connect((self.host, self.port))
            time.sleep(1)

        if self._status in [self._STATUS_CONNECTED, self._STATUS_STOPPED]:
            self._status = self._STATUS_STOPPED
            self.worker.join()
            self.sock.close()
            self.conn.close()

            while not self.out_queue.empty():
                self.out_queue.get_nowait()
                self.out_queue.task_done()

            self.out_queue.join()

    def send(self, msg):
        if self.out_queue is not None:
            self.out_queue.put(msg)

    def _work(self):
        while self._status != self._STATUS_STOPPED:
            try:
                if self._status == self._STATUS_CONNECTED:
                    self._work_connected()
                elif self._status == self._STATUS_WAITING:
                    self._work_waiting()
            except (socket.timeout, Queue.Empty):
                pass
            except KeyboardInterrupt:
                self._status = self._STATUS_STOPPED
            except socket.error as e:
                _LOG.info('connection interrupted: {}'.format(e))
                if e.errno == errno.ECONNRESET:
                    # TODO: handle reconnection
                    self._status = self._STATUS_STOPPED
                else:
                    self._status = self._STATUS_STOPPED

        self.in_queue.put(ExitCommand(DRONOLOGY_LINK, CONTROL_STATION))

    def _work_connected(self):
        msg = self.out_queue.get_nowait()
        self.out_queue.task_done()
        self.conn.send(msg)
        self.conn.send(os.linesep)

        msg = self.conn.recv(4096)
        if os.linesep in msg:
            msg_end, msg = msg.split(os.linesep)
            self.recv_message_buffer += msg_end

            # send the message to control station
            self.in_queue.put(DronologyCommand.from_string(self.recv_message_buffer))
            # now, clear the buffer
            self.recv_message_buffer = ""

        self.recv_message_buffer += msg

    def _work_waiting(self):
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.bind((self.host, self.port))
        self.sock.listen(0)

        client, _ = self.sock.accept()
        _LOG.info('dronology connection established...')
        self.conn = client
        self.conn.settimeout(0.1)
        self.out_queue = Queue.Queue()
        self._status = self._STATUS_CONNECTED
