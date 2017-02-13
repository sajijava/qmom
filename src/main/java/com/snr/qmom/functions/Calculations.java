package com.snr.qmom.functions;

/**
 * Created by sajimathew on 2/13/17.
 */
public class Calculations {
    public static double dailyReturns(double prvDay, double currDay){
        return (prvDay > 0) ?(currDay - prvDay) / prvDay:0;
    }
}
