package com.bluntsoftware.ludwig.conduit.service.nosql;

/**
 * Created by Alex Mcknight on 2/17/2017.
 */
public interface NoSqlListener<T> {
    void save(String database,String collection,T data);
    void find(String database, String collection, NoSqlResult data);
    void get(String database,String collection,T data);
    void remove(String database,String collection,T data);
}
