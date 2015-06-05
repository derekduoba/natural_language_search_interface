'''
Updates movie ids generated with imdbpy with their actual imdb counterparts
WARNING:
This script will take forever to run sinc eit opens, reads, and closes millions of files;
Use the faster _db variant instead

Q: Why does this exist?
A: HASHMAPS
'''

import sys
sys.path.append('/home/dduoba/code/python-scripts/util')
import database
from bson import json_util
import hashlib
import json
from lxml import etree
import os
import pymongo
import traceback

XML_NODE_PATH = '/home/dduoba/data/xmlfiles/'
PARSE_COUNT = 0
SKIP_COUNT = 0

def hash_node(title, year):
    ''' hashes a movie based on it's title and year '''
    hashobject = hashlib.md5()
    hashstring = title + year
    hashobject.update(hashstring)
    return hashobject.hexdigest()

def parse_node(xmlfilepath):
    '''extracts the movie name, id, and title from an xml movie node'''
    global PARSE_COUNT
    global SKIP_COUNT
    parser = etree.XMLParser(remove_blank_text=True, recover=True, huge_tree=True, encoding='latin1')
    try:
        tree = etree.parse(xmlfilepath, parser)
        tree = tree.getroot()
        moviedata = {}
        if tree == None or tree.find("title") == None or tree.find("year") == None:
            moviedata['valid'] = False
            #print "MOVIE %s COULD NOT BE PARSED" % xmlfilepath
            SKIP_COUNT += 1
        else:
            #print "MOVIE %s SUCCESSFULLY PARSED" % xmlfilepath
            moviedata['valid'] = True
            moviedata['id'] = tree.get("id").encode('utf-8')
            moviedata['title'] = tree.find("title").text.encode('utf-8')
            moviedata['year'] = tree.find("year").text.encode('utf-8')
            PARSE_COUNT += 1
    except Exception:
        print "ERROR: COULD NOT PARSE MOVIE AT %s" % xmlfilepath
        print traceback.format_exc()

    return moviedata

def update_querydocs(moviemap):
    ''' find the XML node ID of every movie + append'''
    for entry in database.COLLECTION.find():
        for imdbid, movie in entry['movies'].iteritems():
            hashstring = hash_node(movie['Title'].encode('utf-8'), movie['Year'].encode('utf-8'))
            if hashstring in moviemap:
                xmlnode = moviemap[hashstring]

                print "FOUND XML NODE ID (%s) FOR MOVIE %s" % (xmlnode['id'], movie['Title'])

                dotpath = "movies." + imdbid + ".xmlid"
                database.COLLECTION.update_one({'_id': entry['_id']}, {'$set': {dotpath: xmlnode['id']}}, upsert=False)
            else:
                print "XML NODE FOR MOVIE %s NOT FOUND" % movie['Title']

def main():
    ''' runs the movie id translation process '''
    moviemap = {}

    print "BUILDING HASHMAP OF MOVIES"

    #build a hashmap of all movies
    moviecount = 0
    for nodename in os.listdir(XML_NODE_PATH):
        moviepath = os.path.join(XML_NODE_PATH, nodename)
        moviedata = parse_node(os.path.join(moviepath))
        if moviedata['valid'] == False:
                continue
        else:
            moviedata['filepath'] = moviepath
            hashstring = hash_node(moviedata['title'], moviedata['year'])

            #print hashstring

            moviemap[hashstring] = moviedata
            moviecount += 1
            if moviecount%10000 == 0:
                print "%d MOVIES PROCESSED" % moviecount

    print "HASHING COMPLETE"
    print "PARSED MOVIES: %d" % PARSE_COUNT
    print "SKIPPED MOVIES: %d" % SKIP_COUNT
    # Run through all query docs in mongo + associate them with an xmlnode id
    update_querydocs(moviemap)

if __name__ == '__main__':
    main()

