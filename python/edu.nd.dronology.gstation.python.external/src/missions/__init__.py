import importlib
import json


class Mission(object):
    @staticmethod
    def start(connection, **kwargs):
        raise NotImplementedError

    @staticmethod
    def parse_args(cla):
        return {}

    @staticmethod
    def _parse_controller(control_str):
        """
        e.g. core.ArduPilot
        """
        module_id, attr = control_str.split('.')
        control = getattr(importlib.import_module(module_id), attr)

        return control

    @staticmethod
    def _parse_drone_cfg(config_str):
        with open(config_str) as f:
            cfg = json.load(f)

        return cfg

    @staticmethod
    def _parse_coord(coord):
        """
        e.g. -pls 41.519362,-86.240411
        """
        if coord:
            res = tuple(map(float, coord.split(',')))
        else:
            res = None
        return res

    @staticmethod
    def _parse_coord_bounds(bounds):
        """
        e.g. -b 41.519362,-86.240411|41.519391,-86.239414|41.519028,-86.239411|41.519007,-86.240396
        """
        coords = [Mission._parse_coord(c) for c in bounds.split('|')]
        return coords
