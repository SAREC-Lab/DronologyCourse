import communication
import vehicle
import common
import time
import util

_LOG = util.get_logger()

m0 = communication.core.MessageQueue()
m1 = communication.core.MessageQueue()

v = vehicle.ArduCopter(m0, m1, vehicle_id='test1')

v.connect_vehicle(vehicle_type=common.DRONE_TYPE_SITL_VRTL, async=False, ardupath='/home/sean/git/ardupilot')
takeoff = communication.command.Takeoff(None, None, {'altitude': 5}, None)
v.handle_command(takeoff)
_LOG.info('TAKING OFF')
time.sleep(10.0)
goto = communication.command.GotoLocation(None, None, {'x': 41.519362, 'y': -86.240411, 'z': 5}, None)
_LOG.info('GOTO')
v.handle_command(goto)
time.sleep(10.0)
_LOG.info('LAND')
# land = communication.command.SetMode(None, None, {'mode': 'LAND'}, None)
# v.handle_command(goto)
# time.sleep(10.0)
v.stop()



