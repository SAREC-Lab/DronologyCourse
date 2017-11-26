import logging
import logging.config
import shutil
import signal
import subprocess
import yaml
from common import *
from threading import Timer


def get_logger(name='default_file', p2cfg='../cfg/logging.conf'):
    with open(p2cfg, 'r') as f:
        cfg = yaml.load(f)

    logging.config.dictConfig(cfg)

    return logging.getLogger(name)


_LOG = get_logger('default_file')


class USBListener(object):
    def __init__(self):
        pass


class RepeatedTimer(object):
    def __init__(self, interval, func, *args, **kwargs):
        self._timer = None
        self.interval = interval
        self.function = func
        self.args = args
        self.kwargs = kwargs
        self.is_running = False
        self.start()

    def _run(self):
        self.is_running = False
        self.start()
        self.function(*self.args, **self.kwargs)

    def start(self):
        if not self.is_running:
            self._timer = Timer(self.interval, self._run)
            self._timer.start()
            self.is_running = True

    def set_interval(self, interval):
        self._timer.cancel()
        self.interval = interval
        self.start()

    def stop(self):
        self._timer.cancel()
        self.is_running = False

