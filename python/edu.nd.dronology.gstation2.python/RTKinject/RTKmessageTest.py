#! /usr/bin/env python

import GPSProvider

def printMessage(message):
	print message

rtk = GPSProvider.GPSProvider('/dev/ttyACM0',115200)
rtk.registerNewCallback(printMessage)
rtk.run()
