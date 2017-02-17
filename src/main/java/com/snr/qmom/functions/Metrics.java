package com.snr.qmom.functions;

import com.snr.qmom.db.DBAccess;
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
public class Metrics {
    protected static final Logger logger = LoggerFactory.getLogger(Metrics.class);
    public Metrics() {
        try {
            List<String> symbolList = DBAccess.getInstance().getExistingSymbols();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.YEAR, -1);
            for(String symbol : symbolList){
                List<Quote> quoteList = DBAccess.getInstance().getQuoteFromDate(symbol, new java.sql.Date(cal.getTime().getTime()));
                Map<Date,Double> monthlyReturns = Calculations.monthlyReturns(quoteList);
                Calculations.calcGrossMonthlyReturns(monthlyReturns);

                if(monthlyReturns.keySet().size() >= 12) {
                    // calculate yearly
                    Comparator<Date> comparator = (d1, d2) -> {
                        return d1.compareTo(d2);
                    };
                    List<Date> months = monthlyReturns.keySet().stream().sorted(comparator.reversed()).collect(Collectors.toList());

                    double yearlyReturns = 1d;


                    for (int i = 1; i < 12; i++) {
                        yearlyReturns *= monthlyReturns.get(months.get(i));
                    }
                    yearlyReturns = yearlyReturns - 1;
                    double fid = getFID(quoteList);
                    logger.debug("{} = {} ({}), FID = {}", symbol, yearlyReturns, yearlyReturns*100,fid);
                    DBAccess.getInstance().insertMetrics(symbol,yearlyReturns,fid);
                }
            }
        } catch (SQLException e) {
            logger.error("{}",e);
        }
    }
    public double getFID(List<Quote> quoteList){
        double fid = 0.0d;
        Map<String,AtomicInteger> counter = new HashMap<>();
        counter.put("U",new AtomicInteger());
        counter.put("D",new AtomicInteger());
        quoteList.stream().forEach(q -> {
            if(q.getClose() > q.getOpen())
                counter.get("U").incrementAndGet();
            else if(q.getClose() < q.getOpen())
                counter.get("D").incrementAndGet();

        });

        return (double)counter.get("U").get()/(double)counter.get("D").get();
    }
}
