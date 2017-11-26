#! /usr/bin/env python

from DroneComm import *

drones = [
	{
		'type':'SITL',
		'ConnectionData':{
			'inst':0,
			'home':'41.732955,-86.180886,0,0',
		},
	},
]

# drones = [
	# {
		# 'type':'physical',
		# 'ConnectionData':{
			# 'ConnectionString':'192.168.42.1:1234',
		# },
	# },
# ]

current_drone_id = 0
drones = {}
for drone in drones:
	new_drone = DroneComm(drone['type'],drone['ConnectionData'])
	drones[current_drone_id] = new_drone
	current_drone_id = current_drone_id + 1

drones[0].takeoff(20)
loc = drones[0].getLocation()
drones[0].gotoLocation(Coordinate(loc.toDict(){'x'},loc.toDict(){'y'}+0.00001,loc.toDict(){'z'}))
drones[0].gotoLocation(Coordinate(loc.toDict(){'x'}+0.00001,loc.toDict(){'y'}+0.00001,loc.toDict(){'z'}))
drones[0].gotoLocation(Coordinate(loc.toDict(){'x'}+0.00001,loc.toDict(){'y'},loc.toDict(){'z'}))
drones[0].gotoLocation(loc)
drones[0].setMode('LAND')
