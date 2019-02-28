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

public class Sniffer
    implements DelegatingIncrementalUpdateDecoder.Listener
{
    private static final int MAX_UDP_PACKET_SIZE = 65535 - 8 - 20;

    private static NetworkInterface
    findNetworkInterface (String iface)
            throws SocketException, IOException
    {
        if (iface == null || iface.isEmpty())
        {
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

    private static boolean
    isHeartbeat (@NotNull PacketHeader packetHeader)
    {
        return packetHeader.getMessageCount() == 0;
    }

    private void run (String multicastIp, int port, String networkInterfaceIp, boolean showHeartbeats)
            throws Exception
    {
        NetworkInterface iface = findNetworkInterface(networkInterfaceIp);
        InetAddress group = InetAddress.getByName(multicastIp);

        DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(port));
        MembershipKey key = dc.join(group, iface);
        if (!key.isValid())
        {
            System.err.println("Problem joining multicast group!");
            System.exit(1);
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

        Sniffer sniffer = new Sniffer();
        sniffer.run(cmd.getOptionValue("mcast"),
                    Integer.parseInt(cmd.getOptionValue("port")),
                    cmd.getOptionValue("iface"),
                    cmd.hasOption("heartbeats"));
    }

    @Override
    public boolean onPacketHeader(@NotNull PacketHeader packetHeader) {
        if (!isHeartbeat(packetHeader))
        {
            System.out.println(packetHeader);
        }
        return true;
    }

    @Override
    public void onHeartbeat(@NotNull PacketHeader packetHeader) {
        System.out.println("heartbeat: " + packetHeader);
    }

    @Override
    public boolean onMessageHeader(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader) {
        return true;
    }

    @Override
    public void onUnknownMessage(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader) {
        System.out.println("\tunknown msg. " + messageHeader);
    }

    @Override
    public void onClearBook(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader, @NotNull ClearBook msg) {
        System.out.println("\t" + msg);
    }

    @Override
    public void onAddOrder(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader, @NotNull AddOrder msg) {
        System.out.println("\t" + msg);
    }

    @Override
    public void onReplaceOrder(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader, @NotNull ReplaceOrder msg) {
        System.out.println("\t" + msg);
    }

    @Override
    public void onDeleteOrder(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader, @NotNull DeleteOrder msg) {
        System.out.println("\t" + msg);
    }

    @Override
    public void onTradingStatus(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader, @NotNull TradingStatus msg) {
        System.out.println("\t" + msg);
    }

    @Override
    public void onTrade(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader, @NotNull Trade msg) {
        System.out.println("\t" + msg);
    }

    @Override
    public void onTradeBreak(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader, @NotNull TradeBreak msg) {
        System.out.println("\t" + msg);
    }

    @Override
    public void onSessionEnd(long instrumentId, long sequenceNumber, @NotNull PacketHeader packetHeader, @NotNull MessageHeader messageHeader, @NotNull SessionEnd msg) {
        System.out.println("\t" + msg);
    }
}
