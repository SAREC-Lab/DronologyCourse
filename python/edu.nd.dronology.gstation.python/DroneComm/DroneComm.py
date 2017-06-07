#! /usr/bin/env python

from SITLdrone import *
from PhysicalDrone import *
from HTTPdrone import *
from SimDrone import *

class DroneComm(object):
	def __init__(self,type,ConnectionData):
		self.type = type
		self.ConnectionData = ConnectionData
		if self.type=="SITL":
			self.SITL = SITLdrone(self.ConnectionData)
			self.SITLconnectionData = self.ConnectionData
			self.ConnectionData = self.SITL.getPhysicalConnectionData()
			self.Drone = PhysicalDrone(self.ConnectionData)
		elif self.type=="physical":
			self.Drone = PhysicalDrone(self.ConnectionData)
		elif self.type=="HTTP":
			self.Drone = HTTPdrone(self.ConnectionData)
		elif self.type=="simulated":
			self.Drone = SimDrone(self.ConnectionData)
		else:
			print "WARNING: Unrecognized connection type: {type}".format(type=self.type)
			print "WARNING: Defaulting to simulated drone"
			self.Drone = SimDrone(self.ConnectionData)
		
	
	def gotoLocation(self,location):
		self.Drone.gotoLocation(location)
	
	def takeoff(self,altitude):
		self.Drone.takeoff(altitude)
	
	def getLocation(self):
		return self.Drone.getLocation()
	
	def getAttitude(self):
		return self.Drone.getAttitude()
	
	def getVelocity(self):
		return self.Drone.getVelocity()
	
	def getGimbalRotation(self):
		return self.Drone.getGimbalRotation()
	
	def setGimbalRotation(self,rotation):
		self.Drone.setGimbalRotation(rotation)
	
	def setGimbalTarget(self,target):
		self.Drone.setGimbalTarget(target)
	
	def getBattery(self):
		return self.Drone.getBattery()
	
	def getHome(self):
		return self.Drone.getHome()
	
	def setHome(self,home):
		self.Drone.setHome(home)
	
	def getStatus(self):
		return self.Drone.getStatus()
	
	def getHeading(self):
		return self.Drone.getHeading()
	
	def setHeading(self,heading):
		self.Drone.setHeading(heading)
	
	def getArmable(self):
		return self.Drone.getArmable()
	
	def getAirspeed(self):
		return self.Drone.getAirspeed()
	
	def setAirspeed(self,speed):
		self.Drone.setAirspeed(speed)
	
	def getGroundspeed(self):
		return self.Drone.getGroundspeed()
	
	def setGroundspeed(self,speed):
		self.Drone.setGroundspeed(speed)
	
	def getArmed(self):
		return self.Drone.getArmed()
	
	def setArmed(self,armed):
		self.Drone.setArmed(armed)
	
	def getMode(self):
		return self.Drone.getMode()
	
	def setMode(self,mode):
		self.Drone.setMode(mode)
	
	def step(self):
		self.Drone.step()
	
	def getInfoDict(self):
		return {
			'location': self.getLocation().toDict(),
			'attitude': self.getAttitude().toDict(),
			'velocity': self.getVelocity().toDict(),
			'gimbalRotation': self.getGimbalRotation().toDict(),
			'battery': self.getBattery(),
			'home': self.getHome().toDict(),
			'status': self.getStatus(),
			'heading': self.getHeading(),
			'armable': self.getArmable(),
			'airspeed': self.getAirspeed(),
			'groundspeed': self.getGroundspeed(),
			'armed': self.getArmed(),
			'mode': self.getMode(),
		}
	
