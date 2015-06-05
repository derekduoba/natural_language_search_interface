''' spacy lib dependency parse test '''

from __future__ import unicode_literals
from spacy.en import English
import datetime
import sys
from nltk.corpus import wordnet as wn

def main():

    '''
    base = datetime.date(2000, 1, 1)
    yearrange = 2015 - 2010
    date_list = [base - datetime.timedelta(days=(x*366)) for x in range(0, 10)]
    print [d.year for d in date_list]
    #sys.exit(0)
    '''

    #sentence = "movies where Owen Wilson was a voice actor"
    #sentence = "movies directed by or starring Shinya Tsukamoto"
    #sentence = "movies without Owen Wilson"
    sentence = "all movies created by Pixar"
    #sentence = "movies with Jake Gyllenhaal as an actor"
    #sentence = "every Fast And Furious movie"
    #sentence = "movies based on Nicolas Sparks books"
    #sentence = "movies where Sylvester Stallone acted between 2010, 2015."
    #sentence = "movies performed by Nicholas Cage"
    #sentence = "adventure movies between 2000 and 2015 featuring performances by Daniel Craig"
    #sentence = "all movies where Vin Diesel acted from the 1990s."
    #sentence = "action movies starring vin diesel"
    #sentence = "movies directed by Mel Brooks"
    #sentence = "action movies with Jackie Chan"
    #sentence = "Wes Anderson movies that did not include Bill Murray"
    #sentence = "movies starring sandra bullock and george clooney"
    print sentence

    nlp = English()
    tokens = nlp(sentence)

    print tokens[1].orth
    print tokens[1].lemma_
    print tokens[1].lower_

    #years = list(t.orth_ for t in tokens if t.tag_ == "CD")
    #print years

    '''
    for t in tokens:
        if t.dep_ == 'ROOT':
            print "ROOT IS {0}".format(t.orth_)
            root = t
            break

    #print "WORDS BEFORE ROOT: {0}, WORDS AFTER ROOT: {1}".format(root.n_lefts, root.n_rights)
    print "LEMMA: {0}".format(root.lemma_)

    for c in root.children:
        print "CHILD TOKEN: {0}".format(c.orth_)
        for t in c.subtree:
            print "TOKEN DATA: TOKEN = {0}, TAG = {1}, POS = {2}, HEAD = {3}, DEP = {4}".format(t.orth_, t.tag_, t.pos_, t.head, t.dep_)
    '''

    for t in tokens:
        print "TOKEN DATA: TOKEN = {0}, LEMMA = {5} TAG = {1}, POS = {2}, DEP = {3}, HEAD = {4}".format(t.orth_, t.tag_, t.pos_, t.dep_, t.head.orth_, t.lemma_)

        '''
        subtree = t.subtree
        for t in subtree:
            print "TOKEN DATA: TOKEN = {0}, TAG = {1}, POS = {2}, HEAD = {3}".format(t.orth_, t.tag_, t.pos_, t.head)
        '''

    print "\nENTITY EXTRACTION"
    verb = "NOTHING"
    verbstem = "NOTHING"
    ents = list(tokens.ents)

    enttoks = list(ents[0]._seq)

    print enttoks[0].orth_

    for e in ents:
        print "LABEL: {0}, TOKEN: {1}, START: {2}, END: {3}".format(e.label_, e.orth_, e.start, e.end)
        token = tokens[e.end - 1]
        while token.dep_ != "ROOT":
            if token.pos_ == "VERB":
                verb = token.orth_
                verbstem = token.lemma_
                break
            token = token.head
        print "VERB: {0}, VERB STEM: {1}".format(verb, verbstem)

    '''
    print "DEPENDENCIES"
    sents = list(tokens.sents)
    for s in sents:
        print "ID: {0}, LABEL: {1}, TOKEN: {2}".format(s.label, s.label_, s.orth_)

        #print "HEAD"
        #print s.head.label
        #print s.head.label_
        #print s.head.orth_

        print "SEQ"
        newtokens = s._seq
        for t in newtokens:
            print "\n"
            print "TOKEN DATA: TOKEN = {0}, TAG = {1}, POS = {2}".format(t.orth_, t.tag_, t.pos_)
            print "DEPENDENCY DATA: DEP = {0}, HEAD = {1}".format(t.dep_, t.head.orth_)
    '''
    '''
    print "OTHER"
    print s.rights
    print s.start
    print s.subtree
    '''

if __name__ == "__main__":
    main()
