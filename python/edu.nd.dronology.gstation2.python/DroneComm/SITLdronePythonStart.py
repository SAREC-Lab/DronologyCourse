#! /usr/bin/env python

from PhysicalDrone import *
from dronekit_sitl import SITL
import time
from Logger import Logger

class SITLdronePythonStart(object):
	def __init__(self,ConnectionData,parent_logger=None):
		self.logger = Logger("SITLdronePythonStart",parent=parent_logger)
		self.ConnectionData = ConnectionData
		self.PhysicalConnectionData = {}
		self.instance = 0
		self.home = '41.732955,-86.180886,0,0'
		if 'inst' in self.ConnectionData:
			self.instance = self.ConnectionData['inst']
		if 'home' in self.ConnectionData:
			self.home = self.ConnectionData['home']
		self.sitl_args = ['-S', '-I'+str(self.instance), '--model', '+', '--home', self.home, "--speedup", "1", "--rate", "10", "--defaults", "/home/droneuser/Dronology/python/edu.nd.dronology.gstation.python/ardupilot/Tools/autotest/default_params/copter.parm"]
		self.logger.log("DEBUG",self.sitl_args)
		self.sitl = SITL()
		self.sitl.download('copter', '3.3', verbose=True)
		self.sitl.launch(self.sitl_args, await_ready=True, restart=False, verbose=True)
		self.PhysicalConnectionData['ConnectionString'] = self.sitl.connection_string()
		self.phys_drone = PhysicalDrone(self.PhysicalConnectionData,parent_logger=self.logger)
		self.phys_drone.getVehicleObject().parameters["SYSID_THISMAV"] = self.instance
		time.sleep(10)
		# self.sitl.block_until_ready(verbose=True)
	
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
		self.sitl.stop()
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
	
