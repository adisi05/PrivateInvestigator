import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PrivateInvestigator {

    protected static final int SENTENCE_TOO_DIFFERENT = -1;
    protected static final int NO_GAP_WORD = -1;

    public static void main(String[] args) {
        //TODO get address from args?
        List<String> lines = readFile("C:\\Users\\Adi\\Desktop\\test02.txt");
        SentenceReader reader = new SentenceReader();
        List<Sentence> sentences = reader.convert(lines);//TODO consider using array or vector
        WordsGraph wordsGraph = new WordsGraph();
        sentences.forEach(s->wordsGraph.addSentence(s));
        printGroups(sentences, wordsGraph);
    }

    private static List<String> readFile(String path) {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    path));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void printGroups(List<Sentence> sentences, WordsGraph wordsGraph){
        for(Sentence s : sentences){
            HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndexIgnoreDuplicates = getSimilarSentencesByMissingWordIndex(wordsGraph, s, true);
            System.out.print(allGroupsOfSentenceToString(s, similarSentencesByMissingWordIndexIgnoreDuplicates));
        }
    }

    private static String allGroupsOfSentenceToString(Sentence s, HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex) {
        StringBuilder sb = new StringBuilder();
        if(similarSentencesByMissingWordIndex.size() > 0){
            for(int index : similarSentencesByMissingWordIndex.keySet()){
                sb.append(groupOfSentenceByIndexToString(s, similarSentencesByMissingWordIndex, index));
            }
        }
        return sb.toString();
    }

    private static String groupOfSentenceByIndexToString(Sentence s, HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex, int index) {
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
        changinWordSB.setLength(changinWordSB.length() - 1);//remove last comma
        if (index == NO_GAP_WORD){
            changinWordSB = new StringBuilder("Basically the same sentence");
        }
        sb.append(changinWordSB).append("\n\n");
        return sb.toString();
    }

    private static HashMap<Integer, List<Sentence>> getSimilarSentencesByMissingWordIndex(WordsGraph wordsGraph, Sentence s, boolean ignoreDuplicates) {
        HashMap<Sentence, Pair<Integer,Integer>> sentencesWithLastContainedWordAndLastGap = new HashMap<>();
        Vector<String> words = s.getWords();
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
}
