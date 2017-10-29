import time
import threading
from vehicle import ArduCopter
from util import get_logger
from comms import DroneHandshakeMessage


_LOG = get_logger()


class ControlStation(object):
    def __init__(self, connection, dronology_in_msg_queue, dronology_out_msg_queue, new_vehicle_queue):
        self._conn = connection
        self._v_in_msgs = new_vehicle_queue
        self._d_in_msgs = dronology_in_msg_queue
        self._d_out_msgs = dronology_out_msg_queue
        self._drone_lock = threading.Lock()
        self._drones = {}
        self._status_lock = threading.Lock()
        self._d_in_worker = threading.Thread(target=self._d_in_work)
        self._d_out_worker = threading.Thread(target=self._d_out_work)

        self._do_work = False
        self._register_retry_wait = 5.0

    def start(self):
        self._do_work = True
        self._d_in_worker.start()
        self._d_out_worker.start()

    def stop(self):
        with self._status_lock:
            self._do_work = False

        v_ids = list(self._drones.keys())
        for v_id in v_ids:
            self._remove_vehicle(v_id)

    def _v_in_work(self):
        cont = True
        while cont:
            v_messages = self._v_in_msgs.get_messages()

            for msg in v_messages:
                self.register_vehicle(msg)

            time.sleep(1.0)
            with self._status_lock:
                cont = self._do_work

    def _d_in_work(self):
        cont = True
        while cont:
            in_msgs = self._d_in_msgs.get_messages()
            for msg in in_msgs:
                threading.Thread(target=self._handle_message, args=(msg,)).start()

            time.sleep(0.1)
            with self._status_lock:
                cont = self._do_work

    def _d_out_work(self):
        cont = True
        while cont:
            out_msgs = self._d_out_msgs.get_messages()
            for msg in out_msgs:
                self._conn.send(msg)

            time.sleep(0.1)
            with self._status_lock:
                cont = self._do_work

    def _handle_message(self, msg):
        self._drones[msg.get_target()].handle_command(msg)

    def register_vehicle(self, v_spec):
        threading.Thread(target=self._register_vehicle, args=(v_spec,)).start()

    def remove_vehicle(self, v_id):
        threading.Thread(target=self._remove_vehicle, args=(v_id,)).start()

    def _is_registered(self, v_id):
        with self._drone_lock:
            return v_id in self._drones

    def _register_vehicle(self, v_spec):
        vehicle = ArduCopter(self._d_out_msgs)
        vehicle.connect_vehicle(**v_spec)
        v_id = vehicle.get_vehicle_id()
        registered = self._is_registered(v_id)

        if registered:
            _LOG.error('Failed to register new vehicle, vehicle with id {} already exists!'.format(v_id))
        else:
            self._drones[v_id] = vehicle
            handshake_complete = False
            while not handshake_complete:
                handshake_complete = self._conn.send(DroneHandshakeMessage.from_vehicle(vehicle.get_vehicle(),
                                                                                        v_id))
                time.sleep(self._register_retry_wait)

    def _remove_vehicle(self, v_id):
        registered = self._is_registered(v_id)

        if not registered:
            _LOG.error('Attempting to remove unregistered vehicle {}!'.format(v_id))
        else:
            vehicle = self._drones.pop(v_id)
            vehicle.stop()
