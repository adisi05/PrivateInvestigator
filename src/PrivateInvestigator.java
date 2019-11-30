import jdk.internal.util.xml.impl.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void printGroups(List<Sentence> sentences, WordsGraph wordsGraph){
        StringBuilder sb = new StringBuilder();
        for(Sentence s1 : sentences){
            HashMap<Sentence, Integer> sentencesWithScore = new HashMap<>();
            Vector<String> words = s1.getWords();
            for(int i=0; i<words.size(); i++){
                List<Sentence> sentencesWithSameWordSamePos = wordsGraph.getSentencesByWordAndPosition(words.get(i), i);
                for (Sentence s2 : sentencesWithSameWordSamePos){
                    if(s2.getDateTime().compareTo(s1.getDateTime()) <= 0){
                        continue; //we don't want duplicates, it's1 a symmetrical relation
                    }
                    int currentScore = 0;
                    if (sentencesWithScore.containsKey(s2)){
                        currentScore = sentencesWithScore.get(s2);
                        sentencesWithScore.remove(s2);
                    }
                    if(currentScore >= i - 1){ // check if it's1 relevant to add the sentence, maybe the two sentences have already gone too far from each other
                        sentencesWithScore.put(s2, currentScore + 1);
                    }
                }
            }
            // remove sentences that doesn't have enough score
            Set<Sentence> similarSentences = new HashSet<>();
            for(Sentence s2 : sentencesWithScore.keySet()){
                if (sentencesWithScore.get(s2) >= words.size() - 1){
                    similarSentences.add(s2);
                }
            }
            //TODO maybe here sort by word? instead of sentences with score, use sentences with score and gap (value is Pair), and when aggragating similar sentences make it also a map by gap index

            if(similarSentences.size() > 0){
                sb.append(s1.toString()).append("\n");
                for(Sentence s2 : similarSentences){
                    sb.append(s2.toString()).append("\n");
                }
                sb.append("=== TEST TEST TEST code still have a design problem ===\n");
            }
        }
        System.out.println(sb.toString());
    }
}

