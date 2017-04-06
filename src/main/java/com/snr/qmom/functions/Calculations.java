package com.snr.qmom.functions;

import com.snr.qmom.db.model.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by sajimathew on 2/13/17.
 */
public class Calculations {
    protected static final Logger logger = LoggerFactory.getLogger(Calculations.class);
    public static Comparator<Quote> compareByDate = (o1, o2) -> o1.getDate().compareTo(o2.getDate());

    private static final SimpleDateFormat MMYYYY = new SimpleDateFormat("MM-yyyy");
    private static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("dd-MM-yyyy");

    public static double dailyReturns(final double prvDay, final double currDay){
        return (prvDay > 0) ? (((currDay - prvDay) / prvDay) * 100):0;
    }
    public static Map<Date,Double> monthlyReturns( List<Quote> daily){
        Map<Date,Double> monthlyReturns = new TreeMap<>();
        List<Quote> firstDayQuote = new ArrayList<>();

        List<Quote> sortedList = daily.stream().sorted(compareByDate).collect(Collectors.toList());
        IntStream.range(0,sortedList.size()).forEach(i -> {

            if(i == 0) firstDayQuote.add(sortedList.get(i));
            else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(sortedList.get(i).getDate());

                Calendar calP = Calendar.getInstance();
                calP.setTime(sortedList.get(i - 1).getDate());

                if (cal.get(Calendar.MONTH) != calP.get(Calendar.MONTH)) {
                    firstDayQuote.add(sortedList.get(i));
                }
            }
        });
        for(int i = 1; i < firstDayQuote.size();i++){
            double monthlyReturn = (firstDayQuote.get(i).getClose()/firstDayQuote.get(i - 1).getClose()) - 1;
            monthlyReturns.put(firstDayQuote.get(i).getDate(),monthlyReturn);
        }


        return monthlyReturns;
    }


    public static void calcGrossMonthlyReturns(Map<Date,Double> monthlyReturns){

        for(Map.Entry<Date,Double> entry : monthlyReturns.entrySet()){
            entry.setValue(entry.getValue()+1.0d);
        }
    }
}
