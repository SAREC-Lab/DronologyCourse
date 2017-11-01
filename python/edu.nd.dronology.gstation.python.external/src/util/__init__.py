import json
import etc
from mathtools import Lla, Nvector, Pvector



def get_logger(name='default_file', p2cfg='../cfg/logging.conf'):
    return etc.get_logger(name=name, p2cfg=p2cfg)


def load_drone_configs(p2dcf):
    try:
        with open(p2dcf) as f:
            return json.load(f)
    except Exception as e:
        print(e)