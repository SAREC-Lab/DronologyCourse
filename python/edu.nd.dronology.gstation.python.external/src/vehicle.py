import threading
import dronekit
import dronekit_sitl
from common import *
from pymavlink import mavutil
from communication import *
from util import mathtools as mu
from util import get_logger
from util.etc import RepeatedTimer


_LOG = get_logger()


def make_mavlink_command(command, trg_sys=0, trg_component=0, seq=0,
                         frame=mavutil.mavlink.MAV_FRAME_GLOBAL_RELATIVE_ALT,
                         param1=0, param2=0, param3=0, param4=0,
                         lat_or_param5=0, lon_or_param6=0, alt_or_param7=0):
    """
    Make a new mavlink command.

    :param command:
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
                command,
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

    def update_state_interval(self, state_interval):
        self._state_msg_timer.stop()
        self._state_msg_timer.set_interval(state_interval)
        self._state_msg_timer.start()

    def send_state_message(self):
        self._state_out_msgs.put_message(self.gen_state_message())

    def gen_state_message(self):
        raise NotImplementedError

    def get_vehicle(self):
        return self._vehicle

    def get_vehicle_id(self):
        return self._vid

    def connect_vehicle(self, **kwargs):
        raise NotImplementedError

    def handle_command(self, command):
        raise NotImplementedError

    def stop(self):
        raise NotImplementedError


class CopterControl(VehicleControl):
    def __init__(self, handshake_msg_queue, state_msg_queue, vehicle_id):
        VehicleControl.__init__(self, handshake_msg_queue, state_msg_queue, vehicle_id=vehicle_id)

    def gen_state_message(self):
        return StateMessage.from_vehicle(self._vehicle, self._vid)

    def connect_vehicle(self, **kwargs):
        raise NotImplementedError

    def handle_command(self, command):
        raise NotImplementedError

    def stop(self):
        raise NotImplementedError

    def _set_mode(self, cmd):
        raise NotImplementedError

    def _takeoff(self, cmd):
        raise NotImplementedError

    def _goto_lla(self, cmd):
        raise NotImplementedError

    def _land(self, cmd):
        raise NotImplementedError

    def _set_ground_speed(self, cmd):
        raise NotImplementedError

    def _set_velocity(self, cmd):
        raise NotImplementedError

    def _set_home_location(self, cmd):
        raise NotImplementedError

    def _set_armed(self, cmd=None):
        raise NotImplementedError


class ArduCopter(CopterControl):
    def __init__(self, handshake_msg_queue, state_msg_queue, vehicle_id=None):
        CopterControl.__init__(self, handshake_msg_queue, state_msg_queue, vehicle_id=vehicle_id)
        self._v_type = None
        self._sitl = None
        self._cmd_handlers = {
            SetMode: self._set_mode,
            GotoLocation: self._goto_lla,
            Takeoff: self._takeoff,
            SetGroundSpeed: self._set_ground_speed,
            SetVelocity: self._set_velocity,
            SetHome: self._set_home_location
        }

    def get_location(self):
        lla = self._vehicle.location.global_frame
        return mu.Lla(lla.lat, lla.lon, lla.alt)

    def handle_command(self, command):
        if not self._vehicle:
            _LOG.error('Vehicle {} not connected! Ignoring command.'.format(self._vid))
        elif type(command) not in self._cmd_handlers:
            _LOG.warn('Unrecognized command {} for {} controller!'.format(type(command), self.__class__))
        else:
            self._cmd_handlers[type(command)](command)

    def _set_mode(self, cmd):
        self.__set_mode(cmd.get_mode())

    def __set_mode(self, mode):
        mode_ = self._vehicle.mode.name

        while mode_ != mode:
            mode_ = self._vehicle.mode.name
            self._vehicle.mode = dronekit.VehicleMode(mode)

    def _takeoff(self, cmd):
        self.__takeoff(cmd.get_altitude())

    def __takeoff(self, alt):
        self._vehicle.mode = dronekit.VehicleMode('GUIDED')
        self._set_armed(armed=True)
        self._vehicle.simple_takeoff(alt)

    def _goto_lla(self, cmd):
        lat, lon, alt = cmd.get_lla().as_array()
        self.__goto_lla(lat, lon, alt)

    def __goto_lla(self, lat, lon, alt):
        self._vehicle.simple_goto(dronekit.LocationGlobal(lat, lon, alt))

    def _land(self, cmd=None):
        self.__land()

    def __land(self):
        self.__set_mode('LAND')

        while abs(self._vehicle.location.global_frame.alt - 1) > 1:
            time.sleep(2)

    def _set_ground_speed(self, cmd):
        self.__set_ground_speed(cmd.get_speed())

    def __set_ground_speed(self, speed):
        msg = self._vehicle.message_factory.command_long_encode(
            0, 0,  # target system, target component
            mavutil.mavlink.MAV_CMD_DO_CHANGE_SPEED,  # command
            0,  # confirmation
            0,  # param 1
            speed,  # speed in metres/second
            0, 0, 0, 0, 0  # param 3 - 7
        )

        # send command to vehicle
        self._vehicle.send_mavlink(msg)
        self._vehicle.flush()

    def _set_velocity(self, cmd):
        self.__set_velocity(*cmd.get_ned())

    def __set_velocity(self, north, east, down):
        msg = self._vehicle.message_factory.set_position_target_local_ned_encode(
            0,  # time_boot_ms (not used)
            0, 0,  # target system, target component
            mavutil.mavlink.MAV_FRAME_BODY_NED,  # frame
            0b0000111111000111,  # type_mask (only speeds enabled)
            0, 0, 0,  # x, y, z positions (not used)
            north, east, down,  # x, y, z velocity in m/s
            0, 0, 0,  # x, y, z acceleration (not supported yet, ignored in GCS_Mavlink)
            0, 0)  # yaw, yaw_rate (not supported yet, ignored in GCS_Mavlink)
        # send command to vehicle
        self._vehicle.send_mavlink(msg)
        self._vehicle.flush()

    def _set_home_location(self, cmd):
        self.__set_home_location(*cmd.get_lla().as_array())

    def __set_home_location(self, lat, lon, alt):
        self._vehicle.home_location = dronekit.LocationGlobal(lat, lon, alt)

    def _set_armed(self, armed=True):
        if self._vehicle.armed != armed:
            if armed:
                while not self._vehicle.is_armable:
                    time.sleep(1)

            self._vehicle.armed = armed

            while self._vehicle.armed != armed:
                self._vehicle.armed = armed
                time.sleep(1)

    def _get_armed(self):
        return self._vehicle.armed

    def connect_vehicle(self, vehicle_type=None, vehicle_id=None, ip=None, instance=0, ardupath=ARDUPATH, rate=10,
                        home=(41.732955, -86.180886, 0, 0), baud=115200, speedup=1.0):
        """
        Connect to a SITL vehicle or a real vehicle.

        :param vehicle_type:
        :param vehicle_id:
        :param ip:
        :param instance:
        :param ardupath:
        :param speed:
        :param rate:
        :param home:
        :param baud:
        :return:
        """
        threading.Thread(target=self._connect_vehicle, args=(vehicle_type, vehicle_id, ip, instance, ardupath,
                                                             rate, home, baud, speedup)).start()

    def _connect_vehicle(self, vehicle_type, vehicle_id, ip, instance, ardupath, rate, home, baud, speedup):

        status = 0
        vehicle = None

        if home is not None:
            if len(home) == 2:
                home = tuple(home) + (0, 0)
            else:
                home = tuple(home)

        if vehicle_type == DRONE_TYPE_PHYS:
            vehicle = dronekit.connect(ip, wait_ready=True, baud=baud)
            self._v_type = DRONE_TYPE_PHYS

            if vehicle_id is None:
                vehicle_id = ip

        elif vehicle_type == DRONE_TYPE_SITL_VRTL:
            sitl_args = [
                '--instance', str(instance),
                '--model', '+',
                '--home', ','.join(map(str, home)),
                '--rate', str(rate),
                '--speedup', str(speedup),
                '--defaults', os.path.join(ardupath, 'Tools', 'autotest', 'default_params', 'copter.parm')
            ]
            _LOG.debug('Trying to launch SITL instance {} on tcp:127.0.0.1:{}'.format(instance, 5760 + instance * 10))
            sitl = dronekit_sitl.SITL(path=os.path.join(ardupath, 'build', 'sitl', 'bin', 'arducopter'))
            sitl.launch(sitl_args, await_ready=True)
            tcp, ip, port = sitl.connection_string().split(':')
            port = str(int(port) + instance * 10)
            conn_string = ':'.join([tcp, ip, port])
            _LOG.debug('SITL instance {} launched on: {}'.format(instance, conn_string))
            vehicle = dronekit.connect(conn_string, wait_ready=True, baud=baud)
            _LOG.info('Vehicle {} connected on {}'.format(vehicle_id, conn_string))
            self._v_type = DRONE_TYPE_SITL_VRTL
            self._sitl = sitl

            if vehicle_id is None:
                vehicle_id = vehicle_type + str(instance)

        else:
            _LOG.warn('vehicle type {} not supported!'.format(vehicle_type))
            status = -1

        self._vehicle = vehicle
        self._vid = vehicle_id

        init_complete = False
        while not init_complete:
            lat, lon, alt = self.get_location()
            if alt == alt:
                self._vehicle.send_mavlink(self._vehicle.message_factory.command_long_encode(
                    0, 0,  # target system, target component
                    mavutil.mavlink.MAV_CMD_DO_SET_HOME,  # command
                    0,  # confirmation
                    1,  # param 1: 1 to use current position, 2 to use the entered values.
                    0, 0, 0,  # params 2-4
                    0, 0, 0))
                self._vehicle.flush()
                init_complete = True

        if status >= 0:
            self._handshake_out_msgs.put_message(DroneHandshakeMessage.from_vehicle(self._vehicle, self._vid))
            self._state_msg_timer = RepeatedTimer(self._state_t, self.send_state_message)

    def stop(self):
        if self._vehicle:
            _LOG.info('Closing vehicle {} connection.'.format(self._vid))

            self._set_armed(armed=True)
            self.__set_mode('GUIDED')
            self._land()
            self._set_armed(armed=False)
            self._vehicle.close()
        if self._sitl:
            _LOG.info('Closing SITL connnection for vehicle {}'.format(self._vid))
            self._sitl.stop()
