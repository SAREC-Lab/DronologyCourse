#! /usr/bin/env python

from DroneComm import *

#drone_info = [
#	{
#		'type':'SITL',
#		'ConnectionData':{
#			'inst':0,
#			'home':'41.732955,-86.180886,0,0',
#		},
#	},
#]

drone_info = [
	{
		'type':'physical',
		'ConnectionData':{
			'ConnectionString':'/dev/ttyUSB0',
			'BaudRate':57600,
		},
	},
]

#drone_info = [
#	{
#		'type':'physical',
#		'ConnectionData':{
#			'ConnectionString':'127.0.0.1:14550',
#		},
#	},
#]

current_drone_id = 0
drones = {}
for drone in drone_info:
	new_drone = DroneComm(drone['type'],drone['ConnectionData'])
	drones[current_drone_id] = new_drone
	current_drone_id = current_drone_id + 1

for drone in drones:
	drone.setMode("GUIDED")
	print " Arming motors for testing..."
	drone.setArmed(True)
	
	while not drone.getArmed():
		print " Waiting for arming..."
		drone.setArmed(True)
		time.sleep(1)
	
	print " Disarming motors..."
	drone.setArmed(False)

