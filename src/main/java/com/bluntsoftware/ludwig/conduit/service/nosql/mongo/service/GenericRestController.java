package com.bluntsoftware.ludwig.conduit.service.nosql.mongo.service;

/**
 * Created by Alex Mcknight on 8/11/2017.
 */

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Alexander Mcknight
 * Date: 6/28/15
 * Time: 6:35 PM
 */
public abstract class GenericRestController<T> {
    //Save Update
    @Transactional(readOnly = false)
    @RequestMapping(
            method = {RequestMethod.POST,RequestMethod.PUT},
            produces = "application/json",
            consumes = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public abstract Object saveUpdate(HttpServletRequest request, @RequestBody Map<String,Object> object) throws Exception;

    //Save
    @Transactional(readOnly = false)
    @RequestMapping(
            value="{id}",
            method = { RequestMethod.POST,RequestMethod.PUT},
            produces = "application/json",
            consumes = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public abstract Object save(@PathVariable("id") String id, HttpServletRequest request, @RequestBody T object) throws Exception;

    public abstract T create(T object,HttpServletRequest request)throws Exception;
    public abstract T update(T object,HttpServletRequest request)throws Exception;
    public abstract T get(T object,HttpServletRequest request)throws Exception;
    public abstract T remove(T object,HttpServletRequest request)throws Exception;

    //List
    @RequestMapping(
            value = {"","/data"},
            method = {RequestMethod.GET},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public abstract Object findAll(HttpServletRequest request) throws Exception;

    //Get
    @RequestMapping(
            value = "{id}",
            method = {RequestMethod.GET},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public abstract Object findOne(@PathVariable("id") String id,HttpServletRequest request) throws Exception;

    //Remove
    @RequestMapping(
            value = "{id}",
            method = {RequestMethod.DELETE},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public abstract Object delete(@PathVariable("id") String id,HttpServletRequest request) throws Exception;

    //Show Schema
    @RequestMapping( value = "/schema", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public abstract Map<String, Object> showSchema();

    //List Columns
    @RequestMapping(value = "/columns", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public abstract Map<String, List<String>> columns();

    //get api
    @RequestMapping(value = "/api")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public abstract Map<String, Object> getApi(HttpServletRequest request);

}
