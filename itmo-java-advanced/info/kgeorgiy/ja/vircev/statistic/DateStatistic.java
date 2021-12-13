package info.kgeorgiy.ja.vircev.statistic;

import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class DateStatistic extends AbstractStatistic<Date> {
    public DateStatistic(Locale locale) {
        super(locale);
    }

    @Override
    public String getAverage() {
        BigInteger total = BigInteger.ZERO;
        for (Map.Entry<Date, Integer> entry : getWordStat().entrySet()) {
            BigInteger currDate = BigInteger.valueOf(entry.getKey().getTime());
            currDate = currDate.multiply(BigInteger.valueOf(entry.getValue()));
            total = total.add(currDate);

        }
        BigInteger averageMillis;
        BigInteger amount = BigInteger.valueOf(getTotalCount());
        if (amount.compareTo(BigInteger.ZERO) == 0) {
            averageMillis = BigInteger.ONE;
        } else {
            averageMillis = total.divide(amount);
        }
        Date averageDate = new Date(averageMillis.longValue());
        return averageDate.toString();
    }

    @Override
    protected int compare(Date a, Date b) {
        return a.compareTo(b);
    }
}
