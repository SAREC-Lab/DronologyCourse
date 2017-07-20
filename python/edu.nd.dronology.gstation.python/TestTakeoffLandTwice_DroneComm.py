#! /usr/bin/env python

from DroneComm import *
import time
import sys
import os
import json
import signal

drone_info = {
	'type':'SITL',
	'ConnectionData':{
		'inst':0,
		'home':'41.732955,-86.180886,0,0',
	},
}

# drone_info = {
# 	'type':'physical',
# 	'ConnectionData':{
# 		'ConnectionString':'/dev/ttyUSB0',
# 		'BaudRate':57600,
# 	},
# }

def get_cur_time_str():
	return time.asctime().replace(' ','_').replace(':','-')

def get_log_filename():
	if not os.path.exists('./TestLogs'):
	    os.makedirs('./TestLogs')
	filename = './TestLogs/TestTakeoffLandTwice_DroneComm_'+get_cur_time_str()+'.log'
	with open(filename,mode='w') as log_file:
		log_file.write('')
	return filename

log_filename = get_log_filename()
def log_data(items):
	out_str = get_cur_time_str()
	for item in items:
		out_str = out_str+'\t'+item
	with open(log_filename,mode='a') as log_file:
		log_file.write(out_str+'\n')
	print 'LOG: '+out_str[:150]

def log_AllDroneInfo(drone):
	out = ['InfoDict']
	InfoDict = drone.getInfoDict()
	for param in InfoDict:
		out.append(param)
		out.append(str(InfoDict[param]))
	log_data(out)
	drone_obj = drone.Drone
	if drone.type=='SITL':
		drone_obj = drone.Drone.phys_drone
	vehicle = drone_obj.vehicle
	out = [
		'vehicle_attributes',
		'Autopilot Firmware version', str(vehicle.version),
		'Autopilot capabilities (supports ftp)', str(vehicle.capabilities.ftp),
		'Global Location', str(vehicle.location.global_frame),
		'Global Location (relative altitude)', str(vehicle.location.global_relative_frame),
		'Local Location', str(vehicle.location.local_frame),
		'Attitude', str(vehicle.attitude),
		'Velocity', str(vehicle.velocity),
		'GPS', str(vehicle.gps_0),
		'Groundspeed', str(vehicle.groundspeed),
		'Airspeed', str(vehicle.airspeed),
		'Gimbal status', str(vehicle.gimbal),
		'Battery', str(vehicle.battery),
		'EKF OK?', str(vehicle.ekf_ok),
		'Last Heartbeat', str(vehicle.last_heartbeat),
		'Rangefinder', str(vehicle.rangefinder),
		'Rangefinder distance', str(vehicle.rangefinder.distance),
		'Rangefinder voltage', str(vehicle.rangefinder.voltage),
		'Heading', str(vehicle.heading),
		'Is Armable?', str(vehicle.is_armable),
		'System status', str(vehicle.system_status.state),
		'Mode', str(vehicle.mode.name),
		'Armed', str(vehicle.armed),
	]
	log_data(out)
	out = ['vehicle.parameters']
	for param in vehicle.parameters:
		out.append(param)
		out.append(str(vehicle.parameters[param]))
	log_data(out)

def log_status(status):
	log_data(['status',status])

def waitArmable(drone):
	log_status('waiting until armable')
	while not drone.getArmable():
		print "Waiting for Drone to prepare for arming..."
		time.sleep(0.5)
	log_status('armable wait complete')

def takeoffLand(drone,altitude):
	log_status('taking off')
	log_AllDroneInfo(drone)
	drone.takeoff(altitude)
	current_alt = drone.getLocation().getZ()
	log_status('waiting for target altitude')
	log_AllDroneInfo(drone)
	while current_alt<(altitude-3):
		print "Waiting for Drone to complete takeoff [alt={alt}]...".format(alt=current_alt)
		time.sleep(0.5)
		current_alt = drone.getLocation().getZ()
	log_status('at target altitude')
	log_AllDroneInfo(drone)
	log_status('landing')
	drone.setMode('LAND')
	log_status('waiting for disarm')
	log_AllDroneInfo(drone)
	current_alt = drone.getLocation().getZ()
	while drone.getArmed() and current_alt>1:
		print "Waiting for Drone to be close to ground [alt={alt}]...".format(alt=current_alt)
		time.sleep(0.5)
		current_alt = drone.getLocation().getZ()
	while drone.getArmed():
		print "Waiting for Drone to be disarmed [alt={alt}]...".format(alt=current_alt)
		log_AllDroneInfo(drone)
		time.sleep(0.5)
		current_alt = drone.getLocation().getZ()
	log_status('disarmed')
	log_AllDroneInfo(drone)

log_data(['drone_info',json.dumps(drone_info)])
log_status('initializing drone')
drone = DroneComm(drone_info)
signal.signal(signal.SIGINT,drone.shutdown_handler)
log_status('drone initialized')
log_AllDroneInfo(drone)
waitArmable(drone)
log_status('starting first takeoff')
log_AllDroneInfo(drone)
takeoffLand(drone,30)
log_status('starting second takeoff')
log_AllDroneInfo(drone)
takeoffLand(drone,30)
log_status('complete')
log_AllDroneInfo(drone)
drone.shutdown_handler(None,None)
