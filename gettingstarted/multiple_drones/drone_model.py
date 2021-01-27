import json
class Drone_Model:
    lat=0
    lon=0
    def __init__(self, id, lat, lon):
        self.id = id
        self.lat = lat
        self.lon = lon

    def update_status(self, lat, lon):
        self.lat = lat
        self.lon = lon


    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,sort_keys=True, indent=4)