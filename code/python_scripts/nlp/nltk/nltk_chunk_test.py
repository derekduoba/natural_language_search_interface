''' nltk_test.py - a module for testing nltk's NER and CFG functions '''

import nltk
from nltk.corpus import treebank_chunk
import numpy
from tag_util import backoff_tagger

def conll_tag_chunks(chunk_sents):
    tagged_sents = [nltk.chunk.tree2conlltags(tree) for tree in chunk_sents]
    return [[(t, c) for (w, t, c) in sent] for sent in tagged_sents]

class TagChunker(nltk.chunk.ChunkParserI):
    def __init__(self, train_chunks, tagger_classes=[nltk.tag.UnigramTagger, nltk.tag.BigramTagger]):
        train_sents = conll_tag_chunks(train_chunks)
        self.tagger = backoff_tagger(train_sents, tagger_classes)

    def parse(self, tagged_sent):
        if not tagged_sent: return None
        (words, tags) = zip(*tagged_sent)
        chunks = self.tagger.tag(tags)
        wtc = itertools.izip(words, chunks)
        return nltk.chunk.conlltags2tree([(w,t,c) for (w,(t,c)) in wtc])

def main():
    ''' run nltk tests '''
    print "WELCOME TO NLTK TEST"

    #sentence = ["movies", "directed", "by", "mel", "brooks"]
    #sentence = nltk.corpus.treebank.tagged_sents()[22]

    sentence = "adventure movies between 2000 and 2015 featuring performances by daniel craig"
    #sentence = "all movies where Vin Diesel acted from the 1990s"
    #sentence = "movies directed by mel brooks\r\n"
    #sentence = "action movies with Jackie Chan\r\n"
    print sentence

    #sentence = nltk.sent_tokenize(sentence)
    sentence = nltk.word_tokenize(sentence)
    sentence = nltk.pos_tag(sentence)
    print sentence

    #grammar = "NP: {<DT>?<JJ>*<NN.*>+}"

    '''
    grammar = r"""
        MEDIA: {<DT>?<JJ>*<NN.*>+}
        RELATION: {<V.*>}{<DT>?<JJ>*<NN.*>+}
        ENTITY: {<NN.*>}"
        """
    '''

    grammar = r"""
        NP: {<DT|JJ|NN.*>+}          # Chunk sequences of DT, JJ, NN
        PP: {<IN><NP>}               # Chunk prepositions followed by NP
        VP: {<VB.*><NP|PP|CLAUSE>+$} # Chunk verbs and their arguments
        CLAUSE: {<NP><VP>}           # Chunk NP, VP
        """

    cp = nltk.RegexpParser(grammar)
    result = cp.parse(sentence)
    print result

    print "\r\n"
    #print nltk.ne_chunk(sentence, binary=False)

    '''
    print "OTHER CHUNKER"
    sentence = "adventure movies between 2000 and 2015 featuring performances by daniel craig"
    train_chunks = treebank_chunk.chunked_sents()[:3000]
    chunker = TagChunker(train_chunks)
    chunker.parse(sentence)
    '''


if __name__ == '__main__':
        main()
