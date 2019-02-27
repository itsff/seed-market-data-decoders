package com.seedcx.marketdata;

import org.jetbrains.annotations.NotNull;

public class ExecutionId
        extends ExchangeAssignedId
{
    public ExecutionId(@NotNull byte[] bytes) {
        super(bytes);
    }
}