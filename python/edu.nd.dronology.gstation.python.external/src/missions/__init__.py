import importlib
import json


class Mission(object):
    @staticmethod
    def start(self, connection, **kwargs):
        raise NotImplementedError

    @staticmethod
    def parse_args(cla):
        raise NotImplementedError

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
