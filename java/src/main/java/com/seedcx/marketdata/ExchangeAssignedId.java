package com.seedcx.marketdata;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ExchangeAssignedId
{
    public static final int WIRE_SIZE = 16;

    private final @NotNull byte[] bytes;

    protected ExchangeAssignedId(@NotNull byte[] bytes) {
        if (bytes == null || bytes.length != 16)
        {
            throw new IllegalArgumentException("Bytes must not be null and must contain be 16 bytes.");
        }
        this.bytes = bytes;

    }

    @Override public
    @NotNull String
    toString() {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(this.bytes);
        final long hi = byteBuffer.getLong();

        if (hi == 0)
        {
            final long lo = byteBuffer.getLong();
            return Long.toUnsignedString(lo);
        }
        else
        {
            return Base58.encode(this.bytes);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExchangeAssignedId that = (ExchangeAssignedId) o;

        return Arrays.equals(bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    public
    @NotNull byte[]
    getBytes() {
        return this.bytes;
    }
}