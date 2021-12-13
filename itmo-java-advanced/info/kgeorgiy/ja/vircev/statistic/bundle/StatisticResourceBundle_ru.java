package info.kgeorgiy.ja.vircev.statistic.bundle;

public class StatisticResourceBundle_ru extends AbstractStatisticResourceBundle{

    private static final Object[][] CONTENTS = {
            {"Analyzed file", "Анализируемый файл"},
            {"Base statistic", "Сводная статистика"},
            {"Number of", "Количество"},
            {"sentences", "предложений"},
            {"words", "слов"},
            {"dates", "дат"},
            {"sums", "сумм"},
            {"numbers", "чисел"},
            {"Statistic of", "Статистика"},
            {"Unique", "Уникальных"},
            {"Min", "Минимальный"},
            {"Max", "Максимальный"},
            {"Min length", "Минимальная длина"},
            {"Max length", "Максимальная длина"},
            {"Average value", "Среднее значение"}
    };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
}
