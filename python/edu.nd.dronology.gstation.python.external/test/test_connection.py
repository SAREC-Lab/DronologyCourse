import unittest
import socket
import json
import core
import os
import time
import util

_LOG = util.get_logger()


class TestConnection(unittest.TestCase):
    def setUp(self):
        self.host = core.Host()
        self.host.start()
        time.sleep(2)

        self.client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.client.connect(('127.0.0.1', 1234))

    def test_receive(self):
        cmd = {
            "command": "setMonitorFrequency",
            "uavid": "PHYS_0",
            "msgid": 1234,
            "sendtimestamp": 1500310150000,
            "data": {
                "frequency": 5000
            }
        }
        self.client.send(json.dumps(cmd))
        self.client.send(os.linesep)
        time.sleep(3)

        recvd_cmds = core.get_commands('PHYS_0')
        _LOG.info(recvd_cmds)
        self.assertEqual(1, len(recvd_cmds))
        self.assertEqual(1234, recvd_cmds[0].get_msg_id())

    def test_stop(self):
        self.host.stop()
        time.sleep(1)
        self.client.shutdown(socket.SHUT_RDWR)
        self.client.close()


if __name__ == '__main__':
    unittest.main()