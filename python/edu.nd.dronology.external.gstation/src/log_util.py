import logging
import logging.config
import yaml


def get_logger(name, p2cfg='../cfg/logging.conf'):
    with open(p2cfg, 'r') as f:
        cfg = yaml.load(f)

    logging.config.dictConfig(cfg)

    return logging.getLogger(name)