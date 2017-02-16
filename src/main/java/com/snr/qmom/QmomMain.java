package com.snr.qmom;

import com.snr.qmom.functions.DownloadQuoteHistory;
import com.snr.qmom.functions.Metrics;
import com.snr.qmom.functions.UpdateSymbols;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by sajimathew on 2/13/17.
 */
public class QmomMain {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        QmomArgs arg = new QmomArgs(args);
        if(!StringUtils.isEmpty(arg.getUpdateSymbols())) {
            new UpdateSymbols(arg.getUpdateSymbols());
        }else if(StringUtils.isNoneEmpty(arg.getDownloadSymbol())){
            new DownloadQuoteHistory(arg.getDownloadSymbol());
        }else if(arg.isDownloadQuote()){
            new DownloadQuoteHistory();
        }else if(arg.isMetric()){
            new Metrics();
        }
    }

}
