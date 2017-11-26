#! /usr/bin/env python

import subprocess
from PhysicalDrone import *
from Logger import Logger

class SITLdrone(object):
	def __init__(self,ConnectionData,parent_logger=None):
		self.logger = Logger("SITLdrone",parent=parent_logger)
		self.ConnectionData = ConnectionData
		self.PhysicalConnectionData = {}
		self.instance = 0
		if 'inst' in self.ConnectionData:
			self.instance = self.ConnectionData['inst']
			if 'home' in self.ConnectionData:
				subprocess.call(['./DroneComm/startSITL.sh', str(self.ConnectionData['inst']), self.ConnectionData['home']])
			else:
				subprocess.call(['./DroneComm/startSITL.sh', str(self.ConnectionData['inst'])])
		else:
			subprocess.call(['./DroneComm/startSITL.sh'])
		self.PhysicalConnectionData['ConnectionString'] = "127.0.0.1:" + str(14550 + 10*self.instance)
		self.phys_drone = PhysicalDrone(self.PhysicalConnectionData,parent_logger=self.logger)
	
	# def __del__(self):
	# 	self.logger.log("INFO","SITL drone instance deleted! Shutting down...")
	# 	self.shutdown_handler(None,None)
	# 	self.logger.log("INFO","SITL drone shutdown complete!")
	
	def shutdown_handler(self,signal,frame):
		self.logger.log("INFO","SITL drone shutting down! Shutting down \"physical\" connection to SITL drone...")
		try:
			if self.phys_drone!=None:
				self.phys_drone.shutdown_handler(signal,frame)
				self.logger.log("INFO","SITL drone shutting down! \"Physical\" connection to SITL drone disconnected!")
		except AttributeError:
			self.logger.log("WARN","SITL drone shutting down! Physical drone connection to SITL instance not defined!")
		self.logger.log("INFO","SITL drone shutting down! Stopping SITL simulator...")
		if 'inst' in self.ConnectionData:
			subprocess.call(['./DroneComm/stopSITL.sh', str(self.ConnectionData['inst'])])
		else:
			subprocess.call(['./DroneComm/stopSITL.sh'])
		self.logger.log("INFO","SITL drone shutting down! SITL simulator stopped!")

	def printDronekitStatus(self,status):
		self.logger.log("INFO","MAVLINK [{id}]: {status}".format(id=self.getID(),status=status))
	
	def getPhysicalConnectionData(self):
		return self.PhysicalConnectionData
	
	def getPhysicalDrone(self):
		return self.phys_drone
	
	def gotoLocation(self,location):
		self.phys_drone.gotoLocation(location)
	
	def takeoff(self,altitude):
		self.phys_drone.takeoff(altitude)
	
	def getLocation(self):
		return self.phys_drone.getLocation()
	
	def getAttitude(self):
		return self.phys_drone.getAttitude()
	
	def getVelocity(self):
		return self.phys_drone.getVelocity()
	
	def setVelocity(self,velocity):
		self.phys_drone.setVelocity(velocity)
	
	def getGimbalRotation(self):
		return self.phys_drone.getGimbalRotation()
	
	def setGimbalRotation(self,rotation):
		self.phys_drone.setGimbalRotation(rotation)
	
	def setGimbalTarget(self,target):
		self.phys_drone.setGimbalTarget(target)
	
	def getBattery(self):
		return self.phys_drone.getBattery()
	
	def getHome(self):
		return self.phys_drone.getHome()
	
	def setHome(self,home):
		self.phys_drone.setHome(home)
	
	def getStatus(self):
		return self.phys_drone.getStatus()
	
	def getHeading(self):
		return self.phys_drone.getHeading()
	
	def setHeading(self,heading):
		self.phys_drone.setHeading(heading)
	
	def getArmable(self):
		return self.phys_drone.getArmable()
	
	def getAirspeed(self):
		return self.phys_drone.getAirspeed()
	
	def setAirspeed(self,speed):
		self.phys_drone.setAirspeed(speed)
	
	def getGroundspeed(self):
		return self.phys_drone.getGroundspeed()
	
	def setGroundspeed(self,speed):
		self.phys_drone.setGroundspeed(speed)
	
	def getArmed(self):
		return self.phys_drone.getArmed()
	
	def setArmed(self,armed):
		self.phys_drone.setArmed(armed)
	
	def getMode(self):
		return self.phys_drone.getMode()
	
	def setMode(self,mode):
		self.phys_drone.setMode(mode)
	
	def sendGPS_RTCM_DATA(self,data):
		self.phys_drone.sendGPS_RTCM_DATA(data)
	
	def getGpsInfo(self):
		return self.phys_drone.getGpsInfo()

	def getFirmwareVersion(self):
		return self.phys_drone.getFirmwareVersion()
	
	def getSupportsFTP(self):
		return self.phys_drone.getSupportsFTP()
	
	def getLocationGlobalFrame(self):
		return self.phys_drone.getLocationGlobalFrame()
	
	def getLocationLocalFrame(self):
		return self.phys_drone.getLocationLocalFrame()
	
	def getEkfOk(self):
		return self.phys_drone.getEkfOk()
	
	def getLastHeartbeat(self):
		return self.phys_drone.getLastHeartbeat()
	
	def getRangefinder(self):
		return self.phys_drone.getRangefinder()

	def getAllParameters(self):
		return self.phys_drone.getAllParameters()

	def getFromVehicleAttributesDict(self,attribute):
		return self.phys_drone.getFromVehicleAttributesDict(attribute)
	
	def getID(self):
		# return self.phys_drone.getID()
		return "SITL_"+str(self.instance)
	
	def step(self):
		self.phys_drone.step()
	
