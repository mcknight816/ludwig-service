package com.bluntsoftware.ludwig.conduit.nosql.mongo;


import com.bluntsoftware.ludwig.conduit.nosql.NoSqlConnection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
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
        this.client = new MongoClient(this.host, this.port);
    }

    public MongoConnection(String uri) {
        this.uri =  uri;
        MongoClientURI mongoClientURI = new MongoClientURI(this.uri);
        this.client = new MongoClient(mongoClientURI);
    }

    @Override
    public String getDatabaseName() {
        return mongoDbName;
    }
}
