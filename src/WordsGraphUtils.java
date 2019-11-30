import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordsGraphUtils {

    private static final int SENTENCE_TOO_DIFFERENT = -1;
    private static final int NO_GAP_WORD = -1;

    public static void printSimilarSentences(WordsGraph wordsGraph){
        wordsGraph.getAllSentences().forEach(s -> {
            HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndexIgnoreDuplicates = getSimilarSentencesGroupedByMissingWordIndex(wordsGraph, s, true);
            System.out.print(similarSentencesToString(s, similarSentencesByMissingWordIndexIgnoreDuplicates));
        });
    }

    private static String similarSentencesToString(Sentence s, HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex) {
        StringBuilder sb = new StringBuilder();
        if(similarSentencesByMissingWordIndex.size() > 0){
            for(int index : similarSentencesByMissingWordIndex.keySet()){
                sb.append(similarSentencesByMissingWordIndexToString(s, similarSentencesByMissingWordIndex, index));
            }
        }
        return sb.toString();
    }

    private static String similarSentencesByMissingWordIndexToString(Sentence s, HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex, int index) {
        StringBuilder sb = new StringBuilder();
        StringBuilder changinWordSB;
        if(index != NO_GAP_WORD) {
            changinWordSB =  new StringBuilder("The changing word was:");
            changinWordSB.append(" ").append(s.getWords().get(index)).append(",");
        } else {
            changinWordSB = new StringBuilder("Basically the same sentence");
        }
        sb.append(s.toString());
        for(Sentence s2 : similarSentencesByMissingWordIndex.get(index)){
            sb.append(s2.toString());
            if(index != NO_GAP_WORD) {
                changinWordSB.append(" ").append(s2.getWords().get(index)).append(",");
            }
        }
        if (index != NO_GAP_WORD){
            changinWordSB.setLength(changinWordSB.length() - 1);//remove last comma
        }
        sb.append(changinWordSB).append("\n\n");
        return sb.toString();
    }

    private static HashMap<Integer, List<Sentence>> getSimilarSentencesGroupedByMissingWordIndex(WordsGraph wordsGraph, Sentence s, boolean ignoreDuplicates) {
        HashMap<Sentence, Pair<Integer,Integer>> sentencesWithLastContainedWordAndLastGap = new HashMap<>();
        ArrayList<String> words = s.getWords();
        for(int i = 0; i < words.size(); i++){
            List<Sentence> sentencesWithSameWordSamePos = wordsGraph.getSentencesByWordAndPosition(words.get(i), i);
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
                    sentencesWithLastContainedWordAndLastGap.put(s2, new Pair<>(lastContainedWordIndex,gapIndex));
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
}
