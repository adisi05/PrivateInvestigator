import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PrivateInvestigator {

    public static void main(String[] args) {
        //TODO get address from args?
        List<String> lines = readFile("C:\\Users\\Adi\\Desktop\\test02.txt");
        SentenceReader reader = new SentenceReader();
        List<Sentence> sentences = reader.convert(lines);//TODO consider using array or vector
        WordsGraph wordsGraph = new WordsGraph();
        sentences.forEach(s->wordsGraph.addSentence(s));
        printSimilarSentences(wordsGraph);
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

    private static void printSimilarSentences(WordsGraph wordsGraph){
        for(Sentence s : wordsGraph.getAllSentences()){
            HashMap<Integer, List<Sentence>> similarSentencesByMissingWordIndexIgnoreDuplicates = wordsGraph.getSimilarSentencesGroupedByMissingWordIndex(s, true);
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
