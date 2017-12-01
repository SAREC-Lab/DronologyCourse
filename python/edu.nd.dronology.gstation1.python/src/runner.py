import control
import communication
import argparse
import util
import signal
import time
import threading

_LOG = util.get_logger()


class GCSRunner:
    def __init__(self, g_id, addr, port, global_cfg_path, drone_cfg_path=None):
        self._g_id = g_id
        self._addr = addr
        self._port = port
        self._connection = None
        self._ctrl_station = None
        self._dronology_in = communication.core.MessageQueue()
        self._dronology_handshake_out = communication.core.MessageQueue()
        self._dronology_state_out = communication.core.MessageQueue()
        self._new_vehicle_in = communication.core.MessageQueue()
        self._global_cfg = util.load_json(global_cfg_path)

        if 'ardupath' not in self._global_cfg:
            _LOG.error('You must specify \"ardupath\" in the global config file at {}'.format(global_cfg_path))
            exit(1)

        self._drone_cfgs = {}
        if drone_cfg_path is not None:
            self._drone_cfgs = util.load_json(drone_cfg_path)

        self._is_alive = True

    def wait(self):
        while self._is_alive:
            time.sleep(5.0)

    def start(self):
        threading.Thread(target=self._start).start()

    def _start(self):
        self._connection = communication.core.Connection(self._dronology_in,
                                                         addr=self._addr, port=self._port, g_id=self._g_id)
        self._ctrl_station = control.ControlStation(self._connection, self._dronology_in, self._dronology_handshake_out,
                                                    self._dronology_state_out, self._new_vehicle_in)

        self._connection.start()
        self._ctrl_station.start()
        time.sleep(1.0)

        for dc in self._drone_cfgs:
            self.add_vehicle(dc)

    def add_vehicle(self, v_spec):
        """

        :param v_spec:
        :return:
        """
        v_spec['ardupath'] = self._global_cfg['ardupath']
        self._new_vehicle_in.put_message(v_spec)

    def remove_vehicle(self, vehicle_id):
        if self._ctrl_station is not None:
            self._ctrl_station.remove_vehicle(vehicle_id)

    def is_vehicle_ready(self, v_id):
        return self._ctrl_station.is_vehicle_ready(v_id)

    def stop(self, *args):
        self._is_alive = False
        if self._ctrl_station is not None:
            self._ctrl_station.stop()
        if self._connection is not None:
            self._connection.stop()


