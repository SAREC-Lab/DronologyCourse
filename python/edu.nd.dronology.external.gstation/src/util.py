import nvector
import dronekit


class Position(object):
    def laser_distance(self, other):
        pass

    def travel_distance(self, other):
        pass


class LlaCoordinate(Position):
    def __init__(self, latitude, longitude, altitude):
        self.lat = latitude
        self.lon = longitude
        self.alt = altitude

