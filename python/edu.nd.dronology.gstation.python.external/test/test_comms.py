import os
import json
import time
import communication
import socket
import unittest
import threading


cmd = {
    "command": "setMonitorFrequency",
    "uavid": "PHYS_0",
    "msgid": 1234,
    "sendtimestamp": 1500310150000,
    "data": {
        "frequency": 5000
    }
}


class TestComms(unittest.TestCase):
    def setUp(self):
        self.addr = ''
        self.port = 1234

    def setup_server(self):
        server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        server.settimeout(10.0)
        server.bind((self.addr, self.port))
        server.listen(0)

        return server

    def testConnect(self):
        mq = communication.MessageQueue()
        client = communication.Connection(mq)
        server = self.setup_server()

        def t_server():
            conn, addr = server.accept()

            for i in range(10):
                conn.send(json.dumps(cmd))
                conn.send(os.linesep)
                time.sleep(0.1)

        threading.Thread(target=t_server).start()
        client.start()
        time.sleep(2)
        msgs = client.get_messages('PHYS_0')
        client.stop()
        self.assertEqual(10, len(msgs))


if __name__ == '__main__':
    unittest.main()




