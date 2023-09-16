package com.bluntsoftware.ludwig.conduit.nosql;

public interface NoSqlConnection<T> {
    public abstract T getClient();
    public String getDatabaseName();
}
