#! /usr/bin/env python

from dronekit import *
from Coordinate import *
from Logger import Logger

import socket

class PhysicalDrone(object):
	def __init__(self,ConnectionData,parent_logger=None):
		self.logger = Logger("PhysicalDrone",parent=parent_logger)
		if 'BaudRate' in ConnectionData:
			# self.vehicle = connect(ConnectionData['ConnectionString'], wait_ready=False, status_printer=self.printDronekitStatus, baud=ConnectionData['BaudRate'])
			self.vehicle = connect(ConnectionData['ConnectionString'], wait_ready=True, status_printer=self.printDronekitStatus, baud=ConnectionData['BaudRate'])
		else:
			# self.vehicle = connect(ConnectionData['ConnectionString'], wait_ready=False, status_printer=self.printDronekitStatus)
			self.vehicle = connect(ConnectionData['ConnectionString'], wait_ready=True, status_printer=self.printDronekitStatus)
		# TODO: wait here to make sure home position is set
		self.logger.log("INFO","connected to "+ConnectionData['ConnectionString'])
	
	def shutdown_handler(self,signal,frame):
		self.logger.log("INFO","Loitering before shutting down connection to drone...")
		if self.getArmed() and not (self.getMode()=="LAND"):
			self.setMode('LOITER',timeout=5)
		else:
			self.logger.log("INFO","Already landing! No need to loiter.")
		self.logger.log("INFO","Disconnecting from drone...")
		self.vehicle.close()
		self.logger.log("INFO","Drone disconnected!")
	
	def getVehicleObject(self):
		return self.vehicle

	def printDronekitStatus(self,status):
		self.logger.log("MAVLINK",status)

	def gotoLocation(self,location):
		self.vehicle.simple_goto(location.toGlobalRelative())
	
	def takeoff(self,altitude):
		self.setMode("GUIDED")
		self.logger.log("INFO"," Arming motors for takeoff...")
		self.setArmed(True)
		self.logger.log("INFO"," Taking off...")
		self.vehicle.simple_takeoff(altitude)
		time.sleep(2)
		while not self.getArmed():
			self.logger.log("WARN"," Drone not armed after takeoff command! Retrying...")
			self.logger.log("INFO"," Arming motors for takeoff...")
			self.setArmed(True)
			self.logger.log("INFO"," Taking off...")
			self.vehicle.simple_takeoff(altitude)
			time.sleep(2)
	
	def getLocation(self):
		return CoordFromLocation(self.vehicle.location.global_relative_frame)
	
	def getAttitude(self):
		return CoordFromRotation(self.vehicle.attitude)
	
	def getVelocity(self):
		return CoordFromList(self.vehicle.velocity)
	
	def setVelocity(self,velocity):
		msg = self.vehicle.message_factory.set_position_target_local_ned_encode(
			0,       # time_boot_ms (not used)
			0, 0,    # target_system, target_component
			mavutil.mavlink.MAV_FRAME_BODY_NED, # frame
			0b0000111111000111, # type_mask (only speeds enabled)
			0, 0, 0, # x, y, z positions
			velocity.getX(), velocity.getY(), -1*velocity.getZ(), # x, y, z velocity in m/s
			0, 0, 0, # x, y, z acceleration (not supported yet, ignored in GCS_Mavlink)
			0, 0)    # yaw, yaw_rate (not supported yet, ignored in GCS_Mavlink)
		# send command to vehicle
		self.vehicle.send_mavlink(msg)
	
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
		msg = self.vehicle.message_factory.mav_cmd_condition_yaw_encode(heading, 0, 0, 0, 0, 0, 0)
		# send command to vehicle
		self.vehicle.send_mavlink(msg)
	
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
		self.logger.log("INFO"," Setting armed state to "+str(armed)+"...")
		self.vehicle.armed = armed
	
	def getMode(self):
		return self.vehicle.mode.name
	
	def setMode(self,mode,timeout=-1):
		self.logger.log("INFO"," Changing mode to \""+mode+"\"...")
		self.vehicle.mode = VehicleMode(mode)
		timeout_count = 0
		while (self.getMode()!=mode) and (timeout<0 or timeout>timeout_count):
			self.logger.log("INFO"," Waiting for mode change to \""+mode+"\"...")
			self.vehicle.mode = VehicleMode(mode)
			time.sleep(1)
			timeout_count = timeout_count + 1
	
	def sendGPS_RTCM_DATA(self,data):
		# try:
		# 	s = socket.create_connection(["udp://127.0.0.1",13320])
		# 	s.sendall(data)
		# 	s.close()
		# except:
		# 	print "error sending data"
		if not hasattr(self, 'gps_rtcm_sequence_num'):
			self.gps_rtcm_sequence_num = 0
		else:
			self.gps_rtcm_sequence_num = self.gps_rtcm_sequence_num + 1
		if self.gps_rtcm_sequence_num>=(1<<5):
			self.gps_rtcm_sequence_num = 0
		remaining_len = len(data)
		start_buf = 0
		frag_id = 0
		fragmented = False
		msglen = 180
		# msglen = 110
		while remaining_len>=0 and frag_id<4: # Greater or equal allows for an empty message to be sent if the data is an exact multiple of 180 characters long (as a "terminator")
			length = remaining_len
			if length>msglen:
				length = msglen
				fragmented = True
			packet = data[start_buf:(start_buf+length)]
			# while len(packet)<msglen:
			# 	packet = packet + '\0'
			flags = (self.gps_rtcm_sequence_num << 3) | (frag_id << 2) | (fragmented)
			# buf = []
			# for byte in packet:
			# 	buf.append(ord(byte))
			buf = bytearray(packet.ljust(msglen, '\0'))
			msg = self.vehicle.message_factory.gps_rtcm_data_encode(flags,length,buf)
			# self.vehicle.message_factory.gps_rtcm_data_send(flags,length,buf)
			# msg = self.vehicle.message_factory.gps_inject_data_encode(0,0,length,buf)
			print msg
			self.vehicle.send_mavlink(msg)
			remaining_len = length - msglen
			start_buf = start_buf + msglen
			frag_id = frag_id + 1
		if remaining_len>0:
			self.logger.log("WARN","GPS_RTCM_DATA message too long to transmit! Remaining characters: "+str(remaining_len))

	
	def getGpsInfo(self):
		return {
			'eph': self.vehicle.gps_0.eph,
			'epv': self.vehicle.gps_0.epv,
			'fix_type': self.vehicle.gps_0.fix_type,
			'satellites_visible': self.vehicle.gps_0.satellites_visible,
		}

	def getFirmwareVersion(self):
		return self.vehicle.version
	
	def getSupportsFTP(self):
		return self.vehicle.capabilities.ftp
	
	def getLocationGlobalFrame(self):
		return CoordFromLocation(self.vehicle.location.global_frame)
	
	def getLocationLocalFrame(self):
		return CoordFromNED(self.vehicle.location.local_frame)
	
	def getEkfOk(self):
		return self.vehicle.ekf_ok
	
	def getLastHeartbeat(self):
		return self.vehicle.last_heartbeat
	
	def getRangefinder(self):
		rangefinder = self.vehicle.rangefinder
		return {
			'distance'	: rangefinder.distance,
			'voltage'	: rangefinder.voltage,
		}

	def getAllParameters(self):
		return self.vehicle.parameters

	def getFromVehicleAttributesDict(self,attribute):
		def ensureSerializable(data):
			if type(data) in [int,float,long,bool,str,unicode]:
				return data
			elif data==None:
				return data
			elif type(data) in [list,tuple,bytearray,xrange,set,frozenset]:
				out = []
				for item in data:
					out.append(ensureSerializable(item))
				return out
			elif type(data) is dict:
				out = {}
				for key in data:
					value = data[key]
					if not type(key) in [str,unicode]:
						key = str(key)
					out[key] = ensureSerializable(value)
				return out
			else:
				try:
					data_dict = data.__dict__
					return ensureSerializable(data_dict)
				except AttributeError:
					import json
					try:
						json.dumps(data)
						return data
					except TypeError:
						self.logger.log("WARN","Un-serializable data object! Using default __str__ method! "+str(data))
						return str(data)
		
		if attribute in self.vehicle.__dict__:
			attr = self.vehicle.__dict__[attribute]
			attr_serializable = ensureSerializable(attr)
			return attr_serializable
		else:
			return None

	def getID(self):
		try:
			return 'PHYS_'+str(int(self.vehicle.parameters['SYSID_THISMAV']))
		# except AttributeError:
		except Exception:
			return 'PHYS_UNKNOWN'
	
	def step(self):
		# I believe no action is needed here on the physical drone
		return
	
