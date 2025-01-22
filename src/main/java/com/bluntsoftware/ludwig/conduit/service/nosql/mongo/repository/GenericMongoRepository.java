package com.bluntsoftware.ludwig.conduit.service.nosql.mongo.repository;


import com.bluntsoftware.ludwig.conduit.service.nosql.NoSqlResult;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.MongoRepository;
import com.bluntsoftware.ludwig.conduit.service.nosql.mongo.domain.Domain;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import org.bson.Document;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 8/5/2017.
 */
public class GenericMongoRepository<T extends Domain> {

    private Class<T> domainClass;
    private String collection;
    private MongoRepository mongoRepository;
    private String databaseName;
    private List<Class> subClasses = new ArrayList<>();

    protected void registerSubtypes(Class subType){
        subClasses.add(subType);
    }

    public GenericMongoRepository(Class<T> domainClass, String collection, String databaseName, MongoRepository mongoRepository) {
        this.domainClass = domainClass;
        this.collection = collection;
        this.mongoRepository = mongoRepository;
        this.databaseName = databaseName;
    }

    public T toObject(Document doc){
        T ret = null;
        try{
           if(doc != null){
               doc.put("@class",domainClass.getTypeName());
               ObjectMapper objectMapper = new ObjectMapper();
               objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
               for(Class subType:subClasses){
                   objectMapper.registerSubtypes(subType);
               }
               ret = objectMapper.convertValue(doc, domainClass);
           }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    public String createIndex(BasicDBObject index){
        return this.mongoRepository.createIndex(index,this.databaseName,collection);
    }

    public long count(){
        return this.count(null);
    }

    public long count(Map<String,Object> query){
        return this.mongoRepository.count(query,this.databaseName,collection);
    }

    public List<T> findAll(Map<String,Object> query) {
        List<T> ret = new ArrayList<T>();
        FindIterable<Document> list = this.mongoRepository.findAll(query,this.databaseName,collection);
        for(Document doc:list){
            ret.add(toObject(doc));
        }
        return ret;
    }

    public T findOne(Map<String,Object>  query) {
        Document doc = mongoRepository.findOne(query,this.databaseName,collection);
        return toObject(doc);
    }

    public Document saveMap(Map<String,Object> data){
        Document doc = new Document();
        doc.putAll(data);
        save(toObject(doc));
        return doc;
    }

    public T save(T item){
        ObjectMapper objectMapper = new ObjectMapper();
        Map data = objectMapper.convertValue(item, Map.class);
        Document doc = this.mongoRepository.save(this.databaseName,collection,data,true);
        return toObject(doc);
    }

    public Map<String,Object> delete(T item){
        String _id = item.get_id();
        return this.mongoRepository.remove(this.databaseName,collection,_id);
    }

    public T removeById(String id){
        Document doc =  this.mongoRepository.getById(id,this.databaseName,collection);
        T ret = toObject(doc);
        delete(ret);
        return ret;
    }

    public T getById(String id){
        Document doc =  this.mongoRepository.getById(id,this.databaseName,collection);
        return toObject(doc);
    }

    public NoSqlResult findAll(String filterByFields, String rows) {
        try {
            return this.mongoRepository.findAll(this.databaseName,collection,filterByFields,rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAll(){
        this.mongoRepository.removeCollection(this.databaseName,collection);
    }

    public Map<String, List<String>> getColumns() {
        Map<String, List<String>> columns = new HashMap<>();
        columns.put("columns",this.mongoRepository.columns(this.databaseName,collection));
        return columns;
    }

    public NoSqlResult findAll(String filterByFields, String projection, String sidx, String sord, String rows, String page) {
        try {
            return this.mongoRepository.findAll(this.databaseName,collection,filterByFields,projection,sidx,sord,rows,page);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
