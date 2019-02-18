package com.seedcx.marketdata;

public class MessageTooShortException
    extends RuntimeException
{
    public MessageTooShortException() {
    }

    public MessageTooShortException(String message) {
        super(message);
    }
}
