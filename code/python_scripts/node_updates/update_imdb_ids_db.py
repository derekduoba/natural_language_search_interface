''' Updates movie ids generated with imdbpy with their actual imdb counterparts '''

import sys
sys.path.append('/home/dduoba/code/python-scripts/util')
import database
import pymongo
from sqlalchemy import create_engine, Integer, Table, Column, MetaData
import sqlalchemy.sql
import time
import traceback

XML_NODE_PATH = '/home/dduoba/data/xmlfiles/'

def get_movie(currentsession, title, year):
    ''' get a movie from the database '''
    metadata = MetaData()
    titletable = Table("title", metadata,
        Column('id', Integer),
        Column('title', sqlalchemy.types.TEXT),
        Column('production_year', Integer),
        Column('kind_id', Integer)
    )
    # select "movies" (1) and "videos" (4) from the title table based on production year and name
    movie = currentsession.query(titletable).filter(titletable.c.title == title, titletable.c.production_year == year, sqlalchemy.sql.or_(titletable.c.kind_id == 1, titletable.c.kind_id == 4))
    return movie

def update_querydocs(currentsession):
    ''' find and update movies in the mongo DB'''
    for entry in database.COLLECTION.find():
        for imdbid, movie in entry['movies'].iteritems():
            currentmovie = get_movie(currentsession, movie['Title'], movie['Year'])
            if currentmovie.count() == 1:
                xmlnode = currentmovie.first()
                print "FOUND XML NODE ID (%s) FOR MOVIE %s" % (xmlnode[0], movie['Title'])
                dotpath = "movies." + imdbid + ".xmlid"
                database.COLLECTION.update_one({'_id': entry['_id']}, {'$set': {dotpath: xmlnode[0]}}, upsert=False)
            elif currentmovie.count() > 1:
                xmlnode = currentmovie.first()
                print "FOUND MULTIPLE XML NODE IDS (%s) FOR MOVIE %s. USING %s" % (currentmovie.count(), movie['Title'], xmlnode[0])
                dotpath = "movies." + imdbid + ".xmlid"
                database.COLLECTION.update_one({'_id': entry['_id']}, {'$set': {dotpath: xmlnode[0]}}, upsert=False)
            else:
                print "XML NODE FOR MOVIE %s NOT FOUND" % movie['Title']

def main():
    ''' runs the movie id translation process '''
    starttime = time.time()
    print "STARTING RUN"

    # Run through all query docs in mongo + associate them with an xmlnode id
    currentsession = database.initiate_mysql_connection()
    update_querydocs(currentsession)

if __name__ == '__main__':
    main()

