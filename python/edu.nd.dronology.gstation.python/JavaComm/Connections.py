#! /usr/bin/env python

# A simplified set of classes for managing TCP connections using the Twisted Library
# created by Joshua Huseman, jhuseman@nd.edu, for CSE30332 Programming Paradigms
# Note: many docstrings may be copied from other locations and be misleading!

from twisted.internet.protocol import ClientFactory
from twisted.internet.protocol import Factory
from twisted.internet.protocol import Protocol
from twisted.internet.defer import DeferredQueue
from twisted.internet import reactor

class HostConnectProtocol(Protocol):
	def __init__(self,host,port,callback,conncallback,disconncallback,in_queued,out_queued):
		"""
		initializes variables passed from the parent,
		and creates the queue of input
		"""
		self.host = host
		self.port = port
		self.callback = callback
		self.conncallback = conncallback
		self.disconncallback = disconncallback
		self.in_queued = in_queued
		self.out_queued = out_queued
		
		if self.in_queued:
			self.inqueue = DeferredQueue()
		if self.out_queued:
			self.outqueue = DeferredQueue()
	
	def connectionMade(self):
		"""
		Runs when connection is made to the server.
		Prints out a message and
		starts the callbacks of the queue.
		"""
		print "new connection made to {host} port {port}".format(
			host = self.host,
			port = self.port,
		)
		self.conncallback()
	
	def dataReceived(self, data):
		"""
		Runs after data is received from the server.
		Adds the received data to the client queue.
		"""
		self.inqueue.put({"connected":True,"data":data})
	
	def connectionFailed(self, reason):
		"""
		Connection failed! Print error message
		and add disconnect signal to the client queue.
		"""
		print "Connection to server failed!"
		self.inqueue.put({"connected":False})
	
	def connectionLost(self, reason):
		"""
		Connection lost. Most likely torn down by server.
		Print message and add disconnect signal
		to the client queue.
		"""
		print "lost connection to {host} port {port}".format(
			host = self.host,
			port = self.port,
		)
		self.inqueue.put({"connected":False})
	
	def start_inqueue(self):
		if self.in_queued:
			self.inqueue.get().addCallback(self.inqueue_received)
	
	def start_outqueue(self):
		if self.out_queued:
			self.outqueue.get().addCallback(self.outqueue_received)
	
	def send(self,data):
		if self.out_queued:
			self.outqueue.put(data)
		else:
			self.transport.write(data)
	
	def loseConnection(self):
		self.transport.loseConnection()
	
	def outqueue_received(self, data):
		"""
		Callback function for queue responses.
		If "connected" is false, tears down connection.
		Otherwise, forwards the data on to this connection
		and waits for next queue "put."
		"""
		self.transport.write(data)
		self.outqueue.get().addCallback(self.outqueue_received)	
	
	def inqueue_received(self, result):
		"""
		Callback function for queue responses.
		If "connected" is false, tears down connection.
		Otherwise, forwards the data on to this connection
		and waits for next queue "put."
		"""
		if result["connected"]==True:
			self.callback(result["data"])
			self.inqueue.get().addCallback(self.inqueue_received)
		else:
			self.disconncallback()
		
	

class HostConnect(ClientFactory):
	
	def __init__(self,host,port,callback,conncallback,disconncallback,in_queued,out_queued):
		"""
		Initialize the connection.
		Creates the TCP connection with the server, then
		starts waiting for queued data from client.
		Creating the TCP connection implicitly runs
		connectionMade when the link is successfully created.
		"""
		self.conn = HostConnectProtocol(host,port,callback,conncallback,disconncallback,in_queued,out_queued)
		reactor.connectTCP(host, port, self)
		#reactor.run()
	
	def start_inqueue(self):
		self.conn.start_inqueue()
	
	def start_outqueue(self):
		self.conn.start_outqueue()
	
	def send(self,data):
		self.conn.send(data)
	
	def loseConnection(self):
		self.conn.loseConnection()
	
	def buildProtocol(self, addr):
		return self.conn
	

class ListenPortProtocol(Protocol):
	def __init__(self,addr,callback,conncallback,disconncallback,in_queued,out_queued,id):
		"""
		initializes variables passed from the parent,
		and creates the queue of input
		"""
		self.id = id
		self.addr = addr
		self.callback = callback
		self.conncallback = conncallback
		self.disconncallback = disconncallback
		self.in_queued = in_queued
		self.out_queued = out_queued
		
		if self.in_queued:
			self.inqueue = DeferredQueue()
		if self.out_queued:
			self.outqueue = DeferredQueue()
	
	def connectionMade(self):
		"""
		Runs when connection is made from the client.
		Prints a message to the screen
		and begins connection to server.
		Then starts callbacks of queued data.
		"""
		print "connection received from {addr}".format(
			addr = self.addr,
		)
		self.conncallback(self.id)
	
	def dataReceived(self, data):
		"""
		Runs after data is received from the server.
		Adds the received data to the client queue.
		"""
		if self.in_queued:
			self.inqueue.put({"connected":True,"data":data})
		else:
			self.callback(self.id,data)
	
	def connectionFailed(self, reason):
		"""
		Connection failed! Print error message
		and add disconnect signal to the client queue.
		"""
		print "Connection to server failed!"
		self.inqueue.put({"connected":False})
	
	def connectionLost(self, reason):
		"""
		Connection lost. Most likely torn down by server.
		Print message and add disconnect signal
		to the client queue.
		"""
		print "connection lost from {addr}".format(
			addr = self.addr,
		)
		self.inqueue.put({"connected":False})
	
	def start_inqueue(self):
		if self.in_queued:
			self.inqueue.get().addCallback(self.inqueue_received)
	
	def start_outqueue(self):
		if self.out_queued:
			self.outqueue.get().addCallback(self.outqueue_received)
	
	def send(self,data):
		if self.out_queued:
			self.outqueue.put(data)
		else:
			self.transport.write(data)
	
	def loseConnection(self):
		self.transport.loseConnection()
	
	def outqueue_received(self, data):
		"""
		Callback function for queue responses.
		If "connected" is false, tears down connection.
		Otherwise, forwards the data on to this connection
		and waits for next queue "put."
		"""
		self.transport.write(data)
		self.outqueue.get().addCallback(self.outqueue_received)
		
	
	def inqueue_received(self, result):
		"""
		Callback function for queue responses.
		If "connected" is false, tears down connection.
		Otherwise, forwards the data on to this connection
		and waits for next queue "put."
		"""
		if result["connected"]==True:
			self.callback(self.id,result["data"])
			self.inqueue.get().addCallback(self.inqueue_received)
		else:
			self.disconncallback(self.id)
		
	

class ListenPort(Factory):
	
	def __init__(self,port,callback,conncallback,disconncallback,in_queued,out_queued):
		"""
		Initialize the connection.
		Listens for a TCP connection from the client, then
		creates a corresponding server connection
		when connected (via connectionMade).
		"""
		self.callback = callback
		self.conncallback = conncallback
		self.disconncallback = disconncallback
		self.in_queued = in_queued
		self.out_queued = out_queued
		self.prot_list = {}
		self.num_prot = 0
		reactor.listenTCP(port, self)
		#reactor.run()
	
	def start_inqueue(self,id):
		self.prot_list[id].start_inqueue()
	
	def start_outqueue(self,id):
		self.prot_list[id].start_outqueue()
	
	def send(self,id,data):
		self.prot_list[id].send(data)
	
	def loseAllConnections(self):
		for id in self.prot_list:
			self.loseConnection(id)
	
	def loseConnection(self,id):
		self.prot_list[id].loseConnection()
	
	def buildProtocol(self, addr):
		"""
		Creates the TCP connection with the client.
		Creates a ClientConn, which will call connectionMade 
		when the link is successfully created.
		"""
		thisprot = ListenPortProtocol(addr,self.callback,self.conncallback,self.disconncallback,self.in_queued,self.out_queued,self.num_prot)
		self.prot_list[self.num_prot] = thisprot
		self.num_prot = self.num_prot + 1
		return thisprot
	

	