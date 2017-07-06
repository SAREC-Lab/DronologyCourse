import util
import argparse
from common import *
from comms import drone_link


_default_drone_specs = (DRONE_TYPE_SITL_VRTL, {'instance': 0, D_ATTR_HOME_LOC: (41.519408, -86.239996, 0, 0)},)


class Mission(object):
    def __init__(self, drone_specs=_default_drone_specs, ardupath=ARDUPATH, **kwargs):
        self.ardupath = ardupath
        self.drones = {}

        for i, (d_type, d_kwargs) in enumerate(drone_specs):
            drone = drone_link.make_drone_link(d_type, ardupath=ardupath, **d_kwargs)
            drone.connect()

            # TODO: figure out why SYSID_THISMAV is not unique.
            d_id = '{}{}'.format(d_type, i + 1)
            drone.set_id(d_id)
            self.drones[d_id] = drone

    def get_drones(self):
        return self.drones

    def add_drone(self, d_type=DRONE_TYPE_SITL_VRTL, d_kwargs=None):
        if d_kwargs is None:
            d_kwargs = {}

        drone = drone_link.make_drone_link(d_type, **d_kwargs)
        d_id = '{}{}'.format(d_type, len(self.drones.keys()) + 1)
        drone.set_id(d_id)
        self.drones[d_id] = drone

    def start_mission(self, *args, **kwargs):
        raise NotImplementedError

    def pause_mission(self, *args, **kwargs):
        raise NotImplementedError

    def stop_mission(self, *args, **kwargs):
        raise NotImplementedError

    def on_command(self, cmd):
        raise NotImplementedError


_default_sar_bounds = ((41.519367, -86.240419),
                       (41.519277, -86.240405),
                       (41.519395, -86.239418),
                       (41.519313, -86.239417))


class SAR(Mission):
    def __init__(self, **kwargs):
        super(SAR, self).__init__(**kwargs)

    def start_mission(self, bounds=_default_sar_bounds, last_known_location=None, responsiveness=RESPOND_CRITICAL_ONLY):
        raise NotImplementedError

    def pause_mission(self):
        pass

    def stop_mission(self, return_home=True):
        pass

    def on_command(self, cmd):
        pass
