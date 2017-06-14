#! /usr/bin/env python

from dronekit import *
from Coordinate import *

class PhysicalDrone(object):
	def __init__(self,ConnectionData):
		if 'BaudRate' in ConnectionData:
			self.vehicle = connect(ConnectionData['ConnectionString'], wait_ready=True, baud=ConnectionData['BaudRate'])
		else:
			self.vehicle = connect(ConnectionData['ConnectionString'], wait_ready=True)
		print "connected to "+ConnectionData['ConnectionString']
	
	def gotoLocation(self,location):
		# TODO this doesn't belong here
		# also unsafe max speed
		# self.setGroundspeed(500);
		
		self.vehicle.simple_goto(location.toGlobalRelative())
	
	def takeoff(self,altitude):
		self.setMode("GUIDED")
		print " Arming motors for takeoff..."
		self.setArmed(True)
		
		while not self.getArmed():
			print " Waiting for arming..."
			self.setArmed(True)
			time.sleep(1)
		
		
		
		print " Taking off..."
		
		self.vehicle.simple_takeoff(altitude)
	
	def getLocation(self):
		return CoordFromLocation(self.vehicle.location.global_relative_frame)
	
	def getAttitude(self):
		return CoordFromRotation(self.vehicle.attitude)
	
	def getVelocity(self):
		return CoordFromList(self.vehicle.velocity)
	
	def getGimbalRotation(self):
		return CoordFromRotation(self.vehicle.gimbal)
	
	def setGimbalRotation(self,rotation):
		self.vehicle.gimbal.rotate(rotation.getX(),rotation.getY(),rotation.getZ())
	
	def setGimbalTarget(self,target):
		self.vehicle.gimbal.target_location(target.toGlobalRelative())
	
	def getBattery(self):
		batt = self.vehicle.battery
		return {
			'voltage'	: batt.voltage,
			'current'	: batt.current,
			'level'		: batt.level,
		}
	
	def getHome(self):
		return CoordFromLocation(self.vehicle.home_location)
	
	def setHome(self,home):
		self.vehicle.home_location = home.toGlobal()
	
	def getStatus(self):
		return self.vehicle.system_status.state
	
	def getHeading(self):
		return self.vehicle.heading
	
	def setHeading(self,heading):
		# unknown how to perform this function! Will add later if functionality discovered.
		return
	
	def getArmable(self):
		return self.vehicle.is_armable
	
	def getAirspeed(self):
		return self.vehicle.airspeed
	
	def setAirspeed(self,speed):
		self.vehicle.airspeed = speed
	
	def getGroundspeed(self):
		return self.vehicle.groundspeed
	
	def setGroundspeed(self,speed):
		self.vehicle.groundspeed = speed
	
	def getArmed(self):
		return self.vehicle.armed
	
	def setArmed(self,armed):
		self.vehicle.armed = armed
	
	def getMode(self):
		return self.vehicle.mode.name
	
	def setMode(self,mode):
		print " Changing mode to \""+mode+"\"..."
		self.vehicle.mode = VehicleMode(mode)
		while self.getMode()!=mode:
			print " Waiting for mode change to \""+mode+"\"..."
			self.vehicle.mode = VehicleMode(mode)
			time.sleep(1)
	
	def step(self):
		# I believe no action is needed here on the physical drone
		return
	
