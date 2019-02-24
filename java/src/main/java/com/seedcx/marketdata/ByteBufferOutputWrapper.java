package com.seedcx.marketdata;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferOutputWrapper
    implements Output
{
    private final @NotNull ByteBuffer buffer;

    public ByteBufferOutputWrapper(@NotNull ByteBuffer buffer) {
        this.buffer = buffer;
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public void writeUnsignedByte(int value) {
        this.buffer.put((byte)value);
    }

    @Override
    public void writeUnsignedShort(int value) {
        this.buffer.putShort((short)value);
    }

    @Override
    public void writeUnsignedInt(long value) {
        this.buffer.putInt((int)value);
    }

    @Override
    public void writeLong(long value) {
        this.buffer.putLong(value);
    }

    @Override
    public void writeBytes(@NotNull byte[] bytes, int offset, int length) {
        this.buffer.put(bytes, offset, length);
    }
}
