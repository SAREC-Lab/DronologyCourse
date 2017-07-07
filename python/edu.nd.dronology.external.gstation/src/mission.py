import util
import Queue
import threading
from common import *
from comms import drone_link as dl


_default_drone_specs = ((DRONE_TYPE_SITL_VRTL, {'instance': 0, D_ATTR_HOME_LOC: (41.519408, -86.239996, 0, 0)}),)


class Mission(object):
    def __init__(self, drone_specs=_default_drone_specs, ardupath=ARDUPATH, **kwargs):
        self.drone_specs = drone_specs
        self.ardupath = ardupath
        self.cmd_queue = None
        self.cmd_worker = None
        self.mission_worker = None
        self.drones = {}
        self.is_alive = False

    def start(self, *args, **kwargs):
        # do some stuff
        self.is_alive = True
        self.drones = {}
        for i, (d_type, d_kwargs) in enumerate(self.drone_specs):
            drone = dl.make_drone_link(d_type, ardupath=self.ardupath, **d_kwargs)
            drone.connect()

            # TODO: figure out why SYSID_THISMAV is not unique.
            d_id = '{}{}'.format(d_type, i + 1)
            drone.set_id(d_id)
            self.drones[d_id] = drone

        self.cmd_queue = Queue.Queue()
        self.cmd_worker = threading.Thread(target=self._on_command)
        self.cmd_worker.start()
        self.mission_worker = threading.Thread(target=self.do_mission, args=args, kwargs=kwargs)
        self.mission_worker.start()

    def stop(self):
        self.is_alive = False
        self.drones = {}
        util.clean_up_run()
        self.cmd_worker.join()
        self.mission_worker.join()

    def on_command(self, cmd):
        self.cmd_queue.put(cmd)

    def do_mission(self, **kwargs):
        raise NotImplementedError

    def _on_command(self):
        raise NotImplementedError

    def get_drones(self):
        return self.drones

    def add_drone(self, d_type=DRONE_TYPE_SITL_VRTL, d_kwargs=None):
        if d_kwargs is None:
            d_kwargs = {}

        drone = dl.make_drone_link(d_type, **d_kwargs)
        d_id = '{}{}'.format(d_type, len(self.drones.keys()) + 1)
        drone.set_id(d_id)
        self.drones[d_id] = drone


_default_sar_vertices = ((41.519367, -86.240419),
                         (41.519277, -86.240405),
                         (41.519395, -86.239418),
                         (41.519313, -86.239417))


class SAR(Mission):
    def __init__(self, **kwargs):
        self.commands = []
        super(SAR, self).__init__(**kwargs)

    def do_mission(self, vertices=_default_sar_vertices, last_known_location=None, responsiveness=RESPOND_CRITICAL):
        # set up the mission
        search_path = util.get_search_path(vertices)

        # start the loop
        while self.is_alive:
            # 1. check parameters for changes in state (from a command)
            while self.commands:
                cmd = self.commands.pop(0)

            # 2. do the mission / move the drones

    def _on_command(self):
        while self.is_alive:
            try:
                cmd = self.cmd_queue.get_nowait()
                # check status
                if cmd['type'] in [CMD_TYPE_ERROR]:
                    # if we care about it add it to commands
                    self.commands.append(cmd['data'])
                # NOTE: commands should be popped from index 0
            except Queue.Empty:
                pass

