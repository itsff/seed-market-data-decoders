package com.seedcx.marketdata.sniffer;

import com.seedcx.marketdata.ByteBufferInputWrapper;
import com.seedcx.marketdata.DelegatingIncrementalUpdateDecoder;
import com.seedcx.marketdata.msg.*;
import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sniffer
    implements DelegatingIncrementalUpdateDecoder.Listener
{
    private static final int MAX_UDP_PACKET_SIZE = 65535 - 8 - 20;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final NetworkInterface iface;
    private final InetAddress group;
    private final int port;
    private final boolean printHeartbeats;

    public Sniffer(String multicastIp, int port, String iface, boolean printHeartbeats)
            throws IOException {
        this.group = InetAddress.getByName(multicastIp);
        this.port = port;
        this.iface = findNetworkInterface(iface);
        this.printHeartbeats = printHeartbeats;
    }

    private void run ()
            throws Exception
    {
        DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(this.port));
        MembershipKey key = dc.join(this.group, this.iface);
        if (!key.isValid())
        {
           throw new Exception("Problem joining multicast group!");
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_UDP_PACKET_SIZE);
        DelegatingIncrementalUpdateDecoder decoder = new DelegatingIncrementalUpdateDecoder(this);
        ByteBufferInputWrapper input = new ByteBufferInputWrapper(byteBuffer);

        while (true)
        {
            byteBuffer.clear();
            InetSocketAddress sourceAddress = (InetSocketAddress) dc.receive(byteBuffer);
            byteBuffer.flip();

            decoder.decodePacket(input);
        }
    }


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
            Option
                .builder("i")
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

    private static NetworkInterface
    findNetworkInterface (String iface)
            throws IOException
    {
        if (iface == null || iface.isEmpty())
        {
            // Get the "any" (0.0.0.0) network interface
            MulticastSocket sock = new MulticastSocket();
            return sock.getNetworkInterface();
        }

        InetAddress ip;
        try {
            ip = InetAddress.getByName(iface);
            return NetworkInterface.getByInetAddress(ip);
        } catch (UnknownHostException e) {
            return NetworkInterface.getByName(iface);
        }
    }


    private void log(long instrumentId,
                     long sequenceNumber,
                     @NotNull PacketHeader packetHeader,
                     @NotNull Object msg)
    {
        StringBuilder sb = new StringBuilder()
                .append(LocalDateTime.now().format(this.dateTimeFormatter))
                .append("|seq:").append(sequenceNumber)
                .append("|sent:").append(packetHeader.getSendingTime())
                .append("|inst:").append(instrumentId)
                .append("|").append(msg);
        System.out.println(sb.toString());
    }

    @Override
    public boolean onPacketHeader(@NotNull PacketHeader packetHeader) {
        // Do process this packet
        return true;
    }

    @Override
    public void onHeartbeat(@NotNull PacketHeader packetHeader) {
        if (this.printHeartbeats) {
            this.log(packetHeader.getInstrumentId(),
                     packetHeader.getSequenceNum(),
                     packetHeader,
                    "Heartbeat");
        }
    }

    @Override
    public boolean onMessageHeader(long instrumentId,
                                   long sequenceNumber,
                                   @NotNull PacketHeader packetHeader,
                                   @NotNull MessageHeader messageHeader) {
        // Do process this message
        return true;
    }

    @Override
    public void onUnknownMessage(long instrumentId,
                                 long sequenceNumber,
                                 @NotNull PacketHeader packetHeader,
                                 @NotNull MessageHeader messageHeader) {
        this.log(instrumentId, sequenceNumber, packetHeader, "Unknown message: " + messageHeader);
    }

    @Override
    public void onClearBook(long instrumentId,
                            long sequenceNumber,
                            @NotNull PacketHeader packetHeader,
                            @NotNull MessageHeader messageHeader,
                            @NotNull ClearBook msg) {
        this.log(instrumentId, sequenceNumber, packetHeader, msg);
    }

    @Override
    public void onAddOrder(long instrumentId,
                           long sequenceNumber,
                           @NotNull PacketHeader packetHeader,
                           @NotNull MessageHeader messageHeader,
                           @NotNull AddOrder msg) {
        this.log(instrumentId, sequenceNumber, packetHeader, msg);
    }

    @Override
    public void onReplaceOrder(long instrumentId,
                               long sequenceNumber,
                               @NotNull PacketHeader packetHeader,
                               @NotNull MessageHeader messageHeader,
                               @NotNull ReplaceOrder msg) {
        this.log(instrumentId, sequenceNumber, packetHeader, msg);
    }

    @Override
    public void onDeleteOrder(long instrumentId,
                              long sequenceNumber,
                              @NotNull PacketHeader packetHeader,
                              @NotNull MessageHeader messageHeader,
                              @NotNull DeleteOrder msg) {
        this.log(instrumentId, sequenceNumber, packetHeader, msg);
    }

    @Override
    public void onTradingStatus(long instrumentId,
                                long sequenceNumber,
                                @NotNull PacketHeader packetHeader,
                                @NotNull MessageHeader messageHeader,
                                @NotNull TradingStatus msg) {
        this.log(instrumentId, sequenceNumber, packetHeader, msg);
    }

    @Override
    public void onTrade(long instrumentId,
                        long sequenceNumber,
                        @NotNull PacketHeader packetHeader,
                        @NotNull MessageHeader messageHeader,
                        @NotNull Trade msg) {
        this.log(instrumentId, sequenceNumber, packetHeader, msg);
    }

    @Override
    public void onTradeBreak(long instrumentId,
                             long sequenceNumber,
                             @NotNull PacketHeader packetHeader,
                             @NotNull MessageHeader messageHeader,
                             @NotNull TradeBreak msg) {
        this.log(instrumentId, sequenceNumber, packetHeader, msg);
    }

    @Override
    public void onSessionEnd(long instrumentId,
                             long sequenceNumber,
                             @NotNull PacketHeader packetHeader,
                             @NotNull MessageHeader messageHeader,
                             @NotNull SessionEnd msg) {
        this.log(instrumentId, sequenceNumber, packetHeader, msg);
    }
}
