import os
import re
import json
import datetime
import numpy as np
import db
import pandas as pd
import matplotlib.pyplot as plt
from tabulate import tabulate
from collections import defaultdict as ddict


_db = db.Database()

P2L = '/Users/seanbayley/Desktop/final_runs'
LOG_PAT = re.compile(r'.*;(\d+);(.*);(\d+\.\d+);(\d+\.\d+)')
EVAL_PAT = re.compile(r'.*;(\d+);(.*);(.*);(?:true|false);(.*)')
ST_EVAL_PAT = re.compile(r'.*;(?:true|false);(.*)')


def _load_txt(p2r):
    with open(p2r) as f:
        resource = f.read()

    return resource


def _load_json(p2r):
    with open(p2r) as f:
        resource = json.load(f)

    return resource


def parse_eval_file(rt_txt):
    duration_s = 30 * 60
    data = ddict(list)
    start_time = None
    for j, (ts, uav_id, a_id, e_time) in enumerate(EVAL_PAT.findall(rt_txt)):
        if not j:
            start_time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))

        cur_time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))
        elapsed_s = (cur_time - start_time).total_seconds()
        if elapsed_s < duration_s:
            data[a_id].append(float(e_time) * 1E-6)

    a_ids = sorted(data.keys(), key=lambda a: int(a.split('_')[-1]))
    min_rows = min(map(lambda l: len(l), data.values()))
    data_arr = np.array([data[a][:min_rows] for a in a_ids])

    return a_ids, data_arr


def parse_eval_all():
    static_data = []
    for sc in ['TR', 'UI']:
        for i in range(2):
            sc_id = '{}_{}'.format(sc, i + 1)
            rt_txt = _load_txt(os.path.join(P2L, sc_id, 'run[0]_RT.txt'))
            a_ids, data_arr = parse_eval_file(rt_txt)
            df = pd.DataFrame(data_arr.T)
            df.to_csv(os.path.join(P2L, sc_id, '{}_eval_times.csv'.format(sc_id)), index=False, header=a_ids)

            st_txt = _load_txt(os.path.join(P2L, sc_id, 'run[0]_ST.txt'))
            st_eval_times_ms = map(lambda t: float(t) * 1E-6, ST_EVAL_PAT.findall(st_txt))
            st_eval_mean = np.mean(list(st_eval_times_ms))
            static_data.append([sc_id, st_eval_mean])

        st_eval_times_ms = []
        sc_id = '{}_3'.format(sc)
        data = ddict(list)
        for i in range(20):
            rt_txt = _load_txt(os.path.join(P2L, sc_id, 'run[{}]_RT.txt'.format(i)))
            a_ids, data_arr = parse_eval_file(rt_txt)

            for a_id, d in zip(a_ids, data_arr):
                data[a_id].extend(d)

            st_txt = _load_txt(os.path.join(P2L, sc_id, 'run[0]_ST.txt'))
            st_eval_times_ms.extend(list(map(lambda t: float(t) * 1E-6, ST_EVAL_PAT.findall(st_txt))))

        static_data.append([sc_id, np.mean(st_eval_times_ms)])
        a_ids = sorted(data.keys(), key=lambda a: int(a.split('_')[-1]))
        min_rows = min(map(lambda l: len(l), data.values()))
        data_arr = np.array([data[a][:min_rows] for a in a_ids])
        df = pd.DataFrame(data_arr.T)
        df.to_csv(os.path.join(P2L, sc_id, '{}_eval_times.csv'.format(sc_id)), index=False, header=a_ids)

    print(tabulate(static_data, headers=('run_id', 'static eval time')))


def parse_log(path_to_log):
    txt = _load_txt(path_to_log)

    periods = ddict(list)
    reputations = ddict(list)
    start_time = None
    for ts, uav, period, reputation in LOG_PAT.findall(txt):
        if not start_time:
            start_time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))

        cur_time = datetime.datetime.fromtimestamp(int(float(ts) / 1000))
        x = (cur_time - start_time).total_seconds()

        periods[uav].append((x, float(period)))
        reputations[uav].append((x, float(reputation)))

    return periods, reputations
    # uavs = sorted(list(periods.keys()))
    #
    # periods_arr = np.array([periods[u] for u in uavs]).T
    # reputations_arr = np.array([reputations[u] for u in uavs]).T
    #
    # dirpath = os.path.dirname(path_to_log)
    # run_id = os.path.basename(path_to_log).split('_')[0]
    #
    # pd.DataFrame(periods_arr).to_csv(os.path.join(dirpath, '{}_periods.csv'.format(run_id)), index=False, header=uavs)
    # pd.DataFrame(reputations_arr).to_csv(os.path.join(dirpath, '{}_reputations.csv'.format(run_id)), index=False, header=uavs)


def eval_summary_stats():
    summary = []
    for sc in ['TR', 'UI']:
        for i in range(3):
            sc_id = '{}_{}'.format(sc, i + 1)
            data = pd.read_csv(os.path.join(P2L, sc_id, '{}_eval_times.csv'.format(sc_id))).as_matrix()
            # mean_time_ms = data.mean(axis=0)
            mean_time_total_ms = data.mean()
            num_constraints_evaluated = data.shape[0] * data.shape[1]

            if i == 2:
                num_constraints_evaluated /= 20

            summary.append([sc_id, num_constraints_evaluated, mean_time_total_ms])

    print(tabulate(summary, headers=('run_id', 'n_evaluated', 'mean time')))


def show_rep_and_monitoring(targets, p2l=P2L):
    raw_header = []
    raw_rep = []
    raw_period = []

    fig, axes = plt.subplots(nrows=1, ncols=2)
    for s_id, r_id, uav_id in targets:
        path_to_log = os.path.join(p2l, s_id, 'run[{}]_LOG.txt'.format(r_id))
        periods, reputations = parse_log(path_to_log)

        x0 = [p[0] for p in reputations[uav_id]]
        y0 = [p[1] for p in reputations[uav_id]]
        x1 = [r[0] for r in periods[uav_id]]
        y1 = [r[1] for r in periods[uav_id]]

        df0 = pd.DataFrame(y0, index=x0, columns=['{},{}'.format(s_id, uav_id)])
        df1 = pd.DataFrame(y1, index=x1, columns=['{},{}'.format(s_id, uav_id)])

        df0.plot(ax=axes[0])
        df1.plot(ax=axes[1])
        raw_header.append(','.join([s_id, 'run_{}'.format(r_id), uav_id]))
        raw_rep.append(y0[:413])
        raw_period.append(y1[:413])
        # axes[0].plot(x0, y0)
        # axes[1].plot(x1, y1)

    axes[0].legend().set_visible(False)
    ax = axes[-1]
    box = ax.get_position()
    ax.set_position([box.x0, box.y0, box.width * 0.8, box.height])

    # Put a legend to the right of the current axis
    ax.legend(loc='center left', bbox_to_anchor=(1, 0.5))

    axes[0].set_title('Reputation')
    axes[1].set_title('Period')
    # plt.show()

    pd.DataFrame(np.array(raw_rep).T, columns=raw_header).to_csv('../processed/reputation.csv', index=False)
    pd.DataFrame(np.array(raw_period).T, columns=raw_header).to_csv('../processed/period.csv', index=False)


show_rep_and_monitoring([('TR_1', 0, 'UAVgZ04878'),
                         ('TR_2', 0, 'SITL_9'),
                         ('TR_3', 10, 'SITL_7'),
                         ('UI_3', 17, 'UAV426pI32')])
# parse_eval_all()
# eval_summary_stats()
# parse_log(os.path.join(p2l, 'TR_1', 'run[0]_LOG.txt'))
