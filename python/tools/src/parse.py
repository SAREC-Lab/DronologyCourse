import os
import re
import json
import argparse
import datetime
import numpy as np
import db
from collections import defaultdict as ddict


_db = db.Database()


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

    inj_det = ddict(lambda: ddict(lambda: ddict(int)))
    n_inj = 0
    n_det = 0
    eval_times = ddict(list)
    inject_pat = re.compile(r'.*;(\d+);(INJECT|DETECT);(.*);(.*)')
    eval_pat = re.compile(r'.*;(\d+);(.*);(.*);(?:true|false);(.*)')
    duration_s = 30 * 60
    inj_det_docs = []

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
                    inj_det_docs.append({'uav': uav_id, 'a_id': a_id, 'n_inj': 1, 'n_det': 0, 'ts': cur_time, 'run': i + 1})
                    n_inj += 1
                else:
                    inj_det_docs.append({'uav': uav_id, 'a_id': a_id, 'n_inj': 0, 'n_det': 1, 'ts': cur_time, 'run': i + 1})
                    n_det += 1

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

    _db.drop()
    _db.insert_many(documents=inj_det_docs)
    print('Finished Processing: {}'.format(p2l))
    print('Injected: {}, Detected: {} ({}%)'.format(n_inj, n_det, (n_det / n_inj) * 100))
    a_ids = sorted(eval_times.keys())
    data = [eval_times[k] for k in a_ids]
    N = max(map(lambda l: len(l), data))

    for d in data:
        n = len(d)
        d.extend([''] * (N - n))



    # print(data)


if __name__ == '__main__':
    main()
