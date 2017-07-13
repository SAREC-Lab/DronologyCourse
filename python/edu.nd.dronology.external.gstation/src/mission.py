import util
import threading
import log_util
import time
from common import *


_LOG = log_util.get_logger('default_file')


class Mission(object):
    _WAITING = 1
    _IN_PROGRESS = 2
    _PAUSED = 3
    _EXIT_SUCCESS = 0
    _EXIT_FAIL = -1

    def __init__(self, *args, **kwargs):
        self.in_queue = None
        self.worker = None
        self._status = self._WAITING
        self._status_lock = threading.Lock()

    def get_status(self):
        with self._status_lock:
            return self._status

    def set_status(self, status):
        with self._status_lock:
            self._status = status

    def is_in_progress(self):
        return Mission._IN_PROGRESS == self.get_status()

    @staticmethod
    def notify_start_mission():
        _LOG.info('Mission successfully started.')

    def set_in_queue(self, in_queue):
        self.in_queue = in_queue

    def do_mission(self, drones):
        self.set_status(self._IN_PROGRESS)
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

        cont = True
        # start the loop
        while cont:
            status = self.get_status()
            if status == Mission._IN_PROGRESS:
                # 1. check parameters for changes in state (from a command)
                while not self.in_queue.empty():
                    cmd = self.in_queue.get_nowait()
                    self.in_queue.task_done()
                    # TODO: do something perhaps
            elif status == Mission._PAUSED:
                pass
                # 2. do the mission / move the drones
            else:
                cont = False

            time.sleep(0.1)

        _LOG.info('Mission Complete')

