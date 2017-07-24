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


def waitArmable(vehicle):
	while not vehicle.is_armable:
		print "Waiting for Drone to prepare for arming..."
		time.sleep(0.5)

def takeoffLand(vehicle,altitude):
	def setMode(mode):
		vehicle.mode = VehicleMode(mode)
		while vehicle.mode!=mode:
			vehicle.mode = VehicleMode(mode)
			time.sleep(1)
	def setArmed(armed):
		vehicle.armed = armed
		while vehicle.armed!=armed:
			vehicle.armed = armed
			time.sleep(1)
	def takeoff(altitude):
		if vehicle.armed:
			setMode("LAND")
		setArmed(False)
		setMode("GUIDED")
		setArmed(True)
		vehicle.simple_takeoff(altitude)

	takeoff(altitude)
	current_alt = vehicle.location.global_relative_frame.alt
	while current_alt<(altitude-3):
		print "Waiting for Drone to complete takeoff [alt={alt}]...".format(alt=current_alt)
		time.sleep(0.5)
		current_alt = vehicle.location.global_relative_frame.alt
	setMode('LAND')
	current_alt = vehicle.location.global_relative_frame.alt
	while vehicle.armed and current_alt>1:
		print "Waiting for Drone to be close to ground [alt={alt}]...".format(alt=current_alt)
		time.sleep(0.5)
		current_alt = vehicle.location.global_relative_frame.alt
	while vehicle.armed:
		print "Waiting for Drone to be disarmed [alt={alt}]...".format(alt=current_alt)
		time.sleep(0.5)
		current_alt = vehicle.location.global_relative_frame.alt

def shutdown_handler(signal,frame):
	if is_SITL:
		subprocess.call(['./DroneComm/stopSITL.sh'])

if is_SITL:
	subprocess.call(['./DroneComm/startSITL.sh'])
vehicle = connect(ConnectionString, wait_ready=True, baud=BaudRate)
signal.signal(signal.SIGINT,shutdown_handler)
waitArmable(vehicle)
takeoffLand(vehicle,30)
takeoffLand(vehicle,30)
vehicle.close()
shutdown_handler(None,None)
