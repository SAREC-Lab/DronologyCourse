import db
import os
import re
import json
import argparse
import datetime


_db = db.Database()


def _load_txt(p2r):
    with open(p2r) as f:
        resource = f.read()

    return resource


def _load_json(p2r):
    with open(p2r) as f:
        resource = json.load(f)

    return resource


def _parse(p2l):
    run_id = os.path.basename(p2l)
    txt = _load_txt(os.path.join(p2l, 'run[0]_LOG.txt'))
    pat = re.compile(r'.*;(\d+);(.*);(.*);(.*)')

    documents = []

    for ts, uav_id, r, f in pat.findall(txt):
        time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))
        rep = float(r)
        freq = float(f)

        documents.append({'uav': uav_id, 'time': time, 'rep': rep, 'freq': freq, 'run': run_id})

    _db.remove(collection='log', query={'run': run_id})
    _db.insert_many(collection='log', documents=documents)

    txt = _load_txt(os.path.join(p2l, 'run[0]_ERR.txt'))
    pat = re.compile(r'.*;RATE;(.*);(.*)')
    err = {k: float(v) for k,v in pat.findall(txt)}
    pat = re.compile(r'.*;(\d+);(INJECT|DETECT);(.*);(.*)')

    documents = []

    for ts, action, uav_id, assumption_id in pat.findall(txt):
        time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))
        proba = err[uav_id] if uav_id in err else None
        documents.append({'uav': uav_id, 'time': time, 'action': action, 'assumption': assumption_id.strip(),
                          'proba': proba, 'run': run_id})

    _db.remove(collection='fault', query={'run': run_id})
    _db.insert_many(collection='fault', documents=documents)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('path_to_logs',
                        type=str, help='path to folder containing logs')
    args = parser.parse_args()
    _parse(args.path_to_logs)

if __name__ == '__main__':
    main()

