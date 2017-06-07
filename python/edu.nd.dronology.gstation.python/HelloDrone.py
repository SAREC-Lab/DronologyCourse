#! /usr/bin/env python

# # HelloDrone.py 
# Import DroneKit-Python
from dronekit import connect, VehicleMode, time

#Set up option parsing to get connection string
import argparse
# https://docs.python.org/2/library/argparse.html
parser = argparse.ArgumentParser(description='Print out vehicle state information.')
parser.add_argument('--connect',help="vehicle connection target string.")
parser.add_argument('--baud',help="baud rate of serial connection.")
args=parser.parse_args()

connection_string = args.connect
if args.baud==None:
	baud_rate = 115200
else:
	baud_rate = int(args.baud)

# Connect to the Vehicle.
# Set `wait_ready=True` to ensure default attributes are populated before `connect()` returns.
print "\nConnecting to vehicle on: %s" % connection_string
print "Baud rate: %s" % baud_rate
vehicle = connect(connection_string, wait_ready=True, baud=baud_rate)

#vehicle.wait_ready('autopilot_version')

# Get some vehicle attributes (state)
print "Get some vehicle attribute values:"
print " GPS: %s" % vehicle.gps_0
print " Battery: %s" % vehicle.battery
print " Last Heartbeat: %s" % vehicle.last_heartbeat
print " Is Armable?: %s" % vehicle.is_armable
print " System status: %s" % vehicle.system_status.state
print " Mode: %s" % vehicle.mode.name       # settable

# Close vehicle object before exiting script 
vehicle.close()

time.sleep(5)

print("Completed")