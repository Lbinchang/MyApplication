package com.tiza.pub.rp.support.config;


import org.apache.commons.cli.*;

public class CliParser {
    private Options options;
    private CommandLineParser parser;
    private CommandLine cli;

    private CliParser(Options options, CommandLineParser parser){
        this.options = options;
        this.parser = parser;
    }

    public void parse(String[] args) throws ParseException {
        cli = parser.parse(options, args);
    }

    public String configPath(){
        return cli.getOptionValue("config", "config/application.properties");
    }

    public String profile(){
        return cli.getOptionValue("profile", null);
    }

    public boolean debug(){
        return cli.hasOption("debug");
    }

  /*  public StormRunMode runAt(){
        if(cli.hasOption("runat"))
            return StormRunMode.valueOf(cli.getOptionValue("runat"));
        return StormRunMode.Cluster;
    }*/

    public boolean forceFromStart(){
        return cli.hasOption("fromstart");
    }

    public boolean hasMonitor(){
        return cli.hasOption("monitor");
    }

    public String monitor(){
        if(cli.hasOption("monitor")){
            return cli.getOptionValue("m");
        }
        return null;
    }

    public static class Builder{
        private Options options;

        public Builder(){
            this.options = new Options();
        }

        public CliParser build(){
            options.addOption("d", "debug", false, "run as debug mode");
            options.addOption("r", "runat", true, "run as strom cluster mode:local or cluster");
            options.addOption("c", "config", true, "config properties file path");
            options.addOption("p", "profile", true, "config profile");
            options.addOption("f", "fromstart", false, "force consume from kafka topic start");
            Option monitor = new Option("m", "monitor", true, "set monitor target such as vehicleId/deviceId, comma split with multi target");
            options.addOption(monitor);
            CommandLineParser  parser = new PosixParser();
            return new CliParser(options, parser);
        }
    }
}

