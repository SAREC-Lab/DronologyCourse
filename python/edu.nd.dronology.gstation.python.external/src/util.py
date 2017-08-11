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


def clean_up_run():
    if os.path.exists('.sitl_temp'):
        _LOG.info('Deleting temporary sitl directory')
        shutil.rmtree('.sitl_temp')

    try:
        pids = map(int, subprocess.check_output(['pgrep', 'arducopter']).split())
        for pid in pids:
            os.kill(pid, signal.SIGINT)
        _LOG.warn('ArduCopter processes failed to shut down gracefully.')
    except subprocess.CalledProcessError:
        _LOG.debug('No ArduCopter processes found')
    #
    try:
        pids = map(int, subprocess.check_output(['pgrep', '-f', '/usr/local/bin/mavproxy.py']).split())
        for pid in pids:
            os.kill(pid, signal.SIGINT)
        _LOG.warn('MavProxy processes failed to shut down gracefully.')
    except subprocess.CalledProcessError:
        _LOG.debug('No MavProxy processes found')

