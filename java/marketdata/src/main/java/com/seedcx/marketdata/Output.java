package com.seedcx.marketdata;

import com.seedcx.marketdata.enums.Side;
import com.seedcx.marketdata.enums.SnapshotFailReason;
import com.seedcx.marketdata.enums.TradingStatus;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;


public interface Output
{
    void writeUnsignedByte(int value);
    void writeUnsignedShort(int value);
    void writeUnsignedInt(long value);
    void writeLong(long value);

    void writeBytes(@NotNull byte[] bytes, int offset, int length);

    default void writeBytes(@NotNull byte[] bytes)
    {
        this.writeBytes(bytes, 0, bytes.length);
    }

    default void writeAsciiText(@NotNull String value, int length)
    {
        byte[] asciiBytes;
        try {
            asciiBytes = value.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("String could not be converted to ASCII", e);
        }

        // Write ASCII bytes
        int asciiBytesLength = Math.min(asciiBytes.length, length);
        this.writeBytes(asciiBytes, 0, asciiBytesLength);

        // Write padding
        this.writePadding(length - asciiBytesLength);
    }

    default void writePadding(int length)
    {
        while (length --> 0)
        {
            this.writeUnsignedByte(0);
        }
    }

    default void writeOrderId(@NotNull OrderId value)
    {
        this.writeBytes(value.getBytes());
    }

    default void writeExecutionId(@NotNull ExecutionId value)
    {
        this.writeBytes(value.getBytes());
    }

    default void writeBoolean(boolean value)
    {
        if (value)
        {
            this.writeUnsignedByte(1);
        }
        else
        {
            this.writeUnsignedByte(0);
        }
    }

    default void writeInstrumentId(long value)
    {
        this.writeLong(value);
    }

    default void writeSequenceNumber(long value)
    {
        this.writeLong(value);
    }

    default void writeTimeStamp(long value)
    {
        this.writeLong(value);
    }

    default void writePrice(long value)
    {
        this.writeLong(value);
    }

    default void writeOrderSize(long value)
    {
        this.writeLong(value);
    }

    default void writeSide(@NotNull Side enumeration) {
        this.writeUnsignedByte(enumeration.value());
    }

    default void writeTradingStatus(@NotNull TradingStatus enumeration) {
        this.writeUnsignedByte(enumeration.value());
    }

    default void writeSnapshotFailReason(@NotNull SnapshotFailReason enumeration) {
        this.writeUnsignedByte(enumeration.value());
    }

}
