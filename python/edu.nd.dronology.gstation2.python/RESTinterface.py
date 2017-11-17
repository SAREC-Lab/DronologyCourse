#! /usr/bin/env python

from WebHost import *
import random
import threading
import time

import traceback

# import GroundStation

class RESTinterface(WebInterface):
	def __init__(self,port,ground_station):
		self.port = port
		self.host = WebHost(self.port)
		self.ground_station = ground_station
		
		self.connect('/init_drone/',							'INIT_DRONE',					'PUT')
		self.connect('/init_drone_sitl/:inst/:home',			'INIT_DRONE_SITL',				'GET')
		self.connect('/init_drone_phys/:connect_str/:baud',		'INIT_DRONE_PHYS',				'GET')
		self.connect('/reinit_drone/:id',						'REINIT_DRONE',					'GET')
		self.connect('/remove_drone/:id',						'REMOVE_DRONE',					'GET')
	
	def INIT_DRONE(self):
		""""""
		output = {'result':'success'}
		try:
			cl = cherrypy.request.headers['Content-Length']
			body = cherrypy.request.body.read(int(cl))
			info = json.loads(body)
			output['data'] = self.ground_station.initializeNewDrone(info)
		except Exception as ex:
			output['result'] = 'error'
			output['message'] = str(ex)
			output['traceback'] = traceback.format_exc()
		return json.dumps(output, encoding='latin-1')
	def INIT_DRONE_SITL(self,inst,home):
		""""""
		output = {'result':'success'}
		try:
			info = {
				"type":"SITL",
				"ConnectionData":{
					"inst":int(inst),
					"home":home
				}
			}
			output['data'] = self.ground_station.initializeNewDrone(info)
		except Exception as ex:
			output['result'] = 'error'
			output['message'] = str(ex)
			output['traceback'] = traceback.format_exc()
		return json.dumps(output, encoding='latin-1')
	def INIT_DRONE_PHYS(self,connect_str,baud):
		""""""
		output = {'result':'success'}
		try:
			info = {
				"type":"physical",
				"ConnectionData":{
					"ConnectionString":connect_str.replace('\S','/'),
					"BaudRate":int(baud)
				}
			}
			output['data'] = self.ground_station.initializeNewDrone(info)
		except Exception as ex:
			output['result'] = 'error'
			output['message'] = str(ex)
			output['traceback'] = traceback.format_exc()
		return json.dumps(output, encoding='latin-1')
	def REINIT_DRONE(self,id):
		""""""
		output = {'result':'success'}
		try:
			output['data'] = self.ground_station.reInitializeDrone(id)
		except Exception as ex:
			output['result'] = 'error'
			output['message'] = str(ex)
			output['traceback'] = traceback.format_exc()
		return json.dumps(output, encoding='latin-1')
	def REMOVE_DRONE(self,id):
		""""""
		output = {'result':'success'}
		try:
			self.ground_station.removeDrone(id)
		except Exception as ex:
			output['result'] = 'error'
			output['message'] = str(ex)
			output['traceback'] = traceback.format_exc()
		return json.dumps(output, encoding='latin-1')
	
	def startWebHost(self):
		self.host.start_service()
	
	def stopWebHost(self):
		self.host.stop_service()
	
	def startRESTinterface(self):
		self.thr = threading.Thread(target=self.startWebHost)
		self.thr.daemon = True
		self.thr.start()
		return self.thr
	
	def stopRESTinterface(self):
		self.stopWebHost()