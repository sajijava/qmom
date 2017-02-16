package com.snr.qmom.db;

import com.snr.qmom.db.model.Equity;
import com.snr.qmom.db.model.Quote;
import com.snr.qmom.functions.DownloadQuoteHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sajimathew on 2/13/17.
 */
public class DBAccess {
    protected static final Logger logger = LoggerFactory.getLogger(DBAccess.class);

    private final Connection conn;
    private static DBAccess dbAccess = null;
    private String insertQuoteStmt = "REPLACE INTO quote(symbol,date,open,high,low,close,volume,dailyReturn) values(?,?,?,?,?,?,?,?)";
    private String updateDateStmt = "REPLACE INTO equity_dates(symbol,latest_date,earliest_date) values(?,?,?)";
    private String updateValidEqty = "UPDATE equities SET is_valid = false WHERE symbol = ?";


    PreparedStatement updateValidEqtyPreparedStmt;
    PreparedStatement updateDatePreparedStmt;
    PreparedStatement insertQuotePrepareStmt;
    PreparedStatement getValidEqtyPrepareStmt;


    private  DBAccess() throws SQLException, ClassNotFoundException {

        conn = getDBConnection();
        insertQuotePrepareStmt = conn.prepareStatement(insertQuoteStmt);
        updateDatePreparedStmt = conn.prepareStatement(updateDateStmt);
        updateValidEqtyPreparedStmt = conn.prepareStatement(updateValidEqty);
        getValidEqtyPrepareStmt = conn.prepareStatement("select symbol from equities where is_valid is true");
    }

    public static DBAccess getInstance(){
        if(dbAccess == null){
            try {
                dbAccess = new DBAccess();
            } catch (SQLException e) {
                logger.error("****** {}",e);
            } catch (ClassNotFoundException e) {
                logger.error("****** {}",e);
            }
        }
        return dbAccess;
    }


    private final Connection getDBConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://localhost/qmom?user=root&password=root");
    }
    public void updateTables(List<Equity> equities) throws SQLException, ClassNotFoundException {

        List<String> symbolList = getExistingSymbols();
        String insertStmt = "REPLACE INTO equities(symbol,name,ipoyear,sector,industry) values(?,?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(insertStmt);

        for(Equity equity:equities){
            if(!symbolList.contains(equity.getSymbol())) {
                stmt.setString(1, equity.getSymbol());
                stmt.setString(2, equity.getName());
                stmt.setInt(3, equity.getIpoYear());
                stmt.setString(4, equity.getSector());
                stmt.setString(5, equity.getIndustry());
                stmt.execute();
            }
        }
    }
    public List<String> getExistingSymbols() throws SQLException {

        ResultSet rs = getValidEqtyPrepareStmt.executeQuery();
        List<String> symList = new ArrayList<String>();
        while(rs.next()){
            symList.add(rs.getString(1));
        }

        return symList;

    }
    public void updateInvalidEquity(String symbol) throws SQLException {

        updateValidEqtyPreparedStmt.setString(1,symbol);
        updateValidEqtyPreparedStmt.execute();
    }
    public void insertQuote(List<Quote> quoteList) throws SQLException {
        for(Quote quote : quoteList){
            insertQuote(quote);
        }
    }
    public void insertQuote(Quote quote) throws SQLException {


        insertQuotePrepareStmt.setString(1,quote.getSymbol());
        insertQuotePrepareStmt.setDate(2,quote.getDate());
        insertQuotePrepareStmt.setDouble(3,quote.getOpen());
        insertQuotePrepareStmt.setDouble(4,quote.getHigh());
        insertQuotePrepareStmt.setDouble(5,quote.getLow());
        insertQuotePrepareStmt.setDouble(6,quote.getClose());
        insertQuotePrepareStmt.setLong(7,quote.getVolume());
        insertQuotePrepareStmt.setDouble(8,quote.getDailyReturn());

        insertQuotePrepareStmt.execute();

    }
    public void updateEquityDates(String symbol, Date latestDate, Date earliestDate) throws SQLException {


        updateDatePreparedStmt.setString(1,symbol);
        updateDatePreparedStmt.setDate(2,latestDate);
        updateDatePreparedStmt.setDate(3,earliestDate);

        updateDatePreparedStmt.execute();

    }
    public List<Quote> getQuote(String symbol) throws SQLException {

        String getQuoteStmt = "SELECT symbol,date,open,high,low,close,volume,dailyReturn FROM quote WHERE symbol = ?";
        PreparedStatement getQuotePrepareStmt = conn.prepareStatement(getQuoteStmt);

        List<Quote> quoteList = new ArrayList<Quote>();
        getQuotePrepareStmt.setString(1,symbol);

        ResultSet rs = getQuotePrepareStmt.executeQuery();

        while(rs.next()){
            Quote q = new Quote();
            q.setSymbol(rs.getString(1));
            q.setDate(rs.getDate(2));
            q.setOpen(rs.getDouble(3));
            q.setHigh(rs.getDouble(4));
            q.setLow(rs.getDouble(5));
            q.setClose(rs.getDouble(6));
            q.setVolume(rs.getLong(7));
            q.setDailyReturn(rs.getDouble(8));

            quoteList.add(q);
        }
        getQuotePrepareStmt.close();
        return quoteList;
    }

}
