package info.kgeorgiy.ja.vircev.statistic;

import java.text.BreakIterator;
import java.text.Collator;
import java.util.*;

public abstract class AbstractStatistic<T extends Comparable<? super T>> {
    private Locale locale;
    private Map<T, Integer> wordStat;
    private Set<T> uniqueStat;
    private T min;
    private T max;
    private int minLength;
    private T minLengthElement;
    private T maxLengthElement;
    private int maxLength;
    private int totalLength;
    private int totalCount;
    Collator collator;

    public AbstractStatistic(Locale locale) {
        this.locale = locale;
        wordStat = new HashMap<>();
        uniqueStat = new HashSet<>();
        min = null;
        max = null;
        minLength = 0;
        maxLength = 0;
        totalLength = 0;
        totalCount = 0;
        collator = Collator.getInstance(locale);
        collator.setStrength(Collator.SECONDARY);
    }

    public void addElement(T element) {
        if (element == null) {
            return;
        }
        int length = countLength(element.toString());
        totalLength += length;
        totalCount++;
        addToWordStat(element);
        addToUniqueStat(element);
        updateMin(element);
        updateMax(element);
        updateMinLength(length, element);
        updateMaxLength(length, element);
    }

    protected int countLength(String element) {
        int length = -1;
        BreakIterator it = BreakIterator.getCharacterInstance(locale);
        it.setText(element);
        for (int i = it.first(); i != BreakIterator.DONE; i = it.next()) {
            length++;
        }
        return length;
    }

    private void addToWordStat(T element) {
        wordStat.put(element, wordStat.getOrDefault(element, 0) + 1);
    }

    private void addToUniqueStat(T element) {
        uniqueStat.add(element);
    }

    private void updateMin(T element) {
        if (min == null || compare(element, min) < 0) {
            min = element;
        }
    }

    private void updateMax(T element) {
        if (max == null || compare(element, max) > 0) {
            max = element;
        }
    }

    private void updateMinLength(int length, T element) {
        if (minLength == 0 || length < minLength) {
            minLength = length;
            minLengthElement = element;
        }
    }

    private void updateMaxLength(int length, T element) {
        if (length > maxLength) {
            maxLength = length;
            maxLengthElement = element;
        }
    }

    public abstract String getAverage();

    protected abstract int compare(T a, T b);

    public Map<T, Integer> getWordStat() {
        return wordStat;
    }

    public Set<T> getUniqueStat() {
        return uniqueStat;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public T getMinLengthElement() {
        return minLengthElement;
    }

    public T getMaxLengthElement() {
        return maxLengthElement;
    }

    public int getTotalLength() {
        return totalLength;
    }
    public int getTotalCount() {
        return totalCount;
    }
}
