package com.bluntsoftware.ludwig.conduit.nosql.couch;


import com.bluntsoftware.ludwig.conduit.nosql.NoSqlConnection;
import com.couchbase.client.java.Cluster;
import lombok.Data;

@Data
public class CouchConnection implements NoSqlConnection<Cluster> {
    Cluster client;
    private String databaseName;
    private String host;
    private String uri;
    private int port;
    private String username;
    private String password;

    public CouchConnection(String databaseName,Cluster client) {
        this.databaseName = databaseName;
        this.client = client;
    }

    public CouchConnection(String host, int port) {
        this.host = host;
        this.port = port;
        this.client = Cluster.connect(host + ":" + port,"Administrator","");
    }

    public CouchConnection(String host, int port,String username, String password) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.client = Cluster.connect(host + ":" + port, username, password);
    }

    public CouchConnection(String uri,String username, String password) {
        this.username = username;
        this.password = password;
        this.uri =  uri;
        this.client = Cluster.connect(uri, username, password);
    }

}
