''' nltk_test.py - a module for testing nltk's NER and CFG functions '''

import nltk
from nltk import PCFG
import numpy

def main():
    ''' run nltk tests '''
    print "WELCOME TO NLTK TEST"

    #sentence = ["movies", "directed", "by", "mel", "brooks"]
    #sentence = nltk.corpus.treebank.tagged_sents()[22]

    sentence = "adventure movies between 2000 and 2015 featuring performances by daniel craig"
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
    #sentence = nltk.pos_tag(sentence)
    print sentence


    #TV = transitive verb
    #IV = intransitive verb
    # DatV = Dative verb
    grammar = PCFG.fromstring("""
                S    -> NP VP                   [1.0]
                VP   -> TV IN NP                [0.8]
                VP   -> IV                      [0.1]
                VP   -> DatV NP NP              [0.1]
                IN   -> 'by'                    [1.0]
                TV   -> 'directed'              [1.0]
                IV   -> 'directed'              [1.0]
                DatV -> 'gave'                  [1.0]
                NP   -> 'movies'      [0.5]
                NP   -> 'craig'          [0.5]
            """)

    #print grammar
    viterbi_parser = nltk.ViterbiParser(grammar)
    for tree in viterbi_parser.parse(sentence):
        print tree


if __name__ == '__main__':
        main()
