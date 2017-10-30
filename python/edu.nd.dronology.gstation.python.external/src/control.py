import threading
from vehicle import ArduCopter
from util import get_logger
from common import *


_LOG = get_logger()


class ControlStation(object):
    def __init__(self, connection,
                 dronology_in_msg_queue,
                 dronology_handshake_out_msg_queue, dronology_state_out_msg_queue,
                 new_vehicle_queue):
        self._conn = connection
        self._v_in_msgs = new_vehicle_queue
        self._d_in_msgs = dronology_in_msg_queue
        self._d_state_out_msgs = dronology_state_out_msg_queue
        self._d_handshake_out_msgs = dronology_handshake_out_msg_queue
        self._drone_lock = threading.Lock()
        self._drones = {}
        self._status_lock = threading.Lock()
        self._v_in_worker = threading.Thread(target=self._v_in_work)
        self._d_in_worker = threading.Thread(target=self._d_in_work)
        self._d_out_worker = threading.Thread(target=self._d_out_work)
        self._n_vrtl_drones = 0

        self._do_work = False

    def start(self):
        self._do_work = True
        self._v_in_worker.start()
        self._d_in_worker.start()
        self._d_out_worker.start()

    def join(self):
        for worker in [self._v_in_worker, self._d_in_worker, self._d_out_worker]:
            worker.join()

    def stop(self):
        with self._status_lock:
            self._do_work = False

        v_ids = list(self._drones.keys())
        for v_id in v_ids:
            self._remove_vehicle(v_id)

        self.join()

    def is_alive(self):
        with self._status_lock:
            return self._do_work

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
                _LOG.debug('Command received: {}'.format(str(msg)))
                threading.Thread(target=self._handle_message, args=(msg,)).start()

            time.sleep(0.1)
            with self._status_lock:
                cont = self._do_work

    def _d_out_work(self):
        cont = True
        while cont:
            for msg in self._d_handshake_out_msgs.get_messages():
                success = self._conn.send(str(msg))
                if not success:
                    self._d_handshake_out_msgs.put_message(msg)

            for msg in self._d_state_out_msgs.get_messages():
                self._conn.send(str(msg))

            time.sleep(0.1)
            with self._status_lock:
                cont = self._do_work

    def _handle_message(self, msg):
        v_id = msg.get_target()
        is_registered = self._is_registered(v_id)
        if is_registered:
            self._drones[msg.get_target()].handle_command(msg)
        else:
            _LOG.warn('Command issued for unregistered vehicle {}!'.format(v_id))

    def register_vehicle(self, v_spec):
        threading.Thread(target=self._register_vehicle, args=(v_spec,)).start()

    def remove_vehicle(self, v_id):
        threading.Thread(target=self._remove_vehicle, args=(v_id,)).start()

    def _is_registered(self, v_id):
        with self._drone_lock:
            return v_id in self._drones

    def _register_vehicle(self, v_spec):
        vehicle = ArduCopter(self._d_handshake_out_msgs, self._d_state_out_msgs)

        vehicle_id = v_spec['vehicle_id']
        with self._drone_lock:
            instance = self._n_vrtl_drones
            if vehicle_id is None:
                if v_spec['vehicle_type'] == DRONE_TYPE_SITL_VRTL:
                    self._n_vrtl_drones += 1
                    vehicle_id = v_spec['vehicle_type'] + str(instance)
                else:
                    vehicle_id = v_spec['ip']

            if vehicle_id in self._drones:
                _LOG.error('Failed to register new vehicle, vehicle with id {} already exists!'.format(vehicle_id))

            else:
                v_spec['vehicle_id'] = vehicle_id
                vehicle.connect_vehicle(instance=instance, **v_spec)

        self._drones[vehicle_id] = vehicle

    def _remove_vehicle(self, v_id):
        registered = self._is_registered(v_id)

        if not registered:
            _LOG.error('Attempting to remove unregistered vehicle {}!'.format(v_id))
        else:
            vehicle = self._drones.pop(v_id)
            vehicle.stop()
