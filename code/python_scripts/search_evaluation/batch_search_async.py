'''
batch_search_generator.py generates a list of searches to be used with galago's batch-search program
I decided to experiment with asynchonous db connections via motor in this module
'''
import getopt
import json
import motor
import os
import sys
import time
from tornado import gen
from tornado.ioloop import IOLoop

SAVE_PATH = '/home/dduoba/'
MONGO_CLIENT = motor.MotorClient("mongodb://localhost:27017")
DB = MONGO_CLIENT['querydata']
COLLECTION = DB['querycollection']

@gen.coroutine
def get_documents():
    ''' get some documents from the database'''
    cursor = COLLECTION.find({}, {'longquery': 1, '_id': 0})
    documentcollection = yield cursor.to_list(length=100)
    write_queries(documentcollection)


def write_queries(documentcollection):
    querydata = []
    i = 1
    for entry in documentcollection:
        qid = "{0}-{1}".format(qidprefix, i)
        query = {
                    "number": qid,
                    "text": entry['longquery']
                }
        querydata.append(query)
        i += 1
    searchdata = {'queries': querydata}
    searchfilepath = os.path.join(SAVE_PATH, searchtype + '_batch_search_' + time.strftime("%m%d%Y") + '.json')
    searchfile = open(searchfilepath, 'w')
    json.dump(searchdata, searchfile)
    searchfile.close()

@gen.coroutine
def main(argv):
    ''' runs the batch search list creation code'''
    global searchtype

    try:
        opts, args = getopt.getopt(argv, "t:")
    except getopt.GetoptError:
        print "batch_search.py -t <search type>"
        print "Valid Types: BL, LQ, SQ, PMRS, NLPMRS"

    for opt, arg in opts:
        if opt == "-t":
            searchtype = arg

    print searchtype
    print arg

    #IOLoop.current().run_sync(get_documents)

if __name__ == '__main__':
    main()
