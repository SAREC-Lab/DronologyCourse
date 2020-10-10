#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
NED Utilities
"""
import math
from pymavlink import mavutil
import time
import numpy as np
import nvector as nv
from nvector import rad, deg
from math import sin, cos, atan2, radians, sqrt
from flight_plotter import Location

wgs84 = nv.FrameE(name='WGS84')


class Nedvalues:
    def __init__(self, north=0.0, east=0.0, down=0.0):
        """ Create a new point at the origin """
        self.north = north
        self.east = east
        self.down = down


class ned_controller:

    ################################################################################################
    # function:    Get distance in meters
    # parameters:  Two global relative locations
    # returns:     Distance in meters
    ################################################################################################
    def get_distance_meters(self, locationA, locationB):
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

    # Sends velocity vector message to UAV vehicle
    def send_ned_velocity(self, velocity_x, velocity_y, velocity_z, duration, vehicle):
        """
        Move vehicle in a direction based on specified velocity vectors.
        """
        msg = vehicle.message_factory.set_position_target_local_ned_encode(
            0,  # time_boot_ms (not used)
            0, 0,  # target system, target component
            mavutil.mavlink.MAV_FRAME_LOCAL_NED,  # frame
            0b0000111111000111,  # type_mask (only speeds enabled)
            0, 0, 0,  # x, y, z positions (not used)
            velocity_x, velocity_y, velocity_z,  # x, y, z velocity in m/s
            0, 0, 0,  # x, y, z acceleration (not supported yet, ignored in GCS_Mavlink)
            0, 0)  # yaw, yaw_rate (not supported yet, ignored in GCS_Mavlink)

        #for x in range(0, duration):
        vehicle.send_mavlink(msg)

        time.sleep(0.1)

        # Sends velocity vector message to UAV vehicle

    def send_ned_stop(self, vehicle):
        count = 1
        outerloopcounter = 1

        currentVelocity = vehicle.velocity
        north = currentVelocity[0]
        south = currentVelocity[1]
        msg = vehicle.message_factory.set_position_target_local_ned_encode(
            0,  # time_boot_ms (not used)
            0, 0,  # target system, target component
            mavutil.mavlink.MAV_FRAME_LOCAL_NED,  # frame
            0b0000111111000111,  # type_mask (only speeds enabled)
            0, 0, 0,  # x, y, z positions (not used)
            -north*100,-south*100,0,  # x, y, z velocity in m/s
            0, 0, 0,  # x, y, z acceleration (not supported yet, ignored in GCS_Mavlink)
            0, 0)  # yaw, yaw_rate (not supported yet, ignored in GCS_Mavlink)

        while True:
            if vehicle.groundspeed >  1 and outerloopcounter <= 100:
                if count == 100:
                    count = 1
                    print(vehicle.groundspeed)
                    outerloopcounter = outerloopcounter + 1
                else:
                    count = count + 1

                vehicle.send_mavlink(msg)

            if outerloopcounter == 100:
                break

        msg = vehicle.message_factory.set_position_target_local_ned_encode(
            0,  # time_boot_ms (not used)
            0, 0,  # target system, target component
         mavutil.mavlink.MAV_FRAME_LOCAL_NED,  # frame
            0b0000111111000111,  # type_mask (only speeds enabled)
         0, 0, 0,  # x, y, z positions (not used)
         0,0, 0,  # x, y, z velocity in m/s
         0, 0, 0,  # x, y, z acceleration (not supported yet, ignored in GCS_Mavlink)
         0, 0)  # yaw, yaw_rate (not supported yet, ignored in GCS_Mavlink)

        print ("MESSAGE HERE: ")
        print (msg)
        print ("++++++++++++++++++++++++++++++++++++++")

        time.sleep(0.1)

    # Sets NED given a current and target location
    def setNed(self, current, target):
        lat_C, lon_C = rad(current.lat), rad(current.lon)
        lat_T, lon_T = rad(target.lat), rad(target.lon)
        # create an n-vector for current and target
        nvecC = nv.lat_lon2n_E(lat_C, lon_C)
        nvecT = nv.lat_lon2n_E(lat_T, lon_T)
        # create a p-vec from C to T in the Earth's frame
        # the zeros are for the depth (depth = -1 * altitude)
        p_CT_E = nv.n_EA_E_and_n_EB_E2p_AB_E(nvecC, nvecT, 0, 0)
        # create a rotation matrix
        # this rotates points from the NED frame to the Earth's frame
        R_EN = nv.n_E2R_EN(nvecC)
        # rotate p_CT_E so it lines up with current's NED frame
        # we use the transpose so we can go from the Earth's frame to the NED frame
        n, e, d = np.dot(R_EN.T, p_CT_E).ravel()

        return Nedvalues(n,e,d)

def get_velocity(vehicle):
    v = vehicle.velocity
    return Nedvalues(v[0], v[1], v[2])

def rotate_velocity(ned_velocity, degrees_clockwise, preserve_down_component=True):
    """
    Returns a Nedvalues object holding the velocity vector that is rotated
    clockwise as seen from above. 

    Parameters:
        ned_velocity: a Nedvalues object representing a velocity. Must not be
            Nedvalues(0.0, 0.0, 0.0), pure up or pure down.
        degrees_clockwise: a float
        preserve_down_component: a boolean
    
    When preserve_down_component is true, the down component of the velocity is
    not changed. When it is false, the down component is changed, as the new velocity
    is found by rotating around an axis that is orthogonal to ned_velocity but
    as close to the down axis as possible.

    Here is the use case for this function:
    A drone is traveling in any direction and at any speed.  We want it to turn
    at an X degree angle and send it a new NED.

    Example 0:
    Our vehicle is flying and we want to turn 42 degrees clockwise (as seen from above).

    rotate_velocity(get_velocity(vehicle), 42.0)
    
    This would return a Nedvalues object. Its value would depend on the vehicle's
    current velocity

    Example 1:
    We have a velocity of north and 45 degrees up. We want to rotate this velocity
    180 degrees while not changing the down component. We can do so by calling:
    
    rotate_velocity(Nedvalues(3.0, 0.0, -3.0), 180.0)
    
    This would return Nedvalues(-3.0, 0.0, -3.0)

    Example 2:
    We are flying north and up at 45 degrees. We want to rotate the velocity 180
    degrees while changing down component of velocity:

    rotate_velocity(Nedvalues(3.0, 0.0, -3.0), 180.0, False)
    Would return: Nedvalues(-3.0, 0.0, 3.0)

    Example 3:
    We are flying north west at 1.4 meters per second. We want to rotate 90 degrees
    counter-clockwise:

    rotate_velocity(Nedvalues(1.0, -1.0, 0.0), -90.0)
    would return  Nedvalues(-1.0, -1.0, 0.0)
    """
    # This uses Rodrigues' rotation formula.
    # https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula
    v = np.array([ned_velocity.north, ned_velocity.east, ned_velocity.down])
    theta = math.radians(degrees_clockwise)
    down_axis = np.array([0.0, 0.0, 1.0])
    if preserve_down_component:
        k = down_axis
    else:
        left = np.cross(v, down_axis)
        k = np.cross(left, v)
        magnitude = math.sqrt(np.dot(k, k))
        k = k * (1.0 / magnitude)
    
    # https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula#Statement
    v_rot = v * math.cos(theta) + np.cross(k, v) * math.sin(theta) + k * (np.dot(k, v)) * (1.0 - math.cos(theta))
    return Nedvalues(v_rot[0], v_rot[1], v_rot[2])
