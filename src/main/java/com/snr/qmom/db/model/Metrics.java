package com.snr.qmom.db.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by sajimathew on 2/18/17.
 */
public class Metrics {
    private String symbol;
    private double yearlyReturn;
    private double yearlyFip;
    private double halfYrReturns;
    private double halfYrFip;
    private double quarterlyReturns;
    private double quarterlyFip;
    private double fourMonthReturns;
    private double fourMonthFip;
    private Timestamp lastUpdated;

    public Metrics() {
    }

    public Metrics(String symbol, double yearlyReturn, double yearlyFip, double halfYrReturns, double halfYrFip, double quarterlyReturns, double quarterlyFip, double fourMonthReturns, double fourMonthFip, Timestamp lastUpdated) {
        this.symbol = symbol;
        this.yearlyReturn = yearlyReturn;
        this.yearlyFip = yearlyFip;
        this.halfYrReturns = halfYrReturns;
        this.halfYrFip = halfYrFip;
        this.quarterlyReturns = quarterlyReturns;
        this.quarterlyFip = quarterlyFip;
        this.fourMonthReturns = fourMonthReturns;
        this.fourMonthFip = fourMonthFip;
        this.lastUpdated = lastUpdated;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getYearlyReturn() {
        return yearlyReturn;
    }

    public void setYearlyReturn(double yearlyReturn) {
        this.yearlyReturn = yearlyReturn;
    }

    public double getYearlyFip() {
        return yearlyFip;
    }

    public void setYearlyFip(double yearlyFip) {
        this.yearlyFip = yearlyFip;
    }

    public double getHalfYrReturns() {
        return halfYrReturns;
    }

    public void setHalfYrReturns(double halfYrReturns) {
        this.halfYrReturns = halfYrReturns;
    }

    public double getHalfYrFip() {
        return halfYrFip;
    }

    public void setHalfYrFip(double halfYrFip) {
        this.halfYrFip = halfYrFip;
    }

    public double getQuarterlyReturns() {
        return quarterlyReturns;
    }

    public void setQuarterlyReturns(double quarterlyReturns) {
        this.quarterlyReturns = quarterlyReturns;
    }

    public double getQuarterlyFip() {
        return quarterlyFip;
    }

    public void setQuarterlyFip(double quarterlyFip) {
        this.quarterlyFip = quarterlyFip;
    }

    public double getFourMonthReturns() {
        return fourMonthReturns;
    }

    public void setFourMonthReturns(double fourMonthReturns) {
        this.fourMonthReturns = fourMonthReturns;
    }

    public double getFourMonthFip() {
        return fourMonthFip;
    }

    public void setFourMonthFip(double fourMonthFip) {
        this.fourMonthFip = fourMonthFip;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Metrics{" +
                "symbol='" + symbol + '\'' +
                ", yearlyReturn=" + yearlyReturn +
                ", yearlyFip=" + yearlyFip +
                ", halfYrReturns=" + halfYrReturns +
                ", halfYrFip=" + halfYrFip +
                ", quarterlyReturns=" + quarterlyReturns +
                ", quarterlyFip=" + quarterlyFip +
                ", fourMonthReturns=" + fourMonthReturns +
                ", fourMonthFip=" + fourMonthFip +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
