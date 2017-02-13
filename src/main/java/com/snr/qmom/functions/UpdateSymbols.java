package com.snr.qmom.functions;

import com.opencsv.CSVReader;
import com.snr.qmom.db.DBAccess;
import com.snr.qmom.db.model.Equity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sajimathew on 2/13/17.
 */
public class UpdateSymbols {
    // protected static final Logger logger = LoggerFactory.getLogger(UpdateSymbols.class);



    public UpdateSymbols(String updateFile) throws IOException, SQLException, ClassNotFoundException {
        File fl  = new File(updateFile);
        if(fl.exists()) {

            List<Equity> equites = readCSV(fl);
            DBAccess.getInstance().updateTables(equites);
        }
    }

    private List<Equity> readCSV(File fl) throws IOException {
        List<Equity> equities = new ArrayList<Equity>();


        CSVReader csvReader = new CSVReader((new FileReader(fl)));

        csvReader.readNext();
        String nextLine[];
        while ((nextLine = csvReader.readNext()) != null) {
            if (!nextLine[6].equals("n/a") && !nextLine[6].equals("n/a") && !nextLine[0].contains("^")) {
                int ipoYear = (nextLine[5].equals("n/a")) ? 0 : Integer.parseInt(nextLine[5]);
                Equity eqt = new Equity(nextLine[0], nextLine[1], ipoYear, nextLine[6], nextLine[7]);
                equities.add(eqt);
            }
        }

        return equities;
    }


}
