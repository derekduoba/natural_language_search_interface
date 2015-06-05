''' nltk_test.py - a module for testing nltk's NER and CFG functions '''

import nltk
import numpy

def main():
    ''' run nltk tests '''
    print "WELCOME TO NLTK TEST"

    #sentence = ["movies", "directed", "by", "mel", "brooks"]
    #sentence = nltk.corpus.treebank.tagged_sents()[22]

    sentence = "I like ice cream"
    #sentence = "adventure movies between 2000 and 2015 featuring performances by daniel craig"
    #sentence = "movies directed by craig"
    #sentence = "all movies where Vin Diesel acted from the 1990s"
    #sentence = "movies directed by mel brooks\r\n"
    #sentence = "action movies with Jackie Chan\r\n"
    #print sentence

    sentence = nltk.word_tokenize(sentence)
    print sentence
    #sentence = [nltk.word_tokenize(sent) for sent in sentence]
    #sentence = nltk.word_tokenize(sentence)
    #sentence = [nltk.pos_tag(sent) for sent in sentence]
    sentence = nltk.pos_tag(sentence)
    print sentence

    parser  = nltk.parse.malt.MaltParser("/home/dduoba/data/maltparser", mco="engmalt.linear-1.7", additional_java_args=['-Xmx2048m'])

    dg = parser.tagged_parse(sentence)
    print dg.tree().pprint()

if __name__ == '__main__':
        main()
