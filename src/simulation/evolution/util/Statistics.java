package simulation.evolution.util;

/**
 *
 * @author Tihomir
 */
public class Statistics {

    private float averageLifeSpan;
    private float minLifeSpan;
    private float maxLifeSpan;
    private int currentNumberOfRed;
    private int currentNumberOfBlue;
    private int totalNumberOfRed;
    private int totalNumberOfBlue;

    private Statistics() {
        averageLifeSpan = 0;
        minLifeSpan = 100f;
        maxLifeSpan = 0;
        currentNumberOfBlue = 0;
        currentNumberOfRed = 0;
        totalNumberOfBlue = 0;
        totalNumberOfRed = 0;
    }

    public static Statistics getInstance() {
        return StatisticsHolder.INSTANCE;
    }

    private static class StatisticsHolder {

        private static final Statistics INSTANCE = new Statistics();
    }

    @Override
    public String toString() {
        String s = "Min life span: " + minLifeSpan + "\n";
        s += "Max life span: " + maxLifeSpan + '\n';
        s += "Average life span: " + averageLifeSpan + "\n";
        s += "Current number of red: " + currentNumberOfRed + "\n";
        s += "Current number of blue: " + currentNumberOfBlue + "\n";
        s += "Current total number: " + (currentNumberOfBlue + currentNumberOfRed) + "\n";
        s += "Total number of red: " + totalNumberOfRed + "\n";
        s += "Total number of blue: " + totalNumberOfBlue + "\n";
        s += "Total number of agents: " + (totalNumberOfRed + totalNumberOfBlue) + "\n";
        return s;
    }

    public void addCurrentNumberOfRed() {
        currentNumberOfRed++;
    }

    public void decreaseCurrentNumberOfRed() {
        currentNumberOfRed--;
    }

    public void addCurrentNumberOfBlue() {
        currentNumberOfBlue++;
    }

    public void decreaseCurrentNumberOfBlue() {
        currentNumberOfBlue--;
    }

    public void addTotalNumberOfRed() {
        totalNumberOfRed++;
    }

    public void addTotalNumberOfBlue() {
        totalNumberOfBlue++;
    }

    public void minimumLifeSpan(float minimum) {
        if (minimum < minLifeSpan) {
            minLifeSpan = minimum;
        }
    }

    public void maximumLifeSpan(float maximum) {
        if (maximum > maxLifeSpan) {
            maxLifeSpan = maximum;
        }
    }

    public void averageLifeSpan(float lifeSpan) {
        averageLifeSpan = (averageLifeSpan * (totalNumberOfBlue + totalNumberOfRed) + lifeSpan) / (totalNumberOfBlue + totalNumberOfRed + 1);
    }
}
