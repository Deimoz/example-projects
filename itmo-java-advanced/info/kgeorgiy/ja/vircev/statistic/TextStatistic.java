package info.kgeorgiy.ja.vircev.statistic;

import info.kgeorgiy.ja.vircev.statistic.bundle.AbstractStatisticResourceBundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.*;
import java.util.*;

public class TextStatistic {
    private final Locale inputLocale;
    private final Locale outputLocale;
    private final NumberFormat numberFormat;
    private final NumberFormat currencyFormat;
    private final DateFormat[] dateFormat;

    public TextStatistic(final Locale inputLocale, final Locale outputLocale) {
        this.inputLocale = inputLocale;
        this.outputLocale = outputLocale;
        numberFormat = NumberFormat.getNumberInstance(inputLocale);
        currencyFormat = NumberFormat.getCurrencyInstance(inputLocale);
        dateFormat = new DateFormat[]{
                DateFormat.getDateInstance(DateFormat.SHORT, inputLocale),
                DateFormat.getDateInstance(DateFormat.MEDIUM, inputLocale),
                DateFormat.getDateInstance(DateFormat.LONG, inputLocale),
                DateFormat.getDateInstance(DateFormat.DEFAULT, inputLocale),
                DateFormat.getDateInstance(DateFormat.FULL, inputLocale)
        };
    }

    public void makeStatistics(final String input, final String output) {
        final AllStats stats = parseText(input);
        if (stats == null) {
            return;
        }
        writeText(input, output, stats);
    }

    public AllStats parseText(final String input) {
        final NumberStatistic numberStatistic = new NumberStatistic(inputLocale);
        final NumberStatistic currencyStatistic = new NumberStatistic(inputLocale);
        final DateStatistic dateStatistic = new DateStatistic(inputLocale);
        final WordsStatistic sentenceStatistic = new WordsStatistic(inputLocale);
        final WordsStatistic wordStatistic = new WordsStatistic(inputLocale);

        final List<String> lines = new ArrayList<>();
        // :NOTE: IllegalPathException
        try (final BufferedReader in = Files.newBufferedReader(Paths.get(input),StandardCharsets.UTF_8)) {
            String line = in.readLine();
            while (line != null) {
                lines.add(line);
                line = in.readLine();
            }
        } catch (final IOException e) {
            // :NOTE: Сообщения поьлзователью
            System.err.println("Error while reading input file: " + e.getMessage());
            return null;
        }
        final String text = String.join(" ", lines);

        final BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(inputLocale);
        sentenceIterator.setText(text);
        for (int curr = sentenceIterator.first(), prev = 0; curr != BreakIterator.DONE; prev = curr, curr = sentenceIterator.next()) {
            final String sentence = text.substring(prev, curr).trim();
            if (sentence.isEmpty()) {
                continue;
            }
            sentenceStatistic.addElement(sentence);

            final BreakIterator wordIterator = BreakIterator.getWordInstance(inputLocale);
            wordIterator.setText(sentence);

            for (int i = wordIterator.first(), prevI = 0; i != BreakIterator.DONE; prevI = i, i = wordIterator.next()) {
                final String word = sentence.substring(prevI, i).trim();
                if (word.isEmpty()) {
                    continue;
                }
                parseDate(sentence, prevI, dateStatistic);
                final boolean numberParsed = parseNumber(word, numberStatistic, numberFormat);
                final boolean currencyParsed = parseNumber(word, currencyStatistic, currencyFormat);
                if (numberParsed || currencyParsed) {
                    continue;
                }
                parseWord(word, wordStatistic);
            }
        }
        return new AllStats(
                numberStatistic,
                currencyStatistic,
                dateStatistic,
                sentenceStatistic,
                wordStatistic);
    }

    public void writeText(final String input, final String output, final AllStats stats) {
        final AbstractStatisticResourceBundle bundle = (AbstractStatisticResourceBundle) ResourceBundle
                .getBundle("info.kgeorgiy.ja.vircev.statistic.bundle.StatisticResourceBundle", outputLocale);
        if (bundle == null) {
            System.out.println("This locale is not supported");
            return;
        }

        final String returnText = MessageFormat.format(
                "{0}: {1}\n" +
                        "{2}\n" +
                        "{3}\n" +
                        "{4}\n" +
                        "{5}\n" +
                        "{6}\n" +
                        "{7}\n" +
                        "{8}\n" +
                        "{9}\n" +
                        "{10}\n" +
                        "{11}\n" +
                        "{12}\n",
                bundle.getString("Analyzed file"), input,
                bundle.getString("Base statistic"),
                amountStat("sentences", bundle, stats.sentenceStatistic),
                amountStat("words", bundle, stats.wordStatistic),
                amountStat("sums", bundle, stats.currencyStatistic),
                amountStat("numbers", bundle, stats.numberStatistic),
                amountStat("dates", bundle, stats.dateStatistic),
                wordStats("sentences", bundle, stats.sentenceStatistic),
                wordStats("words", bundle, stats.wordStatistic),
                numberStats("sums", bundle, stats.currencyStatistic),
                numberStats("numbers", bundle, stats.numberStatistic),
                numberStats("dates", bundle, stats.dateStatistic));

        try {
            Files.writeString(Path.of(output), returnText);
        } catch (final IOException e) {
            System.err.println("Error while writing to output file: " + e.getMessage());
        }
    }

    private void parseDate(final String sentence, final int position, final DateStatistic dateStatistic) {
        for (final DateFormat format : dateFormat) {
            Date date = null;
            final ParsePosition p = new ParsePosition(position);
            date = format.parse(sentence, p);
            if (date == null) {
                continue;
            }
            dateStatistic.addElement(date);
            return;
        }
    }

    private boolean parseNumber(final String word, final NumberStatistic statistic, final NumberFormat format) {
        final Double currency;
        try {
            currency = format.parse(word).doubleValue();
        } catch (final ParseException e) {
            return false;
        }
        statistic.addElement(currency);
        return true;
    }

    private void parseWord(final String word, final WordsStatistic wordsStatistic) {
        if (word == null || word.isEmpty()) {
            return;
        }
        boolean isWord = false;
        for (final char c : word.toCharArray()) {
            if (Character.isLetter(c)) {
                isWord = true;
                break;
            }
        }
        if (!isWord) {
            return;
        }
        wordsStatistic.addElement(word);
    }

    private String amountStat(final String key, final AbstractStatisticResourceBundle bundle, final AbstractStatistic<?> statistic) {
        // :NOTE: Конкатенация строк
        return MessageFormat.format(
                "\t{0} {1}: {2}",
                bundle.getString("Number of"), bundle.getString(key), statistic.getUniqueStat().size());
    }

    private String wordStats(final String key, final AbstractStatisticResourceBundle bundle, final WordsStatistic statistic) {
        return MessageFormat.format(
                "{0} {1}\n" +
                        "\t{2} {3}: {4}\n" +
                        "\t{5}: \"{6}\"\n" +
                        "\t{7}: \"{8}\"\n" +
                        "\t{9}: {10} (\"{11}\")\n" +
                        "\t{12}: {13} (\"{14}\")\n" +
                        "\t{15}: {16}",
                bundle.getString("Statistic of"), bundle.getString(key),
                bundle.getString("Number of"), bundle.getString(key), statistic.getTotalCount(),
                bundle.getString("Min"), statistic.getMin(),
                bundle.getString("Max"), statistic.getMax(),
                bundle.getString("Min length"), statistic.getMinLength(), statistic.getMinLengthElement(),
                bundle.getString("Max length"), statistic.getMaxLength(), statistic.getMaxLengthElement(),
                bundle.getString("Average value"), statistic.getAverage());
    }

    private String numberStats(final String key, final AbstractStatisticResourceBundle bundle, final AbstractStatistic<?> statistic) {
        return MessageFormat.format(
                "{0} {1}\n" +
                        "\t{2} {3}: {4}\n" +
                        "\t{5}: \"{6}\"\n" +
                        "\t{7}: \"{8}\"\n" +
                        "\t{9}: {10}",
                bundle.getString("Statistic of"), bundle.getString(key),
                bundle.getString("Number of"), bundle.getString(key), statistic.getTotalCount(),
                bundle.getString("Min"), statistic.getMin(),
                bundle.getString("Max"), statistic.getMax(),
                bundle.getString("Average value"), statistic.getAverage());
    }

    // :NOTE: ???
    public static void main(final String[] args) {
        /*if (args == null || args.length != 4) {
            System.err.println("Error reading arguments. Command should be: [inputLocale] [outputLocale] [input] [output]");
            return;
        }
        Locale inputLocale = new Locale(args[0]);
        Locale outputLocale = new Locale(args[1]);
        String input = args[2];
        String output = args[3];
        TextStatistic statistic = new TextStatistic(inputLocale, outputLocale);
        statistic.makeStatistics(input, output);*/
        final TextStatistic st = new TextStatistic(Locale.US, new Locale("en"));
        st.makeStatistics("D:\\workspace\\metop\\concurrent\\src\\info\\kgeorgiy\\ja\\vircev\\statistic\\input.txt",
                "src\\info\\kgeorgiy\\ja\\vircev\\statistic\\output.txt");
    }
}
