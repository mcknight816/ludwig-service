package com.bluntsoftware.ludwig.conduit.nosql.mongo;


import com.bluntsoftware.ludwig.conduit.nosql.IRepoManager;
import com.bluntsoftware.ludwig.conduit.nosql.NoSqlListener;
import com.bluntsoftware.ludwig.conduit.nosql.NoSqlRepository;
import com.bluntsoftware.ludwig.conduit.nosql.NoSqlResult;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by Alex Mcknight on 2/17/2017.
 */

public class MongoRepository extends NoSqlRepository<MongoClient> implements IRepoManager {
    private final Logger log = LoggerFactory.getLogger(MongoRepository.class);

    public MongoRepository( MongoConnection mongoConnection ) {
        super(mongoConnection);
    }
    private MongoDatabase getDatabase(String database){
        return connection.getClient().getDatabase(database);
    }

    @Override
    public Map deleteDatabase(String database){
         Map<String,String> ret = new HashMap<>();
        connection.getClient().dropDatabase(database);
        ret.put("database",database);
        ret.put("msg","deleted successfully");
        return ret;
    }

    @Override
    public Document findOne(Map<String,Object> query, String databaseName, String collectionName) {
        MongoCollection<Document> collection = getCreateCollection(databaseName,collectionName);
        return collection.find(toBson(query)).first();
    }

    public Mono<Map<String,Object>> findOneNow(Bson query, String databaseName, String collectionName) {
        MongoCollection<Document> collection = getCreateCollection(databaseName,collectionName);
        return Mono.just(collection.find(query).first());
    }

    public long count(Map<String,Object> query, String database, String collection){
        MongoCollection<Document> docList =  getCreateCollection(database,collection);
        if(query == null){
            return docList.count();
        }else{
            return docList.count(toBson(query));
        }
    }

    public FindIterable<Document> findAll(Map<String,Object> query, String database, String collection)  {
        MongoCollection<Document> docList =  getCreateCollection(database,collection);
        if(query == null){
            return docList.find();
        }else{
            return docList.find(toBson(query));
        }
    }
    Bson toBson(Map<String,Object> val){
        return val == null ? new BasicDBObject() :new BasicDBObject(val);
    }
    @Override
    public NoSqlResult findAll(String database, String collection, Map<String,Object> query, Map<String,Object> projection, Map<String,Object> sort, String rows, String page) throws Exception {
        MongoCollection<Document> docList =  getCreateCollection(database,collection);
        int limit = Integer.parseInt(rows);
        int currentPage = Integer.parseInt(page);
        int skip = (currentPage-1) * limit;
        FindIterable<Document> result = null;
         if(skip > 0){
             if(sort != null){
                 //this works but im not sure why we wouldn't do this all the time
                 docList.createIndex(toBson(sort));
                 result  = docList.find(toBson(query)).sort(toBson(sort)).skip(skip).limit(limit).projection(toBson(projection));
             }else{
                 result  = docList.find(toBson(query)).skip(skip).limit(limit).projection(toBson(projection));
             }

         }else{
             if(sort != null && sort.size() > 0){
                 result  = docList.find(toBson(query)).sort(toBson(sort)).limit(limit).projection(toBson(projection));
             }else{
                 result  = docList.find(toBson(query)).limit(limit).projection(toBson(projection));
             }

         }
         //projection

        long totalRecords = docList.count();
        Long totalPages = (totalRecords/limit) + 1;


        List<Document> resultList = new ArrayList<>();
       for(Document doc:result){
           resultList.add(doc);
       }


        NoSqlResult res = NoSqlResult.builder()
                .currpage(currentPage)
                .totalpages(totalPages)
                .totalrecords(totalRecords)
                .rows(resultList).build();

        for(NoSqlListener<Document> listener:listeners){
            listener.find(database,collection,res);
        }

        return res;
    }

    public DistinctIterable distinct(String databaseName, String collectionName, String field, Bson query){
        MongoCollection<Document> collection =  getCreateCollection(databaseName,collectionName);
        return collection.distinct(field,query,Object.class);
    }

    @Override
    public void removeCollection(String databaseName, String collectionName){
        connection.getClient().getDatabase(databaseName).getCollection(collectionName).drop();
    }

    @Override
    public  Map<String,Object> remove(String databaseName, String collectionName, String id){
        MongoCollection<Document> collection =  getCreateCollection(databaseName,collectionName);
        collection.deleteOne(eq("_id", id));
        Map<String,Object> ret = new HashMap<>();
        ret.put("status","success");
        ret.put("id",id);
        ret.put("action","remove");
        for(NoSqlListener listener:listeners){
            listener.remove(databaseName,collectionName,ret);
        }
        return ret;
    }

    private  void createCollection(String database, String collection){
        MongoDatabase db = connection.getClient().getDatabase(database);
        db.createCollection(collection);
    }

    public MongoCollection<Document> getCollection(String databaseName, String collectionName){
        if(databaseName != null && !databaseName.equalsIgnoreCase("")){
            MongoDatabase db = connection.getClient().getDatabase(databaseName);
            if(db != null && collectionName != null && !collectionName.equalsIgnoreCase("")){
                return db.getCollection(collectionName);
            }
        }
        return null;
    }

    public  MongoCollection<Document>  getCreateCollection(String database, String collection){
        MongoCollection<Document> ret = getCollection(database,collection);
        if(ret == null){createCollection(database,collection);}
        return getCollection(database,collection);
    }

    @Override
    public Document getById(String id, String databaseName, String collectionName){
        MongoCollection<Document> collection = getCreateCollection(databaseName,collectionName);
        return collection.find(eq("_id", id)).first();
    }

    @Override
    public Document save(String databaseName, String collectionName, Map<String, Object> data, boolean replace){
        MongoCollection<Document> collection = getCreateCollection(databaseName,collectionName);
        Document document = null;
        if(data.containsKey("_id") && data.get("_id") != null && !data.get("_id").equals("") ){
            document = this.getById(data.get("_id").toString(),databaseName,collectionName);
            if(document != null ){
                if(replace){
                    document.putAll(data);
                    collection.replaceOne(eq("_id", data.get("_id").toString()),document);
                }
            }else{
                document = new Document();
                document.putAll(data);
                collection.insertOne(document);
            }
        }else{
            document = new Document();
            data.put("_id", UUID.randomUUID().toString());
            document.putAll(data);
            collection.insertOne(document);
        }

        for(NoSqlListener listener:listeners){
            listener.save(databaseName,collectionName,document);
        }
        return document;
    }

    @Override
    public List<Map> listDatabases() {
        MongoClient client =  connection.getClient();
        List<Map> ret = new ArrayList<>();
        try{
            for(String dbname:client.listDatabaseNames()){
                Map dbName = new HashMap();
                dbName.put("name",dbname);
                ret.add(dbName);
            }
        }catch(Exception e){
            Map dbName = new HashMap();
            dbName.put("name",this.connection.getDatabaseName());
            ret.add(dbName);
        }
        return ret;
    }

    @Override
    public List<Document> backupCollection(String database, String collection){
        MongoCollection<Document> docList =  getCreateCollection(database,collection);
        FindIterable<Document> result  = docList.find();
        List<Document> resultList = new ArrayList<>();
        for(Document doc:result){
            resultList.add(doc);
        }
        return resultList;
    }

    @Override
    public void restoreCollection(String database, String collection, List data){
         for(Object doc:data){
             save(database,collection,(Map<String,Object>)doc,false);
         }
    }

    public String test(){
        String ret = "A Mongo Database is required. Download Here\n https://www.mongodb.com/download-center?jmp=nav#community";
        try {
            ret = connection.getClient().getConnectPoint();
            System.out.println("Mongo connection " + ret );
            log.info("Mongo connection " + ret);
        }catch (Exception e){
            System.out.println(ret);
            log.error(ret );
        }
        return ret;
    }

    @Override
    public boolean dbExists(String appName) {
        MongoIterable<String> dbNames =  connection.getClient().listDatabaseNames();
        for(String dbName:dbNames){
            if(dbName.equalsIgnoreCase(appName)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
      /*  try{
            MongoRepository mongoService = new MongoRepository();

            MongoCollection<Document> collection = mongoService.getCreateCollection("TestDb","testCol");
            Document doc = new Document("name", "MongoDB")
                    .append("_id",UUID.randomUUID().toString())
                    .append("type", "database")
                    .append("count", 1)
                    .append("info", new Document("x", 203).append("y", 102));

            collection.insertOne(doc);
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document document) {
                    System.out.println(document.toJson());
                }
            };
            collection.find().forEach(printBlock);
        }catch(Exception e){
            System.out.print(e.getMessage());
        }*/
    }

    @Override
    public void restoreCollections(String databaseName, Map data){
        Object collectionsObj = data.get("collections");
        if(collectionsObj != null && collectionsObj instanceof Map){
            Map collections = (Map)collectionsObj;
            for(Object collectionName:collections.keySet()){
                restoreCollection(databaseName,collectionName.toString(),(List)collections.get(collectionName));
            }
        }
    }

    @Override
    public void restoreDatabase(Map data){
        Object dbNameObj = data.get("name");
        if(dbNameObj != null){
            String databaseName = dbNameObj.toString();
            restoreCollections(databaseName,data);
        }
    }

    @Override
    public Map<String,Object> backupDatabase(String databaseName){
        Map<String,Object> ret = new HashMap<>();
        Map<String,Object> collections = new HashMap<>();
        for(String collectionName:getDatabase(databaseName).listCollectionNames()){
            collections.put(collectionName,backupCollection(databaseName,collectionName));
        }
        ret.put("name",databaseName);
        ret.put("collections",collections);
        return ret;
    }

    @Override
    public Map<String,String> deleteCollection(String databaseName, String collectionName) {
        Map<String,String> ret = new HashMap<>();
        getDatabase(databaseName).getCollection(collectionName).drop();
        ret.put("collection",collectionName);
        ret.put("msg","deleted successfully");
        return ret;
    }

    public String createIndex(BasicDBObject index, String databaseName, String collectionName) {
        return getDatabase(databaseName).getCollection(collectionName).createIndex(index);
    }
}
