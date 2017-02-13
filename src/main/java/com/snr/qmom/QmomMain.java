package com.snr.qmom;

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
        if(!StringUtils.isEmpty(arg.getUpdateSymbols())){
            new UpdateSymbols(arg.getUpdateSymbols());
        }
    }

}
