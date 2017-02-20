package com.snr.qmom.functions;

import com.snr.qmom.db.DBAccess;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by sajimathew on 2/18/17.
 */
public class Report {

    public Report(String reportFile) {
        File fl = new File(reportFile);
        if(fl.exists()){
            try {
                String sql = readFile(fl);
                List<String> results = DBAccess.getInstance().runQuery(sql);
                results.stream().forEach(System.out::println);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    private String readFile(File fl) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader((fl)));
        String line = "";
        StringBuffer sb = new StringBuffer();
        while((line = br.readLine()) != null){
            sb.append(line).append(" ");
        }
        return sb.toString();
    }
    private void printCsv()
    {

    }
}
