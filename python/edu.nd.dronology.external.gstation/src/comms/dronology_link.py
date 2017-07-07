import multiprocessing as mp
import threading
import socket
import Queue
import errno
import json
from common import *


class DronologyLink:
    def __init__(self, in_queue, host='127.0.0.1', port=1234):
        self.host = host
        self.port = port
        self.sock = None
        self.conn = None
        self.is_alive = True
        self.in_queue = in_queue
        self.out_queue = None
        self.recv_message_buffer = ''
        self.worker = None
        self.exit_status = STATUS_EXIT

    def start(self):
        threading.Thread(target=self._accept).start()

    def stop(self):
        self.worker.join()

        if self.sock:
            self.sock.close()

        if self.conn:
            self.conn.close()

    def send(self, msg):
        if self.out_queue is not None:
            self.out_queue.put(msg)

    def _accept(self):
        self.is_alive = True
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.bind((self.host, self.port))
        self.sock.listen(0)
        try:
            client, _ = self.sock.accept()
        except KeyboardInterrupt:
            self.stop()
            return
        print('dronology connection established...')
        self.conn = client
        self.conn.settimeout(0.1)
        self.worker = threading.Thread(target=self._work)
        self.out_queue = mp.Queue()
        self.worker.start()

    def _work(self):
        while self.is_alive:
            try:
                try:
                    msg = self.out_queue.get_nowait()
                    self.conn.send(msg)
                    self.conn.send(os.linesep)
                except Queue.Empty:
                    pass

                try:
                    msg = self.conn.recv(4096)
                    if os.linesep in msg:
                        msg_end, msg = msg.split(os.linesep)
                        self.recv_message_buffer += msg_end
                        # TODO: send this message to groundstation
                        self.in_queue.put(self.recv_message_buffer)
                        # now, clear the buffer
                        self.recv_message_buffer = ""

                    self.recv_message_buffer += msg
                except socket.timeout:
                    pass
            except socket.error as e:
                print(e)
                if e.errno == errno.ECONNRESET:
                    self.in_queue.put(json.dumps(ERROR_CONN_RESET))
                    self.exit_status = STATUS_RESET
                self.is_alive = False
            except KeyboardInterrupt:
                self.is_alive = False



