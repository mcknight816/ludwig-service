package com.bluntsoftware.ludwig.conduit.service.nosql;

public interface NoSqlConnection<T> {
    public abstract T getClient();
    public String getDatabaseName();
}
