package com.seedcx.marketdata.sniffer;

import com.seedcx.marketdata.ByteBufferInputWrapper;
import com.seedcx.marketdata.IncrementalUpdateDecoder;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;

public class Sniffer
{
    private static final int MAX_UDP_PACKET_SIZE = 65535 - 8 - 20;

    private final NetworkInterface iface;
    private final InetAddress group;
    private final int port;
    private final IncrementalUpdateDecoder.Listener listener;

    public Sniffer(String multicastIp, int port, String iface, boolean printHeartbeats)
            throws IOException {
        this.group = InetAddress.getByName(multicastIp);
        this.port = port;
        this.iface = findNetworkInterface(iface);
        this.listener = new PrintingListener(printHeartbeats);
    }

    public void run()
            throws Exception {
        DatagramChannel dc = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(new InetSocketAddress(this.port));
        MembershipKey key = dc.join(this.group, this.iface);
        if (!key.isValid()) {
            throw new Exception("Problem joining multicast group!");
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(MAX_UDP_PACKET_SIZE);
        IncrementalUpdateDecoder decoder = new IncrementalUpdateDecoder(this.listener);
        ByteBufferInputWrapper input = new ByteBufferInputWrapper(byteBuffer);

        while (true) {
            byteBuffer.clear();
            InetSocketAddress sourceAddress = (InetSocketAddress) dc.receive(byteBuffer);
            byteBuffer.flip();

            decoder.decodePacket(input);
        }
    }

    private static NetworkInterface
    findNetworkInterface(String iface)
            throws IOException {
        if (iface == null || iface.isEmpty()) {
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
}
