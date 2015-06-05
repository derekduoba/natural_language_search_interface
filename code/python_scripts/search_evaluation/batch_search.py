'''
batch_search_generator.py generates a list of searches to be used with galago's batch-search program
'''
import argparse
import sys
sys.path.append('/home/dduoba/code/python-scripts/util')
import database
import json
import os
import time

SAVE_PATH = '/home/dduoba/data/eval/batch_search/'

def write_queries():
    querydata = []
    i = 1
    for entry in database.COLLECTION.find():
        qid = "{0}-{1}".format(SEARCH_TYPE, i)
        query = {
                    "number": qid,
                    "text": entry[QUERY_TYPE]
                }
        querydata.append(query)
        i += 1
    searchdata = {'queries': querydata}
    searchfilename = '{0}_batch_search_{1}.json'.format(SEARCH_TYPE, time.strftime("%m%d%Y"))
    searchfilepath = os.path.join(SAVE_PATH, searchfilename)
    searchfile = open(searchfilepath, 'w')
    json.dump(searchdata, searchfile)
    searchfile.close()

def python_magic():
    ''' wow python. just wow. '''
    if SEARCH_TYPE == 'NLPRMS':
        sys.exit("NLPRMS isn't implemented yet")
    return {
        'LQ': 'longquery',
        'LQ2' 'longquery2'
        'SQ': 'shortquery',
        'ORACLE': 'oraclequery',
        'NLP': 'nlpquery',
        'NLP2': 'nlpquery2',
        'LPRMS': 'longprmsquery',
        'SPRMS': 'shortprmsquery',
        'NLPRMS': 'nlprmsquery'
    }[SEARCH_TYPE]

def main():
    ''' runs the batch search list creation code'''
    global SEARCH_TYPE
    global QUERY_TYPE
    parser = argparse.ArgumentParser()
    parser.add_argument("-t", "--type", choices=['LQ', 'LQ2' 'SQ', 'ORACLE', 'NLP', 'NLP2', 'LPRMS', 'SPRMS', 'NLPRMS'], required=True, help="Valid Types: BL, LQ, 'LQ2', SQ, NLP, NLP2, LPRMS, SPRMS, NLPRMS")
    args = parser.parse_args()
    SEARCH_TYPE = args.type
    QUERY_TYPE = python_magic()
    write_queries()

if __name__ == '__main__':
    main()
