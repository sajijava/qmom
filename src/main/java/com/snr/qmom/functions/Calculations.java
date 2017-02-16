package com.snr.qmom.functions;

import com.snr.qmom.db.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sajimathew on 2/13/17.
 */
public class Calculations {
    protected static final Logger logger = LoggerFactory.getLogger(Calculations.class);
    public static Comparator<Quote> compareByDate = (o1, o2) -> o1.getDate().compareTo(o2.getDate());

    private static final SimpleDateFormat MMYYYY = new SimpleDateFormat("MM-yyyy");
    private static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("dd-MM-yyyy");

    public static double dailyReturns(final double prvDay, final double currDay){
        return (prvDay > 0) ? (((currDay - prvDay) / prvDay) * 100 ):0;
    }
    public static Map<Date,Double> monthlyReturns( List<Quote> daily){
        Map<Date,Double> monthlyReturns = new HashMap<>();
        daily.stream().sorted(compareByDate).forEach( q -> {
            try {
                String dd = MMYYYY.format(q.getDate());

                Date dt = MMYYYY.parse(dd);
                logger.debug("{} -> {}",dd,dt);
                if (monthlyReturns.containsKey(dt)){
                    monthlyReturns.put(dt, monthlyReturns.get(dt) * q.getDailyReturn());
                }else{
                    monthlyReturns.put(dt, q.getDailyReturn());
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        });


        return monthlyReturns;
    }
}
