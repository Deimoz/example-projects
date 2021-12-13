package info.kgeorgiy.ja.vircev.statistic;

import java.util.Locale;

public class WordsStatistic extends AbstractStatistic<String>{

    public WordsStatistic(Locale locale) {
        super(locale);
    }

    @Override
    public void addElement(String element) {
        if (element.isEmpty()) {
            return;
        }
        super.addElement(element);
    }

    @Override
    public String getAverage() {
        int amount = getTotalCount();
        if (amount == 0) {
            amount++;
        }
        return String.valueOf((double) getTotalLength() / amount);
    }

    @Override
    protected int compare(String a, String b) {
        return collator.compare(a, b);
    }
}
