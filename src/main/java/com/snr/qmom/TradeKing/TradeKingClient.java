package com.snr.qmom.TradeKing;


import com.snr.qmom.functions.CheckOptionPrices;
import com.sun.deploy.net.URLEncoder;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by sajimathew on 2/19/17.
 */
public class TradeKingClient
{
    private static final String CONSUMER_KEY = "DJOSGj44FTCBqfVznFXkkk3UiJGB43nmoHrJ6XHx";
    private static final String CONSUMER_SECRET = "krgbfkezq7mx6Jw7jVaytMoa07RXVbtoowGj8YHB";
    private static final String OAUTH_TOKEN = "dJx3amo15p2mdePHVR8bKHfqr39A772Zt5R4rkqM";
    private static final String OAUTH_TOKEN_SECRET = "JnAwXqZOCOkyrwwZVYJmFVQxRohjZnUNgREsPczW";

    private static final String PROTECTED_RESOURCE_URL = "https://api.tradeking.com/v1/";

    protected static final Logger logger = LoggerFactory.getLogger(TradeKingClient.class);

    private OAuthService service = null;

    public TradeKingClient() {
    }

    public Object get(String restfulApi) {

        logger.info("RestAPI Request : {}",restfulApi);

        OAuthService service = new ServiceBuilder()
                .provider(TradeKingAPI.class)
                .apiKey(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .build();
        Token accessToken = new Token(OAUTH_TOKEN, OAUTH_TOKEN_SECRET);

        String url = PROTECTED_RESOURCE_URL+restfulApi;
        logger.info("Request Url : {}",url);
        OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(accessToken, request);
        Response response = request.send();
        return response.getBody();
    }
}