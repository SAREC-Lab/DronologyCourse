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

# drone_info = [
	# {
		# 'type':'physical',
		# 'ConnectionData':{
			# 'ConnectionString':'/dev/ttyUSB0',
			# 'BaudRate':57600,
		# },
	# },
# ]

drone_info = [
	{
		'type':'physical',
		'ConnectionData':{
			'ConnectionString':'127.0.0.1:14550',
		},
	},
]

current_drone_id = 0
drones = {}
for drone in drone_info:
	new_drone = DroneComm(drone['type'],drone['ConnectionData'])
	drones[current_drone_id] = new_drone
	current_drone_id = current_drone_id + 1

drones[0].setMode("GUIDED")
print " Arming motors for testing..."
drones[0].setArmed(True)

while not drones[0].getArmed():
	print " Waiting for arming..."
	drones[0].setArmed(True)
	time.sleep(1)

print " Disarming motors..."
drones[0].setArmed(False)

