package com.seedcx.marketdata.snapshot;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;
import com.seedcx.marketdata.*;
import com.seedcx.marketdata.enums.MessageType;
import com.seedcx.marketdata.msg.SnapshotRequest;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

public class Snapshot
{
    private final static int PROTOCOL_VERSION = 2;

    private final String host;
    private int port;
    private String compId;
    private List<Long> instrumentIds;

    public Snapshot(String host, int port, String compId, List<Long> instrumentIds) {
        this.host = host;
        this.port = port;
        this.compId = compId;
        this.instrumentIds = instrumentIds;
    }


    private void
    sendSnapshotRequest (
            @NotNull DataOutput outputStream,
            @NotNull Long instrumentId)
    {
        Output output = new DataOutputWrapper(outputStream);

        SnapshotRequest req = SnapshotRequest
                .newBuilder()
                .setProtocolVersion(PROTOCOL_VERSION)
                .setRequestMsgType(MessageType.SNAPSHOT_REQUEST.value())
                .setLength(SnapshotRequest.EXPECTED_WIRE_SIZE)
                .setSenderCompId(this.compId)
                .setInstrumentId(instrumentId)
                .build();

        req.writeTo(output);
    }

    void run () throws IOException {
        SocketAddress address = new InetSocketAddress(this.host, this.port);
        Socket socket = new Socket();
        socket.connect(address);

        final DataOutput outputStream = new LittleEndianDataOutputStream(socket.getOutputStream());
        final DataInput inputStream = new LittleEndianDataInputStream(socket.getInputStream());
        final Input input = new DataInputWrapper(inputStream);

        // Let's send requests first
        for (Long instrumentId : this.instrumentIds)
        {
            this.sendSnapshotRequest(outputStream, instrumentId);
        }

        // Now let's collect the responses
        ResponsePrinter printer = new ResponsePrinter(System.out);
        SnapshotResponseDecoder decoder = new SnapshotResponseDecoder(printer);

        for (Long instrumentId : this.instrumentIds)
        {
            decoder.decode(input);
        }
    }
}

