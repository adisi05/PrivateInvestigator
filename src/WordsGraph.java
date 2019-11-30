import java.util.*;

public class WordsGraph {

    protected HashMap<Integer, HashMap<String, List<Sentence>>> wordsByPosition = new HashMap<>();
    protected HashSet<Sentence> allSentences = new HashSet<>();


    public static final int SENTENCE_TOO_DIFFERENT = -1;
    public static final int NO_GAP_WORD = -1;

    public void addSentence(Sentence sentence){
        Vector<String> words = sentence.getWords();
        for(int i = 0; i < words.size(); i++){
            addWord(words.get(i), i, sentence);
        }
        getAllSentences().add(sentence);
    }

    public void addWord(String word, Integer position, Sentence sentence) {
        HashMap<String, List<Sentence>> sentencesByWord = null;
        if (wordsByPosition.containsKey(position)) {
            sentencesByWord = wordsByPosition.get(position);
        } else {
            sentencesByWord = new HashMap<>();
            wordsByPosition.put(position, sentencesByWord);
        }

        List<Sentence> sentences = null;
        if (sentencesByWord.containsKey(word)) {
            sentences = sentencesByWord.get(word);
        } else {
            sentences = new ArrayList<>();
            sentencesByWord.put(word, sentences);
        }
        sentences.add(sentence);
    }

    public List<Sentence> getSentencesByWordAndPosition(String word, int position){
        if(!wordsByPosition.containsKey(position)){
            return null;
        }
        HashMap<String, List<Sentence>> sentencesByWord = wordsByPosition.get(position);
        if(sentencesByWord == null || !sentencesByWord.containsKey(word)){
            return null;
        }
        return sentencesByWord.get(word);
    }

    public HashMap<Integer, List<Sentence>> getSimilarSentencesGroupedByMissingWordIndex(Sentence s, boolean ignoreDuplicates) {
        HashMap<Sentence, Pair<Integer,Integer>> sentencesWithLastContainedWordAndLastGap = new HashMap<>();
        Vector<String> words = s.getWords();
        for(int i = 0; i < words.size(); i++){
            List<Sentence> sentencesWithSameWordSamePos = getSentencesByWordAndPosition(words.get(i), i);
            for (Sentence s2 : sentencesWithSameWordSamePos){
                if(ignoreDuplicates && s2.getDateTime().compareTo(s.getDateTime()) <= 0){
                    continue; //we don't want duplicates, it's1 a symmetrical relation
                }
                int lastContainedWordIndex = SENTENCE_TOO_DIFFERENT;
                int gapIndex = NO_GAP_WORD;
                if (sentencesWithLastContainedWordAndLastGap.containsKey(s2)){
                    lastContainedWordIndex = sentencesWithLastContainedWordAndLastGap.get(s2).getElement0();
                    gapIndex = sentencesWithLastContainedWordAndLastGap.get(s2).getElement1();
                    sentencesWithLastContainedWordAndLastGap.remove(s2);
                }
                if (lastContainedWordIndex == i - 1){// since SENTENCE_TOO_DIFFERENT=-1 this condition is always true when i is 0
                    lastContainedWordIndex = i;
                } else if (lastContainedWordIndex == i - 2){// since SENTENCE_TOO_DIFFERENT=-1 this condition is always true when i is 1
                    if (gapIndex == NO_GAP_WORD) {//didn't have gap before, this is the first gap
                        gapIndex = i - 1;
                        lastContainedWordIndex = i;
                    } else {// already had gap before
                        lastContainedWordIndex = SENTENCE_TOO_DIFFERENT;// don't add again
                    }
                } else {
                    lastContainedWordIndex = SENTENCE_TOO_DIFFERENT;
                }
                if(lastContainedWordIndex == i){
                    sentencesWithLastContainedWordAndLastGap.put(s2, new Pair<Integer,Integer>(lastContainedWordIndex,gapIndex));
                }
            }
        }
        // exclude s2 sentences that didn't end after the same words amount as s
        HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex = new HashMap<>();
        for(Sentence s2 : sentencesWithLastContainedWordAndLastGap.keySet()){
            Pair<Integer,Integer> lastContainedWordAndGap = sentencesWithLastContainedWordAndLastGap.get(s2);
            int lastContainedWord = lastContainedWordAndGap.getElement0();
            int missingWordIndex = lastContainedWordAndGap.getElement1();
            if(lastContainedWord == words.size() - 1){
                List<Sentence> similarSentencesAtSpecifiedIndex;
                if(similarSentencesByMissingWordIndex.containsKey(missingWordIndex)){
                    similarSentencesAtSpecifiedIndex = similarSentencesByMissingWordIndex.get(missingWordIndex);
                } else {
                    similarSentencesAtSpecifiedIndex = new ArrayList<>();
                }
                similarSentencesAtSpecifiedIndex.add(s2);
                similarSentencesByMissingWordIndex.put(missingWordIndex, similarSentencesAtSpecifiedIndex);
            }
        }
        return similarSentencesByMissingWordIndex;
    }

    public HashSet<Sentence> getAllSentences() {
        return allSentences;
    }

    public void setAllSentences(HashSet<Sentence> allSentences) {
        this.allSentences = allSentences;
    }
}
