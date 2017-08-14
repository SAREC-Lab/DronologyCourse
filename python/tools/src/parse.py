import db
import os
import re
import json
import pickle
import argparse
import datetime


_db = db.Database()
_PDIR = '../processed'

if not os.path.exists(_PDIR):
    os.makedirs(_PDIR)

_PAT_ERR_RATE = re.compile(r'.*;RATE;(.*);(.*)')


def _load_txt(p2r):
    with open(p2r) as f:
        resource = f.read()

    return resource


def _load_json(p2r):
    with open(p2r) as f:
        resource = json.load(f)

    return resource


def _parse_err(p2f):
    txt = _load_txt(p2f)
    data = {}
    for uav_id, rate in _PAT_ERR_RATE.findall(txt):
        data[uav_id] = {'rate': float(rate)}
    pat_inject = '.*;INJECT;{};(.*)'
    pat_detect = '.*;DETECT;{};(.*)'

    for uav_id in data.keys():
        injected = re.compile(pat_inject.format(uav_id)).findall(txt)
        detected = re.compile(pat_detect.format(uav_id)).findall(txt)

        for inj in injected:
            if inj not in data[uav_id]:
                data[uav_id][inj] = {'n_injected': 0, 'n_detected': 0}

            data[uav_id][inj]['n_injected'] += 1

        for det in detected:
            if det not in data[uav_id]:
                data[uav_id][det] = {'n_injected': 0, 'n_detected': 0}

            data[uav_id][det]['n_detected'] += 1

    return data


def _parse(p2l, force):
    run_id = os.path.basename(p2l)
    out_fp = os.path.join(_PDIR, run_id + '.p')

    if not force:
        if os.path.exists(out_fp):
            exit('{} already exists: delete or run with flag -f')

    # err_data = _parse_err(os.path.join(p2l, 'run[0]_ERR.txt'))
    txt = _load_txt(os.path.join(p2l, 'run[0]_LOG.txt'))
    pat = re.compile(r'.*;(\d+);(.*);(.*);(.*)')

    documents = []

    for ts, uav_id, r, f in pat.findall(txt):
        time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))
        rep = float(r)
        freq = float(f)

        documents.append({'uav': uav_id, 'time': time, 'rep': rep, 'freq': freq, 'run': run_id})

    _db.drop('log')
    _db.insert_many(collection='log', documents=documents)

    txt = _load_txt(os.path.join(p2l, 'run[0]_ERR.txt'))
    pat = re.compile(r'.*;RATE;(.*);(.*)')
    err = {k: float(v) for k,v in pat.findall(txt)}
    pat = re.compile(r'.*;(\d+);(INJECT|DETECT);(.*);(.*)')

    documents = []

    for ts, action, uav_id, assumption_id in pat.findall(txt):
        time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))
        proba = err[uav_id] if uav_id in err else None
        documents.append({'uav': uav_id, 'time': time, 'action': action, 'assumption': assumption_id,
                          'proba': proba, 'run': run_id})

    _db.drop('fault')
    _db.insert_many(collection='fault', documents=documents)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('path_to_logs',
                        type=str, help='path to folder containing logs')
    parser.add_argument('-f', '--force',
                        action='store_true', help='force analysis (delete previous analysis if already exists)')
    parser.set_defaults(force=False)
    args = parser.parse_args()
    _parse(args.path_to_logs, args.force)

if __name__ == '__main__':
    main()

