import socket
import json
import os
import etc
from mathtools import Lla, Nvector, Pvector


def get_logger(name='default_file', p2cfg='../cfg/logging.conf'):
    return etc.get_logger(name=name, p2cfg=p2cfg)


def setup_dronology_connection(gid, addr, port):
    sock = None

    while not sock:
        sock = socket.create_connection((addr, port), timeout=5.0)

    handshake = {
        'type': 'connect',
        'uavid': gid
    }

    sock.send(json.dumps(handshake))
    sock.send(os.linesep)


def load_drone_configs(p2dcf):
    try:
        return json.load(p2dcf)
    except:
        pass