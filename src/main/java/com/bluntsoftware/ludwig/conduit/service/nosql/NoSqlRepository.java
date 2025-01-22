package com.bluntsoftware.ludwig.conduit.service.nosql;

import com.mongodb.BasicDBObject;
import org.bson.Document;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class NoSqlRepository<T> implements IRepository {
    protected NoSqlConnection<T> connection;
    protected List<NoSqlListener<Document>> listeners = new ArrayList<>();

    public NoSqlRepository(NoSqlConnection<T> connection){
        this.connection = connection;
    }

    public T getClient(){
        return connection.getClient();
    }

    @Override
    public void addListener(NoSqlListener<Document> listener){
        listeners.add(listener);
    }
    @Override
    public List<NoSqlListener<Document>> getListeners() {
        return listeners;
    }

    @Override
    public Flux<Map<String,Object>> find(String filterByFields, String database, String collection) throws Exception{
        return Flux.fromStream(findAll(database,collection,filterByFields,"25").getRows().stream());
    }

    @Override
    public Document findOne(String filterByFields, String databaseName, String collectionName) {
        BasicDBObject query = new BasicDBObject();
        if(filterByFields != null && !filterByFields.equalsIgnoreCase("")){
            query = BasicDBObject.parse(filterByFields);
        }
        return findOne(query,databaseName,collectionName);
    }

    @Override
    public NoSqlResult findAll(String database, String collection) throws Exception {
        String filterByFields = "{}";
        String rows = "25";
        return this.findAll(database,collection,filterByFields,rows);
    }

    @Override
    public NoSqlResult findAll(String database, String collection, String filterByFields, String rows) throws Exception {
        return this.findAll(database,collection,filterByFields,null,null,null,rows,"1");
    }

    @Override
    public NoSqlResult findAll(String database, String collection, String filterByFields, String projection, String sidx, String sord, String rows, String page) throws Exception {
        // Mongo DB Syntax IE - The compound query below selects all records where the
        // `status` equals "A" and either age is less than 30 or type equals 1:
        // {status: "A", $or: [ { age: { $lt: 30 } }, { type: 1 } ]}
        BasicDBObject query = new BasicDBObject();
        if(filterByFields != null && !filterByFields.equalsIgnoreCase("")){
            query = BasicDBObject.parse(filterByFields);
        }

        BasicDBObject projectionQry = new BasicDBObject();
        if(projection != null && !projection.equalsIgnoreCase("")){
            projectionQry = BasicDBObject.parse(projection);
            if(projectionQry.isEmpty()){
                projectionQry = null;
            }
        }

        BasicDBObject sort = new BasicDBObject();
        if(sidx != null && sord != null){
            if(sord.equalsIgnoreCase("asc")){
                sort.put(sidx,1);
            }else{
                sort.put(sidx,-1);
            }
        }

        return findAll(database,collection,query,projectionQry,sort,rows,page);
    }

    @Override
    public List<String> columns(String databaseName, String collectionName) {
        Document document = this.findOne("{}",databaseName,collectionName);
        List<String> keys = new ArrayList<>();
        parse(document,keys);
        return keys;
    }

    private void parse(Document doc, List<String> keys){
        parse(doc,keys,"");
    }

    private void parse(Document doc, List<String> keys, String base){
        for(String key:doc.keySet()){
            String fullKey = base + key;
            keys.add(fullKey);
            Object object = doc.get(key);
            if (object instanceof Document) {
                parse((Document)object,keys,fullKey + ".");
            }
        }
    }
}
