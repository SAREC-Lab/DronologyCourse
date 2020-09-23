##!/ usr / bin / env python
# -*- coding: utf-8 -*-

"""
Modified from 3DR simple_goto.py
Added code for flying using NED Velocity Vectors - Jane Cleland-Huang 1/15
"""
import math
import os
import time
from math import sin, cos, atan2, radians, sqrt

from dronekit import Vehicle, connect, VehicleMode, LocationGlobalRelative
from dronekit_sitl import SITL

from flight_plotter import Location, CoordinateLogger, GraphPlotter
from ned_utilities import ned_controller

def connect_virtual_vehicle(instance, home):
    sitl = SITL()
    sitl.download('copter', '3.3', verbose=True)
    instance_arg = '-I%s' %(str(instance))
    home_arg = '--home=%s, %s,%s,180' % (str(home[0]), str(home[1]), str(home[2]))
    speedup_arg = '--speedup=4'
    sitl_args = [instance_arg, '--model', 'quad', home_arg, speedup_arg]
    #sitl_args = [instance_arg, '--model', 'quad', home_arg]
    sitl.launch(sitl_args, await_ready=True)
    tcp, ip, port = sitl.connection_string().split(':')
    port = str(int(port) + instance * 10)
    conn_string = ':'.join([tcp, ip, port])
    print('Connecting to vehicle on: %s' % conn_string)

    vehicle = connect(conn_string)
    vehicle.wait_ready(timeout=120)
    print("Reached here")
    return vehicle, sitl


################################################################################################
# ARM and TAKEOFF
################################################################################################

# function:   	arm and takeoff
# parameters: 	target altitude (e.g., 10, 20)
# returns:	n/a

def arm_and_takeoff(aTargetAltitude):
    """
    Arms vehicle and fly to aTargetAltitude.
    """

    print("Basic pre-arm checks")
    # Don't try to arm until autopilot is ready
    while not vehicle.is_armable:
        print(" Waiting for vehicle to initialise...")
        time.sleep(1)

    print("home: " + str(vehicle.location.global_relative_frame.lat))

    print("Arming motors")
    # Copter should arm in GUIDED mode
    vehicle.mode = VehicleMode("GUIDED")
    vehicle.armed = True

    # Confirm vehicle armed before attempting to take off
    while not vehicle.armed:
        print(" Waiting for arming...")
        time.sleep(1)

    print("Taking off!")
    vehicle.simple_takeoff(aTargetAltitude)  # Take off to target altitude

    while True:
        # Break and return from function just below target altitude.
        if vehicle.location.global_relative_frame.alt >= aTargetAltitude * .95:
            print("Reached target altitude")
            break
        time.sleep(1)


################################################################################################
# function:    Get distance in meters
# parameters:  Two global relative locations
# returns:     Distance in meters
################################################################################################
def get_distance_meters(locationA, locationB):
    # approximate radius of earth in km
    R = 6373.0

    lat1 = radians(locationA.lat)
    lon1 = radians(locationA.lon)
    lat2 = radians(locationB.lat)
    lon2 = radians(locationB.lon)

    dlon = lon2 - lon1
    dlat = lat2 - lat1

    a = sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    distance = (R * c) * 1000

    # print("Distance (meters):", distance)
    return distance


################################################################################################
# Fly to a target location using a waypoint
################################################################################################
def fly_to(vehicle, targetLocation, groundspeed):
    print("Flying from: " + str(vehicle.location.global_frame.lat) + "," + str(
        vehicle.location.global_frame.lon) + " to " + str(targetLocation.lat) + "," + str(targetLocation.lon))
    vehicle.groundspeed = groundspeed
    currentTargetLocation = targetLocation
    vehicle.simple_goto(currentTargetLocation)
    remainingDistance = get_distance_meters(currentTargetLocation, vehicle.location.global_frame)

    while vehicle.mode.name == "GUIDED":
        remainingDistance = get_distance_meters(currentTargetLocation, vehicle.location.global_frame)
        print(remainingDistance)
        if remainingDistance < 1:
            print("Reached target")
            break
        time.sleep(1)

################################################################################################
# Given the GPS coordinates of the center of a circle, and a degree (0-360) and radius
# return a point on the circumference.
################################################################################################
def point_on_circle(radius, angle_indegrees, latitude, longitude):
    # Convert from degrees to radians
    lon = (radius * math.cos(angle_indegrees * math.pi / 180)) + longitude
    lat = (radius * math.sin(angle_indegrees * math.pi / 180)) + latitude
    return Location(lat, lon)


############################################################################################
# Main functionality
############################################################################################

vehicle, sitl = connect_virtual_vehicle(1,([41.7144367,-86.2417136,0])) #"41.7144367,-86.2417136,221,0"
start = time.time()

arm_and_takeoff(10)

# Get current location of vehicle and establish a conceptual circle around it for flying
center = Location(vehicle.location.global_relative_frame.lat,
                  vehicle.location.global_relative_frame.lon)  
radius = .0001
angle = 0  #Starting angle

# Fly to starting position using waypoint
currentLocation = center
startingPos = point_on_circle(radius*.95, angle+1, center.lat, center.lon)  # Close to first position on circle perimeter
firstTargetPosition = LocationGlobalRelative(startingPos.lat, startingPos.lon, 10)
fly_to(vehicle, firstTargetPosition, 10)

# Establish a starting angle to compute next position on circle
angle = 0

# Establish an instance of CoordinateLogger
log1 = CoordinateLogger()

# Create a NedController
nedcontroller = ned_controller()
waypoint_goto = False

# Fly from one point on the circumference to the next
while angle <= 360:

    # For NED flying compute the NED Velocity vector
    print("\nNew target " + str(angle))
    nextTarget = (point_on_circle(radius, angle, center.lat, center.lon))
    currentLocation = Location(vehicle.location.global_relative_frame.lat, vehicle.location.global_relative_frame.lon)
    distance_to_target = get_distance_meters(currentLocation, nextTarget)
    closestDistance=distance_to_target
    ned = nedcontroller.setNed(currentLocation, nextTarget)

    # Keep going until target is reached
    while distance_to_target > 1:
        currentLocation = Location(vehicle.location.global_relative_frame.lat,
                                   vehicle.location.global_relative_frame.lon)
        distance_to_target = get_distance_meters(currentLocation, nextTarget)
        print ('Current Pos: (' + str(currentLocation.lat) + "," + str(currentLocation.lon) +
               ') Target Pos: ' + str(nextTarget.lat) + ' Target  lon: ' + str(nextTarget.lon) + ' Distance: ' + str(
                    distance_to_target) + " NED: " + str(ned.north) + " " + str(ned.east))

        if distance_to_target > closestDistance: #Prevent unwanted fly-by
            break
        else:
            closestDistance = distance_to_target

        # Needed if the ned vectors don't take you to the final target i.e.,
        # You'll need to create another NED.  You could reuse the previous one
        # but recomputing it can mitigate small errors which otherwise build up.
        currentLocation = Location(vehicle.location.global_relative_frame.lat,
                                   vehicle.location.global_relative_frame.lon)
        ned = nedcontroller.setNed(currentLocation, nextTarget)

        # Either fly using waypoints or NEDs
        if waypoint_goto == True:
            firstTargetPosition = LocationGlobalRelative(nextTarget.lat, nextTarget.lon, 10)
            fly_to(vehicle, firstTargetPosition, 10)
        else:
            nedcontroller.send_ned_velocity(ned.north, ned.east, ned.down, 1, vehicle)

        #Log data
        log1.add_data(currentLocation.lat,currentLocation.lon)

    angle = angle + 2

print("Returning to Launch")
vehicle.mode = VehicleMode("RTL")
# Close vehicle object before exiting script
print("Close vehicle object")
vehicle.close()

# Shut down simulator if it was started.
if sitl is not None:
    sitl.stop()

end = time.time()
print(end - start)

###########################################################################
# Create the target coordinates
###########################################################################
log2 = CoordinateLogger()
angle = 0
while angle <= 360:
    point = (point_on_circle(radius, angle, center.lat, center.lon))
    log2.add_data(point.lat,point.lon)
    angle = angle + 1

###########################################################################
# Plot graph
###########################################################################
plotter = GraphPlotter(log1.lat_array,log1.lon_array,log2.lat_array,log2.lon_array,"Longitude","Latitude","NED vs. target Coordinates")
plotter.scatter_plot()
