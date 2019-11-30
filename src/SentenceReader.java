import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceReader{
    final protected String DATE_TIME = "(\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2})";
    final protected int DATE_TIME_GROUP = 1;
    final protected String WORD = "(\\S+)";
    final protected String WORDS = "(("+WORD+" )*"+WORD+")";
    final protected int WORDS_GROUP = 2;
    final protected String SENTENCE= DATE_TIME+" "+WORDS;

    List<Sentence> convert(List<String> lines){
        Pattern p = Pattern.compile(SENTENCE);
        ArrayList<Sentence> sentences = new ArrayList<>();//TODO consider vector
        for(String line : lines){
            Matcher m = p.matcher(line);
            if(m.find()){
                Sentence sentence = new Sentence();
                sentence.setDateTime(m.group(DATE_TIME_GROUP));
                String words = m.group(WORDS_GROUP);
                Vector<String> wordsSplitted = new Vector<String>(Arrays.asList(words.split(" ")));
                wordsSplitted.add(Sentence.ENF_OF_SENTENCE);
                sentence.setWords(wordsSplitted);
                sentences.add(sentence);
            }
            else{
                //TODO throw exception
            }

        }
        return sentences;
    }
}
