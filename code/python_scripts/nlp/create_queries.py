''' create_queries.py translates natural language queries into galago queries'''

from __future__ import unicode_literals
import argparse
import sys
sys.path.append('/home/dduoba/code/python-scripts/util')
import database
import datetime
import json
from nltk.corpus import wordnet as wn
import os
import re
from spacy.en import English
import sys

NEGATION_WORDS = ['no', 'not', 'without']
GENRE_WORDS = ['adventure', 'action'] # For demo purposes. Production versions should use IMDB genre lists
YEAR_RANGE_AMOUNT = 11
NLP = English()

class EntityData(object):
    def __init__(self):
        self.name = None # This is an entity object
        self.relation = None # This contains relation data
        #self.modifier = None # This is a space-delimited string of role modifiers
        #self.negative = None # This is True/False
        self.namesubtreeroot = None # This is a token containing the subtree root

class DateData(object):
    def __init__(self):
        self.dates = [] # This is a list containing int representations of years
        self.operator = None # This is the operator to be applied to the years (not usefule at the moment)

class QueryData(object):
    def __init__(self):
        self.entitydata = [] # This contains entity data
        self.root = None # This contains the dependency tree's root token
        self.genre = None # This contains the token representing the related genre
        self.datedata = None # This contains date information

class EntityExpression(object):
    def __init__(self):
        self.children = [] # The children nodes of the expression tree
        #self.expression = None # Not currently used, but would contain a Galago query
        self.operator = None
        self.relation = None
        self.negative = False

def update_querydocs():
    ''' find and update movies in the mongo DB'''
    for entry in database.COLLECTION.find():
        nlquery = entry[QUERY_TYPE['input']]

        print "Processing Query: {0}".format(nlquery)

        galagoquery = create_galago_query(nlquery)

        print entry['_id']

        database.COLLECTION.update_one({'_id': entry['_id']}, {'$set': {QUERY_TYPE['output']: galagoquery}}, upsert=False)


'''
Create a galago-formatted query given a natural language query
'''
def create_galago_query(nlquery):
    querydata = QueryData()
    tokens = NLP(nlquery)
    entities = list(tokens.ents)

    for e in entities:
        if e.label_ != "DATE":

            #print "ENTITY: {0}".format(e.orth_)

            find_entity_relations(querydata, tokens, e)
    find_genre_relation(querydata, tokens)
    find_date_constraints(querydata, tokens)
    return format_galago_query(querydata, tokens)


def format_galago_query(querydata, tokens):
    galagoquery = ""

    # Named Entities
    entityrules = []
    if len(querydata.entitydata) > 1:
        for e in querydata.entitydata:
            # I have a tree now
            # perform a inorder traversal
            entityrules.append(build_entity_query(e, e.relation))

        entityrule = ' '.join([r for r in entityrules])
        entityrule = "#band( {0} )".format(entityrule)
    elif len(querydata.entitydata) == 1:
        entityrule = build_entity_query(querydata.entitydata[0], querydata.entitydata[0].relation)
    else: # No entities
        noun = next((t for t in tokens if t.pos_ == "NOUN" and (t.lemma_ != "movie" and t.lemma_ != "film")), None)
        if noun != None:
            entityrule = ("#inside(#od:1({0}) {1})".format((noun.orth_).lower(), "#field:title()"))

    # Genre
    genrerule = ""
    if querydata.genre != None:
        genrerule = "#inside({0} #field:genres())".format(querydata.genre.orth_)

    # Date(s)
    daterule = ""
    if querydata.datedata != None:
        if len(querydata.datedata.dates) == 1:
            daterule = "#inside({0} #field:year())".format(querydata.datedata.dates[0])
        elif len(querydata.datedata.dates) > 1:
            dates = ' '.join([str(d.year) for d in querydata.datedata.dates])
            daterule = "#inside(#syn({0}) #field:year())".format(dates)

    # Build query
    galagoquery = "#combine(#bool(#band({0} {1} {2} #inside(movie #field:kind()) #bnot(#inside(#od:1(tv movie) #field:kind())) #bnot(#inside(#od:1(video movie) #field:kind())) #bnot(#inside(documentary #field:genres()))  #bnot(#inside(short #field:genres())) )))".format(entityrule, genrerule, daterule)

    print "THE QUERY:"
    print galagoquery
    return galagoquery


def build_entity_query(entity, treenode):
    ''' Traverse an entity tree (in order) to create a galago expression '''

    #print "BUILDING AN ENTITY QUERY!"

    if not treenode.children:
        return generate_entity_rule(entity, treenode)
    else:
        # create 1 rule per child node
        # 2 cases: operator and relation
        if treenode.operator != None: # Operator node case

            #print "TREENODE OPERATOR IS... {0}".format(treenode.operator.lemma_)

            if treenode.operator.pos_ == "CONJ" and treenode.operator.lemma_ == "and": # AND case example
                rule = ""
                for c in treenode.children:
                    rule += " {0}".format(build_entity_query(entity, c))
                return "#band( {0} )".format(rule)
            else: # OR case
                #return '#bor( ' + ' '.join(['%s']*len(treenode.children)) + ')'
                rule = ""
                for c in treenode.children:
                    rule += " {0}".format(build_entity_query(entity, c))
                return "#bor( {0} )".format(rule)

        else: # Relation node case (This shouldn't ever happen)
            return generate_entity_rule(entity, treenode)


def generate_entity_rule(entity=None, treenode=None):
    ''' build an entity-relation rule '''

    if treenode and treenode.relation:
        relation = treenode.relation
    elif entity: # No relation detected by standard means, so try to find one another way
        tokens = list(entity.name._seq)
        relation = next((t for t in tokens if t.pos_ == "NOUN" and t.dep_ != "ROOT" and t.tag_ != "NNP" and t.lemma_ != "film" and t.lemma_ != "movie"), None)

    if entity:
        name = (entity.name.orth_).lower()

    if entity and relation:
        if (relation.lemma_ == "act" or "act" in relation.orth_ or determine_similarity(relation.lemma_, 'act', lch_threshold=2.6, verbose=False)
        or relation.lemma_ == "feature" or "featur" in relation.orth_ or determine_similarity(relation.lemma_, 'feature', lch_threshold=2.3, verbose=False)
        or relation.lemma_ == "star" or "star" in relation.orth_ or determine_similarity(relation.lemma_, 'star', lch_threshold=2.3, verbose=False)):
            entityrule = "#inside(#od:1({0}) {1})".format(name, "#field:cast()")
        elif relation.lemma_ == "direct" or "direct" in relation.orth_ or determine_similarity(relation.lemma_, 'direct', lch_threshold=2.6, verbose=False):
            entityrule = "#inside(#od:1({0}) {1})".format(name, "#field:director()")
        elif relation.lemma_ == "produce" or "produce" in relation.orth_ or determine_similarity(relation.lemma_, 'produce', lch_threshold=2.3, verbose=False):
            if entity.name.label_ == "PERSON":
                entityrule = "#inside(#od:1({0}) {1})".format(name, "#field:producer()")
            elif entity.name.label_ == "ORG" or entity.name.label_ == "PRODUCT" or entity.name.label_ == "GPE":
                entityrule = "#inside(#od:1({0}) {1})".format(name, "#field:production-companies()")
            else:
                entityrule = "#bor( #inside(#od:1({0}) {1}) #inside(#od:1({0}) {2}))".format(name, "#field:production-companies()", "#field:producer()")
        elif (relation.lemma_ == "write" or "write" in relation.orth_ or determine_similarity(relation.lemma_, 'write', lch_threshold=2.3, verbose=False)
        or relation.lemma_ == "base" or "base" in relation.orth_ or determine_similarity(relation.lemma_, 'base', lch_threshold=2.3, verbose=False)):
            entityrule = "#inside(#od:1({0}) {1})".format(name, "#field:writer()")
        elif relation.lemma_ == "create" or "creat" in relation.orth_ or determine_similarity(relation.lemma_, 'create', lch_threshold=2.3, verbose=False):
            if entity.name.label_ == "PERSON":
                entityrule = "#bor(#inside(#od:1({0}) {1}) #inside(#od:1({0}) {2}) )".format(name, "#field:producer()", "#field:writer()")
            elif entity.name.label_ == "ORG" or entity.name.label_ == "PRODUCT" or entitiy.name.label_ == "GPE":
                entityrule = "#inside(#od:1({0}) {1})".format(name, "#field:production-companies()")
            else:
                entityrule = "#bor( #inside(#od:1({0}) {1}) #inside(#od:1({0}) {2}) #inside(#od:1({0}) {3}))".format(name, "#field:production-companies()", "#field:producer()", "#field:writer()")
        else:
            if entity.name.label_ == "PERSON":
                entityrule = "#bor( #inside(#od:1({0}) {1}) #inside(#od:1({0}) {2}) #inside(#od:1({0}) {3}) #inside(#od:1({0}) {4}) )".format(name, "#field:producer()", "#field:cast()", "#field:director()", "#field:writer()")
            else:
                entityrule = "#bor( #inside(#od:1({0}) {1}) #inside(#od:1({0}) {2}))".format(name, "#field:production-companies()", "#field:title()")
    elif entity and not relation:

        #print "NO RELATION FOUND WHILE MAKING RULE FOR {0}".format(name)

        if entity.name.label_ == "PERSON":
            entityrule = "#bor( #inside(#od:1({0}) {1}) #inside(#od:1({0}) {2}) #inside(#od:1({0}) {3}) #inside(#od:1({0}) {4}) )".format(name, "#field:producer()", "#field:cast()", "#field:director()", "#field:writer()")
        else:
            entityrule = "#bor( #inside(#od:1({0}) {1}) #inside(#od:1({0}) {2}) )".format(name, "#field:production-companies()", "#field:title()")

    if entity and treenode.negative == True:
        entityrule = "#bnot({0})".format(entityrule)

    return entityrule


def determine_similarity(word1, word2, lch_threshold=2.3, verbose=False):
    """Determine if two (already lemmatized) words are similar or not.

    Call with verbose=True to print the WordNet senses from each word
    that are considered similar.

    The documentation for the NLTK WordNet Interface is available here:
    http://nltk.googlecode.com/svn/trunk/doc/howto/wordnet.html

    Code borrowed from Wesley Baugh:
    http://stackoverflow.com/users/1988505/wesley-baugh

    """
    results = []
    for net1 in wn.synsets(word1):
        for net2 in wn.synsets(word2):
            try:
                lch = net1.lch_similarity(net2)
            except:
                continue
            # The value to compare the LCH to was found empirically.
            # (The value is very application dependent. Experiment!)
            if lch >= lch_threshold:
                results.append((net1, net2))
    if not results:
        return False
    if verbose:
        for net1, net2 in results:
            print net1
            print net1.definition
            print net2
            print net2.definition
            print 'path similarity:'
            print net1.path_similarity(net2)
            print 'lch similarity:'
            print net1.lch_similarity(net2)
            print 'wup similarity:'
            print net1.wup_similarity(net2)
            print '-' * 79
    return True


'''
Computes entity-action relations by traversing through a dependency tree
'''
def find_entity_relations(querydata, tokens, namedentity):
    currententity = EntityData()
    currententity.name = namedentity
    relationdata = EntityExpression()
    token = tokens[namedentity.end - 1]
    relationdata = build_expression_tree(token, relationdata, querydata, currententity)
    currententity.relation = relationdata
    querydata.entitydata.append(currententity)


def build_expression_tree(token, relationdata, querydata, currententity):

    #print "TOKEN IS: {0}".format(token.orth_)

    if token.head.dep_ == "ROOT":
        querydata.root = token.head
        currententity.subtreeroot = token
        return relationdata
    elif token.head.pos_ == "VERB" and relationdata.relation == None:
        if relationdata.operator == None:
            if token.lemma_ == "be": # Handle actions associated with "be" verbs
                relationdata.relation = next((c for c in token.children if c.pos_ == "NOUN" and c.tag_ != "NNP"))
            else:
                relationdata.relation = token.head
                #relationdata.relation = ' '.join([m.orth_ for m in currententity.relation.children]) # Check for modifiers

            #print "RELATION: {0} ; TOKEN: {1}".format(relationdata.relation.orth_, token.orth_)

            negative_search(token.head, relationdata)
            relationdata = connector_search(token, relationdata)

            if relationdata.operator != None:
                secondary_relation_search(token.head, relationdata)

            if len(relationdata.children) > 1:
                token = next((t.relation for t in relationdata.children if t.relation.orth != token.head.orth))

                #print "TOKEN UPGRADED TO: {0}".format(token)

            else:
                token = token.head

            return build_expression_tree(token, relationdata, querydata, currententity)

        else:
            relationdata = connector_search(token, relationdata)
            newnode = EntityExpression()
            newnode.relation = token.head
            relationdata.children.append(newnode)
            token = token.head
            return build_expression_tree(token, relationdata, querydata, currententity)
    else:
        token = token.head
        return build_expression_tree(token, relationdata, querydata, currententity)


def negative_search(token, relationdata):
    ''' traverse up the dependency tree to look for negatives  '''

    #print "LOOKING FOR A NEGATIVE FOR TOKEN: {0} ...".format(token.orth_)

    if token.head.dep_ == "ROOT" or (token.head.pos_ == "VERB" and token.head.dep_ != "SBAR"): # Stop before the next verb

        #print "NO NEGATIVE FOUND. CURRENT TOKEN: {0}".format(token.head.orth_)

        return
    elif token.head.lemma_ in NEGATION_WORDS:

        #print "FOUND ONE!"

        relationdata.negative = True
        return
    else:
        for c in token.head.children:
            if c.orth != token.orth and token.head.dep_ != "ROOT": # Look for a negative in a sibling subtree
                for t in c.subtree:
                    if t.lemma_ in NEGATION_WORDS:

                        #print "FOUND ONE!"

                        relationdata.negative = True
                        return
        return negative_search(token.head, relationdata)


def connector_search(token, relationdata):
    ''' traverse up the dependency tree to look for connective words '''

    #print "LOOKING FOR A CONNECTOR FOR TOKEN: {0} ...".format(token.orth_)

    if token.head.dep_ == "ROOT":
        return relationdata
    elif token.head.pos_ == "VERB":
        for c in token.head.children:
            for t in c.subtree:
                if t.pos_ == "CONJ" or t.pos_ == "PUNCT":
                    newnode = EntityExpression()
                    newnode.operator = t

                    #print "FOUND A CONNECTOR: {0}".format(t.orth_)
                    #print "NEWNODE's OPERATOR IS: {0}".format(newnode.operator.orth_)

                    newnode.children.append(relationdata)
                    relationdata = newnode

                    #print "NEWNODE's OPERATOR IS: {0}".format(newnode.operator.orth_)
                    #print "RELATIONDATA's OPERATOR IS: {0}".format(relationdata.operator.orth_)

                    return relationdata
        return connector_search(token.head, relationdata)
    else:
        return connector_search(token.head, relationdata)


def secondary_relation_search(token, relationdata):
    ''' traverse up the dependency tree to look for the secondary relation '''

    #print "LOOKING FOR A SECONDARY FOR TOKEN: {0} ...".format(token.orth_)

    if token.head.dep_ == "ROOT": # Shouldn't ever get here; this indicates incorrect grammar
        return
    elif token.head.pos_ == "VERB":
        newnode = EntityExpression()
        if token.head.lemma_ == "be": # Handle actions associated with "be" verbs
            newnode.relation = next((c for c in token.head.children if c.pos_ == "NOUN" and c.tag_ != "NNP"))
        else:
            newnode.relation = token.head
            #newnode.relation = ' '.join([m.orth_ for m in currententity.relation.children]) # Check for modifiers

        #print "FOUND A SECONDARY RELATION: {0}".format(newnode.relation.orth_)

        relationdata.children.append(newnode)
        return
    else:
        return secondary_relation_search(token.head, relationdata)


'''
Determine whether if any genre relations exist, and include them in the querydata dictionary
'''
def find_genre_relation(querydata, tokens):
    if not querydata.root:
        querydata.root = next((t for t in tokens if t.dep_ == "ROOT"))

    for c in querydata.root.children:
        genre = next((t for t in c.subtree if t.orth != querydata.root.orth and t.lower_ in GENRE_WORDS), None)
        if genre != None:
            querydata.genre = genre
            return


'''
Determine whether if any date constraints exist, and add a date object if they do
Currently, this only supports a single date or date range (e.g. 2000, 1990s, 2010 to 2015, etc.)
'''
def find_date_constraints(querydata, tokens):
    datedata = DateData()
    parentdate = next((t for t in tokens if t.tag_ == "CD" and t.head.tag_ != "CD"), None)
    if parentdate == None:
        return
    parent = parentdate.head
    childdate = next((t for t in parentdate.children if t.tag_ == "CD"), None)
    conjunction = next((t for t in parentdate.children if t.tag_ == "CC" or t.pos_ == "PUNCT"), None)
    operator = parentdate.head
    if parentdate != None and childdate == None:
        if operator != None and operator.lower_ == "before":
            date = int(re.sub(r"\D", "", parentdate.orth_))
            end = datetime.date(date, 1, 1)
            datedata.dates = [end - datetime.timedelta(days=(x*365)) for x in range(0, YEAR_RANGE_AMOUNT)]
        elif operator != None and operator.lower_ == "after":
            date = int(re.sub(r"\D", "", parentdate.orth_))
            if "s" in str(parentdate.lower_):
                date = date + 9
            beginning = datetime.date(date, 1, 1)
            datedata.dates = [beginning + datetime.timedelta(days=(x*366)) for x in range(0, YEAR_RANGE_AMOUNT)]
        elif operator != None and "s" in str(parentdate.lower_): #assume between
            date = int(re.sub(r"\D", "", parentdate.orth_))
            end = datetime.date(date, 1, 1)
            datedata.dates = [end + datetime.timedelta(days=(x*366)) for x in range(0, YEAR_RANGE_AMOUNT)]
        else: # one year
            datedata.dates.append(int(re.sub(r"\D", "", parentdate.orth_)))
    elif parentdate != None and childdate != None: # assume between
        if conjunction != None and conjunction.pos_ == "PUNCT":
            years = list(t.orth_ for t in tokens if t.tag_ == "CD")
            end = datetime.date(int(re.sub(r"\D", "", max(years))), 1, 1)
            yearrange = int(re.sub(r"\D", "", max(years))) - int(re.sub(r"\D", "", min(years)))
        else:
            end = datetime.date(int(re.sub(r"\D", "", parentdate.orth_)), 1, 1)
            yearrange = int(re.sub(r"\D", "", parentdate.orth_)) - int(re.sub(r"\D", "", childdate.orth_))
        datedata.dates = [end - datetime.timedelta(days=(x*365)) for x in range(0, (yearrange + 1))]
    datedata.operator = operator
    querydata.datedata = datedata
    return


def python_magic():
    ''' python can't even. oh wait. yes it can. '''
    return {
            'NLP': {'input': 'longquery', 'output': 'nlpquery'},
            'NLP2': {'input': 'longquery2', 'output': 'nlpquery2'},
    }[SELECTION]


def main():
    global SELECTION
    global QUERY_TYPE
    parser = argparse.ArgumentParser()
    parser.add_argument("-t", "--type", choices=['NLP', 'NLP2'], required=True, help="Valid Types: NLP, NLP2")
    args = parser.parse_args()
    SELECTION = args.type
    QUERY_TYPE = python_magic()

    batch = True
    if batch == True:
        print "Creating Galago Queries from NL Queries..."
        update_querydocs()
        print "Finished!"
    else:
        #nlquery = "movies where Owen Wilson was a voice actor"
        #nlquery = "movies directed by or starring Shinya Tsukamoto"
        #nlquery = "Wes Anderson movies that did not include Bill Murray"
        #nlquery = "action movies with Jackie Chan"
        #nlquery = "all movies where Vin Diesel acted from the 1990s."
        #nlquery = "movies with Jake Gyllenhaal as an actor"
        #nlquery = "adventure movies between 2000 and 2015 featuring performances by Daniel Craig"
        #nlquery = "movies based on Nicolas Sparks books"
        #nlquery = "every Fast And Furious movie"
        #nlquery = "movies directed by Wes Anderson starring Bill Murray"
        nlquery = "all movies created by Pixar"

        print nlquery

        create_galago_query(nlquery)

if __name__ == '__main__':
    main()
