import util
import multiprocessing
from Queue import Queue
from common import *
from comms import drone_link as dl


_default_drone_specs = ((DRONE_TYPE_SITL_VRTL, {'instance': 0, D_ATTR_HOME_LOC: (41.519408, -86.239996, 0, 0)}),)


class Mission(object):
    def __init__(self, drone_specs=_default_drone_specs, ardupath=ARDUPATH, **kwargs):
        self.ardupath = ardupath
        self.cmd_queue = Queue()
        self.cmd_worker = multiprocessing.Process(target=self._on_command)
        self.drones = {}

        for i, (d_type, d_kwargs) in enumerate(drone_specs):
            drone = dl.make_drone_link(d_type, ardupath=ardupath, **d_kwargs)
            drone.connect()

            # TODO: figure out why SYSID_THISMAV is not unique.
            d_id = '{}{}'.format(d_type, i + 1)
            drone.set_id(d_id)
            self.drones[d_id] = drone

        self.is_alive = False

    def get_drones(self):
        return self.drones

    def add_drone(self, d_type=DRONE_TYPE_SITL_VRTL, d_kwargs=None):
        if d_kwargs is None:
            d_kwargs = {}

        drone = dl.make_drone_link(d_type, **d_kwargs)
        d_id = '{}{}'.format(d_type, len(self.drones.keys()) + 1)
        drone.set_id(d_id)
        self.drones[d_id] = drone

    def start(self, *args, **kwargs):
        # do some stuff

        # start the mission
        self.start_mission(*args, **kwargs)

    def on_command(self, cmd):
        self.cmd_queue.put(cmd)

    def start_mission(self, *args, **kwargs):
        raise NotImplementedError

    def pause_mission(self, *args, **kwargs):
        raise NotImplementedError

    def stop_mission(self, *args, **kwargs):
        raise NotImplementedError

    def _on_command(self):
        raise NotImplementedError

_default_sar_vertices = ((41.519367, -86.240419),
                         (41.519277, -86.240405),
                         (41.519395, -86.239418),
                         (41.519313, -86.239417))


class SAR(Mission):
    def __init__(self, **kwargs):
        super(SAR, self).__init__(**kwargs)

    def start_mission(self, vertices=_default_sar_vertices, last_known_location=None,
                      responsiveness=RESPOND_CRITICAL):
        search_path = util.get_search_path(vertices)

    def pause_mission(self):
        pass

    def stop_mission(self, return_home=True):
        pass

    def _on_command(self):
        while self.is_alive:
            cmd = self.cmd_queue.get()
            # TODO: do something with this command

