import java.util.*;

public class WordsGraph {

    protected HashMap<Integer, HashMap<String, List<Sentence>>> wordsByPosition = new HashMap<>();
    protected HashSet<Sentence> allSentences = new HashSet<>();

    public void addSentence(Sentence sentence){
        ArrayList<String> words = sentence.getWords();
        for(int i = 0; i < words.size(); i++){
            addWord(words.get(i), i, sentence);
        }
        allSentences.add(sentence);
    }

    private void addWord(String word, Integer position, Sentence sentence) {
        HashMap<String, List<Sentence>> sentencesByWord;
        if (wordsByPosition.containsKey(position)) {
            sentencesByWord = wordsByPosition.get(position);
        } else {
            sentencesByWord = new HashMap<>();
            wordsByPosition.put(position, sentencesByWord);
        }

        List<Sentence> sentences;
        if (sentencesByWord.containsKey(word)) {
            sentences = sentencesByWord.get(word);
        } else {
            sentences = new ArrayList<>();
            sentencesByWord.put(word, sentences);
        }
        sentences.add(sentence);
    }

    public Set<Sentence> getAllSentences() {
        return allSentences;
    }

    public List<Sentence> getSentencesByWordAndPosition(String word, int position){
        if(!wordsByPosition.containsKey(position)){
            return null;
        }
        HashMap<String, List<Sentence>> sentencesByWord = wordsByPosition.get(position);
        if(sentencesByWord == null || !sentencesByWord.containsKey(word)){
            return null;
        }
        return sentencesByWord.get(word);
    }

}
