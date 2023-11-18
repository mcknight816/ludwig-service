package com.bluntsoftware.ludwig.conduit.config.sql;


import com.bluntsoftware.ludwig.conduit.impl.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class SQLConnectionConfig extends ActivityConfigImpl {

    @Override
    public JsonSchema getRecord() {
        JsonSchema connection = new JsonSchema("connection");
        List<String> db = new ArrayList<String>();
        db.add("Postgres");
        db.add("SQL Server");
        db.add("Oracle");
        db.add("My Sql");

        connection.addEnum("database_type",db,"Postgres");
        connection.addString("server","localhost",null);
        connection.addString("port","5432",null);
        connection.addString("user","postgres",null);
        connection.addString("password","stud1o","password");
        return connection;
    }

    @Override
    public Map test() {
        return null;
    }
}
