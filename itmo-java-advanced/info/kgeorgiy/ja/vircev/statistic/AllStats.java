package info.kgeorgiy.ja.vircev.statistic;

public class AllStats {
    public final NumberStatistic numberStatistic;
    public final NumberStatistic currencyStatistic;
    public final DateStatistic dateStatistic;
    public final WordsStatistic sentenceStatistic;
    public final WordsStatistic wordStatistic;

    public AllStats(NumberStatistic numberStatistic,
                    NumberStatistic currencyStatistic,
                    DateStatistic dateStatistic,
                    WordsStatistic sentenceStatistic,
                    WordsStatistic wordStatistic) {
        this.numberStatistic = numberStatistic;
        this.currencyStatistic = currencyStatistic;
        this.dateStatistic = dateStatistic;
        this.sentenceStatistic = sentenceStatistic;
        this.wordStatistic = wordStatistic;
    }
}
