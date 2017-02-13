package com.snr.qmom.db;

import com.snr.qmom.db.model.Equity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sajimathew on 2/13/17.
 */
public class DBAccess {
    private final Connection conn;
    private static DBAccess dbAccess = null;

    public static DBAccess getInstance(){
        if(dbAccess == null){
            try {
                dbAccess = new DBAccess();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
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
        String insertStmt = "INSERT INTO equities(symbol,name,ipoyear,sector,industry) values(?,?,?,?,?)";
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
        PreparedStatement stmt = conn.prepareStatement("select symbol from equities");
        ResultSet rs = stmt.executeQuery();
        List<String> symList = new ArrayList<String>();
        while(rs.next()){
            symList.add(rs.getString(1));
        }

        return symList;

    }

}
