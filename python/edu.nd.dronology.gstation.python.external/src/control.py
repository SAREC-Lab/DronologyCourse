import time
import threading
from util import get_logger
from comms import DroneHandshakeMessage


_LOG = get_logger()


class ControlStation(object):
    def __init__(self, connection, in_msg_queue, out_msg_queue):
        self._conn = connection
        self._in_msgs = in_msg_queue
        self._out_msgs = out_msg_queue
        self._drone_lock = threading.Lock()
        self._drones = {}
        self._status_lock = threading.Lock()
        self._worker = threading.Thread(target=self._work)
        self._do_work = False
        self._register_retry_wait = 5.0

    def start(self):
        self._do_work = True
        self._worker.start()

    def stop(self):
        with self._status_lock:
            self._do_work = False

    def _work(self):
        cont = True
        while cont:
            in_msgs = self._in_msgs.get_messages()
            for msg in in_msgs:
                threading.Thread(target=self._handle_message, args=(msg,)).start()

            out_msgs = self._out_msgs.get_messages()
            for msg in out_msgs:
                threading.Thread(target=self._conn.send, args=(msg,)).start()

            time.sleep(0.1)
            with self._status_lock:
                cont = self._do_work

    def _handle_message(self, msg):
        self._drones[msg.get_target()].handle_command(msg)

    def register_vehicle(self, vehicle):
        threading.Thread(target=self._register_vehicle, args=(vehicle,)).start()

    def remove_vehicle(self, vehicle):
        threading.Thread(target=self._remove_vehicle, args=(vehicle,)).start()

    def _is_registered(self, v_id):
        with self._drone_lock:
            return v_id in self._drones

    def _register_vehicle(self, vehicle):
        v_id = vehicle.get_vehicle_id()
        registered = self._is_registered(v_id)

        if registered:
            _LOG.error('Failed to register new vehicle, vehicle with id {} already exists!'.format(v_id))
        else:
            self._drones[v_id] = vehicle
            handshake_complete = False
            while not handshake_complete:
                handshake_complete = self._conn.send(DroneHandshakeMessage.from_vehicle(vehicle))
                time.sleep(self._register_retry_wait)

    def _remove_vehicle(self, vehicle):
        v_id = vehicle.get_vehicle_id()
        registered = self._is_registered(v_id)

        if not registered:
            _LOG.error('Attempting to remove unregistered vehicle {}!'.format(v_id))
        else:
            self._drones.pop(v_id)
            vehicle.stop()
