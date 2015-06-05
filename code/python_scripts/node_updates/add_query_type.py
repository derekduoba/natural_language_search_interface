'''
add_query_type.py adds a query type to the querycollection collection in mongo
This currently defaults to #prms queries, but could easily be extended to support
other types as well.
'''
import sys
sys.path.append('/home/dduoba/code/python-scripts/util')
import argparse
import database
import re

'''
NOTE: This method signature is used in a bunch of places (to do marginally different things)
and should probably be consolidated
'''
def update_querydocs():
    ''' find and update movies in the mongo DB'''
    for entry in database.COLLECTION.find():
        query = entry[QUERY_TYPE['source']].lower()

        print "Processing Query: {0}".format(query)

        galagoquery = create_prms_query(query)

        print galagoquery

        database.COLLECTION.update_one({'_id': entry['_id']}, {'$set': {QUERY_TYPE['result']: galagoquery}}, upsert=False)

'''
def fix_long_short_queries():
    for entry in database.COLLECTION.find():
        longquery = entry['longquery']
        shortquery = entry['shortquery']

        print "Processing Query: {0}".format(shortquery)
        fixedshortquery = re.search('(?<=\#prms\().*?(?=\))', shortquery)
        print fixedshortquery.group(0)

        print "Processing Query: {0}".format(longquery)
        fixedlongquery = re.search('(?<=\#prms\().*?(?=\))', longquery)
        print fixedlongquery.group(0)

        database.COLLECTION.update_one({'_id': entry['_id']}, {'$set': {'longquery': fixedlongquery.group(0), 'shortquery': fixedshortquery.group(0)} }, upsert=False)
'''


def create_prms_query(query):
    ''' Wrap a query in a #prms() tag '''
    prmsquery = "#prms({0})".format(query)
    return prmsquery


def python_magic():
    ''' this function had me like '''
    return {
            'LPRMS': {'source': 'longquery', 'result': 'longprmsquery'},
            'SPRMS': {'source': 'shortquery', 'result':'shortprmsquery'},
    }[SEARCH_TYPE]


def main():
    global SEARCH_TYPE
    global QUERY_TYPE

    parser = argparse.ArgumentParser()
    parser.add_argument("-t", "--type", choices=['LPRMS','SPRMS'], required=True, help="Valid Types: LRMS, SPRMS")
    args = parser.parse_args()
    SEARCH_TYPE = args.type
    QUERY_TYPE = python_magic()

    print "Updating querydocs with PRMS queries"
    update_querydocs()
    print "DONE!"

if __name__ == "__main__":
    main()
