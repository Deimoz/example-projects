package info.kgeorgiy.ja.vircev.statistic;

import info.kgeorgiy.ja.vircev.bank.RemoteBank;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Locale;
import java.util.Optional;

public class StatisticsTest {
    private static AllStats stats;
    private static String inputFile = "java-solutions\\info\\kgeorgiy\\ja\\vircev\\statistic\\input.txt";
    private static String text = "very very cool test 1 2 3.\n" +
                                "I've got $100,000.00 at Monday, May 31, 2021.";
    private static int words = 9;
    private static int uniqueWords = 8;
    private static int sentences = 2;
    private static int currencies = 1;
    private static int numbers = 5;
    private static int dates = 1;

    @BeforeClass
    public static void beforeClass() {
        TextStatistic statistic = new TextStatistic(new Locale("en", "US"), new Locale("en"));
        stats = statistic.parseText(inputFile);
    }

    @org.junit.Test
    public void numbersTest() {
        Assert.assertEquals(numbers, stats.numberStatistic.getTotalCount());
        Assert.assertEquals(1 + 2 + 3 + 31 + 2021, stats.numberStatistic.totalSum, 0.1);
        Assert.assertNotNull(stats.numberStatistic.getMin());
        Assert.assertEquals(java.util.Optional.ofNullable(stats.numberStatistic.getMin()), java.util.Optional.of(1d));
        Assert.assertEquals(java.util.Optional.ofNullable(stats.numberStatistic.getMax()), java.util.Optional.of(2021d));
    }

    @org.junit.Test
    public void currencyTest() {
        Assert.assertEquals(currencies, stats.currencyStatistic.getTotalCount());
        Assert.assertEquals(java.util.Optional.ofNullable(stats.currencyStatistic.getMin()), java.util.Optional.of(100000d));
        Assert.assertEquals(java.util.Optional.ofNullable(stats.currencyStatistic.getMax()), java.util.Optional.of(100000d));
    }

    @org.junit.Test
    public void sentencesTest() {
        Assert.assertEquals(sentences, stats.sentenceStatistic.getTotalCount());
        Assert.assertEquals(stats.sentenceStatistic.getMaxLengthElement(), "I've got $100,000.00 at Monday, May 31, 2021.");
        Assert.assertEquals(stats.sentenceStatistic.getMinLengthElement(), "very very cool test 1 2 3.");
    }

    @org.junit.Test
    public void wordsTest() {
        Assert.assertEquals(words, stats.wordStatistic.getTotalCount());
        Assert.assertEquals(uniqueWords, stats.wordStatistic.getUniqueStat().size());
        Assert.assertEquals(stats.wordStatistic.getMaxLengthElement(), "Monday");
        Assert.assertEquals(stats.wordStatistic.getMinLengthElement(), "at");
    }
}
