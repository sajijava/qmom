package com.snr.qmom;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


/**
 * Created by sajimathew on 2/13/17.
 */
public class QmomArgs {
    protected static final Logger logger = LoggerFactory.getLogger(QmomArgs.class);

    @Parameter(names = {"--updateSymbols"},  description = "Update Symbols")
    private String updateSymbols;

    @Parameter(names = {"--downloadQuote", "-d"},  description = "downloadQuote")
    private boolean downloadQuote;

    @Parameter(names = {"--downloadSymbol", "-ds"},  description = "Download history for one symbol")
    private String downloadSymbol;

    @Parameter(names = {"--momentum", "-m"},  description = "Calculate momentum yearly return and FIP")
    private boolean momentum;

    @Parameter(names = {"--report", "-r"},  description = "Fetch report based on the given SQL query.")
    private String report;

    @Parameter(names = {"--checkoption", "-ck"},  description = "Check option price. By default it will check for  6mnth expire, 1 price point apart. e.g. <file.csv>~~Sym=C1~Pr=c2")
    private String checkOption;




    private final JCommander commander;

    public QmomArgs(String[] args)
    {
        this.commander = new JCommander(this);

        try{
            this.commander.parse(args);
        }catch(Exception e){
            logger.error("Error while parsing command arguments : " + Arrays.toString(args), e);
        }
    }

    public void usage(String programName){
        this.commander.setProgramName("java -cp $classpath " + programName);
        this.commander.usage();
    }

    public String getUpdateSymbols() {
        return updateSymbols;
    }

    public void setUpdateSymbols(String updateSymbols) {
        this.updateSymbols = updateSymbols;
    }

    public boolean isDownloadQuote() {
        return downloadQuote;
    }

    public void setDownloadQuote(boolean downloadQuote) {
        this.downloadQuote = downloadQuote;
    }

    public String getDownloadSymbol() {
        return downloadSymbol;
    }

    public void setDownloadSymbol(String downloadSymbol) {
        this.downloadSymbol = downloadSymbol;
    }

    public boolean isMomentum() {
        return momentum;
    }

    public void setMomentum(boolean momentum) {
        this.momentum = momentum;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getCheckOption() {
        return checkOption;
    }

    public void setCheckOption(String checkOption) {
        this.checkOption = checkOption;
    }
}

