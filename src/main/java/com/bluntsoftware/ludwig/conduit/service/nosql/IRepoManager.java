package com.bluntsoftware.ludwig.conduit.service.nosql;

import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface IRepoManager {
    void removeCollection(String databaseName, String collectionName);
    List<Document> backupCollection(String database, String collection);
    void restoreCollection(String database, String collection, List data);
    boolean dbExists(String appName);
    void restoreCollections(String databaseName, Map data);
    void restoreDatabase(Map data);
    Map deleteDatabase(String database);
    Map<String,Object> backupDatabase(String databaseName);
    Map<String,String> deleteCollection(String databaseName, String collectionName);
    List<Map> listDatabases();
}
