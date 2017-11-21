#! /usr/bin/env python

from DroneComm import *
import time
import sys
import os
import json
import signal

import serial
import re

from RTKinject import GPSProvider

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

drone = DroneComm(drone_info)
rtk = GPSProvider.GPSProvider('/dev/ttyACM0',115200)
looping = True
def shutdown_handler(signal,frame):
	global looping
	looping = False
	rtk.shutdown_handler(signal,frame)
	drone.shutdown_handler(signal,frame)

signal.signal(signal.SIGINT,shutdown_handler)

def printMessage(message):
	print message

def sendMessage(message):
	drone.sendGPS_RTCM_DATA(message)

# rtk.registerNewCallback(printMessage)
rtk.registerNewCallback(sendMessage)
rtk.run_async()

while looping:
	print drone.getGpsInfo()
	time.sleep(0.5)

# shutdown_handler(None,None)
