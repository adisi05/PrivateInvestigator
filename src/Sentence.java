import java.util.List;
import java.util.Vector;

public class Sentence {
    Long id;//TODO needed?
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
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getDateTime());
        words.stream().forEach(w->sb.append(" ").append(w));
        return sb.toString();
    }
}
