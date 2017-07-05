#! /usr/bin/env python

# A simplified set of classes for managing TCP connections using the Twisted Library
# created by Joshua Huseman, jhuseman@nd.edu, for CSE30332 Programming Paradigms
# Note: many docstrings may be copied from other locations and be misleading!
import threading
import json
import os
from twisted.internet.protocol import ClientFactory
from twisted.internet.protocol import Factory
from twisted.internet.protocol import Protocol
from twisted.internet.defer import DeferredQueue
from twisted.internet import reactor


class HostConnectProtocol(Protocol):
    def __init__(self, host, port, callback, conncallback, disconncallback, in_queued, out_queued):
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
            host=self.host,
            port=self.port,
        )
        self.conncallback()

    def dataReceived(self, data):
        """
        Runs after data is received from the server.
        Adds the received data to the client queue.
        """
        self.inqueue.put({"connected": True, "data": data})

    def connectionFailed(self, reason):
        """
        Connection failed! Print error message
        and add disconnect signal to the client queue.
        """
        print "Connection to server failed!"
        self.inqueue.put({"connected": False})

    def connectionLost(self, reason):
        """
        Connection lost. Most likely torn down by server.
        Print message and add disconnect signal
        to the client queue.
        """
        print "lost connection to {host} port {port}".format(
            host=self.host,
            port=self.port,
        )
        self.inqueue.put({"connected": False})

    def start_inqueue(self):
        if self.in_queued:
            self.inqueue.get().addCallback(self.inqueue_received)

    def start_outqueue(self):
        if self.out_queued:
            self.outqueue.get().addCallback(self.outqueue_received)

    def send(self, data):
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
        if result["connected"] == True:
            self.callback(result["data"])
            self.inqueue.get().addCallback(self.inqueue_received)
        else:
            self.disconncallback()


class HostConnect(ClientFactory):
    def __init__(self, host, port, callback, conncallback, disconncallback, in_queued, out_queued):
        """
        Initialize the connection.
        Creates the TCP connection with the server, then
        starts waiting for queued data from client.
        Creating the TCP connection implicitly runs
        connectionMade when the link is successfully created.
        """
        self.conn = HostConnectProtocol(host, port, callback, conncallback, disconncallback, in_queued, out_queued)
        reactor.connectTCP(host, port, self)

    # reactor.run()

    def start_inqueue(self):
        self.conn.start_inqueue()

    def start_outqueue(self):
        self.conn.start_outqueue()

    def send(self, data):
        self.conn.send(data)

    def loseConnection(self):
        self.conn.loseConnection()

    def buildProtocol(self, addr):
        return self.conn


class ListenPortProtocol(Protocol):
    def __init__(self, addr, callback, conncallback, disconncallback, in_queued, out_queued, id):
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
            addr=self.addr,
        )
        self.conncallback(self.id)

    def dataReceived(self, data):
        """
        Runs after data is received from the server.
        Adds the received data to the client queue.
        """
        if self.in_queued:
            self.inqueue.put({"connected": True, "data": data})
        else:
            self.callback(self.id, data)

    def connectionFailed(self, reason):
        """
        Connection failed! Print error message
        and add disconnect signal to the client queue.
        """
        print "Connection to server failed!"
        self.inqueue.put({"connected": False})

    def connectionLost(self, reason):
        """
        Connection lost. Most likely torn down by server.
        Print message and add disconnect signal
        to the client queue.
        """
        print "connection lost from {addr}".format(
            addr=self.addr,
        )
        self.inqueue.put({"connected": False})

    def start_inqueue(self):
        if self.in_queued:
            self.inqueue.get().addCallback(self.inqueue_received)

    def start_outqueue(self):
        if self.out_queued:
            self.outqueue.get().addCallback(self.outqueue_received)

    def send(self, data):
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
        if result["connected"] == True:
            self.callback(self.id, result["data"])
            self.inqueue.get().addCallback(self.inqueue_received)
        else:
            self.disconncallback(self.id)


class ListenPort(Factory):
    def __init__(self, port, callback, conncallback, disconncallback, in_queued, out_queued):
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

    # reactor.run()

    def start_inqueue(self, id):
        self.prot_list[id].start_inqueue()

    def start_outqueue(self, id):
        self.prot_list[id].start_outqueue()

    def send(self, id, data):
        print(data)
        self.prot_list[id].send(data)

    def loseAllConnections(self):
        for id in self.prot_list:
            self.loseConnection(id)

    def loseConnection(self, id):
        self.prot_list[id].loseConnection()

    def buildProtocol(self, addr):
        """
        Creates the TCP connection with the client.
        Creates a ClientConn, which will call connectionMade
        when the link is successfully created.
        """
        thisprot = ListenPortProtocol(addr, self.callback, self.conncallback, self.disconncallback, self.in_queued,
                                      self.out_queued, self.num_prot)
        self.prot_list[self.num_prot] = thisprot
        self.num_prot = self.num_prot + 1
        return thisprot


class JavaComm():
    def __init__(self, port):
        """
        start listening for connections from Java code
        """
        self.port = port
        self.last_id = None
        self.conn = ListenPort(self.port, self.received, self.connected, self.disconnect, True, True)
        print "Java connection listening on port {port}".format(port=port)
        self.received_data = {}
        self.data_handler = None

    def startComm(self):
        # self.get_and_send_async()
        reactor.run()

    def connected(self, id):
        """
        called when connection made from java program
        start accepting input for control
        """
        print "Java connected on id {id}".format(id=id)
        self.received_data[id] = ''
        self.last_id = id
        self.conn.start_inqueue(id)
        self.conn.start_outqueue(id)

    def disconnect(self, id):
        """
        called when connection lost from java program
        """
        print "Java lost connection on id {id}".format(id=id)
        del self.received_data[id]

    def received(self, id, data):
        """
        Callback function for received data
        """
        self.received_data[id] = self.received_data[id] + data
        self.parseReceivedData(id)

    # print "command received data on id {id}:".format(id=id)
    # print data
    # sys.stdout.write(data)
    # print "{Not shown to reduce output!}"

    def parseReceivedData(self, id):
        pos = self.received_data[id].find('\r')
        if pos >= 0:
            data_chunk = self.received_data[id][0:pos]
            self.received_data[id] = self.received_data[id][(pos + 1):]
            data = json.loads(data_chunk)
            self.handleData(id, data)

            self.parseReceivedData(id)  # might still have data left to parse after this, so recurse to keep on parsing

    def handleData(self, id, data):
        print "received:"
        print data
        if self.data_handler != None:
            self.data_handler(id, data['type'], data['data'])

    def setDataHandler(self, handler):
        self.data_handler = handler

    def send(self, id, data):
        self.conn.send(id, data)
        self.conn.send(id, os.linesep)

    def send_last(self, data):
        if (self.last_id != None):
            self.send(self.last_id, data)

    def send_dict(self, id, dict):
        data = json.dumps(dict)
        self.send(id, data)

    def send_dict_last(self, dict):
        if (self.last_id != None):
            self.send_dict(self.last_id, dict)

    def get_and_send(self):
        while True:
            line = raw_input()
            self.send_last(line)

    def get_and_send_async(self):
        threading.Thread(target=self.get_and_send).start()


if __name__ == "__main__":
    comm = JavaComm(1234)

