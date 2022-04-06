import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Perceptron {


    private LinkedHashMap<String, Double> expectedValuesForInput = new LinkedHashMap<>();
    private List<LanguageSample> languageSamples;
    private List<Double> weights;
    private double theta = 0;
    private double learningRate = 0.001;
    private String languageName;

    public Perceptron(List<LanguageSample> languageSamples, String languageName) {
        this.languageSamples = languageSamples;
        this.weights = new ArrayList<>(Collections.nCopies(26, 0.0));
        this.languageName = languageName;
        expectedValuesForInput.put(languageName, 1.0);
        expectedValuesForInput.put("otherThenMine", 0.0);

    }

    public void doDelta() {

        for (int i = 0, languageSamplesSize = languageSamples.size(); i < languageSamplesSize; i++) {
            LanguageSample languageSample = languageSamples.get(i);
            String mapKey = "otherThenMine";
            if (languageSample.getLanguageName().equals(languageName)) {
                mapKey = languageSample.getLanguageName();
            }
            //System.out.println("Perceptron: " + languageName + " sample: " + languageSample.getLanguageName() + " expected value: " + expectedValuesForInput.get(mapKey) + "and y = " + getAnswer(i));

            List<Double> newWeight = calculateWeight(expectedValuesForInput.get(mapKey), i);
            double newTheta = calculateTheta(expectedValuesForInput.get(mapKey), i);

            this.weights = newWeight;
            this.theta = newTheta;


        }


    }


    public double getAnswer(int index){
        List<Double> coords = languageSamples.get(index).getVectorOfCharacterFrequency();

        double net = 0.0;
        for (int i = 0; i < coords.size(); i++) {
            net += coords.get(i) * weights.get(i);
        }

        BigDecimal bd = new BigDecimal(net).setScale(7, RoundingMode.HALF_UP);

        return net >= theta ? 1.0 : 0.0;
    }

    public double getAnswer(List<String> text){
        LanguageSample test = new LanguageSample("test", text);

        List<Double> coords = test.getVectorOfCharacterFrequency();

        double net = 0.0;
        for (int i = 0; i < coords.size(); i++) {
            net += coords.get(i) * weights.get(i);
        }

        return net;
        //return new BigDecimal(net).setScale(7, RoundingMode.HALF_UP);

    }



    public double calculateTheta(double expectedValue, int index) {

        return theta - ((expectedValue - getAnswer(index)) * learningRate);


    }


    private List<Double>  calculateWeight(double expectedValue, int index) {
        List<Double> newX = languageSamples.get(index).getVectorOfCharacterFrequency().stream()
                .map(xn -> xn * learningRate * (expectedValue - getAnswer(index)))
                .collect(Collectors.toList());

        ArrayList<Double> tmpWeights = new ArrayList<>(weights);

        for (int i = 0; i < tmpWeights.size(); i++) {
            tmpWeights.set(i, tmpWeights.get(i) + newX.get(i));
        }

        return tmpWeights;
    }

    public String getLanguageName() {
        return languageName;
    }

    @Override
    public String toString() {
        return "Perceptron{" +
                "languageName='" + languageName + '\'' +
                '}';
    }

    public List<Double> getWeights() {
        return weights;
    }
}
