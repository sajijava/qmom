package com.snr.qmom.functions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.snr.qmom.TradeKing.TradeKingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sajimathew on 2/20/17.
 *
 *
 * Running example
 *
 * -ck /Users/sajimathew/Downloads/longTop50HalfYrlyMetric.csv~~sym=c6~pr=c3~exm=6
 */
public class CheckOptionPrices {
    public static final String COL_POS_SYMBOL = "sym";
    public static final String COL_POS_PRICE = "pr";
    public static final String COL_POS_EXP_MONTHS = "exm";


    private static final SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    private static final String GET_EXPIRATIONS = "market/options/expirations.json?symbol=%s";
    private static final String GET_STRIKES = "market/options/strikes.json?symbol=%s";
    private static final String SEARCH_OPTION = "market/options/search.json?";
    private static final String SEARCH_OPTION_PARAMS = "symbol=%s&query=xdate-eq:%s AND strikeprice-gt:%s";

    protected static final Logger logger = LoggerFactory.getLogger(CheckOptionPrices.class);
    TradeKingClient tkClient = new TradeKingClient();
    File tickerFile = null;
    Map<String,String> columnPos = new HashMap<>();
    public CheckOptionPrices(String tickerSymbolFile) throws IOException {


        String[] params = tickerSymbolFile.split("~~");
        tickerFile = new File(params[0]);

        if(!tickerFile.exists()) {
            logger.error("Cannot find file {}", params[0]);

        }else if(params.length > 1) {
            String[] pos = params[1].split("~");
            for (String col : pos) {
                String[] cols = col.split("=");
                columnPos.put(cols[0],cols[1]);
            }

            fetchOptionPremiums();

/*
            String symbol = columnPos.get(COL_POS_SYMBOL);
            Date expire = getClosestExpire(symbol,Integer.parseInt(columnPos.get(COL_POS_EXP_MONTHS)));
            double strike = getClosestStrike(symbol,Double.parseDouble(columnPos.get(COL_POS_PRICE)));
*/


        }else{
            logger.error("No Column mappings provided {}",tickerSymbolFile);
        }

        logger.debug("{}",tkClient.get("market/options/search.json?symbol=MSFT&query=strikeprice-gt:20"));
    }

    public void fetchOptionPremiums() throws IOException {

        CSVReader csvReader = new CSVReader((new FileReader(tickerFile)));
        Map<String,OptionPrice> options = new HashMap<>();
        String[] row = csvReader.readNext();
        while((row = csvReader.readNext()) != null){

            OptionPrice price = new OptionPrice();
            String symbol = row[getColumnIndex("sym")];

            price.setSymbol(symbol);
            price.setClose(Double.parseDouble(row[getColumnIndex("pr")]));

            options.put(symbol,price);
        }

        options.entrySet().stream().forEach(x -> {
            Date expire = getClosestExpire(x.getKey(),Integer.parseInt(columnPos.get(COL_POS_EXP_MONTHS)));
            if(expire != null){
                //double strike = getClosestStrike(x.getKey(), x.getValue().getClose());
                Map<String,String> optionData = searchOptionPrice(x.getKey(),expire,x.getValue().getClose());

            }

        });



    }
    private int getColumnIndex(String key){
        return Integer.parseInt(this.columnPos.get(key).substring(1)) - 1;
    }
    /*private double getClosestStrike(String symbol,double currentPrice){
        double strike = 0d;
        String strikes = (String)tkClient.get(String.format(GET_STRIKES,symbol));
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(strikes);
            logger.debug("found closest strike {} after {} ",currentPrice,currentPrice);
        } catch (IOException e) {
            logger.error("{}",e);
        }

        return strike;
    }*/

    private Map<String,String> searchOptionPrice(String symbol,Date expireDate, double currentPrice){
        ObjectMapper mapper = new ObjectMapper();
        Map<String,String> returnObj = null;

        try {
            String url = String.format(SEARCH_OPTION_PARAMS,symbol,yyyyMMdd.format(expireDate), currentPrice);
            url = url.replaceAll(":","%3A");
            url = url.replaceAll(" ","%20");
            String optionPrice = (String)tkClient.get(SEARCH_OPTION+url);
            JsonNode node = mapper.readTree(optionPrice);
            JsonNode data = node.path("response").path("quotes").path("quote");
            if(data.isArray() && data.iterator().hasNext()){
                JsonNode d = data.iterator().next();
                returnObj = mapper.convertValue(d,Map.class);
            }
            logger.debug("search for symbol {} date {} currentPrice {}",symbol,expireDate,currentPrice);
        } catch (IOException e) {
            logger.error("{}",e);
        }
        return null;
    }


    private Date getClosestExpire(String symbol,int months){
        Date expire = null;
        Date lastExpire = null;
        ObjectMapper mapper = new ObjectMapper();

        Calendar today = Calendar.getInstance();
        today.add(Calendar.MONTH,+months);



        try {
            String expirations = (String)tkClient.get(String.format(GET_EXPIRATIONS,symbol));

            JsonNode node = mapper.readTree(expirations);
            JsonNode obj = node.path("response").path("expirationdates").path("date");
            if(obj.isArray()){
                Iterator<JsonNode> iterator = obj.iterator();
                while(iterator.hasNext()){
                    String dt = iterator.next().textValue();
                    logger.debug("{}",dt);
                    lastExpire = yyyy_MM_dd.parse(dt);

                    if(lastExpire.after(today.getTime())){
                        expire = lastExpire;

                        break;
                    }
                }
            }
            if(expire == null){
                expire = lastExpire;
            }
            logger.debug("found expire after {} month(s) {} from {}",months,lastExpire,today.getTime());
        } catch (IOException e) {
            logger.error("{}",e);
        } catch (ParseException e) {
            logger.error("{}",e);
        }
        return expire;
    }

    static class OptionPrice{
        private String symbol;
        private double close;
        private double strike;
        private Date expire;
        private double premium;
        private double delta;
        private double gamma;
        private double impliedVol;
        private double historicVol;

        public OptionPrice() {
        }

        public double getClose() {
            return close;
        }

        public void setClose(double close) {
            this.close = close;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public double getStrike() {
            return strike;
        }

        public void setStrike(double strike) {
            this.strike = strike;
        }

        public Date getExpire() {
            return expire;
        }

        public void setExpire(Date expire) {
            this.expire = expire;
        }

        public double getPremium() {
            return premium;
        }

        public void setPremium(double premium) {
            this.premium = premium;
        }

        public double getDelta() {
            return delta;
        }

        public void setDelta(double delta) {
            this.delta = delta;
        }

        public double getGamma() {
            return gamma;
        }

        public void setGamma(double gamma) {
            this.gamma = gamma;
        }

        public double getImpliedVol() {
            return impliedVol;
        }

        public void setImpliedVol(double impliedVol) {
            this.impliedVol = impliedVol;
        }

        public double getHistoricVol() {
            return historicVol;
        }

        public void setHistoricVol(double historicVol) {
            this.historicVol = historicVol;
        }
    }

}
