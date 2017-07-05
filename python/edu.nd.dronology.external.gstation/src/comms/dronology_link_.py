import threading
import socket
import os
import Queue


class DronologyLink:
    def __init__(self, host='127.0.0.1', port=1234):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        self.accept_worker = None
        self.recv_worker = None
        self.send_worker = None

        self.out_queue = Queue.Queue()

        self.is_alive = True
        self.is_connected = False

        self.client = None

    def start(self):
        self.recv_worker = threading.Thread(target=self._receive).start()
        self.send_worker = threading.Thread(target=self._send).start()
        self.accept_worker = threading.Thread(target=self._accept).start()

    def stop(self):
        self.out_queue.join()

        self.sock.close()
        del self.sock
        self.client.close()
        del self.client

        self.is_alive = False
        self.accept_worker.join()
        self.recv_worker.join()
        self.send_worker.join()

    def send(self, msg):
        self.out_queue.put(msg)

    def _accept(self):
        self.sock.bind((self.host, self.port))
        self.sock.listen(0)
        client, _ = self.sock.accept()
        self.client = client

    def _receive(self):
        msg_buffer = ""
        while self.is_alive:
            if self.client is not None:
                msg = self.client.recv(1024)
                if os.linesep in msg:
                    msg_end, msg = msg.split(os.linesep)
                    msg_buffer += msg_end
                    # TODO: send this message to groundstation

                    # now, clear the buffer
                    msg_buffer = ""

                msg_buffer += msg

    def _send(self):
        while self.is_alive:
            if self.client is not None:
                msg = self.out_queue.get()
                self.client.send(msg)
                self.client.send(os.linesep)

