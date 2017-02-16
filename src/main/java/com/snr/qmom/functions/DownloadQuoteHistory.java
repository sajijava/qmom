package com.snr.qmom.functions;

import com.snr.qmom.db.DBAccess;
import com.snr.qmom.db.model.Quote;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sajimathew on 2/13/17.
 */
public class DownloadQuoteHistory {


    protected static final Logger logger = LoggerFactory.getLogger(DownloadQuoteHistory.class);
    private static final SimpleDateFormat histDt = new SimpleDateFormat("dd-MMM-yy");

    private static final String HISTORY_URL = "https://www.google.com/finance/historical?output=csv&q=%s";
    public DownloadQuoteHistory() {
        logger.info("Beginning quote history downloads...");
        try {
            List<String> symbolList = DBAccess.getInstance().getExistingSymbols();
            for(String symbol : symbolList)
            {
                processSymbol(symbol);
                Thread.sleep(1000);
            }
        } catch (SQLException e) {
            logger.error("******* {}",e);
        } catch (MalformedURLException e) {
            logger.error("******* {}",e);
        } catch (IOException e) {
            logger.error("******* {}",e);
        } catch (ParseException e) {
            logger.error("******* {}",e);
        } catch (InterruptedException e) {
            logger.error("******* {}",e);
        }
    }
    public DownloadQuoteHistory(String symbol){
        try {
            processSymbol(symbol);
        } catch (IOException e) {
            logger.error("******* {}",e);
        } catch (ParseException e) {
            logger.error("******* {}",e);
        } catch (SQLException e) {
            logger.error("******* {}",e);
        }
    }
    private void processSymbol(String symbol) throws ParseException, SQLException, MalformedURLException {

        logger.info("Fetching history for {}",symbol);

        URL url = new URL(String.format(HISTORY_URL,symbol));
        HttpsURLConnection httpsConn = null;
        try {
            httpsConn = (HttpsURLConnection)url.openConnection();

            BufferedReader d = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
            String line = "";
            List<Quote> quoteHist = new ArrayList<Quote>();
            while( (line = d.readLine()) != null)
            {
                if(line.contains("Close") || StringUtils.isEmpty(line) || StringUtils.isBlank(line)) continue;

                //logger.debug("{}",line);
                Quote  quote = makeQuote(symbol,line);
                if(quote != null ) quoteHist.add(quote);

            }
            if(!quoteHist.isEmpty()) {
                calculateDailyReturns(quoteHist);
                DBAccess.getInstance().insertQuote(quoteHist);
                DBAccess.getInstance().updateEquityDates(symbol, quoteHist.get(0).getDate(), quoteHist.get(quoteHist.size() - 1).getDate());
            }else{
                logger.error("*** No history found for {}",symbol);
                DBAccess.getInstance().updateInvalidEquity(symbol);
            }
        } catch (IOException e) {
            logger.error("Error while fetching {}",e);
            DBAccess.getInstance().updateInvalidEquity(symbol);
        }

    }
    private Quote makeQuote(String symbol,String line) throws ParseException {
        Quote quote = null;
        String[] lineItem = line.split(",");
        if(!lineItem[4].equals("-")) {
            quote = new Quote();
            quote.setSymbol(symbol);
            quote.setDate(new Date(histDt.parse(lineItem[0]).getTime()));
            quote.setOpen((lineItem[1].equals("-")) ? 0 : Double.parseDouble(lineItem[1]));
            quote.setHigh((lineItem[2].equals("-")) ? 0 : Double.parseDouble(lineItem[2]));
            quote.setLow((lineItem[3].equals("-")) ? 0 : Double.parseDouble(lineItem[3]));
            quote.setClose(Double.parseDouble(lineItem[4]));
            quote.setVolume(Long.parseLong(lineItem[5]));

        }


        return quote;
    }
    private void calculateDailyReturns(List<Quote> quoteHist){
        for(int i = 0; i < quoteHist.size() - 1; i++){
            quoteHist.get(i).setDailyReturn( Calculations.dailyReturns(quoteHist.get(i+1).getClose(),quoteHist.get(i).getClose()));
        }
    }
}
