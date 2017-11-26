#! /usr/bin/env python

class GPSHelper():
	def __init__(self,callback):
		self.callback = callback
	
	def gotRTCMMessage(self,buf):
		self.callback('gotRTCMMessage', buf)
