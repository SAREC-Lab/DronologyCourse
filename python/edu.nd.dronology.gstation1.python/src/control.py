import threading
from vehicle.copter import ArduCopter
from util import get_logger
from common import *


_LOG = get_logger()


class ControlStation(object):
    """
    ControlStation is primarily responsible for message marshalling. It uses four message queues and three worker
    threads:
     1. _v_in_worker monitors the _v_in_msg queue. Whenever a new message is added, this worker attempts to register
        a new vehicle per the vehicle specification provided in the message.
     2. _d_in_worker monitors the _d_in_msg queue. Messages in this queue represent commands issued by Dronology for
        a specific drone, i.e., "drone0 takeoff to 10m". Whenever a message is added, this worker ensures that
        the command is dispatched to the appropriate drone.
     3. _d_out_worker monitors the _d_state_out and _d_handshake_out msg queues. Messages in these queues represent
        information from all drones that is meant to be relayed back to Dronology. Whenever a message is added to either
        of these queues, this worker forwards the message to dronology via the Connection class (self._conn).

    """
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
        """
        Start everything up.

        """
        self._do_work = True
        self._v_in_worker.start()
        self._d_in_worker.start()
        self._d_out_worker.start()

    def join(self):
        """
        Join all the workers.

        """
        for worker in [self._v_in_worker, self._d_in_worker, self._d_out_worker]:
            worker.join()

    def stop(self):
        """
        Stop everything.

        1. Shut down all worker threads by setting _do_work to False.
        2. Iterate through all registered vehicles and remove them.
        3. Ensure all workers have stopped.

        """
        with self._status_lock:
            # Stop workers
            self._do_work = False

        v_ids = list(self._drones.keys())
        for v_id in v_ids:
            # Remove and shutdown vehicles
            self._remove_vehicle(v_id)

        # wait for workers to finish
        self.join()

    def is_alive(self):
        """
        Check to see if the ControlStation is still alive (i.e., operating).
        :return: True if the ControlStation hasn't been stopped else False
        """
        with self._status_lock:
            return self._do_work

    def is_vehicle_ready(self, v_id):
        """
        Determine if a vehicle is ready, i.e., has VehicleControl.connect_vehicle been called?
        :param v_id: the unique vehicle id (string)
        :return: True if the vehicle is ready
        """
        ready = False
        if v_id in self._drones:
            ready = self._drones[v_id].is_ready()

        return ready

    def _v_in_work(self):
        """
        This is the vehicle_in_worker thread. It is responsible for ensuring that new vehicles are properly registered.

        Each message in the _v_in_msg queue represents a vehicle specification (i.e., the vehicle type and other
        relevant parameters). Whenever a new message is placed on this queue, the worker registers the vehicle
        with the ControlStation by calling self.register_vehicle

        """
        cont = True
        while cont:
            v_messages = self._v_in_msgs.get_messages()

            for msg in v_messages:
                self.register_vehicle(msg)
                time.sleep(2.0)

            time.sleep(1.0)
            with self._status_lock:
                cont = self._do_work

    def _d_in_work(self):
        """
        This is the dronology_in_worker thread. It is responsible for processing all commands received by Dronology.

        Whenever a message is placed on the _d_in_msg queue, this worker spins up a new thread to handle the command
        by calling self._handle_message. A new thread is created because, ultimately, the specific vehicle is
        responsible for handling the command, and it is not known whether or not the vehicle will return immediately.

        """
        cont = True
        while cont:
            in_msgs = self._d_in_msgs.get_messages()
            for msg in in_msgs:
                _LOG.debug('Command received: {}'.format(str(msg)))
                # Handle the command
                threading.Thread(target=self._handle_message, args=(msg,)).start()

            time.sleep(0.1)
            with self._status_lock:
                cont = self._do_work

    def _d_out_work(self):
        """
        This is the dronology out worker thread. It is responsible for sending information about all the vehicles to
        Dronology.

        Whenever a message is placed on either _d_handshake_out or _d_state_out, this worker will forward the message
        to Dronology via the communication.core.Connection class (self._conn).

        """
        cont = True
        while cont:
            # Take care of handshake messages FIRST.
            for msg in self._d_handshake_out_msgs.get_messages():
                success = self._conn.send(str(msg))
                # if the message fails to send for some reason, put it back on the queue (i.e., try again next time)
                if not success:
                    self._d_handshake_out_msgs.put_message(msg)

            # now take care of state messages
            for msg in self._d_state_out_msgs.get_messages():
                self._conn.send(str(msg))

            time.sleep(0.1)
            with self._status_lock:
                cont = self._do_work

    def _handle_message(self, msg):
        """
        Dispatch the command to the intended UAV.
        :param msg: The command sent by Dronology
        """
        if self._do_work:
            # get the vehicle_id from the command
            v_id = msg.get_target()
            # determine if the intended vehicle is actually registered
            is_registered = self._is_registered(v_id)
            if is_registered:
                # if registered, dispatch the command to the appropriate vehicle.
                self._drones[msg.get_target()].handle_command(msg)
            else:
                _LOG.warn('Command issued for unregistered vehicle {}!'.format(v_id))

    def register_vehicle(self, v_spec):
        """
        Register a new vehicle with the control station
        :param v_spec: the vehicle type and any other kwarg specified in vehicle.connect_vehicle (dictionary)
        """
        if self._do_work:
            threading.Thread(target=self._register_vehicle, args=(v_spec,)).start()

    def remove_vehicle(self, v_id):
        """
        Remove a vehicle from the control station.
        :param v_id: The unique vehicle id (string)
        """
        threading.Thread(target=self._remove_vehicle, args=(v_id,)).start()

    def _is_registered(self, v_id):
        """
        Determine if the vehicle is registered with the control station
        :param v_id: The unique vehicle id (string)
        :return: True if the vehicle has been registered.
        """
        with self._drone_lock:
            return v_id in self._drones

    def _register_vehicle(self, v_spec):
        """
        Register a vehicle with the control station.
        :param v_spec: the vehicle type and any other kwarg specified in vehicle.connect_vehicle (dictionary)
        """

        # TODO: this should be made a bit more general, i.e., the type (ArduCopter) should be specified in the v_spec.
        vehicle = ArduCopter(self._d_handshake_out_msgs, self._d_state_out_msgs)

        vehicle_id = v_spec['vehicle_id']
        with self._drone_lock:
            instance = self._n_vrtl_drones

            # if a vehicle_id is not provided, we need to generate one.
            if vehicle_id is None:
                # if its a virtual drone, we'll call it "VRTLx" where x is the number of registered virtual drones.
                if v_spec['vehicle_type'] == DRONE_TYPE_SITL_VRTL:
                    self._n_vrtl_drones += 1
                    vehicle_id = v_spec['vehicle_type'] + str(instance)
                # otherwise we will use the IP (i.e., /dev/ttyUSB0)
                else:
                    vehicle_id = v_spec['ip']
            else:
                # if its a virtual drone we need to increment the counter
                if v_spec['vehicle_type'] == DRONE_TYPE_SITL_VRTL:
                    self._n_vrtl_drones += 1

            # make sure we don't have duplicate vehicle ids
            if vehicle_id in self._drones:
                _LOG.error('Failed to register new vehicle, vehicle with id {} already exists!'.format(vehicle_id))

            # otherwise, connect to the virtual or physical drone.
            else:
                v_spec['vehicle_id'] = vehicle_id
                vehicle.connect_vehicle(instance=instance, **v_spec)

        self._drones[vehicle_id] = vehicle

    def _remove_vehicle(self, v_id):
        """
        Remove a vehicle from the control station and ensure that it is properly shutdown.
        :param v_id: The unique vehicle id (string)
        """

        # determine if the vehicle is actually registered
        registered = self._is_registered(v_id)

        if not registered:
            _LOG.error('Attempting to remove unregistered vehicle {}!'.format(v_id))
        else:
            with self._drone_lock:
                vehicle = self._drones.pop(v_id)
                # ensure that the vehicle is properly shut down.
                vehicle.stop()
