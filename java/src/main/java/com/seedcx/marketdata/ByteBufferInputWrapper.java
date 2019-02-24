package com.seedcx.marketdata;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferInputWrapper
        implements Input
{
    private final @NotNull ByteBuffer buffer;

    public ByteBufferInputWrapper(@NotNull ByteBuffer buffer) {
        this.buffer = buffer;
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public int readUnsignedByte() {
        return Byte.toUnsignedInt(this.buffer.get());
    }

    @Override
    public int readUnsignedShort() {
        short value = this.buffer.getShort();
        return Short.toUnsignedInt(value);
    }

    @Override
    public long readUnsignedInt() {
        return Integer.toUnsignedLong(this.buffer.getInt());
    }

    @Override
    public long readLong() {
        return this.buffer.getLong();
    }

    @Override
    public String readAsciiText(int length) {
        return this.readAsciiText(length);
    }

    private byte[]
    readArray (int size) {
        byte[] bytes = new byte[size];
        this.buffer.get(bytes);
        return bytes;
    }

    @Override
    public OrderId readOrderId() {
        return new OrderId(readArray(OrderId.WIRE_SIZE));
    }

    @Override
    public ExecutionId readExecutionId() {
        return new ExecutionId(readArray(ExecutionId.WIRE_SIZE));
    }
}
