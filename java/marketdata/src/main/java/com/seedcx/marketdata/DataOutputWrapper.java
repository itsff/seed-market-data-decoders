package com.seedcx.marketdata;

import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.BufferOverflowException;

public class DataOutputWrapper
    implements Output
{
    private final @NotNull DataOutput out;

    public DataOutputWrapper(@NotNull DataOutput out) {
        this.out = out;
    }

    @Override
    public void writeUnsignedByte(int value) {
        try {
            this.out.writeByte(value);
        } catch (IOException e) {
            throw new BufferOverflowException();
        }
    }

    @Override
    public void writeUnsignedShort(int value) {
        try {
            this.out.writeShort(value);
        } catch (IOException e) {
            throw new BufferOverflowException();
        }
    }

    @Override
    public void writeUnsignedInt(long value) {
        try {
            this.out.writeInt((int)value);
        } catch (IOException e) {
            throw new BufferOverflowException();
        }
    }

    @Override
    public void writeLong(long value) {
        try {
            this.out.writeLong(value);
        } catch (IOException e) {
            throw new BufferOverflowException();
        }
    }

    @Override
    public void writeBytes(@NotNull byte[] bytes, int offset, int length) {
        try {
            this.out.write(bytes, offset, length);
        } catch (IOException e) {
            throw new BufferOverflowException();
        }
    }
}
