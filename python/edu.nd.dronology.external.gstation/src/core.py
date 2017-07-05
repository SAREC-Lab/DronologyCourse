

class DroneStrategy(object):
    @staticmethod
    def respond(cmd, vehicle, **kwargs):
        raise NotImplementedError


class ResponsiveDrone(DroneStrategy):
    @staticmethod
    def respond(cmd, vehicle, **kwargs):
        pass
