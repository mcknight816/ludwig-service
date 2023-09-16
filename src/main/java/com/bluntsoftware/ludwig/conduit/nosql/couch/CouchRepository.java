package com.bluntsoftware.ludwig.conduit.nosql.couch;


import com.bluntsoftware.ludwig.conduit.nosql.NoSqlConnection;
import com.bluntsoftware.ludwig.conduit.nosql.NoSqlListener;
import com.bluntsoftware.ludwig.conduit.nosql.NoSqlRepository;
import com.bluntsoftware.ludwig.conduit.nosql.NoSqlResult;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.query.QueryResult;
import org.bson.Document;

import java.util.*;


public class CouchRepository extends NoSqlRepository<Cluster> {

    public CouchRepository(NoSqlConnection<Cluster> connection) {
        super(connection);
    }

    /* Implementation */

    public static void main(String[] args)  {
        CouchConnection connection = new CouchConnection("localhost",11210,"admin","4465$tud1O");
        CouchRepository repo = new CouchRepository(connection);
        try {
            System.out.println(repo.findAll("travel-sample",""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Document findOne(Map<String,Object> query, String databaseName, String collectionName) {
        try {
            return this.findAll(databaseName,collectionName,query,null,null,"1","1").getRows().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public NoSqlResult findAll(String databaseName, String collectionName, Map<String,Object> query, Map<String,Object> projection, Map<String,Object> sort, String rows, String page) throws Exception {

        String n1ql = "select * from `" + databaseName + "`";

        if(query != null && query.size() > 0){
            n1ql += " where " + jsonToSql(query);
        }

        n1ql += " limit " + rows;
        this.connection.getClient().buckets().getBucket(databaseName);
        QueryResult result = this.connection.getClient().query(n1ql);
        List<Document> rowList = result.rowsAs(Document.class);
        List<Document> ret = new ArrayList<>();
        for(Document doc:rowList){
            ret.add(new Document((Map<String, Object>)doc.get(databaseName)));
        }
        return NoSqlResult.builder().rows(ret).build();
    }

    @Override
    public Map<String, Object> remove(String databaseName, String collectionName, String id) {
        Collection collection = getCreateCollection(databaseName,collectionName);
        Document doc = getById(id,databaseName,collectionName);
        if(doc != null ){
            collection.remove(id);
        }
        return doc;
    }

    @Override
    public Document getById(String id, String databaseName, String collectionName) {
        Collection collection = getCreateCollection(databaseName,collectionName);
        GetResult result = collection.get(id);
        if(result != null){
            return result.contentAs(Document.class);
        }
        return null;
    }

    @Override
    public Document save(String databaseName, String collectionName, Map<String, Object> data, boolean replace) {
        Collection collection = getCreateCollection(databaseName,collectionName);
        Document document = null;
        if(isValid(data,"_id") ){
            String id = data.get("_id").toString();
            if(collection.exists(id).exists()){
                document = this.getById(id,databaseName,collectionName);
                if(replace){
                    document.putAll(data);
                    collection.replace(id,document);
                }
            }else{
                document = new Document();
                document.putAll(data);
                collection.insert(id,document);
            }
        }else{
            document = new Document();
            String id = UUID.randomUUID().toString();
            data.put("_id",id);
            document.putAll(data);
            collection.insert(id,document);
        }

        for(NoSqlListener<Document> listener:listeners){
            listener.save(databaseName,collectionName,document);
        }
        return document;
    }

    /* Helper Functions */

    boolean isValid(Map<String, Object> data, String key){
        return data.containsKey(key) && data.get(key) != null && !data.get(key).equals("");
    }

    public Collection getCreateCollection(String databaseName, String collectionName) {
        Cluster cluster = this.connection.getClient();
        return cluster.bucket(databaseName).defaultCollection();
    }

    public List<Map> listDatabases() {
        List<Map> databases = new ArrayList<>();
        Cluster cluster = this.connection.getClient();
        BucketManager bucketManager = cluster.buckets();
        Map<String, BucketSettings> map = bucketManager.getAllBuckets();
        for(String key :map.keySet()){
            BucketSettings settings = map.get(key);
            Map<String,Object> data = new HashMap<>();
            data.put("name",key);
            databases.add(data);
        }
        return databases;
    }

    private String jsonToSql( Map<String,Object> query){
        StringBuilder whereClause = new StringBuilder();
        for(String key : query.keySet()){
            if(!whereClause.toString().equalsIgnoreCase("")){
                whereClause.append(" and ");
            }
            whereClause.append(key).append(" = ").append("'").append(query.get(key).toString()).append("'");
        }
        return whereClause.toString();
    }
}
