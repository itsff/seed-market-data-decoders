package com.seedcx.marketdata;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.charset.Charset;

public class DataInputWrapper
    implements Input
{
    private @NotNull java.io.DataInput input;

    public DataInputWrapper(@NotNull java.io.DataInput input)
    {
        this.input = input;
    }

    private byte[]
    readArray (int size) {
        byte[] bytes = new byte[size];
        try {
            this.input.readFully(bytes);
        } catch (IOException e) {
            throw new BufferUnderflowException();
        }
        return bytes;
    }

    @Override
    public OrderId readOrderId () {
        return new OrderId(readArray(OrderId.WIRE_SIZE));
    }

    @Override
    public boolean readBoolean () {
        int b = this.readUnsignedByte();
        return (b != 0);
    }

    @Override
    public ExecutionId readExecutionId () {
        return new ExecutionId(readArray(ExecutionId.WIRE_SIZE));
    }

    @Override
    public int skipBytes(int n) {
        try {
            return this.input.skipBytes(n);
        } catch (IOException e) {
            throw new BufferUnderflowException();
        }
    }

    @Override
    public int readUnsignedShort() {
        try {
            return this.input.readUnsignedShort();
        } catch (IOException e) {
            throw new BufferUnderflowException();
        }
    }

    @Override
    public long readUnsignedInt() {
        try {
            return Integer.toUnsignedLong(this.input.readInt());
        } catch (IOException e) {
            throw new BufferUnderflowException();
        }
    }

    @Override
    public long readLong() {
        try {
            return this.input.readLong();
        } catch (IOException e) {
            throw new BufferUnderflowException();
        }
    }

    @Override
    public String readAsciiText(int length) {
        byte[] bytes = this.readArray(length);
        return new String(bytes, Charset.forName("US-ASCII"));
    }

    @Override
    public int readUnsignedByte() {
        try {
            return this.input.readUnsignedByte();
        } catch (IOException e) {
            throw new BufferUnderflowException();
        }
    }

}
