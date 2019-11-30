import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceReader {
    private static final String DATE_TIME = "(\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2})";
    private static final int DATE_TIME_GROUP = 1;
    private static final String WORD = "(\\S+)";
    private static final String WORDS = "(("+WORD+" )*"+WORD+")";
    private static final int WORDS_GROUP = 2;
    private static final String SENTENCE= DATE_TIME+" "+WORDS;

    public static List<Sentence> convert(List<String> lines) throws Exception {
        Pattern p = Pattern.compile(SENTENCE);
        ArrayList<Sentence> sentences = new ArrayList<>();
        for(String line : lines){
            Matcher m = p.matcher(line);
            if(m.find()){
                String dateTimeString = m.group(DATE_TIME_GROUP);
                Date dateTime = new SimpleDateFormat(Sentence.DATE_TIME_PATTERN).parse(dateTimeString);
                String words = m.group(WORDS_GROUP);
                ArrayList<String> wordsSplitted = new ArrayList<>(Arrays.asList(words.split(" ")));
                wordsSplitted.add(Sentence.END_OF_SENTENCE);
                Sentence sentence = new Sentence(dateTime, wordsSplitted);
                sentences.add(sentence);
            } else {
                throw new Exception(line + " - line has a wrong format");
            }

        }
        return sentences;
    }
}
