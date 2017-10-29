import etc
from mathtools import Lla, Nvector, Pvector

def get_logger(name='default_file', p2cfg='../cfg/logging.conf'):
    return etc.get_logger(name=name, p2cfg=p2cfg)