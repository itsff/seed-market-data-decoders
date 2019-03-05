package com.seedcx.marketdata.snapshot;


import org.apache.commons.cli.*;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption(
                Option.builder("e")
                        .longOpt("endpoint")
                        .hasArg()
                        .desc("Snapshot endpoint address.")
                        .required()
                        .build());
        options.addOption(
                Option.builder("p")
                        .longOpt("port")
                        .hasArg()
                        .desc("Snapshot endpoint port.")
                        .required()
                        .build());
        options.addOption(
                Option.builder("c")
                        .longOpt("comp_id")
                        .hasArg()
                        .desc("Comp ID.")
                        .required()
                        .build());
        options.addOption(
                Option.builder("i")
                        .longOpt("instrument")
                        .desc("Instrument ID.")
                        .required(true)
                        .hasArgs()
                        .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp("snapshot", options);

            System.exit(1);
            return;
        }

        List<Long> instruments = new ArrayList<>();
        for (final String i : cmd.getOptionValues("instrument"))
        {
            instruments.add(Long.parseUnsignedLong(i));
        }

        Snapshot snapshot = new Snapshot(cmd.getOptionValue("endpoint"),
                Integer.parseInt(cmd.getOptionValue("port")),
                cmd.getOptionValue("comp_id"),
                instruments);
        snapshot.run();
    }
}
