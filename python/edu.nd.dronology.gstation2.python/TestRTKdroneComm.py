#! /usr/bin/env python

from DroneComm import *
import time
import sys
import os
import json
import signal

import serial
import re

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

def shutdown_handler(signal,frame):
	drone.shutdown_handler(signal,frame)
	# ser.close()

drone = DroneComm(drone_info)
# ser = serial.Serial('/dev/ttyACM0', 115200)
signal.signal(signal.SIGINT,shutdown_handler)
line_end_regex = re.compile('\*..\r\n')
while True:
	buf = ''
	char = ''
	# while not line_end_regex.search(buf):
	# 	char = ser.read()
	# 	buf = buf + char
	time.sleep(0.25)
	buf = '$aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa*00\r\n'
	print buf[:-2]
	# drone.sendGPS_RTCM_DATA(buf[:-2])
	drone.sendGPS_RTCM_DATA(buf)
	# drone.sendGPS_RTCM_DATA(buf[1:-2])
shutdown_handler(None,None)
