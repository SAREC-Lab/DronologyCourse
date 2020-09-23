#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
© Copyright 2015-2016, 3D Robotics.
simple_goto.py: GUIDED mode "simple goto" example (Copter Only)
Demonstrates how to arm and takeoff in Copter and how to navigate to points using Vehicle.simple_goto.
Full documentation is provided at http://python.dronekit.io/examples/simple_goto.html
"""

from __future__ import print_function
import time
from dronekit_sitl import SITL
from dronekit import Vehicle, VehicleMode, connect, LocationGlobalRelative


import json
from websocket import create_connection
from drone_model import Drone_Model
ws = create_connection("ws://localhost:8000")

drone_model_object =  Drone_Model(1,0,0)

# Set up option parsing to get connection string
import argparse
parser = argparse.ArgumentParser(description='Commands vehicle using vehicle.simple_goto.')
parser.add_argument('--connect',
                    help="Vehicle connection target string. If not specified, SITL automatically started and used.")
args = parser.parse_args()
connection_string = args.connect
sitl = None


# Start SITL if no connection string specified
# This technique for starting SITL allows us to specify defffaults 
if not connection_string:
    sitl_defaults = '~/git/ardupilot/tools/autotest/default_params/copter.parm'
    sitl = SITL()
    sitl.download('copter', '3.3', verbose=True)
    sitl_args = ['-I0', '--model', 'quad', '--home=-41.714469, -86.241786,0,180']
    sitl.launch(sitl_args, await_ready=True, restart=True)
    connection_string = 'tcp:127.0.0.1:5760'

# Connect to the Vehicle
print('Connecting to vehicle on: %s' % connection_string)
vehicle = connect(connection_string, wait_ready=True, baud=57600)
print ('Current position of vehicle is: %s' % vehicle.location.global_frame)


def custom_sleep(drone_model, sleep_time):
    current_time = 0
    while(current_time<sleep_time):
        lat = vehicle.location.global_relative_frame.lat
        lon = vehicle.location.global_relative_frame.lon
        drone_model.update_status(lat,lon)
        ws.send(drone_model.toJSON())
        print('Current location is: {0},{1}'.format(lat,lon))
        time.sleep(1)
        current_time+=1

def arm_and_takeoff(aTargetAltitude):
    """
    Arms vehicle and fly to aTargetAltitude.
    """

    print("Basic pre-arm checks")
    # Don't try to arm until autopilot is ready
    while not vehicle.is_armable:
        print(" Waiting for vehicle to initialise...")
        time.sleep(3)
        print("Arming motors")
        vehicle.mode = VehicleMode("GUIDED")
        vehicle.armed = True

    while not vehicle.armed:
        print(" Waiting for arming...")
        time.sleep(1)
    
    print("Vehicle armed!")
    print("Taking off!")
    vehicle.simple_takeoff(aTargetAltitude)  # Take off to target altitude

    # Wait until the vehicle reaches a safe height before processing the goto
    #  (otherwise the command after Vehicle.simple_takeoff will execute
    #   immediately).
    while True:
        print(" Altitude: ", vehicle.location.global_relative_frame.alt)
        drone_model_object.update_status(vehicle.location.global_relative_frame.lat, vehicle.location.global_relative_frame.lon)
        ws.send(drone_model_object.toJSON())
        # Break and return from function just below target altitude.
        if vehicle.location.global_relative_frame.alt >= aTargetAltitude * 0.95:
            print("Reached target altitude")
            break
        time.sleep(1)




arm_and_takeoff(10)




print("Set default/target airspeed to 3")
vehicle.airspeed = 3

print("Going towards first point for 30 seconds ...")
point1 = LocationGlobalRelative(41.714469, -86.241786, 20)
vehicle.simple_goto(point1)

# sleep so we can see the change in map
#time.sleep(30)
custom_sleep(drone_model_object,30)


print("Going towards second point for 30 seconds (groundspeed set to 10 m/s) ...")
point2 = LocationGlobalRelative(-41.714500, -86.241650, 20)
vehicle.simple_goto(point2, groundspeed=10)

# sleep so we can see the change in map
#time.sleep(30)
custom_sleep(drone_model_object,30)

print("Returning to Launch")
vehicle.mode = VehicleMode("RTL")

# Close vehicle object before exiting script
print("Close vehicle object")
vehicle.close()

# Shut down simulator if it was started.
if sitl:
    sitl.stop()











