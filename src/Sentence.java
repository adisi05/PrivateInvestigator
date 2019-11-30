import java.util.List;
import java.util.Vector;

public class Sentence {
    public static final String ENF_OF_SENTENCE = "\n";//TODO if I use spring, am I cool with static?
    private Long id;//TODO needed?
    private String dateTime;
    private Vector<String> words;//TODO consider using list or array

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Vector<String> getWords() {
        return words;
    }

    public void setWords(Vector<String> words) {
        this.words = words;
        if (!words.get(words.size() - 1).equals(ENF_OF_SENTENCE)){
            words.add(ENF_OF_SENTENCE);
        }
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getDateTime());
        words.stream().forEach(w->sb.append(" ").append(w));
        return sb.toString();
    }
}
