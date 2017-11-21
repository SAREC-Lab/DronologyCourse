#! /usr/bin/env python

from dronekit import *
import subprocess
import time
import sys
import os
import json
import signal

is_SITL = True
ConnectionString='127.0.0.1:14550'
BaudRate=57600

# is_SITL = False
# ConnectionString='/dev/ttyUSB0'
# BaudRate=57600

def get_cur_time_str():
	return time.asctime().replace(' ','_').replace(':','-')

def get_log_filename():
	if not os.path.exists('./TestLogs'):
	    os.makedirs('./TestLogs')
	filename = './TestLogs/TestTakeoffLandTwice_Raw_NoPrearm_'+get_cur_time_str()+'.log'
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

def log_AllDroneInfo(vehicle):
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

def waitArmable(vehicle):
	log_status('waiting until armable')
	while not vehicle.is_armable:
		print "Waiting for Drone to prepare for arming..."
		time.sleep(0.5)
	log_status('armable wait complete')

def takeoffLand(vehicle,altitude):
	def setMode(mode):
		vehicle.mode = VehicleMode(mode)
		while vehicle.mode!=mode:
			vehicle.mode = VehicleMode(mode)
			time.sleep(1)
	def setArmed(armed):
		vehicle.armed = armed
		# while vehicle.armed!=armed:
		# 	vehicle.armed = armed
		# 	time.sleep(1)
	def takeoff(altitude):
		if vehicle.armed:
			setMode("LAND")
		setArmed(False)
		setMode("GUIDED")
		setArmed(True)
		vehicle.simple_takeoff(altitude)

	log_status('taking off')
	log_AllDroneInfo(vehicle)
	takeoff(altitude)
	current_alt = vehicle.location.global_relative_frame.alt
	log_status('waiting for target altitude')
	log_AllDroneInfo(vehicle)
	while current_alt<(altitude-3):
		print "Waiting for Drone to complete takeoff [alt={alt}]...".format(alt=current_alt)
		time.sleep(0.5)
		current_alt = vehicle.location.global_relative_frame.alt
	log_status('at target altitude')
	log_AllDroneInfo(vehicle)
	log_status('landing')
	setMode('LAND')
	log_status('waiting for disarm')
	log_AllDroneInfo(vehicle)
	current_alt = vehicle.location.global_relative_frame.alt
	while vehicle.armed and current_alt>1:
		print "Waiting for Drone to be close to ground [alt={alt}]...".format(alt=current_alt)
		time.sleep(0.5)
		current_alt = vehicle.location.global_relative_frame.alt
	while vehicle.armed:
		print "Waiting for Drone to be disarmed [alt={alt}]...".format(alt=current_alt)
		log_AllDroneInfo(vehicle)
		time.sleep(0.5)
		current_alt = vehicle.location.global_relative_frame.alt
	log_status('disarmed')
	log_AllDroneInfo(vehicle)

def shutdown_handler(signal,frame):
	if is_SITL:
		subprocess.call(['./DroneComm/stopSITL.sh'])

log_data(['is_SITL',str(is_SITL)])
log_data(['ConnectionString',ConnectionString])
log_data(['BaudRate',str(BaudRate)])
log_status('initializing drone')
if is_SITL:
	subprocess.call(['./DroneComm/startSITL.sh'])
vehicle = connect(ConnectionString, wait_ready=True, baud=BaudRate)
signal.signal(signal.SIGINT,shutdown_handler)
log_status('drone initialized')
log_AllDroneInfo(vehicle)
waitArmable(vehicle)
log_status('starting first takeoff')
log_AllDroneInfo(vehicle)
takeoffLand(vehicle,30)
log_status('starting second takeoff')
log_AllDroneInfo(vehicle)
takeoffLand(vehicle,30)
log_status('complete')
log_AllDroneInfo(vehicle)
vehicle.close()
shutdown_handler(None,None)
