import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PrivateInvestigator {

    public static void main(String[] args) {
        //TODO get address from args?
        List<String> lines = readFile("C:\\Users\\Adi\\Desktop\\test01.txt");
        SentenceReader reader = new SentenceReader();
        List<Sentence> sentences = reader.convert(lines);//TODO consider using array or vector
        WordsGraph wordsGraph = new WordsGraph();
        sentences.forEach(s-> { wordsGraph.addSentence(s); });
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
            System.out.println(printAllGroupsOfSentence(s, similarSentencesByMissingWordIndexIgnoreDuplicates));
        }
    }

    private static String printAllGroupsOfSentence(Sentence s, HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex) {
        StringBuilder sb = new StringBuilder();
        if(similarSentencesByMissingWordIndex.size() > 0){
            for(int index : similarSentencesByMissingWordIndex.keySet()){
                sb.append(printGroupOfSentenceByIndex(s, similarSentencesByMissingWordIndex, index));
            }
        }
        return sb.toString();
    }

    private static String printGroupOfSentenceByIndex(Sentence s, HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex, int index) {
        StringBuilder sb = new StringBuilder();
        StringBuilder changinWordSB = new StringBuilder("The changing word was:");
        sb.append(s.toString());
        changinWordSB.append(" ").append(s.getWords().get(index)).append(",");
        for(Sentence s2 : similarSentencesByMissingWordIndex.get(index)){
            sb.append(s2.toString());
            changinWordSB.append(" ").append(s2.getWords().get(index)).append(",");
        }
        changinWordSB.setLength(changinWordSB.length() - 1);//remove last comma
        sb.append(changinWordSB).append("\n");
        return sb.toString();
    }

    private static HashMap<Integer, List<Sentence>> getSimilarSentencesByMissingWordIndex(WordsGraph wordsGraph, Sentence s1, boolean ignoreDuplicates) {
        HashMap<Sentence, Pair<Integer,Integer>> sentencesWithMissingWord = new HashMap<>();
        Vector<String> words = s1.getWords();
        for(int i = 0; i < words.size(); i++){
            List<Sentence> sentencesWithSameWordSamePos = wordsGraph.getSentencesByWordAndPosition(words.get(i), i);
            for (Sentence s2 : sentencesWithSameWordSamePos){
                if(ignoreDuplicates && s2.getDateTime().compareTo(s1.getDateTime()) <= 0){
                    continue; //we don't want duplicates, it's1 a symmetrical relation
                }
                int lastContainedWordIndex = -1;
                int gapIndex = -1;
                if (sentencesWithMissingWord.containsKey(s2)){
                    lastContainedWordIndex = sentencesWithMissingWord.get(s2).getElement0();
                    gapIndex = sentencesWithMissingWord.get(s2).getElement1();
                    sentencesWithMissingWord.remove(s2);
                }
                if (lastContainedWordIndex == i - 1){
                    lastContainedWordIndex = i;
                } else if (lastContainedWordIndex == i - 2){
                    if (gapIndex == -1) {//didn't have gap before, this is the first gap
                        gapIndex = i - 1;
                        lastContainedWordIndex = i;
                    } else {// already had gap before
                        lastContainedWordIndex = -1;// don't add again
                    }
                } else {
                    lastContainedWordIndex = -1;
                }
                if(lastContainedWordIndex == i){
                    sentencesWithMissingWord.put(s2, new Pair<Integer,Integer>(lastContainedWordIndex,gapIndex));
                }
            }
        }
        // remove s2 sentences that didn't end after the same word amount as s1, and had only one changed word
        HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex = new HashMap<>();
        for(Sentence s2 : sentencesWithMissingWord.keySet()){
            Pair<Integer,Integer> lastContainedWordAndGap = sentencesWithMissingWord.get(s2);
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
