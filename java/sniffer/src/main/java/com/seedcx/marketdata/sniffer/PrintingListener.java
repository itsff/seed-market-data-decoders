package com.seedcx.marketdata.sniffer;

import com.seedcx.marketdata.IncrementalUpdateDecoder;
import com.seedcx.marketdata.msg.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintingListener
        implements IncrementalUpdateDecoder.Listener
{
    private final boolean printHeartbeats;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public PrintingListener(boolean printHeartbeats) {
        this.printHeartbeats = printHeartbeats;
    }

    private void log(long instrumentId,
                     long sequenceNumber,
                     @NotNull PacketHeader packetHeader,
                     @NotNull Object msg) {
        String sb = LocalDateTime.now().format(this.dateTimeFormatter) +
                "|seq:" + sequenceNumber +
                "|sent:" + packetHeader.getSendingTime() +
                "|inst:" + instrumentId +
                "|" + msg;
        System.out.println(sb);
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
