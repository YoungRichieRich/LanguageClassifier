import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static final int AMOUNT_OF_LEARNING_ITERATION = 2;

    public static void main(String[] args) {
        
        final String testFileName = "testFile.txt";
        
        HashMap<String, List<List<String>>> labelsAndTexts = getLanguageLabelsAndTexts();

        ArrayList<LanguageSample> languageSamples = creatingListOfLanguageSamples(labelsAndTexts);

        List<Perceptron> perceptrons = createListOfPerceptrons(labelsAndTexts, languageSamples);

        doLearningStuff(perceptrons);

        List<String> testFile = readFile(testFileName);

        HashMap<String, Double> hashMap = createMapWithLearnedLayer(perceptrons, testFile);

        showKeyWithMaxValue(hashMap);


    }

    private static List<Perceptron> createListOfPerceptrons(HashMap<String, List<List<String>>> labelsAndTexts, ArrayList<LanguageSample> languageSamples) {
        return labelsAndTexts.keySet()
                .stream()
                .map(entry -> new Perceptron(languageSamples, entry))
                .toList();
    }

    private static ArrayList<LanguageSample> creatingListOfLanguageSamples(HashMap<String, List<List<String>>> labelsAndTexts) {
        ArrayList<LanguageSample> languageSamples = new ArrayList<>();
        labelsAndTexts.forEach((key, value) ->
                value.stream()
                        .map(strings -> new LanguageSample(key, strings))
                        .forEach(languageSamples::add));
        return languageSamples;
    }

    private static void doLearningStuff(List<Perceptron> perceptrons){
        for (int i = 0; i < Main.AMOUNT_OF_LEARNING_ITERATION; i++) {
            int finalI = i;
            perceptrons.forEach(p -> p.shuffleLangueageSamplesList(finalI));
            perceptrons.forEach(Perceptron::doDelta);
        }
    }

    private static HashMap<String, Double> createMapWithLearnedLayer(List<Perceptron> perceptrons, List<String> testFile) {
        HashMap<String, Double> hashMap = new HashMap<>();
        perceptrons.forEach(perceptron -> hashMap.put(perceptron.getLanguageName(), perceptron.getAnswer(testFile)));
        return hashMap;
    }

    private static void showKeyWithMaxValue(HashMap<String, Double> hashMap) {
        Map.Entry<String, Double> maxEntry = null;

        for (Map.Entry<String, Double> entry : hashMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        assert maxEntry != null;
        System.out.println(maxEntry.getKey());
    }

    private static HashMap<String, List<List<String>>> getLanguageLabelsAndTexts() {
        List<String> directoryNames = new ArrayList<>();


        HashMap<String, List<List<String>>> labelsAndTexts = new HashMap<>();

        try {

            Files.walk(Path.of("Languages"))
                    .filter(Files::isDirectory)
                    .forEach(d -> directoryNames.add(d.getFileName().toString()));

            String mainDirectoryName = directoryNames.get(0);
            directoryNames.remove(0);

            for (String directoryName : directoryNames) {
                Files.walk(Path.of(mainDirectoryName + "/" + directoryName))
                        .filter(Files::isRegularFile)
                        .forEach(f -> {

                            if (!labelsAndTexts.containsKey(directoryName)) {
                                labelsAndTexts.put(directoryName, new ArrayList<>());
                            }
                            labelsAndTexts.get(directoryName).add(readFile(mainDirectoryName + "/" + directoryName + "/" + f.getFileName().toString()));

                        });
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return labelsAndTexts;
    }

    public static List<String> readFile(String fileName) {
        List<String> result = null;
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            result = lines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
