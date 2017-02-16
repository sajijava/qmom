package com.snr.qmom.functions;

import com.snr.qmom.db.model.Quote;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sajimathew on 2/15/17.
 */
public class CalculationsTest {

    @Test
    public void testMontlyReturns(){

        List<Quote> quote = new ArrayList<Quote>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        try {
            quote.add(new Quote("AA",new Date(sdf.parse("01/01/16").getTime()),0d,0d,0d,0d,0l,1.0d));
            quote.add(new Quote("AA",new Date(sdf.parse("01/03/16").getTime()),0d,0d,0d,0d,0l,2.0d));
            quote.add(new Quote("AA",new Date(sdf.parse("01/04/16").getTime()),0d,0d,0d,0d,0l,3.0d));

            quote.add(new Quote("AA",new Date(sdf.parse("02/03/16").getTime()),0d,0d,0d,0d,0l,5.0d));
            quote.add(new Quote("AA",new Date(sdf.parse("02/04/16").getTime()),0d,0d,0d,0d,0l,6.0d));

            quote.add(new Quote("AA",new Date(sdf.parse("03/03/16").getTime()),0d,0d,0d,0d,0l,7.0d));
            quote.add(new Quote("AA",new Date(sdf.parse("03/04/16").getTime()),0d,0d,0d,0d,0l,8.0d));

            quote.add(new Quote("AA",new Date(sdf.parse("04/04/16").getTime()),0d,0d,0d,0d,0l,9.0d));
/*
            Map<Date,Double> r = Calculations.monthlyReturns(quote);
            Assert.assertNotNull(r);
            Assert.assertEquals(r.size(),4);
            Assert.assertEquals(r.get("2016-0").doubleValue(),6,0.0);
            Assert.assertEquals(r.get("2016-1").doubleValue(),30,0.0);
            Assert.assertEquals(r.get("2016-2").doubleValue(),56,0.0);
            Assert.assertEquals(r.get("2016-3").doubleValue(),9,0.0);*/

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
