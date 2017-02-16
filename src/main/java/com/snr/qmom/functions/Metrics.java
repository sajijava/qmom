package com.snr.qmom.functions;

import com.snr.qmom.db.DBAccess;
import com.snr.qmom.db.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * Created by sajimathew on 2/15/17.
 */
public class Metrics {
    protected static final Logger logger = LoggerFactory.getLogger(Metrics.class);
    public Metrics() {
        try {
            List<String> symbolList = DBAccess.getInstance().getExistingSymbols();
            for(String symbol : symbolList){
                List<Quote> quoteList = DBAccess.getInstance().getQuote(symbol);
                Map<Date,Double> monthlyReturns = Calculations.monthlyReturns(quoteList);
                // calculate yearly
                Comparator<Date> comparator = (d1, d2) -> { return d1.compareTo(d2); };
                List<Date> months = monthlyReturns.keySet().stream().sorted(comparator.reversed()).collect(Collectors.toList());

                double yearlyReturns = 1d;


                for(int i = 1; i < 12; i++){
                    yearlyReturns *= monthlyReturns.get(months.get(i));
                }
                logger.debug("{} = {}",symbol,yearlyReturns);
            }
        } catch (SQLException e) {
            logger.error("{}",e);
        }
    }
}
