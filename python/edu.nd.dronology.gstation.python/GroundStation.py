#! /usr/bin/env python

from JavaComm import *
from DroneComm import *
import threading


class GroundStation():
	def __init__(self,port,drones):
		self.java = JavaComm(port)
		self.java.setDataHandler(self.receivedData)
		self.current_drone_id = 0
		self.drones = {}
		for drone in drones:
			new_drone = DroneComm(drone['type'],drone['ConnectionData'])
			self.drones[self.current_drone_id] = new_drone
			self.current_drone_id = self.current_drone_id + 1
		print "finished initializing drones..."
		self.send_drone_list_cont_async()
		self.java.startComm()
	
	def receivedData(self,id,type,data):
		"""
		translates the different types of received data
		into different actions
		"""
		if type=="command":
			self.handleCommand(data["id"],data["command"],data["data"])
		else:
			print "Unrecognized data type:"
			print "Type: {type}".format(type=type)
			print "Data: {data}".format(data=data)
	
	def handleCommand(self,id,command,data):
		"""
		handles different commands sent from the java program
		"""
		drone = self.drones[id]
		if command=="gotoLocation":
			coord = CoordFromDict(data)
			drone.gotoLocation(coord)
		elif command=="takeoff":
			drone.takeoff(data['altitude'])
		elif command=="setGimbalRotation":
			coord = CoordFromDict(data)
			drone.setGimbalRotation(coord)
		elif command=="setGimbalTarget":
			coord = CoordFromDict(data)
			drone.setGimbalTarget(coord)
		elif command=="setHome":
			coord = CoordFromDict(data)
			drone.setHome(coord)
		elif command=="setHeading":
			drone.setHeading(data['heading'])
		elif command=="setAirspeed":
			drone.setAirspeed(data['speed'])
		elif command=="setGroundspeed":
			drone.setGroundspeed(data['speed'])
		elif command=="setArmed":
			drone.setArmed(data['armed'])
		elif command=="setMode":
			drone.setMode(data['mode'])
		else:
			print "Unrecognized command:"
			print "Id:      {id}".format(id=id)
			print "Command: {command}".format(command=command)
			print "Data:    {data}".format(data=data)
	
	def send_new_drone(self,drone_id):
		drone = self.drones[drone_id]
		data = {
			'type':'new_drone',
			'data':{
				'id':	drone_id,
				'data':	drone.getInfoDict(),
			},
		}
		self.java.send_last(data)
	
	def send_drone_list(self):
		drone_list = {}
		for drone_id in self.drones:
			drone = self.drones[drone_id]
			info = drone.getInfoDict()
			drone_list[str(drone_id)] = info # str here ensures is following JSON standard of using only strings as keys
		data = {
			'type':'drone_list',
			'data':drone_list,
		}
		#print "sending drone list: "
		#print drone_list
		self.java.send_dict_last(data)
	
	def send_drone_list_cont(self):
		while True:
			self.send_drone_list()
			time.sleep(1)
	
	def send_drone_list_cont_async(self):
		threading.Thread(target=self.send_drone_list_cont).start()
	
if __name__=="__main__":
	drones = [
		{
			'type':'SITL',
			'ConnectionData':{
				'inst':0,
				'home':'41.732955,-86.180886,0,0',
			},
		},
	]
	#drones = [
	#	{
	#		'type':'physical',
	#		'ConnectionData':{
	#			'ConnectionString':'/dev/ttyUSB0',
	#			'BaudRate':57600,
	#		},
	#	},
	#]
	comm = GroundStation(1234,drones)
	
