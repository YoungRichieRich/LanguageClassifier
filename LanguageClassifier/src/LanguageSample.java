import java.util.*;
import java.util.stream.Collectors;

public class LanguageSample {


    private final String languageName;
    private final Map<Character, Integer> characterFrequencyMap = new TreeMap<>(Character::compareTo);
    private List<String> text;
    private List<Double> vectorOfCharacterFrequency;
    private int charactersAmount;

    public LanguageSample(String languageName, List<String> text) {
        this.languageName = languageName;
        this.text = text;

        initializeFrequencyMap();
        removeAllNotEnglishAlphabetLetters();
        fillInCharacterFrequencyMap();
        normalizeVectorOfCharacterFrequency();
    }

    public void removeAllNotEnglishAlphabetLetters() {

        text = text.stream().map(s -> s.replaceAll("[^a-zA-Z]", ""))
                .collect(Collectors.toCollection(ArrayList::new));

    }


    private void fillInCharacterFrequencyMap() {

        for (String s : text) {

            s = s.toLowerCase();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                charactersAmount++;
                Integer frequencyTmp = characterFrequencyMap.get(c) + 1;
                characterFrequencyMap.replace(c, frequencyTmp);

            }

        }

    }


    private void normalizeVectorOfCharacterFrequency() {

        Collection<Integer> values = characterFrequencyMap.values();
        vectorOfCharacterFrequency = values.stream()
                .map(Integer::doubleValue)
                .map(aDouble -> aDouble / charactersAmount)
                .collect(Collectors.toList());

    }


    private void initializeFrequencyMap() {

        for (int i = 'a'; i <= 'z'; i++) {
            characterFrequencyMap.put((char) i, 0);
        }

    }


    public List<Double> getVectorOfCharacterFrequency() {
        return vectorOfCharacterFrequency;
    }


    public String getLanguageName() {
        return languageName;
    }

    public List<String> getText() {
        return text;
    }

    public Map<Character, Integer> getCharacterFrequencyMap() {
        return characterFrequencyMap;
    }
}
