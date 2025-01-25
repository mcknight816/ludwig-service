package com.bluntsoftware.ludwig.conduit.config.sql.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SQLConnection implements EntitySchema {

    String databaseType = "Postgres";
    String server = "localhost";
    String port = "5432";
    String userName;
    String password;
    public JsonSchema getSchema(){
        JsonSchema connection = new JsonSchema("connection");
        List<String> db = new ArrayList<String>();
        db.add("Postgres");
        db.add("SQL Server");
        db.add("Oracle");
        db.add("My Sql");

        connection.addEnum("database_type",db,"Postgres");
        connection.addString("server","localhost",null);
        connection.addString("port","5432",null);
        connection.addString("user","",null);
        connection.addString("password","", PropertyFormat.PASSWORD);
        return connection;
    }
}
