#! /usr/bin/env python

import dronekit

class Coordinate(object):
	def __init__(self,x,y,z):
		self.x = x
		self.y = y
		self.z = z
	
	def getX(self):
		return self.x
	
	def getY(self):
		return self.y
	
	def getZ(self):
		return self.z
	
	def toGlobalRelative(self):
		return dronekit.LocationGlobalRelative(self.x, self.y, self.z)
	
	def toGlobal(self):
		return dronekit.LocationGlobal(self.x, self.y, self.z)
	
	def toDict(self):
		return {
			'x': self.x,
			'y': self.y,
			'z': self.z,
		}
	

def CoordFromLocation(loc):
	if loc==None:
		return Coordinate(0,0,0)
	return Coordinate(loc.lat,loc.lon,loc.alt)

def CoordFromNED(loc):
	if loc==None:
		return Coordinate(0,0,0)
	down = loc.down
	if down!=None:
		down = -1*down
	return Coordinate(loc.north,loc.east,down)

def CoordFromRotation(rot):
	if rot==None or rot.pitch==None:
		return Coordinate(0,0,0)
	return Coordinate(rot.pitch,rot.yaw,rot.roll)

def CoordFromList(list):
	if list==None:
		return Coordinate(0,0,0)
	return Coordinate(list[0],list[1],list[2])

def CoordFromDict(dict):
	if dict==None:
		return Coordinate(0,0,0)
	return Coordinate(dict['x'],dict['y'],dict['z'])
