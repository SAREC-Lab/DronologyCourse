from pymongo import MongoClient

COLL_FAULTS = 'faults'


class Database:
    class __Database:
        def __init__(self):
            self.db = MongoClient().dronology

    instance = None

    def __init__(self):
        if not Database.instance:
            Database.instance = Database.__Database()

    @staticmethod
    def find(collection=COLL_FAULTS, query=None, sort=None, projection=None):
        return [x for x in Database.instance.db[collection].find(query, sort=sort, projection=projection)]

    @staticmethod
    def find_one(collection=COLL_FAULTS, query=None):
        return Database.instance.db[collection].find_one(query)

    @staticmethod
    def aggregate(collection=COLL_FAULTS, pipe=None):
        return [x for x in Database.instance.db[collection].aggregate(pipe, allowDiskUse=True)]

    @staticmethod
    def count(collection=COLL_FAULTS, query=None):
        return Database.instance.db[collection].count(query)

    @staticmethod
    def init_ordered_bulk_op(collection=COLL_FAULTS):
        return Database.instance.db[collection].initialize_ordered_bulk_op()

    @staticmethod
    def remove(collection=COLL_FAULTS, query=None):
        Database.instance.db[collection].remove(query)

    @staticmethod
    def drop(collection=COLL_FAULTS):
        Database.instance.db[collection].drop()

    @staticmethod
    def insert_many(collection=COLL_FAULTS, documents=None):
        Database.instance.db[collection].insert_many(documents)
