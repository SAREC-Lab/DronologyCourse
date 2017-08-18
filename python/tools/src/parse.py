import os
import re
import json
import argparse
import datetime
import numpy as np
from collections import defaultdict as ddict

def _load_txt(p2r):
    with open(p2r) as f:
        resource = f.read()

    return resource


def _load_json(p2r):
    with open(p2r) as f:
        resource = json.load(f)

    return resource


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('path_to_logs', type=str)
    args = parser.parse_args()
    p2l = args.path_to_logs

    injected = []
    detected = []
    eval_times = ddict(list)
    inject_pat = re.compile(r'.*;(\d+);(INJECT|DETECT);(.*);(.*)')
    eval_pat = re.compile(r'.*;(\d+);(.*);(.*);(?:true|false);(.*)')
    duration_s = 30 * 60

    for i in range(20):
        faults_txt = _load_txt(os.path.join(p2l, 'run[{}]_ERR.txt'.format(i)))
        start_time = None

        for j, (ts, ttype, uav_id, a_id) in enumerate(inject_pat.findall(faults_txt)):
            if not j:
                start_time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))

            cur_time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))
            elapsed_s = (cur_time - start_time).total_seconds()
            if elapsed_s < duration_s:
                if ttype == 'INJECT':
                    injected.append((uav_id, a_id))
                else:
                    detected.append((uav_id, a_id))

        rt_txt = _load_txt(os.path.join(p2l, 'run[{}]_RT.txt'.format(i)))
        start_time = None

        for j, (ts, uav_id, a_id, e_time) in enumerate(eval_pat.findall(rt_txt)):
            if not j:
                start_time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))

            cur_time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))
            elapsed_s = (cur_time - start_time).total_seconds()
            if elapsed_s < duration_s:
                e_time_ms = float(e_time) * 1E-6
                eval_times[a_id].append(e_time_ms)

    print('Finished Processing: {}'.format(p2l))
    n_inj, n_det = len(injected), len(detected)
    print('Injected: {}, Detected: {} ({}%)'.format(n_inj, n_det, (n_det / n_inj) * 100))
    a_ids = sorted(eval_times.keys())
    data = [eval_times[k] for k in a_ids]
    data = np.array(data).T

    print(data)


if __name__ == '__main__':
    main()
