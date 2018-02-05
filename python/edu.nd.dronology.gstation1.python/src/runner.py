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
        # Messages received from Dronology
        self._dronology_in = communication.core.MessageQueue()
        # Handshake messages sent by ControlStation to Dronology
        self._dronology_handshake_out = communication.core.MessageQueue()
        # State messages sent by ControlSTation to Dronology
        self._dronology_state_out = communication.core.MessageQueue()
        # New vehicle messages sent by <anyone> to ControlStation.
        self._new_vehicle_in = communication.core.MessageQueue()
        self._global_cfg = util.load_json(global_cfg_path)

        if 'ardupath' not in self._global_cfg:
            _LOG.error('You must specify \"ardupath\" in the global config file at {}'.format(global_cfg_path))
            exit(1)

        self._drone_cfgs = []
        # If a JSON drone configuration path is provided, load the drone configurations.
        # These will be used to create or connect to virtual or physical drones.
        if drone_cfg_path is not None:
            self._drone_cfgs = util.load_json(drone_cfg_path)

        self._is_alive = True

    def wait(self):
        """
        Called by main to wait until the runner has completed its execution.
        :return:
        """
        while self._is_alive:
            time.sleep(5.0)

    def start(self):
        """
        Start everything up.
        :return:
        """
        # create a thread to run this program.
        threading.Thread(target=self._start).start()

    def _start(self):
        """
        This method has three responsibilities:
            1. Establish a socket connection with Dronology using communication.core.Connection.
            2. Create the control station, passing it the Dronology connection as well as the dronology_in and
               dronology_out message queues.
            3. Add all vehicles specified in the JSON drone configuration file.

        :return:
        """
        # Create the Dronology connection.
        self._connection = communication.core.Connection(self._dronology_in,
                                                         addr=self._addr, port=self._port, g_id=self._g_id)
        # Create the control station.
        self._ctrl_station = control.ControlStation(self._connection, self._dronology_in, self._dronology_handshake_out,
                                                    self._dronology_state_out, self._new_vehicle_in)

        # Connect to Dronology and begin receiving messages.
        self._connection.start()
        # Start the Control Station.
        self._ctrl_station.start()
        time.sleep(1.0)

        # Register any pre-specified vehicles.
        for dc in self._drone_cfgs:
            self.add_vehicle(dc)

    def add_vehicle(self, v_spec):
        """
        Adds a vehicle specification to the new_vehicle message queue.

        The control station is responsible for pulling messages off of this message queue and instantiating the
        actual vehicle (virtual or physical) per the vehicle specification.

        :param v_spec: specifies vehicle properties which are eventually sent to
                       vehicle.VehicleControl.connect_vehicle (dictionary)

        """
        v_spec['ardupath'] = self._global_cfg['ardupath']
        self._new_vehicle_in.put_message(v_spec)

    def remove_vehicle(self, vehicle_id):
        """
        Remove a vehicle from the control station.

        The control station is responsible for properly shutting down the vehicle.

        :param vehicle_id: The unique identifier of the vehicle to be removed (string)
        """
        if self._ctrl_station is not None:
            self._ctrl_station.remove_vehicle(vehicle_id)

    def is_vehicle_ready(self, v_id):
        """
        Determine if a connection has been made with a virtual (SITL) or physical drone.

        In other words: has vehicle.VehicleControl.connnect_vehicle been called and completed?

        :param v_id: The unique identifier of the vehicle to be removed (string)
        :return: True if the vehicle has established a connection to its virtual or physical drone.
        """
        return self._ctrl_station.is_vehicle_ready(v_id)

    def stop(self, *args):
        self._is_alive = False
        if self._ctrl_station is not None:
            self._ctrl_station.stop()
        if self._connection is not None:
            self._connection.stop()


