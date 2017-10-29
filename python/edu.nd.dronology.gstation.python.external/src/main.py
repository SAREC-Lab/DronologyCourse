import os
import control
import json
import comms
import socket
import argparse
from util import get_logger

_LOG = get_logger()


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


def main(gid, addr, port):
    setup_dronology_connection(gid, addr, port)
    in_msg_queue = comms.MessageQueue()
    out_msg_queue = comms.MessageQueue()
    connection = comms.Connection(in_msg_queue, addr=addr, port=port)
    connection.start()
    ctrl_station = control.ControlStation(connection, in_msg_queue, out_msg_queue)
    ctrl_station.start()
    connection.stop()


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-gid', '--gcs_id',
                        type=str, default='default_ground_station')
    parser.add_argument('-addr', '--address',
                        type=str, default='')
    parser.add_argument('-p', '--port',
                        type=int, default=1234)
    args = parser.parse_args()
    main(args.gcs_id, args.address, args.port)
