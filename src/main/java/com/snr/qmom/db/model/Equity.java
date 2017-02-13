package com.snr.qmom.db.model;

/**
 * Created by sajimathew on 2/13/17.
 */
public class Equity {
    private String symbol;
    private String name;
    private int ipoYear;
    private String sector;
    private String industry;


    public Equity() {
    }

    public Equity(String symbol, String name, int ipoYear, String sector, String industry) {
        this.symbol = symbol;
        this.name = name;
        this.ipoYear = ipoYear;
        this.sector = sector;
        this.industry = industry;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIpoYear() {
        return ipoYear;
    }

    public void setIpoYear(int ipoYear) {
        this.ipoYear = ipoYear;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    @Override
    public String toString() {
        return "Equity{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", ipoYear=" + ipoYear +
                ", sector='" + sector + '\'' +
                ", industry='" + industry + '\'' +
                '}';
    }
}
