package com.snr.qmom.db;

import com.snr.qmom.db.model.Equity;
import com.snr.qmom.db.model.Quote;
import com.snr.qmom.functions.DownloadQuoteHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sajimathew on 2/13/17.
 */
public class DBAccess {
    protected static final Logger logger = LoggerFactory.getLogger(DBAccess.class);

    private final Connection conn;
    private static DBAccess dbAccess = null;

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

    private  DBAccess() throws SQLException, ClassNotFoundException {

        conn = getDBConnection();
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
        PreparedStatement stmt = conn.prepareStatement("select symbol from equities where is_valid is true");
        ResultSet rs = stmt.executeQuery();
        List<String> symList = new ArrayList<String>();
        while(rs.next()){
            symList.add(rs.getString(1));
        }

        return symList;

    }
    public void updateInvalidEquity(String symbol) throws SQLException {
        String insertStmt = "UPDATE equities SET is_value = false WHERE symbol = ?";
        PreparedStatement stmt = conn.prepareStatement(insertStmt);
        stmt.setString(1,symbol);
        stmt.execute();
    }
    public void insertQuote(List<Quote> quoteList) throws SQLException {
        for(Quote quote : quoteList){
            insertQuote(quote);
        }
    }
    public void insertQuote(Quote quote) throws SQLException {
        String insertStmt = "REPLACE INTO quote(symbol,date,open,high,low,close,volume,dailyReturn) values(?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(insertStmt);

        stmt.setString(1,quote.getSymbol());
        stmt.setDate(2,quote.getDate());
        stmt.setDouble(3,quote.getOpen());
        stmt.setDouble(4,quote.getHigh());
        stmt.setDouble(5,quote.getLow());
        stmt.setDouble(6,quote.getClose());
        stmt.setLong(7,quote.getVolume());
        stmt.setDouble(8,quote.getDailyReturn());

        stmt.execute();

    }
    public void updateEquityDates(String symbol, Date latestDate, Date earliestDate) throws SQLException {
        String insertStmt = "REPLACE INTO equity_dates(symbol,latest_date,earliest_date) values(?,?,?)";

        PreparedStatement stmt = conn.prepareStatement(insertStmt);

        stmt.setString(1,symbol);
        stmt.setDate(2,latestDate);
        stmt.setDate(3,earliestDate);

        stmt.execute();

    }

}
