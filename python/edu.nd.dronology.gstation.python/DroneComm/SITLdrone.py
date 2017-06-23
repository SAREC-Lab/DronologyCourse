#! /usr/bin/env python

import subprocess

class SITLdrone(object):
	def __init__(self,ConnectionData):
		self.ConnectionData = ConnectionData
		self.PhysicalConnectionData = {}
		self.instance = 0
		if 'inst' in self.ConnectionData:
			self.instance = self.ConnectionData['inst']
			if 'home' in self.ConnectionData:
				subprocess.call(['./DroneComm/startSITL.sh', str(self.ConnectionData['inst']), self.ConnectionData['home']])
			else:
				subprocess.call(['./DroneComm/startSITL.sh', str(self.ConnectionData['inst'])])
		else:
			subprocess.call(['./DroneComm/startSITL.sh'])
		self.PhysicalConnectionData['ConnectionString'] = "127.0.0.1:" + str(14550 + 10*self.instance)
	
	def __del__(self):
		subprocess.call(['./DroneComm/stopSITL.sh'])
	
	def getPhysicalConnectionData(self):
		return self.PhysicalConnectionData
	
