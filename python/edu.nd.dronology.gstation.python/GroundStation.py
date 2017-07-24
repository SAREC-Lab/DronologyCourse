#! /usr/bin/env python

from JavaComm import *
from DroneComm import *
import threading
import signal
from Logger import Logger
import json
import os
from RESTinterface import *
import time


class GroundStation():
	def __init__(self,port,initial_connections):
		self.logger = Logger("GroundStation",output_handler=self.messageHandler)
		signal.signal(signal.SIGINT,self.shutdown_handler)
		self.java = JavaComm(port,self.newconn_handler,parent_logger=self.logger)
		self.java.setDataHandler(self.receivedData)
		self.drones = {}
		self.logger.log("INFO","Initializing all drones...")
		self.initializeDroneList(initial_connections)
		self.logger.log("INFO","Finished initializing drones!")
		self.rest_api = RESTinterface(9999,self)
		self.rest_api.startRESTinterface()
		self.java.startComm()
	
	def getCurrentTimestamp(self):
		return int(time.time()*1000)

	def initializeDroneList(self,drone_info_list):
		for drone_info in drone_info_list:
			self.initializeNewDrone(drone_info)
	
	def initializeNewDrone(self,drone_info):
		new_drone = DroneComm(drone_info,self.sendMessage,parent_logger=self.logger)
		new_drone_id = new_drone.getID()
		self.drones[new_drone_id] = new_drone
		return new_drone_id

	def removeDrone(self,drone_id,signal=None,frame=None):
		drone = self.drones[drone_id]
		drone.shutdown_handler(signal,frame)
		del self.drones[drone_id]

	def reInitializeDrone(self,drone_id):
		drone = self.drones[drone_id]
		drone_info = drone.getDroneInfoDict()
		self.removeDrone(drone_id)
		self.initializeNewDrone(drone_info)

	def messageHandler(self,msg_type,classpath,message):
		"""
		Prints out status information output by all classes.
		"""
		self.logger.outputMessageStdout(msg_type,classpath,message)
		data = {
			'type':'status_message',
			'data':{
				'msg_type':msg_type,
				'classpath':classpath,
				'message':message,
			},
		}
		try:
			# self.java.send_dict_all(data) # TODO: this causes errors as Dronology tries to parse this
			pass
		except AttributeError:
			pass

	def receivedData(self,id,data):
		"""
		translates the different types of received data
		into different actions
		"""
		uavid = data["uavid"]
		command = data["command"]
		innerData = data["data"]
		sendtimestamp = data["sendtimestamp"]
		self.logger.log("DEBUG", "Recived \"{command}\" command for UAV #{uavid} from base station #{id} with data {data} and delay of {delay}ms".format(command = command, uavid = uavid, id = id, data = innerData, delay = self.getCurrentTimestamp()-sendtimestamp))
		self.handleCommand(data["uavid"],data["command"],data["data"])
	
	def handleCommand(self,id,command,data):
		"""
		handles different commands sent from the java program
		"""
		if id in self.drones:
			drone = self.drones[id]
			drone.handleCommand(command,data)
		else:
			self.logger.log("ERROR","Drone with ID #{id} not recognized!".format(id=id))
	
	def newconn_handler(self,id):
		for drone_id in self.drones:
			drone = self.drones[drone_id]
			drone.sendHandshakeData()

	def sendMessage(self,javaid,uavid,msgtype,data):
		msg = {
			'type': msgtype,
			'uavid': uavid,
			'sendtimestamp': self.getCurrentTimestamp(),
			'data': data,
		}
		if javaid==None:
			self.java.send_dict_all(msg)
		else:
			self.java.send_dict(javaid,msg)
	
	def shutdown_handler(self,signal,frame):
		self.java.shutdown_handler(signal,frame)
		self.logger.log("INFO","Stopping all drone connections...")
		for drone_id in self.drones.keys():
			self.removeDrone(drone_id,signal=signal,frame=frame)
		self.logger.log("INFO","All drone connections stopped!")
		self.rest_api.stopRESTinterface()
	
if __name__=="__main__":
	connection_list_dir = 'initial_connection_lists/'
	default_connection_list_path = connection_list_dir + 'location.default.conf'
	override_connection_list_path = connection_list_dir + 'location.override.conf'
	connection_list_path = default_connection_list_path
	
	if os.path.isfile(override_connection_list_path):
		connection_list_path = override_connection_list_path
	
	with open(connection_list_path) as initial_connection_list_loc_conf:
		initial_connections_json_path = connection_list_dir + initial_connection_list_loc_conf.read()
	
	with open(initial_connections_json_path) as initial_connections_json:
		initial_connections = json.load(initial_connections_json)
	
	comm = GroundStation(1234,initial_connections)
	
