package com.seedcx.marketdata.snapshot;

import com.seedcx.marketdata.ByteBufferOutputWrapper;
import com.seedcx.marketdata.DelegatingSnapshotResponseDecoder;
import com.seedcx.marketdata.Output;
import com.seedcx.marketdata.enums.MessageType;
import com.seedcx.marketdata.msg.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

public class Snapshot
{

    private final static int PROTOCOL_VERSION = 2;

    private String host;
    private int port;
    private String compId;
    private List<Long> instrumentIds;


    private void
    sendSnapshotRequest (
            @NotNull SocketChannel channel,
            @NotNull Long instrumentId)
        throws IOException
    {
        byte[] bytes = new byte[SnapshotRequest.EXPECTED_WIRE_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Output output = new ByteBufferOutputWrapper(buffer);

        SnapshotRequest req = SnapshotRequest
                .newBuilder()
                .setProtocolVersion(PROTOCOL_VERSION)
                .setRequestMsgType(MessageType.SNAPSHOT_REQUEST.value())
                .setLength(SnapshotRequest.EXPECTED_WIRE_SIZE)
                .setSenderCompId(this.compId)
                .setInstrumentId(instrumentId)
                .build();

        req.writeTo(output);
        buffer.flip();

        channel.write(buffer);
    }

    private void run ()
            throws Exception
    {
        SocketAddress address = new InetSocketAddress(this.host, this.port);
        SocketChannel channel = SocketChannel.open(address);


        // Let's send requests first
        for (Long instrumentId : this.instrumentIds)
        {
            this.sendSnapshotRequest(channel, instrumentId);
        }

        // Now let's collect the responses
        ResponsePrinter printer = new ResponsePrinter(System.out);
        DelegatingSnapshotResponseDecoder decoder = new DelegatingSnapshotResponseDecoder(printer);

        for (Long instrumentId : this.instrumentIds)
        {
            //decoder.
        }

    }


    public static void main(String[] args) throws Exception {


        System.out.println("Bye!");
    }


}

class ResponsePrinter implements DelegatingSnapshotResponseDecoder.Listener
{
    private final @NotNull PrintStream printStream;

    ResponsePrinter (@NotNull PrintStream printStream)
    {
        this.printStream = printStream;
    }

    private String getTimeStamp ()
    {
        return ""; // TODO: Implement me!
    }

    private void print(Object msg)
    {
        this.printStream.println(String.format("%s: %s",
                this.getTimeStamp(), msg.toString()));
    }

    @Override
    public void onMessageHeader(@NotNull SnapshotMessageHeader messageHeader)
    {
        // Do nothing
    }

    @Override
    public void onUnknownMessage(@NotNull SnapshotMessageHeader messageHeader)
    {
        this.print("Unknown response. Type: " + messageHeader.getMsgType());
    }

    @Override
    public void onSnapshotFailedResponse(
            @NotNull SnapshotMessageHeader messageHeader,
            @NotNull SnapshotFailedResponse msg)
    {
        this.print(msg);
    }

    @Override
    public void onSnapshotSuccessResponse(
            @NotNull SnapshotMessageHeader messageHeader,
            @NotNull SnapshotSuccessResponse msg)
    {
        this.print(msg);
    }

    @Override
    public void onOrder(
            long orderNumber,
            long totalOrderCount,
            @NotNull SnapshotMessageHeader messageHeader,
            @NotNull SnapshotSuccessResponse response,
            @NotNull AddOrder order)
    {
        this.printStream.println(String.format("\t%d : %s", orderNumber, order));
        if (orderNumber == totalOrderCount - 1)
        {
            this.printStream.println();
        }
    }
}
