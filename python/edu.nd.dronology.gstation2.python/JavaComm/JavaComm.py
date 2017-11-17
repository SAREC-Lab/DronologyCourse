#! /usr/bin/env python

from Connections import *
import sys
import threading
import json
from Logger import Logger

class JavaComm():
	def __init__(self,port,newconn_handler,parent_logger=None):
		"""
		start listening for connections from Java code
		"""
		self.newconn_handler = newconn_handler
		self.logger = Logger("JavaComm",parent=parent_logger)
		self.port = port
		self.connected_ids = []
		self.last_id = None
		self.sending_lock = threading.Lock()
		self.conn = ListenPort(self.port,self.received,self.connected,self.disconnect,True,True)
		self.logger.log("INFO","Java connection listening on port {port}".format(port=port))
		self.received_data = {}
		self.data_handler = None
	
	def startComm(self):
		# self.get_and_send_async()
		reactor.run()
	
	def shutdown_handler(self,signal,frame):
		self.logger.log("INFO","Stopping \"twisted\" reactor on port for Java connection...")
		reactor.stop()
		self.logger.log("INFO","\"Twisted\" reactor stopped!")

	def connected(self,id):
		"""
		called when connection made from java program
		start accepting input for control
		"""
		self.logger.log("INFO","Java connected on id {id}".format(id=id))
		self.received_data[id] = ''
		self.last_id = id
		self.connected_ids.append(id)
		self.conn.start_inqueue(id)
		self.conn.start_outqueue(id)
		self.newconn_handler(id)
	
	def disconnect(self,id):
		"""
		called when connection lost from java program
		"""
		self.logger.log("INFO","Java lost connection on id {id}".format(id=id))
		del self.received_data[id]
		self.connected_ids.remove(id)
	
	def received(self,id,data):
		"""
		Callback function for received data
		"""
		self.received_data[id] = self.received_data[id] + data
		self.parseReceivedData(id)
		#self.logger.log("DEBUG","command received data on id {id}:".format(id=id))
		#self.logger.log("DEBUG",data)
		
	def parseReceivedData(self,id):
		pos = self.received_data[id].find('\r')
		if pos>=0:
			data_chunk = self.received_data[id][0:pos]
			self.received_data[id] = self.received_data[id][(pos+1):]
			data = json.loads(data_chunk)
			self.handleData(id,data)
			
			self.parseReceivedData(id) # might still have data left to parse after this, so recurse to keep on parsing
			# TODO: remove this recursive loop!
		
	def handleData(self,id,data):
		# self.logger.log("DEBUG","received:")
		# self.logger.log("DEBUG",data)
		if self.data_handler!=None:
			self.data_handler(id,data)
		
	def setDataHandler(self,handler):
		self.data_handler = handler
		
	def send(self,id,data):
		with self.sending_lock:
			self.conn.send(id,data+'\r\n')
			# self.logger.log("DEBUG","sent:")
			# self.logger.log("DEBUG",data)
	
	def send_last(self,data):
		if (self.last_id!=None):
			self.send(self.last_id,data)
	
	def send_all(self,data):
		for id in self.connected_ids:
			self.send(id,data)

	def send_dict(self,id,dict):
		data = json.dumps(dict)
		self.send(id,data)
	
	def send_dict_last(self,dict):
		if (self.last_id!=None):
			self.send_dict(self.last_id,dict)
	
	def send_dict_all(self,dict):
		# self.logger.log("DEBUG","sent:")
		# self.logger.log("DEBUG",str(dict))
		for id in self.connected_ids:
			self.send_dict(id,dict)
	
	def get_and_send(self):
		while True:
			line = raw_input()
			self.send_last(line)
	
	def get_and_send_async(self):
		threading.Thread(target=self.get_and_send).start()
	
if __name__=="__main__":
	comm = JavaComm(1234)
	
