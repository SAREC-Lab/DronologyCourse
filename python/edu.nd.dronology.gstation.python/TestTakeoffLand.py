#! /usr/bin/env python

from DroneComm import *

drone_info = [
	{
		'type':'SITL',
		'ConnectionData':{
			'inst':0,
			'home':'41.732955,-86.180886,0,0',
		},
	},
]

# drone_info = [
	# {
		# 'type':'physical',
		# 'ConnectionData':{
			# 'ConnectionString':'192.168.42.1:1234',
		# },
	# },
# ]

current_drone_id = 0
drones = {}
for drone in drone_info:
	new_drone = DroneComm(drone['type'],drone['ConnectionData'])
	drones[current_drone_id] = new_drone
	current_drone_id = current_drone_id + 1

drones[0].takeoff(20)
loc = drones[0].getLocation()
# drones[0].gotoLocation(Coordinate(41.7,-86.2,20))
drones[0].gotoLocation(loc)
drones[0].setMode('LAND')
