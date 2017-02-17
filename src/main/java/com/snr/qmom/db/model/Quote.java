package com.snr.qmom.db.model;

import java.sql.Date;

/**
 * Created by sajimathew on 2/13/17.
 */
public class Quote {
    private String symbol;
    private Date date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    private double dailyReturn = 1.0d;

    public Quote(String symbol, Date date, double open, double high, double low, double close, long volume, double dailyReturn) {
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.dailyReturn = dailyReturn;
    }

    public Quote() {
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public double getDailyReturn() {
        return dailyReturn;
    }

    public void setDailyReturn(double dailyReturn) {
        this.dailyReturn = dailyReturn;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "symbol='" + symbol + '\'' +
                ", date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                ", dailyReturn=" + dailyReturn +
                '}';
    }
}

