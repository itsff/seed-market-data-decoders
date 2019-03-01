package com.seedcx.marketdata.sniffer;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption(
                Option.builder("m")
                        .longOpt("mcast")
                        .hasArg()
                        .desc("Multicast address.")
                        .required()
                        .build());
        options.addOption(
                Option.builder("p")
                        .longOpt("port")
                        .hasArg()
                        .desc("Multicast port.")
                        .required()
                        .build());
        options.addOption(
                Option.builder("i")
                        .longOpt("iface")
                        .desc("Network interface IP")
                        .required(false)
                        .hasArg()
                        .build());
        options.addOption(
                Option.builder("b")
                        .longOpt("heartbeats")
                        .desc("Display heartbeats")
                        .required(false)
                        .hasArg(false)
                        .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp("sniffer", options);

            System.exit(1);
            return;
        }

        Sniffer sniffer = new Sniffer(cmd.getOptionValue("mcast"),
                Integer.parseInt(cmd.getOptionValue("port")),
                cmd.getOptionValue("iface"),
                Boolean.parseBoolean(cmd.getOptionValue("heartbeats", "false")));
        sniffer.run();
    }
}
