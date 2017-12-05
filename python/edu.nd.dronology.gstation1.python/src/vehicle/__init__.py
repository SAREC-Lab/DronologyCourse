import threading
import dronekit
from pymavlink import mavutil

def make_mavlink_command(cmd, trg_sys=0, trg_component=0, seq=0,
                         frame=mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT,
                         param1=0, param2=0, param3=0, param4=0,
                         lat_or_param5=0, lon_or_param6=0, alt_or_param7=0):
    """
    Make a new mavlink command.

    :param cmd:
    :param trg_sys:
    :param trg_component:
    :param seq:
    :param frame:
    :param param1:
    :param param2:
    :param param3:
    :param param4:
    :param lat_or_param5:
    :param lon_or_param6:
    :param alt_or_param7:
    :return:
    """
    cmd_args = [trg_sys, trg_component,
                seq,
                frame,
                cmd,
                0, 0,
                param1, param2, param3, param4,
                lat_or_param5, lon_or_param6, alt_or_param7]

    return dronekit.Command(*cmd_args)


class VehicleControl(object):
    def __init__(self, handshake_msg_queue, state_msg_queue, vehicle_id=None, state_interval=1.0):
        self._vehicle = None
        self._vid = vehicle_id
        self._handshake_out_msgs = handshake_msg_queue
        self._state_out_msgs = state_msg_queue
        self._state_t = state_interval
        self._state_msg_timer = None
        self._responsive = True
        self._drone_lock = threading.Lock()
        self._msg_lock = threading.Lock()
        self._connection_initiated = False
        self._connection_complete = False

    def update_state_interval(self, state_interval):
        with self._msg_lock:
            self._state_msg_timer.stop()
            self._state_msg_timer.set_interval(state_interval)
            self._state_msg_timer.start()

    def send_state_message(self):
        with self._msg_lock:
            msg = self.gen_state_message()
            self._state_out_msgs.put_message(msg)

    def gen_state_message(self):
        raise NotImplementedError

    def get_vehicle(self):
        return self._vehicle

    def get_vehicle_id(self):
        return self._vid

    def connect_vehicle(self, **kwargs):
        raise NotImplementedError

    def handle_command(self, cmd):
        raise NotImplementedError

    def stop(self):
        raise NotImplementedError

    def is_ready(self):
        return self._connection_complete
