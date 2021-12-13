package info.kgeorgiy.ja.vircev.statistic;

import java.nio.channels.DatagramChannel;
import java.util.Locale;

public class NumberStatistic extends AbstractStatistic<Double> {
    double totalSum;

    public NumberStatistic(Locale locale) {
        super(locale);
        totalSum = 0;
    }

    @Override
    public String getAverage() {
        int amount = getTotalCount();
        if (amount == 0) {
            amount++;
        }
        return String.valueOf(totalSum / amount);
    }

    @Override
    protected int compare(Double a, Double b) {
        return a.compareTo(b);
    }

    @Override
    public void addElement(Double element) {
        super.addElement(element);
        totalSum += element;
    }

    @Override
    protected int countLength(String element) {
        double number = Double.parseDouble(element);
        if (number == (int) number) {
            return super.countLength(String.valueOf((int) number));
        }
        return super.countLength(element);
    }
}
