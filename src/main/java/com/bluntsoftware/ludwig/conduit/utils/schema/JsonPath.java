package com.bluntsoftware.ludwig.conduit.utils.schema;

import java.util.*;
import java.util.stream.Collectors;
public class JsonPath {

    private Map<String,Object> json;

    public JsonPath(Map<String, Object> json) {
        this.json = json;
    }

    public static List<String> parsePath(String path){
        if(path.startsWith("['")){
            String[] paths = path.substring(2,path.length()-2).split("\']\\[\'");
            return Arrays.stream(paths).collect(Collectors.toList());
        }else{
            return Arrays.stream(path.split("\\.")).collect(Collectors.toList());
        }
    }
    public static void createValue(Map<String,Object> map,String path,Object value){
        Map<String,Object> tgt = map;
        List<String> keys = parsePath(path);
        String targetKey = keys.get(keys.size()-1);
        for (String key : keys) {
              if(key.equalsIgnoreCase(targetKey)){
                  tgt.put(targetKey,value);
              }else if(tgt.containsKey(key)){
                 tgt = (Map<String,Object>)tgt.get(key);
              }else{
                  Map<String,Object> newTgt = new HashMap<>();
                 tgt.put(key,newTgt);
                 tgt = newTgt;
              }
        }
    }
    public void setValue(String path,Object value){
        Object tgt = this.json;
        List<String> keys = parsePath(path);
        String targetKey = keys.remove(keys.size()-1);
        for (String key : keys) {
            tgt = getValue(key, tgt);
        }
        if(tgt instanceof Map){
            ((Map)tgt).put(targetKey,value);
        }else if(tgt instanceof List){
            ((List)tgt).set(Integer.parseInt(targetKey),value);
        }
    }

    private Object getValue(String key,Object val){
        if(val instanceof Map){
            return ((Map)val).get(key);
        }else if(val instanceof List){
            return ((List)val).get(Integer.parseInt(key));
        }
        return val;
    }

    public Object getValue(String path) {
        Object obj = this.json;
        if(path != null && !path.equalsIgnoreCase("")){
            for (String key : parsePath(path)) {
                obj = getValue(key, obj);
            }
        }
        return obj;
    }

    public static void main(String[] args) {
        Map<String,Object> person = new HashMap<>();
        Map<String,Object> address = new HashMap<>();
        address.put("address","816 Stony brook");
        address.put("city","W. Norriton");
        address.put("state","PA");
        address.put("zip","19403");
        person.put("address",address);
        person.put("firstName","Alex");
        person.put("lastName","Mcknight");
         System.out.println(person);
        JsonPath jsonPath = new JsonPath(person);

        Map<String,Object> diffrentaddress = new HashMap<>();
        diffrentaddress.put("address","2121 Tamreal ave");
        diffrentaddress.put("city","Glass Borugh");
        diffrentaddress.put("state","NJ");
        diffrentaddress.put("zip","55555");
        List allAddresses = new ArrayList();
        allAddresses.add(diffrentaddress);
        allAddresses.add(address);
        person.put("allAddresses",allAddresses );
        jsonPath.setValue("address",diffrentaddress);
        jsonPath.setValue("address.zip","11111");
        jsonPath.setValue("firstName","Fred");
        jsonPath.setValue("allAddresses.1.zip","999999");
        System.out.println(person);

    }
}
