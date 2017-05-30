#! /usr/bin/env python

from Connections import *
import sys
import threading
import json

class JavaComm():
	def __init__(self,port):
		"""
		start listening for connections from Java code
		"""
		self.port = port
		self.last_id = None
		self.conn = ListenPort(self.port,self.received,self.connected,self.disconnect,True,True)
		print "Java connection listening on port {port}".format(port=port)
		self.received_data = {}
		self.data_handler = None
	
	def startComm(self):
		# self.get_and_send_async()
		reactor.run()
	
	def connected(self,id):
		"""
		called when connection made from java program
		start accepting input for control
		"""
		print "Java connected on id {id}".format(id=id)
		self.received_data[id] = ''
		self.last_id = id
		self.conn.start_inqueue(id)
		self.conn.start_outqueue(id)
	
	def disconnect(self,id):
		"""
		called when connection lost from java program
		"""
		print "Java lost connection on id {id}".format(id=id)
		del self.received_data[id]
	
	def received(self,id,data):
		"""
		Callback function for received data
		"""
		self.received_data[id] = self.received_data[id] + data
		self.parseReceivedData(id)
		#print "command received data on id {id}:".format(id=id)
		#print data
		# sys.stdout.write(data)
		#print "{Not shown to reduce output!}"
		
	def parseReceivedData(self,id):
		pos = self.received_data[id].find('\r')
		if pos>=0:
			data_chunk = self.received_data[id][0:pos]
			self.received_data[id] = self.received_data[id][(pos+1):]
			data = json.loads(data_chunk)
			self.handleData(id,data)
			
			self.parseReceivedData(id) # might still have data left to parse after this, so recurse to keep on parsing
		
	def handleData(self,id,data):
		print "received:"
		print data
		if self.data_handler!=None:
			self.data_handler(id,data['type'],data['data'])
		
	def setDataHandler(self,handler):
		self.data_handler = handler
		
	def send(self,id,data):
		self.conn.send(id,data)
		self.conn.send(id,'\r\n')
	
	def send_last(self,data):
		if (self.last_id!=None):
			self.send(self.last_id,data)
		
	def send_dict(self,id,dict):
		data = json.dumps(dict)
		self.send(id,data)
	
	def send_dict_last(self,dict):
		if (self.last_id!=None):
			self.send_dict(self.last_id,dict)
	
	def get_and_send(self):
		while True:
			line = raw_input()
			self.send_last(line)
	
	def get_and_send_async(self):
		threading.Thread(target=self.get_and_send).start()
	
if __name__=="__main__":
	comm = JavaComm(1234)
	
