package com.snr.qmom.db;

import com.snr.qmom.db.model.Equity;
import com.snr.qmom.db.model.Metrics;
import com.snr.qmom.db.model.Quote;
import com.snr.qmom.functions.DownloadQuoteHistory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sajimathew on 2/13/17.
 */
public class DBAccess {
    protected static final Logger logger = LoggerFactory.getLogger(DBAccess.class);
    private static final SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");

    private final Connection conn;
    private static DBAccess dbAccess = null;

    private  DBAccess() throws SQLException, ClassNotFoundException {

        conn = getDBConnection();
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

        PreparedStatement getValidEqtyPrepareStmt = conn.prepareStatement("select symbol from equities where is_valid is true");

        ResultSet rs = getValidEqtyPrepareStmt.executeQuery();
        List<String> symList = new ArrayList<String>();
        while(rs.next()){
            symList.add(rs.getString(1));
        }

        getValidEqtyPrepareStmt.close();
        return symList;

    }
    public void updateInvalidEquity(String symbol) throws SQLException {

        String updateValidEqty = "UPDATE equities SET is_valid = false WHERE symbol = ?";

        PreparedStatement updateValidEqtyPreparedStmt = conn.prepareStatement(updateValidEqty);
        updateValidEqtyPreparedStmt.setString(1,symbol);
        updateValidEqtyPreparedStmt.execute();
        updateValidEqtyPreparedStmt.close();
    }
    public void insertQuote(List<Quote> quoteList) throws SQLException {
        for(Quote quote : quoteList){
            insertQuote(quote);
        }
    }
    public void tagLatestQuote(Quote quote) throws SQLException {
        String clearLatesFlag = "update quote set latestQuote = false where symbol = ?";
        PreparedStatement clearStmt = conn.prepareStatement(clearLatesFlag);
        clearStmt.setString(1,quote.getSymbol());
        clearStmt.execute();
        clearStmt.close();

        String updateLatesFlag = "update quote set latestQuote = true where symbol = ? and date = ?";
        PreparedStatement updateLatestStmt = conn.prepareStatement(updateLatesFlag);
        updateLatestStmt.setString(1,quote.getSymbol());
        updateLatestStmt.setDate(2,quote.getDate());

        updateLatestStmt.execute();
        updateLatestStmt.close();


    }
    public void insertQuote(Quote quote) throws SQLException {

        String insertQuoteStmt = "REPLACE INTO quote(symbol,date,open,high,low,close,volume,dailyReturn) values(?,?,?,?,?,?,?,?)";

        PreparedStatement insertQuotePrepareStmt = conn.prepareStatement(insertQuoteStmt);
        insertQuotePrepareStmt.setString(1,quote.getSymbol());
        insertQuotePrepareStmt.setDate(2,quote.getDate());
        insertQuotePrepareStmt.setDouble(3,quote.getOpen());
        insertQuotePrepareStmt.setDouble(4,quote.getHigh());
        insertQuotePrepareStmt.setDouble(5,quote.getLow());
        insertQuotePrepareStmt.setDouble(6,quote.getClose());
        insertQuotePrepareStmt.setLong(7,quote.getVolume());
        insertQuotePrepareStmt.setDouble(8,quote.getDailyReturn());

        insertQuotePrepareStmt.execute();
        insertQuotePrepareStmt.close();

    }
    public void updateEquityDates(String symbol, Date latestDate, Date earliestDate) throws SQLException {

        String updateDateStmt = "REPLACE INTO equity_dates(symbol,latest_date,earliest_date) values(?,?,?)";

        PreparedStatement updateDatePreparedStmt = conn.prepareStatement(updateDateStmt);

        updateDatePreparedStmt.setString(1,symbol);
        updateDatePreparedStmt.setDate(2,latestDate);
        updateDatePreparedStmt.setDate(3,earliestDate);

        updateDatePreparedStmt.execute();
        updateDatePreparedStmt.close();

    }
    public List<Quote> getQuote(String symbol) throws SQLException {

        String getQuoteStmt = "SELECT symbol,date,open,high,low,close,volume,dailyReturn FROM quote WHERE symbol = ?";
        PreparedStatement getQuotePrepareStmt = conn.prepareStatement(getQuoteStmt);


        getQuotePrepareStmt.setString(1,symbol);

        ResultSet rs = getQuotePrepareStmt.executeQuery();

        List<Quote> quoteList = makeQuoteList(rs);
        getQuotePrepareStmt.close();
        return quoteList;
    }

    public List<Quote> getQuoteFromDate(String symbol,Date date) throws SQLException {

        String getQuoteStmt = "select * from qmom.quote where symbol = ?  and MONTH(date) >= ? and YEAR(date) >= ? order by date";
        PreparedStatement getQuotePrepareStmt = conn.prepareStatement(getQuoteStmt);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        getQuotePrepareStmt.setString(1,symbol);
        getQuotePrepareStmt.setInt(2,cal.get(Calendar.MONTH));
        getQuotePrepareStmt.setInt(3,cal.get(Calendar.YEAR));

        ResultSet rs = getQuotePrepareStmt.executeQuery();
        List<Quote> quoteList = makeQuoteList(rs);
        getQuotePrepareStmt.close();

        return quoteList;
    }

    private List<Quote> makeQuoteList(ResultSet rs) throws SQLException {
        List<Quote> quoteList = new ArrayList<Quote>();
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
        return quoteList;
    }
    public void insertMomentum(Metrics metrics) throws SQLException {


        String insertMetricsStmt = "REPLACE INTO metrics(symbol,yearlyReturn,yearlyFip,halfYearlyReturn,halfYearlyFip,quartelyReturn,quartelyFip,fourMonthReturn,fourMonthFip,lastUpdate) " +
                "values(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement insertMetricsPrepareStmt = conn.prepareStatement(insertMetricsStmt);

        insertMetricsPrepareStmt.setString(1,metrics.getSymbol());
        insertMetricsPrepareStmt.setDouble(2,metrics.getYearlyReturn());
        insertMetricsPrepareStmt.setDouble(3,metrics.getYearlyFip());
        insertMetricsPrepareStmt.setDouble(4,metrics.getHalfYrReturns());
        insertMetricsPrepareStmt.setDouble(5,metrics.getHalfYrFip());
        insertMetricsPrepareStmt.setDouble(6,metrics.getQuarterlyReturns());
        insertMetricsPrepareStmt.setDouble(7,metrics.getQuarterlyFip());
        insertMetricsPrepareStmt.setDouble(8,metrics.getFourMonthReturns());
        insertMetricsPrepareStmt.setDouble(9,metrics.getFourMonthFip());
        insertMetricsPrepareStmt.setTimestamp(10,metrics.getLastUpdated());

        insertMetricsPrepareStmt.execute();

        insertMetricsPrepareStmt.close();

    }

    public Map<String,Date> getLatestDateForSymbol(String symbol) throws SQLException {

        String getLatestDates = "SELECT symbol,latest_date FROM equity_dates order by symbol";
        PreparedStatement getLatestDtPrepareStmt = conn.prepareStatement(getLatestDates);

        Map<String,Date> latestDates  = new HashMap<>();

        //getLatestDtPrepareStmt.setString(1,symbol);

        ResultSet rs = getLatestDtPrepareStmt.executeQuery();
        while(rs.next()){
            latestDates.put(rs.getString(1),rs.getDate(2));
        }

        getLatestDtPrepareStmt.close();
        return latestDates;
    }
    public List<String> runQuery(String sql) throws SQLException {
        List<String> results = new ArrayList<>();
        PreparedStatement runSql = conn.prepareStatement(sql);
        ResultSet rs = runSql.executeQuery();
        ResultSetMetaData rsMeta = rs.getMetaData();
        List<String> labels = new ArrayList<>();
        for(int i = 1; i <= rsMeta.getColumnCount();i++){
            labels.add(rsMeta.getColumnLabel(i));
        }
        results.add(StringUtils.join(labels,","));
        while(rs.next()){
            List<String> row = new ArrayList<>();
            for(int i = 1; i <= rsMeta.getColumnCount();i++){
                switch(rsMeta.getColumnType(i)){
                    case Types.DECIMAL:
                        row.add(new Double(rs.getDouble(i)).toString());
                        break;
                    case Types.DATE:
                        row.add(ddMMyyyy.format(rs.getDate(i)));
                        break;
                    case Types.TIMESTAMP:
                        row.add(ddMMyyyy.format(rs.getTimestamp(i)));
                        break;
                    case Types.BIT:
                        row.add(rs.getBoolean(i)?"True":"False");
                        break;
                    default:
                        row.add((String) rs.getObject(i));
                }
            }
            results.add(StringUtils.join(row,","));
        }
        return results;
    }
}
