package com.snr.qmom.functions;

import com.snr.qmom.TradeKing.TradeKingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sajimathew on 2/20/17.
 */
public class CheckOptionPrices {

    protected static final Logger logger = LoggerFactory.getLogger(CheckOptionPrices.class);
    TradeKingClient tkClient = new TradeKingClient();

    public CheckOptionPrices() {
        logger.debug("{}",tkClient.get("market/options/search.json?symbol=MSFT&query=strikeprice-gt:20"));
    }

    public static void main(String[] arg){
        new CheckOptionPrices();
    }
}
