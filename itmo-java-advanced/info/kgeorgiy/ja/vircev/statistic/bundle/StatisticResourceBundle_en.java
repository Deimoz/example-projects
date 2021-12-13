package info.kgeorgiy.ja.vircev.statistic.bundle;

public class StatisticResourceBundle_en extends AbstractStatisticResourceBundle{

    private static final Object[][] CONTENTS = {
            {"Analyzed file", "Analyzed file"},
            {"Base statistic", "Base statistic"},
            {"Number of", "Number of"},
            {"sentences", "sentences"},
            {"words", "words"},
            {"dates", "dates"},
            {"sums", "sums"},
            {"numbers", "numbers"},
            {"Statistic of", "Statistic of"},
            {"Unique", "Unique"},
            {"Min", "Minimum"},
            {"Max", "Maximum"},
            {"Min length", "Minimum length"},
            {"Max length", "Maximum length"},
            {"Average value", "Average value"}
    };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
}
