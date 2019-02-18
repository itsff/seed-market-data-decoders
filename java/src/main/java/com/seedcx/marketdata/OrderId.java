package com.seedcx.marketdata;

import org.jetbrains.annotations.NotNull;

public class OrderId
    extends ExchangeAssignedId
{

    public OrderId(@NotNull byte[] bytes) {
        super(bytes);
    }
}