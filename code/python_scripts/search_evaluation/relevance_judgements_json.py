'''
create a relevance judgements file from query information
FORMAT:
(QueryID) 0 (Document Path) (Relevance Score; 0 = Irrelevant; 1 = Relevant)

EXAMPLE:
1 0 /home/dduoba/data/xmlfiles/movie-1955098.xml 1
'''

import argparse
import sys
sys.path.append('/home/dduoba/code/python-scripts/util')
import database
import os
import time

XML_NODE_DIR = '/home/dduoba/data/xmlfiles/'
RELEVANCE_JUDGEMENTS_FILEPATH = '/home/dduoba/data/eval/relevance_judgments/'

def create_relevance_judgement(querynumber, xmlid, relevancefile):
    ''' write a relevance judgement to an open file; all judgements are "relevant" '''
    xmlnodepath = os.path.join(XML_NODE_DIR, 'movie-' + str(xmlid) + '.xml')
    relevancejudgement = "{0}-{1} 0 {2} 1\n".format(SEARCH_TYPE, querynumber, xmlnodepath)
    relevancefile.write(relevancejudgement)

def create_rj_file(relevancefile):
    ''' build the relevance judgement file'''
    querynumber = 1
    for entry in database.COLLECTION.find():
        for imdbid, movie in entry['movies'].iteritems():
            if 'xmlid' in movie:
                create_relevance_judgement(querynumber, movie['xmlid'], relevancefile)
        querynumber += 1


def main():
    ''' create a relevance judgements file '''
    global SEARCH_TYPE
    parser = argparse.ArgumentParser()
    parser.add_argument("-t", "--type", choices=['LQ', 'SQ', 'ORACLE', 'PRMS', 'NLPRMS'], required=True,help="Valid Types: BL, LQ, SQ, PRMS, NLPRMS")
    args = parser.parse_args()
    SEARCH_TYPE = args.type

    relevancefilepath = os.path.join(RELEVANCE_JUDGEMENTS_FILEPATH, 'relevance_judgments_' + SEARCH_TYPE + '_' + time.strftime("%m%d%Y") + '.json')

    print "PRINTING RELEVANCE JUDGEMENTS TO:"
    print relevancefilepath

    relevancefile = open(relevancefilepath, 'w')
    create_rj_file(relevancefile)

if __name__ == '__main__':
    main()

