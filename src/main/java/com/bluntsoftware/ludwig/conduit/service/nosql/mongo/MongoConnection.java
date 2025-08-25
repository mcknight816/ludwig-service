package com.bluntsoftware.ludwig.conduit.service.nosql.mongo;


import com.bluntsoftware.ludwig.conduit.service.nosql.NoSqlConnection;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MongoConnection implements NoSqlConnection<MongoClient> {
    private MongoClient client;
    private String mongoDbName;
    private String host;
    private String uri;
    private int port;

    public MongoConnection(String mongoDbName, MongoClient client) {
        this.mongoDbName = mongoDbName;
        this.client = client;
    }

    public MongoConnection(String host, int port) {
        this.host = host;
        this.port = port;
        String connectionString = "mongodb://" + this.host + ":" + this.port;
        this.client = MongoClients.create(connectionString);

    }

    public MongoConnection(String uri) {
        this.uri = uri;
        // Create client directly from the connection string (supports SRV, auth, options, etc.)
        this.client = MongoClients.create(this.uri);

    }

    @Override
    public String getDatabaseName() {
        return mongoDbName;
    }
}
