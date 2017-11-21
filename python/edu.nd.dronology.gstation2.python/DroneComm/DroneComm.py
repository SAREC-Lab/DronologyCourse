#! /usr/bin/env python

from SITLdrone import *
from SITLdronePythonStart import *
from PhysicalDrone import *
from HTTPdrone import *
from SimDrone import *
from Logger import Logger
from FileLogging import *
import time

class DroneComm(object):
	def __init__(self,drone_info,outgoingMessageHandler,parent_logger=None):
		self.drone_info = drone_info
		self.outgoingMessageHandler = outgoingMessageHandler
		self.logger = Logger("DroneComm",parent=parent_logger)
		# TODO: move more of this initialization code into the initialize_async function! Currently must wait for the self.Drone object to be populated before returning, because it is used by the calling function in getID()
		self.type = self.drone_info['type']
		self.ConnectionData = self.drone_info['ConnectionData']
		if self.type=="SITL":
			# self.Drone = SITLdronePythonStart(self.ConnectionData,parent_logger=self.logger)
			self.Drone = SITLdrone(self.ConnectionData,parent_logger=self.logger)
		elif self.type=="physical":
			self.Drone = PhysicalDrone(self.ConnectionData,parent_logger=self.logger)
		elif self.type=="HTTP":
			self.Drone = HTTPdrone(self.ConnectionData,parent_logger=self.logger)
		elif self.type=="simulated":
			self.Drone = SimDrone(self.ConnectionData,parent_logger=self.logger)
		else:
			self.logger.log("ERROR","Unrecognized connection type: {type}".format(type=self.type))
			self.logger.log("WARN","Defaulting to simulated drone")
			self.Drone = SimDrone(self.ConnectionData,parent_logger=self.logger)
		self.fileLogger = FileLogging(self)
		# start asynchronous initialization thread
		threading.Thread(target=self.initialize_async).start()
	
	def initialize_async(self):
		self.logger.setClassname("DroneComm[{id}]".format(id=self.getID()))
		self.sendHandshakeData()
		self.logger.log("INFO","drone initialized")
		self.running_timers = True
		self.message_timers = {}
		self.send_state_and_monitoring()

	def getDroneInfoDict(self):
		return self.drone_info

	def shutdown_handler(self,signal,frame):
		self.fileLogger.shutdown_handler(signal,frame)
		self.logger.log("INFO","Stopping timers...")
		self.running_timers = False
		self.logger.log("INFO","Timers stopped!")
		self.logger.log("INFO","Shutting down drone connection...")
		self.Drone.shutdown_handler(signal,frame)
		self.logger.log("INFO","Drone connection shut down!")

	def gotoLocation(self,location):
		self.Drone.gotoLocation(location)
	
	def takeoff(self,altitude):
		self.Drone.takeoff(altitude)
	
	def getLocation(self):
		return self.Drone.getLocation()
	
	def getLocationDict(self):
		return self.getLocation().toDict()

	def getAttitude(self):
		return self.Drone.getAttitude()
	
	def getAttitudeDict(self):
		return self.getAttitude().toDict()

	def getVelocity(self):
		return self.Drone.getVelocity()
	
	def getVelocityDict(self):
		return self.getVelocity().toDict()
	
	def setVelocity(self,velocity):
		self.Drone.setVelocity(velocity)
	
	def getGimbalRotation(self):
		return self.Drone.getGimbalRotation()
	
	def getGimbalRotationDict(self):
		return self.getGimbalRotation().toDict()

	def setGimbalRotation(self,rotation):
		self.Drone.setGimbalRotation(rotation)
	
	def setGimbalTarget(self,target):
		self.Drone.setGimbalTarget(target)
	
	def getBattery(self):
		return self.Drone.getBattery()
	
	def getHome(self):
		return self.Drone.getHome()

	def getHomeDict(self):
		return self.getHome().toDict()
	
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
	
	def sendGPS_RTCM_DATA(self,data):
		self.Drone.sendGPS_RTCM_DATA(data)
	
	def getGpsInfo(self):
		return self.Drone.getGpsInfo()

	def getGps_eph(self):
		gps_info = self.getGpsInfo()
		return gps_info['eph']

	def getGps_epv(self):
		gps_info = self.getGpsInfo()
		return gps_info['epv']

	def getGps_fix_type(self):
		gps_info = self.getGpsInfo()
		return gps_info['fix_type']

	def getGps_satellites_visible(self):
		gps_info = self.getGpsInfo()
		return gps_info['satellites_visible']
	
	def getFirmwareVersion(self):
		return self.Drone.getFirmwareVersion()
	
	def getSupportsFTP(self):
		return self.Drone.getSupportsFTP()
	
	def getLocationGlobalFrame(self):
		return self.Drone.getLocationGlobalFrame()
	
	def getLocationGlobalFrameDict(self):
		return self.getLocationGlobalFrame().toDict()
	
	def getLocationLocalFrame(self):
		return self.Drone.getLocationLocalFrame()
	
	def getLocationLocalFrameDict(self):
		return self.getLocationLocalFrame().toDict()
	
	def getEkfOk(self):
		return self.Drone.getEkfOk()
	
	def getLastHeartbeat(self):
		return self.Drone.getLastHeartbeat()
	
	def getRangefinder(self):
		return self.Drone.getRangefinder()
	
	def getRangefinder_distance(self):
		rangefinder = self.Drone.getRangefinder()
		return rangefinder['distance']
	
	def getRangefinder_voltage(self):
		rangefinder = self.Drone.getRangefinder()
		return rangefinder['voltage']

	def getID(self):
		return self.Drone.getID()

	def setStateFrequency(self,freq):
		self.setMessageDelay('state',freq)

	def setMonitorFrequency(self,freq):
		self.setMessageDelay('monitoring',freq)
	
	def step(self):
		self.Drone.step()
	
	def getAllAttributeFunctions(self):
		return {
			'location': 				self.getLocationDict,
			'attitude': 				self.getAttitudeDict,
			'velocity': 				self.getVelocityDict,
			'home': 					self.getHomeDict,
			'gimbalRotation': 			self.getGimbalRotationDict,
			'batterystatus': 			self.getBattery,
			'status': 					self.getStatus,
			'heading': 					self.getHeading,
			'armable': 					self.getArmable,
			'airspeed': 				self.getAirspeed,
			'groundspeed': 				self.getGroundspeed,
			'armed': 					self.getArmed,
			'mode': 					self.getMode,
			'gpsInfo': 					self.getGpsInfo,
			'gps_eph': 					self.getGps_eph,
			'gps_epv': 					self.getGps_epv,
			'gps_fix_type': 			self.getGps_fix_type,
			'gps_satellites_visible': 	self.getGps_satellites_visible,
			'firmware_version':			self.getFirmwareVersion,
			'supports_ftp':				self.getSupportsFTP,
			'location_global_frame':	self.getLocationLocalFrameDict,
			'location_local_frame':		self.getLocationLocalFrameDict,
			'ekf_ok':					self.getEkfOk,
			'last_heartbeat':			self.getLastHeartbeat,
			'Rangefinder':				self.getRangefinder,
			'rangefinder_distance':		self.getRangefinder_distance,
			'rangefinder_voltage':		self.getRangefinder_voltage,
			'id': 						self.getID,
			'droneInfoDict': 			self.getDroneInfoDict,
		}

	def getParameterByName(self,param_name):
		param_functions = self.getAllAttributeFunctions()
		if param_name in param_functions:
			func = param_functions[param_name]
			param = func()
			return param
		else:
			vehicle_parameters = self.Drone.getAllParameters()
			if param_name in vehicle_parameters:
				param = vehicle_parameters[param_name]
				return param
			else:
				param = self.Drone.getFromVehicleAttributesDict()
				if param!=None:
					return param
				else:
					self.logger.log("ERROR","Unknown vehicle parameter "+param_name)
					return None

	def getAllAttributes(self):
		attribute_dict = {}
		param_functions = self.getAllAttributeFunctions()
		for param_name in param_functions:
			attribute_dict[param_name] = self.getParameterByName(param_name)
		return attribute_dict

	def getDataDictFromList(self,list):
		data_dict = {}
		for param_name in list:
			data_dict[param_name] = self.getParameterByName(param_name)
		return data_dict

	def getStateData(self):
		return self.getDataDictFromList([
			'location',
			'attitude',
			'velocity',
			'batterystatus',
			'status',
			'armable',
			'groundspeed',
			'armed',
			'mode',
		])
	
	def sendStateData(self):
		self.outgoingMessageHandler(None,self.getID(),"state",self.getStateData())

	def getMonitoringData(self):
		return self.getDataDictFromList([
			'gps_eph',
			'gps_epv',
			'gps_fix_type',
			'gps_satellites_visible',
			'groundspeed',
		])
	
	def sendMonitoringData(self):
		self.outgoingMessageHandler(None,self.getID(),"monitoring",self.getMonitoringData())
	
	def getHandshakeData(self):
		return self.getDataDictFromList([
			'home',
		])
	
	def sendHandshakeData(self):
		self.outgoingMessageHandler(None,self.getID(),"handshake",self.getHandshakeData())

	def sendHandshakeDataToID(self,id):
		self.outgoingMessageHandler(id,self.getID(),"handshake",self.getHandshakeData())

	def getTimeMsec(self):
		return time.time()*1000

	def reset_message_timer(self,msgtype):
		message_timer = self.message_timers[msgtype]
		new_last_message = self.getTimeMsec()-message_timer['delay']
		old_last_message = message_timer['last_message']
		if new_last_message>old_last_message:
			message_timer['last_message'] = new_last_message
	
	def initialize_message_timer(self,msgtype,callback,delay):
		self.message_timers[msgtype] = {
			'callback': callback,
			'delay': delay,
			'last_message': self.getTimeMsec()-delay,
		}
	
	def setMessageDelay(self,msgtype,delay):
		minimum_delay_sanitycheck = 1
		if delay>=minimum_delay_sanitycheck:
			self.message_timers[msgtype]['delay'] = delay
			self.reset_message_timer(msgtype)

	def send_state_and_monitoring(self):
		self.initialize_message_timer('state',self.sendStateData,200)
		self.initialize_message_timer('monitoring',self.sendMonitoringData,5000)
		self.run_timers()
		
	def run_timers(self):
		while self.running_timers:
			current_time = self.getTimeMsec()
			shortest_time = None
			for msgtype in self.message_timers:
				timer = self.message_timers[msgtype]
				delay = timer['delay']
				last_message = timer['last_message']
				time_since_last = current_time - last_message
				if delay<=time_since_last:
					callback = timer['callback']
					callback()
					timer['last_message'] = last_message + delay
					last_message = timer['last_message']
					time_since_last = current_time - last_message
				if shortest_time==None or time_since_last<shortest_time:
					shortest_time = time_since_last
			sleep_time = shortest_time*0.9/1000
			if sleep_time>=0:
				time.sleep(sleep_time)

	def handleCommand(self,command,data):
		def cmd_setStateFrequency(data):
			self.setStateFrequency(data['frequency'])
		def cmd_setMonitorFrequency(data):
			self.setMonitorFrequency(data['frequency'])
		def cmd_gotoLocation(data):
			coord = CoordFromDict(data)
			self.gotoLocation(coord)
		def cmd_takeoff(data):
			self.takeoff(data['altitude'])
		def cmd_setVelocity(data):
			coord = CoordFromDict(data)
			self.setVelocity(coord)
		def cmd_setGimbalRotation(data):
			coord = CoordFromDict(data)
			self.setGimbalRotation(coord)
		def cmd_setGimbalTarget(data):
			coord = CoordFromDict(data)
			self.setGimbalTarget(coord)
		def cmd_setHome(data):
			coord = CoordFromDict(data)
			self.setHome(coord)
		def cmd_setHeading(data):
			self.setHeading(data['heading'])
		def cmd_setAirspeed(data):
			self.setAirspeed(data['speed'])
		def cmd_setGroundspeed(data):
			self.setGroundspeed(data['speed'])
		def cmd_setArmed(data):
			self.setArmed(data['armed'])
		def cmd_setMode(data):
			self.setMode(data['mode'])
		commandFunctions = {
			'setStateFrequency': 	cmd_setStateFrequency,
			'setMonitorFrequency': 	cmd_setMonitorFrequency,
			'gotoLocation': 		cmd_gotoLocation,
			'takeoff': 				cmd_takeoff,
			'setVelocity': 			cmd_setVelocity,
			'setGimbalRotation': 	cmd_setGimbalRotation,
			'setGimbalTarget': 		cmd_setGimbalTarget,
			'setHome': 				cmd_setHome,
			'setHeading': 			cmd_setHeading,
			'setAirspeed': 			cmd_setAirspeed,
			'setGroundspeed': 		cmd_setGroundspeed,
			'setArmed': 			cmd_setArmed,
			'setMode': 				cmd_setMode,
		}
		if command in commandFunctions:
			commandFunctions[command](data)
		else:
			self.logger.log("ERROR","Unrecognized command:")
			self.logger.log("ERROR","Command: {command}".format(command=command))
			self.logger.log("ERROR","Data:    {data}".format(data=data))
