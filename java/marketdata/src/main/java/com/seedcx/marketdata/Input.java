package com.seedcx.marketdata;

import com.seedcx.marketdata.enums.Side;
import com.seedcx.marketdata.enums.SnapshotFailReason;
import com.seedcx.marketdata.enums.TradingStatus;


public interface Input
{
    int readUnsignedByte();
    int readUnsignedShort();
    long readUnsignedInt();
    long readLong();

    String readAsciiText (int length);

    OrderId readOrderId ();
    ExecutionId readExecutionId ();

    default long skipBytes(long n)
    {
        while (n --> 0)
        {
            this.readUnsignedByte();
        }
        return n;
    }

    default boolean readBoolean ()
    {
        int b = this.readUnsignedByte();
        return (b != 0);
    }

    default long readInstrumentId ()
    {
        return this.readLong();
    }

    default long readSequenceNumber ()
    {
        return this.readLong();
    }

    default long readTimeStamp ()
    {
        return this.readLong();
    }

    default long readPrice ()
    {
        return this.readLong();
    }

    default long readOrderSize ()
    {
        return this.readLong();
    }

    default Side readSide () {
        int b = this.readUnsignedByte();
        return Side.fromValue(b);
    }

    default TradingStatus readTradingStatus () {
        int b = this.readUnsignedByte();
        return TradingStatus.fromValue(b);
    }

    default SnapshotFailReason readSnapshotFailReason () {
        int b = this.readUnsignedByte();
        return SnapshotFailReason.fromValue(b);
    }

}
