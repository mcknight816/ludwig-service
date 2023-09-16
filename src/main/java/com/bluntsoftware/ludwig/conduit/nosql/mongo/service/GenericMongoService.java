package com.bluntsoftware.ludwig.conduit.nosql.mongo.service;


import com.bluntsoftware.ludwig.conduit.nosql.mongo.MongoServiceImpl;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.domain.Domain;
import com.bluntsoftware.ludwig.conduit.nosql.mongo.repository.GenericMongoRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex Mcknight on 8/11/2017.
 *
 */
public  class GenericMongoService<T extends Domain,X extends GenericMongoRepository<T>> extends GenericRestController<T>{
    @Autowired
    protected GenericMongoRepository<T> repository;
    public void setRepository(X repository) {
        this.repository = repository;
    }
    public GenericMongoRepository<T> getRepository() {
        return repository;
    }

    @Override
    public Object saveUpdate(HttpServletRequest request, @RequestBody Map<String, Object> object) throws Exception {
        return saveMap(object);
    }

    protected Document saveMap(Map<String, Object> object){
        return repository.saveMap(object);
    }


    @Override
    protected Object save(@PathVariable("id") String id, HttpServletRequest request, @RequestBody T object) throws Exception {
        object.set_id(id);
        return repository.save(object);
    }

    @Override
    public T create(T object, HttpServletRequest request) throws Exception {
        return repository.save(object);
    }

    @Override
    public T update(T object, HttpServletRequest request) throws Exception {
        return repository.save(object);
    }

    @Override
    public T get(T object, HttpServletRequest request) throws Exception {
        return repository.getById(object.get_id());
    }

    @Override
    public T remove(T object, HttpServletRequest request) throws Exception {
        return repository.removeById(object.get_id());
    }

    @Override
    public Object findAll(HttpServletRequest request) throws Exception {
        String filterByFields =  MongoServiceImpl.validString(request.getParameter("filterByFields"), "{}");
        String rows = MongoServiceImpl.validString(request.getParameter("rows"),"25");
        String page = MongoServiceImpl.validString(request.getParameter("page"),"1");
        return repository.findAll(filterByFields,rows);
    }

    @Override
    public Object findOne(@PathVariable("id") String id, HttpServletRequest request) throws Exception {
        return repository.getById(id);
    }

    @Override
    public Object delete(@PathVariable("id") String id, HttpServletRequest request) throws Exception {
        return repository.removeById(id);
    }

    @Override
    public Map<String, Object> showSchema() {
        return null;
    }

    @Override
    public Map<String, List<String>> columns() {
        return repository.getColumns();
    }

    @Override
    public Map<String, Object> getApi(HttpServletRequest request) {
        return null;
    }
}
