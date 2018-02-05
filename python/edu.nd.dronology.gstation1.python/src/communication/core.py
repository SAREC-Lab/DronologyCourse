import json
import os
import socket
import threading
import time
import util
from boltons import socketutils
from communication import command

_LOG = util.get_logger()


class MessageQueue:
    def __init__(self):
        self._lock = threading.Lock()
        self._messages = []

    def put_message(self, msg):
        with self._lock:
            self._messages.append(msg)

    def get_messages(self):
        msgs = []
        with self._lock:
            while self._messages:
                msgs.append(self._messages.pop(0))

        return msgs


class Connection:
    _WAITING = 1
    _CONNECTED = 2
    _DEAD = -1

    def __init__(self, msg_queue, addr='', port=1234, g_id='default_groundstation'):
        self._g_id = g_id
        self._msgs = msg_queue
        self._addr = addr
        self._port = port
        self._sock = None
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
        """
        Start the connection
        :return:
        """
        threading.Thread(target=self._work).start()

    def stop(self):
        """
        Shutdown the connection.
        :return:
        """
        self.set_status(Connection._DEAD)

    def send(self, msg):
        """
        Send a message to Dronology.
        :param msg: the message to be sent over the socket (string)
        :return: True if the message was able to be sent else False
        """
        success = False
        with self._conn_lock:
            if self._status == Connection._CONNECTED:
                try:
                    self._sock.send(msg)
                    self._sock.send(os.linesep)
                    success = True
                except Exception as e:
                    _LOG.warn('failed to send message! ({})'.format(e))

        return success

    def get_messages(self, vid):
        """
        Get all the messages for a specific vehicle.
        :param vid:
        :return:
        """
        # TODO: is this deprecated?
        return self._msgs.get_messages(vid)

    def _work(self):
        """
        Main loop.
            1. Wait for a connection
            2. Once connected, wait for commands from dronology
            3. If connection interrupted, wait for another connection again.
            4. Shut down when status is set to DEAD
        :return:
        """
        cont = True
        while cont:
            status = self.get_status()
            if status == Connection._DEAD:
                # Shut down
                cont = False
            elif status == Connection._WAITING:
                # Try to connect, timeout after 10 seconds.
                try:
                    sock = socket.create_connection((self._addr, self._port), timeout=5.0)
                    self._sock = socketutils.BufferedSocket(sock)
                    handshake = json.dumps({'type': 'connect', 'uavid': self._g_id})
                    self._sock.send(handshake)
                    self._sock.send(os.linesep)
                    self.set_status(Connection._CONNECTED)
                except socket.error as e:
                    _LOG.info('Socket error ({})'.format(e))
                    time.sleep(10.0)
            else:
                # Receive messages
                try:
                    msg = self._sock.recv_until(os.linesep, timeout=0.1)
                    cmd = command.CommandFactory.get_command(msg)
                    if isinstance(cmd, command.Command):
                        self._msgs.put_message(cmd)
                except socket.timeout:
                    pass
                except socket.error as e:
                    _LOG.warn('connection interrupted! ({})'.format(e))
                    self._sock.shutdown(socket.SHUT_RDWR)
                    self._sock.close()
                    self._sock = None
                    self.set_status(Connection._WAITING)
                    time.sleep(20.0)

        if self._sock is not None:
            _LOG.info('Shutting down socket.')
            self._sock.shutdown(socket.SHUT_WR)
            _LOG.info('Closing socket.')
            self._sock.close()
            return