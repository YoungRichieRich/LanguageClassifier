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

        ArrayList<LanguageSample> languageSamples = new ArrayList<>();
        labelsAndTexts.forEach((key, value) ->
            value.stream().map(strings -> new LanguageSample(key, strings)).forEach(languageSamples::add));


        Collections.shuffle(languageSamples, new Random(0));

        List<Perceptron> perceptrons = labelsAndTexts.keySet().stream()
                .map(entry -> new Perceptron(languageSamples, entry))
                .collect(Collectors.toList());

        for (int i = 0; i < AMOUNT_OF_LEARNING_ITERATION; i++) {
            perceptrons.forEach(Perceptron::doDelta);
        }

        List<String> testFile = readFile("testFile.txt");

        HashMap<String, Double> hashMap = new HashMap<>();
        perceptrons.forEach(perceptron -> hashMap.put(perceptron.getLanguageName(), perceptron.getAnswer(testFile)));

        System.out.println(hashMap);
        Map.Entry<String, Double> maxEntry = null;

        for (Map.Entry<String, Double> entry : hashMap.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        //perceptrons.forEach(perceptron -> System.out.println( perceptron.getLanguageName() + " " + perceptron.getAnswer(testFile) ));
        assert maxEntry != null;
        System.out.println(maxEntry.getKey());


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
