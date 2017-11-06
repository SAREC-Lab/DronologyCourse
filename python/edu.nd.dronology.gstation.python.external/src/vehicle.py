import util
import threading
import dronekit
import dronekit_sitl
from communication import message, command
from common import *
from pymavlink import mavutil
# from

_LOG = util.get_logger()



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

    def update_state_interval(self, state_interval):
        self._state_msg_timer.stop()
        self._state_msg_timer.set_interval(state_interval)
        self._state_msg_timer.start()

    def send_state_message(self):
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


class CopterControl(VehicleControl):
    def __init__(self, handshake_msg_queue, state_msg_queue, vehicle_id):
        VehicleControl.__init__(self, handshake_msg_queue, state_msg_queue, vehicle_id=vehicle_id)

    def gen_state_message(self):
        return message.StateMessage.from_vehicle(self._vehicle, self._vid)

    def connect_vehicle(self, **kwargs):
        raise NotImplementedError

    def handle_command(self, cmd):
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
            command.SetMode: self._set_mode,
            command.GotoLocation: self._goto_lla,
            command.Takeoff: self._takeoff,
            command.SetGroundSpeed: self._set_ground_speed,
            command.SetVelocity: self._set_velocity,
            command.SetHome: self._set_home_location,
            command.SetArmed: self._set_armed
        }
        self._sensors = {
            '3D_GYRO': mavutil.mavlink.MAV_SYS_STATUS_SENSOR_3D_GYRO,
            '3D_ACCEL': mavutil.mavlink.MAV_SYS_STATUS_SENSOR_3D_ACCEL
        }

    def get_location(self):
        lla = self._vehicle.location.global_frame
        return util.Lla(lla.lat, lla.lon, lla.alt)

    def handle_command(self, cmd):
        if not self._vehicle:
            _LOG.error('Vehicle {} not connected! Ignoring command.'.format(self._vid))
        elif type(cmd) not in self._cmd_handlers:
            _LOG.warn('Unrecognized command {} for {} controller!'.format(type(cmd), self.__class__))
        else:
            self._cmd_handlers[type(cmd)](cmd)

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
        self.__set_mode('STABILIZE')
        self._set_armed(armed=True)
        self.__set_mode('GUIDED')
        self._vehicle.simple_takeoff(alt)
        time.sleep(2.0)

    def _goto_lla(self, cmd):
        lat, lon, alt = cmd.get_lla().as_array()
        self.__goto_lla(lat, lon, alt)

    def __goto_lla(self, lat, lon, alt):
        self._vehicle.simple_goto(dronekit.LocationGlobalRelative(lat, lon, alt))

    def _land(self, cmd=None):
        self.__land()

    def __land(self):
        self.__set_mode('LAND')

        while abs(self._vehicle.location.global_relative_frame.alt - 1) > 1:
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
        self._vehicle.send_mavlink(self._vehicle.message_factory.command_long_encode(
            0, 0,  # target system, target component
            mavutil.mavlink.MAV_CMD_DO_SET_HOME,  # command
            0,  # confirmation
            2,  # param 1: 1 to use current position, 2 to use the entered values.
            0, 0, 0,  # params 2-4
            lat, lon, alt))
        self._vehicle.flush()

    def _set_armed(self, armed=True):
        if self._vehicle.armed != armed:
            if armed:
                while not self._vehicle.is_armable:
                    time.sleep(0.1)

            self._vehicle.armed = armed

            while self._vehicle.armed != armed:
                self._vehicle.armed = armed
                time.sleep(0.1)

    def _get_armed(self):
        return self._vehicle.armed

    def connect_vehicle(self, vehicle_type=None, vehicle_id=None, ip=None, instance=0, ardupath=ARDUPATH, rate=10,
                        home=(41.519412, -86.239830, 0, 0), baud=57600, speedup=1.0, async=True):
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
        args = vehicle_type, vehicle_id, ip, instance, ardupath, rate, home, baud, speedup
        if async:
            threading.Thread(target=self._connect_vehicle, args=args).start()
        else:
            self._connect_vehicle(*args)

    def _connect_vehicle(self, vehicle_type, vehicle_id, ip, instance, ardupath, rate, home, baud, speedup):

        status = 0
        vehicle = None

        if home is not None:
            if len(home) == 2:
                home = tuple(home) + (0, 0)
            else:
                home = tuple(home)

        if vehicle_type == DRONE_TYPE_PHYS:
            vehicle = dronekit.connect(ip, wait_ready=False, baud=baud)
            self._v_type = DRONE_TYPE_PHYS

            if vehicle_id is None:
                vehicle_id = ip

        elif vehicle_type == DRONE_TYPE_SITL_VRTL:
            vehicle_id = self._vid
            if vehicle_id is None:
                vehicle_id = vehicle_type + str(instance)

            sitl_args = [
                '-I{}'.format(str(instance)),
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

        else:
            _LOG.warn('vehicle type {} not supported!'.format(vehicle_type))
            status = -1

        while not vehicle.is_armable:
            time.sleep(1.0)

        lat, lon, alt = self.get_location()
        # Home location is GlobalRelative, so use 0 altitude. x`
        self.__set_home_location(lat, lon, 0)
        self._register_message_handlers(vehicle)

        self._vehicle = vehicle
        self._vid = vehicle_id

        if status >= 0:
            _LOG.info('Vehicle {} successfully initialized.'.format(self._vid))
            self._handshake_out_msgs.put_message(message.DroneHandshakeMessage.from_vehicle(self._vehicle, self._vid))
            self._state_msg_timer = util.etc.RepeatedTimer(self._state_t, self.send_state_message)
        else:
            _LOG.error('Vehicle {} failed to initialize.'.format(self._vid))

    def _register_message_handlers(self, vehicle):
        self._register_sys_status_handler(vehicle)

    def _register_sys_status_handler(self, vehicle):
        @vehicle.on_message('SYS_STATUS')
        def handle_sys_status(_, name, msg):
            for sid, bits in self._sensors.items():
                present = True if ((msg.onboard_control_sensors_enabled & bits) == bits) else False
                healthy = True if ((msg.onboard_control_sensors_health & bits) == bits) else False
                if not present:
                    _LOG.warn('Vehicle {} sensor {} not present!'.format(self._vid, sid))
                elif not healthy:
                    _LOG.warn('Vehicle {} sensor {} not healthy!'.format(self._vid, sid))

    def stop(self):
        if self._vehicle:
            _LOG.info('Closing vehicle {} connection.'.format(self._vid))
            self._land()
            self._set_armed(armed=False)
            self._vehicle.close()
            self._state_msg_timer.stop()
        if self._sitl:
            _LOG.info('Closing SITL connnection for vehicle {}'.format(self._vid))
            self._sitl.stop()
