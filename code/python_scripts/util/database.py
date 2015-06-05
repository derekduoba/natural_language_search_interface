''' database: contains all utility functions for connecting to mongo + mysql dbs '''

import imdb
import pymongo
from sqlalchemy import create_engine, Integer, Table, Column, MetaData
from sqlalchemy.orm import sessionmaker

XML_NODE_PATH = '/home/dduoba/data/xmlfiles/'
MYSQL_URI = 'mysql://root:password@localhost/imdb'

MONGO_CLIENT = pymongo.MongoClient("mongodb://localhost:27017")
DB = MONGO_CLIENT['querydata']
COLLECTION = DB['querycollection']


def initiate_mysql_connection():
    ''' initialize a DB engine + session for the current process'''
    currentengine = create_engine(MYSQL_URI, echo=False)
    currentsessionclass = sessionmaker(bind=currentengine)
    currentsession = currentsessionclass()
    return currentsession

def initiate_imdb_connection():
    ''' conects to the local mysql imdb database using the imdpy library '''
    return imdb.IMDb('sql', uri=MYSQL_URI)
