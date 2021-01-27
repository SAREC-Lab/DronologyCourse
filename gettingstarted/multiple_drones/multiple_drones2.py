#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
© Copyright 2015-2016, 3D Robotics.
simple_goto.py: GUIDED mode "simple goto" example (Copter Only)
Demonstrates how to arm and takeoff in Copter and how to navigate to points using Vehicle.simple_goto.
Full documentation is provided at http://python.dronekit.io/examples/simple_goto.html
"""

#from __future__ import print_function
import time
from dronekit_sitl import SITL
from dronekit import Vehicle, VehicleMode, connect, LocationGlobalRelative

def connect_virtual_vehicle(instance, home):
    sitlx = SITL()
    sitlx.download('copter', '3.3', verbose=True)
    instance_arg = '-I%s' %(str(instance))
    print("Drone instance is: %s" % instance_arg)
    home_arg = '--home=%s, %s,%s,180' % (str(home[0]), str(home[1]), str(home[2]))
    sitl_args = [instance_arg, '--model', 'quad', home_arg]
    sitlx.launch(sitl_args, await_ready=True)
    tcp, ip, port = sitlx.connection_string().split(':')
    port = str(int(port) + instance * 10)
    conn_string = ':'.join([tcp, ip, port])
    print('Connecting to vehicle on: %s' % conn_string)

    vehicle = connect(conn_string)
    vehicle.wait_ready(timeout=120)
    print("Reached here")
    return vehicle, sitlx

def arm_and_takeoff(aTargetAltitude):
    """
    Arms vehicle and fly to aTargetAltitude.
    """
    print("Basic pre-arm checks")
    # Don't try to arm until autopilot is ready
    while not (vehicle.is_armable and vehicle2.is_armable):
        if (not vehicle.is_armable):
            print(" Waiting for vehicle 1 to initialise...")
        if (not vehicle2.is_armable):
            print(" Waiting for vehicle 2 to initialise...") 
        time.sleep(3)
 
    print("Arming motors")
    vehicle.mode = VehicleMode("GUIDED")
    vehicle.armed = True
    vehicle2.mode = VehicleMode("GUIDED")
    vehicle2.armed = True

    while not (vehicle.armed and vehicle2.armed):
        print(" Waiting for arming...")
        time.sleep(1)
    
    print("Vehicle armed!")
    print("Both drones are now Taking off!")
    vehicle.simple_takeoff(aTargetAltitude)  # Take off to target altitude
    vehicle2.simple_takeoff(aTargetAltitude)

    # Wait for both drones
    while True:
        print(" Altitude V1: ", vehicle.location.global_relative_frame.alt)
        print(" Altitude V2: ", vehicle2.location.global_relative_frame.alt)

        # Break and return from function just below target altitude.
        if vehicle.location.global_relative_frame.alt >= aTargetAltitude * 0.95 and vehicle.location.global_relative_frame.alt >= aTargetAltitude * 0.95:
            print("Both Drones Reached target altitude")
            break
        time.sleep(1)
    
vehicle, sitl = connect_virtual_vehicle(0,([41.715446209367,-86.242847096132,0]))
vehicle2, sitl2 = connect_virtual_vehicle(1,([41.715469, -86.242543,0]))

arm_and_takeoff(10) 

# Shut down simulator if it was started.
if sitl:
    sitl.stop()
    sitl2.stop()
