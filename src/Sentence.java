import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sentence implements Comparable<Sentence>{
    public static final String END_OF_SENTENCE = "\n";
    public static final String DATE_TIME_PATTERN = "dd-mm-yyyy hh:mm:ss";
    private Date dateTime;
    private ArrayList<String> words;

    public Sentence(Date dateTime, List<String> words) {
        this.dateTime = dateTime;
        this.words = new ArrayList<>(words);
        if (!words.get(words.size() - 1).equals(END_OF_SENTENCE)){
            words.add(END_OF_SENTENCE);
        }
    }


    public Date getDateTime() {
        return dateTime;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        String strDate = dateFormat.format(getDateTime());
        sb.append(strDate);
        words.stream().forEach(w->sb.append(" ").append(w));
        return sb.toString();
    }

    @Override
    public int compareTo(Sentence s2) {
        int timeDiff = this.getDateTime().compareTo(s2.getDateTime());
        return timeDiff != 0 ? timeDiff : this.toString().compareTo(s2.toString());
    }
}
