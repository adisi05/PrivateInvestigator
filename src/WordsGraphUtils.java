import java.util.HashMap;
import java.util.List;

public class WordsGraphUtils {

    public static void printSimilarSentences(WordsGraph wordsGraph){
        for(Sentence s : wordsGraph.getAllSentences()){
            HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndexIgnoreDuplicates = wordsGraph.getSimilarSentencesGroupedByMissingWordIndex(s, true);
            System.out.print(similarSentencesToString(s, similarSentencesByMissingWordIndexIgnoreDuplicates));
        }
    }

    private static String similarSentencesToString(Sentence s, HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex) {
        StringBuilder sb = new StringBuilder();
        if(similarSentencesByMissingWordIndex.size() > 0){
            for(int index : similarSentencesByMissingWordIndex.keySet()){
                sb.append(similarSentencesByWordIndexToString(s, similarSentencesByMissingWordIndex, index));
            }
        }
        return sb.toString();
    }

    private static String similarSentencesByWordIndexToString(Sentence s, HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndex, int index) {
        StringBuilder sb = new StringBuilder();
        StringBuilder changinWordSB;
        if(index != WordsGraph.NO_GAP_WORD) {
            changinWordSB =  new StringBuilder("The changing word was:");
            changinWordSB.append(" ").append(s.getWords().get(index)).append(",");
        } else {
            changinWordSB = new StringBuilder("Basically the same sentence");
        }
        sb.append(s.toString());
        for(Sentence s2 : similarSentencesByMissingWordIndex.get(index)){
            sb.append(s2.toString());
            if(index != WordsGraph.NO_GAP_WORD) {
                changinWordSB.append(" ").append(s2.getWords().get(index)).append(",");
            }
        }
        if (index != WordsGraph.NO_GAP_WORD){
            changinWordSB.setLength(changinWordSB.length() - 1);//remove last comma
        }
        sb.append(changinWordSB).append("\n\n");
        return sb.toString();
    }

}
