package com.snr.qmom.functions;

import com.snr.qmom.db.DBAccess;
import com.snr.qmom.db.model.Metrics;
import com.snr.qmom.db.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by sajimathew on 2/15/17.
 */
public class CalcMomentum {
    protected static final Logger logger = LoggerFactory.getLogger(CalcMomentum.class);
    public CalcMomentum() {
        try {
            List<String> symbolList = DBAccess.getInstance().getExistingSymbols();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            for(String symbol : symbolList) {
                List<Quote> quoteList = DBAccess.getInstance().getQuoteFromDate(symbol, new java.sql.Date(cal.getTime().getTime()));
                Map<Date, Double> monthlyReturns = Calculations.monthlyReturns(quoteList);
                Calculations.calcGrossMonthlyReturns(monthlyReturns);

                Comparator<Date> comparator = (d1, d2) -> {
                    return d1.compareTo(d2);
                };
                List<Date> months = monthlyReturns.keySet().stream().sorted(comparator.reversed()).collect(Collectors.toList());


                Metrics metrics = new Metrics();
                metrics.setSymbol(symbol);
                metrics.setLastUpdated(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                if (monthlyReturns.keySet().size() >= 12) {
                    double yearlyReturns = getCummulativeReturns(monthlyReturns, months);
                    metrics.setYearlyReturn(yearlyReturns);
                    metrics.setYearlyFip(getFIP(quoteList, (yearlyReturns > 0 ? 1 : (yearlyReturns < 0) ? -1 : 0)));
                }
                if (monthlyReturns.keySet().size() >= 6){
                    double halfYearlyReturns = getCummulativeReturns(monthlyReturns, months.subList(0, 6));
                    metrics.setHalfYrReturns(halfYearlyReturns);
                    metrics.setHalfYrFip(getFIP(getSubset(quoteList, months.subList(0, 6)), (halfYearlyReturns > 0 ? 1 : (halfYearlyReturns < 0) ? -1 : 0)));
                }
                if (monthlyReturns.keySet().size() >= 3) {
                    double quaterlyReturns = getCummulativeReturns(monthlyReturns, months.subList(0, 3));
                    metrics.setQuarterlyReturns(quaterlyReturns);
                    metrics.setQuarterlyFip(getFIP(getSubset(quoteList, months.subList(0, 3)), (quaterlyReturns > 0 ? 1 : (quaterlyReturns < 0) ? -1 : 0)));
                }

                if (monthlyReturns.keySet().size() >= 4) {
                    double fourMonthlyReturns = getCummulativeReturns(monthlyReturns, months.subList(0, 4));
                    metrics.setFourMonthReturns(fourMonthlyReturns);
                    metrics.setFourMonthFip(getFIP(getSubset(quoteList, months.subList(0, 4)), (fourMonthlyReturns > 0 ? 1 : (fourMonthlyReturns < 0) ? -1 : 0)));
                }

                    logger.debug("Returns/Fip {}",metrics);
                    DBAccess.getInstance().insertMomentum(metrics);

            }
        } catch (SQLException e) {
            logger.error("{}",e);
        }
    }
    private List<Quote> getSubset(List<Quote> quoteList,List<Date> months){
        List<Quote> returnList = new ArrayList<>();
        Date lastDate = months.get(months.size() - 1);
        Calendar calLast = dateToCalendar(months.get(months.size() - 1));
        returnList = quoteList.stream().filter(q ->{
          Calendar  calQuote = dateToCalendar(q.getDate());
            return (calQuote.get(Calendar.MONTH) >= calLast.get(Calendar.MONTH)
                    && calQuote.get(Calendar.YEAR) >= calLast.get(Calendar.YEAR) );

        }).collect(Collectors.toList());

        return returnList;
    }
    private final Calendar dateToCalendar(Date dt){
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal;
    }
    private double getCummulativeReturns(Map<Date,Double> monthlyReturns,List<Date> months ){
        double cummReturns = 1.0d;
        for (int i = 1; i < months.size(); i++) {
            double  monthReturn = monthlyReturns.get(months.get(i));
            if(monthReturn != 0)
            cummReturns *= monthReturn;
        }
        cummReturns = cummReturns - 1;
        return cummReturns;
    }
    public double getFIP(List<Quote> quoteList,int sign){

        Map<String,AtomicInteger> counter = new HashMap<>();

        counter.put("U",new AtomicInteger());
        counter.put("D",new AtomicInteger());

       quoteList.stream().forEach(q -> {
           if(q.getDailyReturn() > 0)
               counter.get("U").incrementAndGet();
           else if(q.getDailyReturn() < 0)
               counter.get("D").incrementAndGet();

       });

        return sign * (((double)counter.get("D").get()/quoteList.size()) - ((double)counter.get("U").get()/quoteList.size()));
    }
}
