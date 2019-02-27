package com.seedcx.marketdata.sniffer;

import com.seedcx.marketdata.ByteBufferInputWrapper;
import com.seedcx.marketdata.Test;
import com.seedcx.marketdata.msg.AddOrder;
import com.seedcx.marketdata.msg.SnapshotMessageHeader;
import com.seedcx.marketdata.msg.SnapshotSuccessResponse;
import org.apache.commons.cli.*;

import javax.xml.bind.DatatypeConverter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.nio.channels.MulticastChannel;
import java.util.ArrayList;
import java.util.List;

public class Sniffer
{
    private static final int MAX_UDP_PACKET_SIZE = 65535 - 8 - 20;

    private static NetworkInterface
    findNetworkInterface (String iface)
            throws SocketException
    {
        if (iface == null || iface.isEmpty())
        {
            iface = "0.0.0.0";
        }

        InetAddress ip;
        try {
            ip = InetAddress.getByName(iface);
            return NetworkInterface.getByInetAddress(ip);
        } catch (UnknownHostException e) {
            return NetworkInterface.getByName(iface);
        }
    }

    /*
    private static void run (String multicastIp, int port, String networkInterfaceIp, boolean showHeartbeats)
            throws Exception
    {
        NetworkInterface iface = findNetworkInterface(networkInterfaceIp);
        InetAddress group = InetAddress.getByName(multicastIp);

        MulticastChannel dc = MulticastChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(port));
        MembershipKey key = dc.join(group, iface);
        if (!key.isValid())
        {
            System.err.println("Problem joining multicast group!");
            System.exit(1);
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_UDP_PACKET_SIZE);

        while (true)
        {
            byteBuffer.clear();
            InetSocketAddress sourceAddress = (InetSocketAddress) dc.receive(byteBuffer);
            byteBuffer.flip();

            // TODO: Parse message
        }
    }*/

    public static String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public static byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }

    public static void main(String[] args) throws Exception {
        String hex = "2800180002160000f8dd6ec3f0a8821500000000000000000000000000000000000000000000000001000000000000004103000000000000030028002e0000005f41ba45c9b29e04000000000000000010180000000000000c0000000000000000000000000000007a41ba45c9b29e0400000000000000000b18000000000000b20000000000000000000000000000007141ba45c9b29e0400000000000000000118000000000000a40000000000000000000000000000006241ba45c9b29e040000000000000000e817000000000000aa0000000000000000000000000000006741ba45c9b29e040000000000000000ac17000000000000240000000000000000000000000000007c41ba45c9b29e0400000000000000006b17000000000000a00000000000000000000000000000007041ba45c9b29e0400000000000000006117000000000000a00000000000000000000000000000006a41ba45c9b29e04000000000000000057170000000000008f0000000000000000000000000000006d41ba45c9b29e0400000000000000004d17000000000000590000000000000000000000000000006941ba45c9b29e0400000000000000003e170000000000008f0000000000000000000000000000001a41ba45c9b29e0400000000000000007f0d000000000000770100000000000000000000000000000e40ba45c9b29e0400000000000000007a0d000000000000500000000000000000000000000000007a40ba45c9b29e0400000000000000006b0d000000000000010000000000000000000000000000009640ba45c9b29e0400000000000000006b0d00000000000001000000000000000000000000000000aa40ba45c9b29e0400000000000000006b0d00000000000001000000000000000000000000000000c940ba45c9b29e0400000000000000006b0d00000000000001000000000000000000000000000000e140ba45c9b29e0400000000000000006b0d00000000000001000000000000000000000000000000ed40ba45c9b29e0400000000000000006b0d000000000000010000000000000000000000000000007940ba45c9b29e040000000000000000660d000000000000010000000000000000000000000000009740ba45c9b29e040000000000000000660d00000000000001000000000000000000000000000000a740ba45c9b29e040000000000000000660d00000000000001000000000000000000000000000000c640ba45c9b29e040000000000000000660d00000000000001000000000000000000000000000000e440ba45c9b29e040000000000000000660d00000000000001000000000000000000000000000000ee40ba45c9b29e040000000000000000660d00000000000001000000000000000000000000000000f340ba45c9b29e0400000000000000004d0d00000000000001000000000000000000000000000000f540ba45c9b29e0400000000000000004d0d000000000000010000000000000000000000000000007e41ba45c9b29e0400000000000000005019000000000000060000000000000001000000000000007241ba45c9b29e0400000000000000005f19000000000000560000000000000001000000000000006c41ba45c9b29e04000000000000000082190000000000005f0000000000000001000000000000007741ba45c9b29e0400000000000000009619000000000000b70000000000000001000000000000007841ba45c9b29e040000000000000000b419000000000000c10000000000000001000000000000007341ba45c9b29e040000000000000000be190000000000001d0000000000000001000000000000007b41ba45c9b29e040000000000000000c819000000000000720000000000000001000000000000006541ba45c9b29e040000000000000000dc190000000000003c0000000000000001000000000000007441ba45c9b29e040000000000000000e119000000000000a00000000000000001000000000000006441ba45c9b29e0400000000000000000e1a000000000000710000000000000001000000000000006641ba45c9b29e0400000000000000003b1a000000000000280000000000000001000000000000006e41ba45c9b29e0400000000000000005e1a000000000000a30000000000000001000000000000006341ba45c9b29e0400000000000000006d1a000000000000aa0000000000000001000000000000006841ba45c9b29e040000000000000000811a000000000000730000000000000001000000000000005d41ba45c9b29e0400000000000000009f1a000000000000220000000000000001000000000000006f41ba45c9b29e040000000000000000a91a0000000000004f0000000000000001000000000000007641ba45c9b29e040000000000000000bd1a0000000000004b0000000000000001000000000000005e41ba45c9b29e040000000000000000db1a000000000000350000000000000001000000000000005f40ba45c9b29e040000000000000000841c000000000000020000000000000001000000000000006340ba45c9b29e040000000000000000841c00000000000002000000000000000100000000000000";

        byte[] bytes = toByteArray(hex);

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        ByteBufferInputWrapper wrapper = new ByteBufferInputWrapper(buffer);

        SnapshotMessageHeader hdr = SnapshotMessageHeader.fromInput(wrapper);

        int delta = hdr.getHeaderLength() - SnapshotMessageHeader.EXPECTED_WIRE_SIZE;
        if (delta > 0)
        {
            wrapper.skipBytes(delta);
        }

        SnapshotSuccessResponse successResponse = SnapshotSuccessResponse.fromInput(wrapper);
        delta = hdr.getMessageLength() - SnapshotSuccessResponse.EXPECTED_WIRE_SIZE;
        if (delta > 0)
        {
            wrapper.skipBytes(delta);
        }


        List<AddOrder> orderList = new ArrayList<>();
        for (long i = 0; i < successResponse.getNumberOfOrders(); ++i)
        {
            AddOrder order = AddOrder.fromInput(wrapper);
            System.out.println(i + "\t==>\t" + order);
            orderList.add(order);
        }
    }

    public static void main2(String[] args) throws Exception {
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

//        run(cmd.getOptionValue("mcast"),
//                Integer.parseInt(cmd.getOptionValue("port")),
//                cmd.getOptionValue("iface"),
//                cmd.hasOption("heartbeats"));
    }
}
