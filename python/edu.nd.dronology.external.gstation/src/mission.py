import util
import threading
import log_util
import time
from common import *


_LOG = log_util.get_logger('default_file')


class Mission(object):
    _WAITING = 1
    _IN_MISSION = 2
    _EXIT_SUCCESS = 0
    _EXIT_FAIL = -1

    def __init__(self, *args, **kwargs):
        self.in_queue = None
        self.worker = None
        self._status = self._WAITING

    @staticmethod
    def notify_start_mission():
        _LOG.info('Mission: mission successfully started.')

    def set_in_queue(self, in_queue):
        self.in_queue = in_queue

    def do_mission(self, drones):
        self._status = self._IN_MISSION
        self.worker = threading.Thread(target=self._do_mission, args=drones)
        self.worker.start()
        self.notify_start_mission()

    def stop(self, exit_status=_EXIT_SUCCESS):
        self._status = exit_status
        self.worker.join()

    def _do_mission(self, drones):
        raise NotImplementedError


_default_sar_vertices = ((41.519367, -86.240419, 0),
                         (41.519277, -86.240405, 0),
                         (41.519395, -86.239418, 0),
                         (41.519313, -86.239417, 0))


class SAR(Mission):
    def __init__(self, vertices=_default_sar_vertices, last_known_location=None, responsiveness=RESPOND_CRITICAL):
        self.vertices = vertices
        self.lkl = last_known_location
        self.rspns = responsiveness
        super(SAR, self).__init__()

    @staticmethod
    def notify_start_mission():
        _LOG.info('Search and Rescue mission successfully started.')

    def _do_mission(self, drones):
        # set up the mission
        vertices_ = [util.Lla(*v).to_pvector().as_array()[:-1] for v in self.vertices]
        search_path = util.get_search_path(vertices_)

        # start the loop
        while self._status == self._IN_MISSION:
            # 1. check parameters for changes in state (from a command)
            while not self.in_queue.empty():
                cmd = self.in_queue.get_nowait()
                # TODO: do something perhaps

            # 2. do the mission / move the drones



