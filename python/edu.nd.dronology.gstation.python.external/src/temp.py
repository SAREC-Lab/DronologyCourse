import communication
import vehicle
import common

m0 = communication.core.MessageQueue()
m1 = communication.core.MessageQueue()

v = vehicle.ArduCopter(m0, m1, vehicle_id='test1')

v.connect_vehicle(vehicle_type=common.DRONE_TYPE_SITL_VRTL, async=False)
cmd = communication.command.SetArmed('test1', None, {'armed': True}, None)
v.handle_command(cmd)



