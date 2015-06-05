''' wordnet_tests.py '''

import sys
from nltk.corpus import wordnet as wn

def sim(word1, word2, lch_threshold=2.15, verbose=False):
    """Determine if two (already lemmatized) words are similar or not.

    Call with verbose=True to print the WordNet senses from each word
    that are considered similar.

    The documentation for the NLTK WordNet Interface is available here:
    http://nltk.googlecode.com/svn/trunk/doc/howto/wordnet.html
    """
    from nltk.corpus import wordnet as wn
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


def main():

    '''
    #actsets  = wn.synsets('act')
    #print actsets

    directsets = wn.synsets('direct')
    print directsets

    act  = wn.synset('act.v.10')
    perform = wn.synset('perform.v.03')
    direct = wn.synset('direct.v.03')

    act = direct

    print act.path_similarity(direct)

    #sys.exit(0)

    print act.hypernyms()
    print "\n"
    print act.hyponyms()
    print "\n"
    print act.member_holonyms()
    print "\n"
    print act.root_hypernyms()
    print "\n"
    #print wn.synset('act.v.10').lowest_common_hypernyms(wn.synset('perform.v.03'))
    sys.exit(0)

    #perform v 3 and 1 are good
    # acvt v 10 matches perform v 3 match is .5
    '''
    print sim('starring', 'act', lch_threshold=2.3, verbose=True)



if __name__ == '__main__':
    main()
