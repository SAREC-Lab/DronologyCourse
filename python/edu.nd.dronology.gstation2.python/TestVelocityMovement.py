#! /usr/bin/env python

from DroneComm import *
from DroneComm.Coordinate import *
import time

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
# 	{
# 		'type':'physical',
# 		'ConnectionData':{
# 			'ConnectionString':'/dev/ttyUSB0',
# 			'BaudRate':57600,
# 		},
# 	},
# ]

current_drone_id = 0
drones = {}
for drone in drone_info:
	new_drone = DroneComm(drone['type'],drone['ConnectionData'])
	drones[current_drone_id] = new_drone
	current_drone_id = current_drone_id + 1

drones[0].takeoff(20)
loc = drones[0].getLocation()
print 'a'
time.sleep(20)
print 'b'
for a in range(0,20*10):
	time.sleep(0.1)
	drones[0].setVelocity(Coordinate(5,0,0))
print 'c'
for a in range(0,20*10):
	time.sleep(0.1)
	drones[0].setVelocity(Coordinate(0,5,0))
print 'd'
for a in range(0,20*10):
	time.sleep(0.1)
	drones[0].setVelocity(Coordinate(0,0,1))
print 'e'
drones[0].gotoLocation(loc)
time.sleep(20)
print 'f'
drones[0].setMode('LAND')
print 'g'

for drone_id in drones:
	drone = drones[drone_id]
	drone.shutdown_handler(None,None)
