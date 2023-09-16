package com.bluntsoftware.ludwig.conduit.nosql;

import org.bson.Document;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public interface IRepository {

    void addListener(NoSqlListener<Document> listener);
    List<NoSqlListener<Document>> getListeners();

    Document findOne(String filterByFields, String databaseName, String collectionName);
    Document findOne(Map<String,Object> query, String databaseName, String collectionName);

    Flux<Map<String,Object>> find(String filterByFields, String database, String collection) throws Exception;

    NoSqlResult findAll(String database, String collection) throws Exception;
    NoSqlResult findAll(String database, String collection, String filterByFields, String rows) throws Exception;
    NoSqlResult findAll(String database, String collection, String filterByFields, String projection, String sidx, String sord, String rows, String page) throws Exception;
    NoSqlResult findAll(String database, String collection, Map<String,Object> query, Map<String,Object> projection, Map<String,Object> sort, String rows, String page) throws Exception;

    Map<String,Object> remove(String databaseName, String collectionName, String id);
    Document getById(String id, String databaseName, String collectionName);
    Document save(String databaseName, String collectionName, Map<String, Object> data, boolean replace);
    List<String> columns(String databaseName, String collectionName);

}
