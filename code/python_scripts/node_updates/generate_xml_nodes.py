''' generate_xml_nodes generates a list of xml trees from the imdb database '''

import sys
sys.path.append('/home/dduoba/code/python-scripts/util')
import database
from fractions import Fraction
import imdb
from lxml import etree
import multiprocessing
from multiprocessing import Process
import os
from sqlalchemy import create_engine, Integer, Table, Column, MetaData
import time
import traceback
from xml.etree import ElementTree

SAVE_PATH = '/home/dduoba/xmlfiles/'
PROCESS_COUNT = multiprocessing.cpu_count()

def count_records(currentsession):
    ''' count the number of movie titles in the database '''
    metadata = MetaData()
    titletable = Table("title", metadata, Column('id', Integer))
    return currentsession.query(titletable).count()

def create_processes(recordcount):
    ''' create the processes that will be used to create xmlnodes '''
    processes = []
    start = 1
    for proc in range(1, (PROCESS_COUNT + 1)):
        processingfraction = Fraction(proc, PROCESS_COUNT)
        end = int(processingfraction*recordcount)
        currentprocess = Process(target=create_nodes, args=(start, end))
        processes.append(currentprocess)
        start = end
    return processes

def create_nodes(start, stop):
    ''' creates XML representations of each movie in IMDB '''
    localdb = database.initiate_imdb_connection()
    for mid in range(start, stop):
        moviefile = 'movie-%d' % mid
        moviefilepath = os.path.join(SAVE_PATH, moviefile + '.xml')
        if not os.path.isfile(moviefilepath):
            result = localdb.get_movie(mid)
            parser = etree.XMLParser(remove_blank_text=True, recover=True, huge_tree=True, encoding='latin1')
            imdbfile = open(moviefilepath, 'w')
            try:
                tree = ElementTree.XML(result.asXML(), parser)
                imdbfile.write(etree.tostring(tree, pretty_print=True))
            except Exception:
                print "ERROR: COULD NOT PARSE MOVIE WITH ID: %d" % mid
                print "TRACEBACK:"
                print traceback.format_exc()
            imdbfile.close()


def main():
    ''' runs the xml node creation code '''
    starttime = time.time()
    print "STARTING RUN WITH %d PROCESSES" % PROCESS_COUNT

    recordsession = database.initiate_mysql_connection()
    recordcount = count_records(recordsession)
    print "CREATING NODES FOR %d MOVIES" % recordcount

    processbatch = create_processes(recordcount)
    [x.start() for x in processbatch]
    [x.join() for x in processbatch]

    endtime = time.time()
    elapsedtime = (endtime - starttime)/60
    print "JOB FINISHED IN %d MINUTES" % elapsedtime


if __name__ == '__main__':
    main()

