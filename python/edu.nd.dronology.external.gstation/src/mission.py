import util
import Queue
import threading
import log_util
from common import *
from comms import drone_link as dl


_LOG = log_util.get_logger('default_file')
_default_drone_specs = ((DRONE_TYPE_SITL_VRTL, {'instance': 0, D_ATTR_HOME_LOC: (41.519408, -86.239996, 0, 0)}),)


class Mission(object):
    _STATUS_WAITING = 0
    _STATUS_RUNNING = 1
    _STATUS_STOPPED = 2

    def __init__(self, queue_to_control, drone_specs=_default_drone_specs, ardupath=ARDUPATH, **kwargs):
        self.drone_specs = drone_specs
        self.ardupath = ardupath
        self.queue_to_control = queue_to_control
        self.queue_from_control = Queue.Queue()
        self.mission_worker = None
        self.cmd_worker = threading.Thread(target=self._on_command)
        self.drones = {}
        self._status = self._STATUS_WAITING
        self._status_lock = threading.Lock()

    def start(self, *args, **kwargs):
        self.drones = {}
        for i, (d_type, d_kwargs) in enumerate(self.drone_specs):
            drone = dl.make_drone_link(d_type, ardupath=self.ardupath, **d_kwargs)
            drone.connect()

            # TODO: figure out why SYSID_THISMAV is not unique.
            d_id = '{}{}'.format(d_type, i + 1)
            drone.set_id(d_id)
            self.drones[d_id] = drone

        self.cmd_worker.start()
        self.mission_worker = threading.Thread(target=self.do_mission, args=args, kwargs=kwargs)
        self.mission_worker.start()
        self._status = self._STATUS_RUNNING

    def stop(self):
        if self._status != self._STATUS_WAITING:
            self._status = self._STATUS_STOPPED
            self.cmd_worker.join()
            self.mission_worker.join()
            self.drones = {}
            self.queue_from_control.join()

        util.clean_up_run()

    def on_command(self, cmd):
        self.queue_from_control.put(cmd)

    def _on_command(self):
        raise NotImplementedError

    def do_mission(self, **kwargs):
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
    def __init__(self, *args, **kwargs):
        self.commands = []
        super(SAR, self).__init__(*args, **kwargs)

    def do_mission(self, vertices=_default_sar_vertices, last_known_location=None, responsiveness=RESPOND_CRITICAL):
        # set up the mission
        search_path = util.get_search_path(vertices)

        # start the loop
        while self._status != self._STATUS_STOPPED:
            # 1. check parameters for changes in state (from a command)
            while self.commands:
                cmd = self.commands.pop(0)
                # TODO: do something perhaps

            # 2. do the mission / move the drones

        self.queue_to_control.put(ExitCommand(MISSION, CONTROL_STATION))

    def _on_command(self):
        while self._status != self._STATUS_STOPPED:
            try:
                cmd = self.queue_from_control.get_nowait()
                self.queue_from_control.task_done()
                # check status
                if cmd['type'] in [CMD_TYPE_ERROR]:
                    # if we care about it add it to commands
                    self.commands.append(cmd['data'])
                # NOTE: commands should be popped from index 0
            except Queue.Empty:
                pass

