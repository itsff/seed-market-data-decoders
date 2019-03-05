package com.seedcx.marketdata.snapshot;

import com.seedcx.marketdata.SnapshotResponseDecoder;
import com.seedcx.marketdata.msg.AddOrder;
import com.seedcx.marketdata.msg.SnapshotFailedResponse;
import com.seedcx.marketdata.msg.SnapshotMessageHeader;
import com.seedcx.marketdata.msg.SnapshotSuccessResponse;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class ResponsePrinter
        implements SnapshotResponseDecoder.Listener
{
    private final @NotNull PrintStream printStream;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    ResponsePrinter (@NotNull PrintStream printStream)
    {
        this.printStream = printStream;
    }

    private String getTimeStamp ()
    {
        return LocalDateTime.now().format(this.dateTimeFormatter);
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
