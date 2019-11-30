import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PrivateInvestigator {

    // put in program arguments one of the test files, for example: "test_files/test01.txt"
    public static void main(String[] args) throws Exception {
        List<String> lines = readFile(args[0]);
        List<Sentence> sentences = SentenceReader.convert(lines);
        WordsGraph wordsGraph = new WordsGraph();
        sentences.forEach(wordsGraph::addSentence);
        WordsGraphUtils.printSimilarSentences(wordsGraph);
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
}
