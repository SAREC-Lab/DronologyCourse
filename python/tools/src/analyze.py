import numpy as np
from tabulate import tabulate
from db import Database

arr = np.array

_db = Database()


def tabulate_missing_by_uav_and_assmpt():
    pipe = [
        {'$group': {'_id': {'uav': '$uav', 'run': '$run', 'a_id': '$a_id'},
                    'n_inj': {'$sum': '$n_inj'}, 'n_det': {'$sum': '$n_det'}}},
        {'$project': {'uav': '$_id.uav', 'run': '$_id.run', 'a_id': '$_id.a_id', 'n_inj': 1, 'n_det': 1,
                      'lt': {'$lt': ['$n_inj', '$n_det']},
                      'gt': {'$gt': ['$n_inj', '$n_det']},
                      '_id': 0}},
        {'$match': {'gt': True}},
        {'$group': {'_id': '$run', 'count': {'$sum': 1}, 'uavs': {'$push': '$uav'}, 'a_ids': {'$push': '$a_id'}}}
    ]

    docs = _db.aggregate(pipe=pipe)

    data = arr([[d['_id'], d['count'], ','.join(['({},{})'.format(a, b) for a, b in zip(d['uavs'], d['a_ids'])])] for d in docs])
    order = np.argsort(data[:, 0].astype(int))
    # data.sort(axis=0)

    print(tabulate(data[order], headers=('run_id', 'num missing detects', 'uav/assumption')))


def tabulate_missing():
    pipe = [
        {'$group': {'_id': {'uav': '$uav', 'run': '$run', 'a_id': '$a_id'},
                    'n_inj': {'$sum': '$n_inj'}, 'n_det': {'$sum': '$n_det'}}},
        {'$project': {'uav': '$_id.uav', 'run': '$_id.run', 'a_id': '$_id.a_id', 'n_inj': 1, 'n_det': 1,
                      'lt': {'$lt': ['$n_inj', '$n_det']},
                      'gt': {'$gt': ['$n_inj', '$n_det']},
                      '_id': 0}},
        {'$match': {'gt': True}},
        {'$sort': {'run': 1}}
    ]

    docs = _db.aggregate(pipe=pipe)
    data = arr([[d[k] for k in ['uav', 'run', 'n_inj', 'n_det']] for d in docs])
    print(tabulate(data, headers=('uav', 'run', 'n_injected', 'n_detected')))


tabulate_missing()
