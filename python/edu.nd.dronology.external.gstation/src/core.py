

class Mission(object):
    def __init__(self):
        pass


class SAR(Mission):
    def __init__(self):
        super(SAR, self).__init__()


class DroneStrategy(object):
    @staticmethod
    def respond(cmd, vehicle, **kwargs):
        raise NotImplementedError


class ResponsiveDrone(DroneStrategy):
    @staticmethod
    def respond(cmd, vehicle, **kwargs):
        pass
