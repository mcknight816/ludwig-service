package com.bluntsoftware.ludwig.conduit.impl;


import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex Mcknight on 1/4/2017.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class ActivityImpl implements Activity {

    private final static Map<String,Activity> activities = new HashMap<>();


    public ActivityImpl() {

        if(getClass().isAnnotationPresent(Service.class)){
            activities.put(getClass().getTypeName(),this);
        }
    }

    public Map<String, Object> getExternalConfigByName(Object configName,Class clazz) {
        if(configName != null){
            return new HashMap<>();
            /*if(configRepository != null){
                return configRepository.getConfig(configName.toString(),clazz).orElse(null);
            }*/
        }
        return null;
    }

    public enum Category{
        Conduit,Input,FileAndFolders
    }
    public abstract Map<String,Object> run(Map<String,Object> input) throws Exception;
    public abstract JsonSchema getSchema();
    public static Map<String,Activity> list(){return activities;}
    public static Activity getByClassName(String className){
        Activity activity = activities.get(className);
        try {
            if(activity == null){
                activity =  (Activity)Class.forName(className).newInstance();
                activities.put(className,activity);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return activity;
    }

    public Map<String,Object> execute(Map<String,Object> input) throws Exception {
        Map<String,Object> activityInput = getInput();
        input.putAll(activityInput);
        return run(activityInput);
    }

    @Override
    public String getName() {
        String name = this.getClass().getSimpleName();
        if(name.contains("Activity")){
                return name.substring(0,name.indexOf("Activity"));
        }
        return name;
    }

    @Override
    public String getIcon() {
        return "fa-university";
    }

    @Override
    public String getActivityClass() {
        return getClass().getTypeName();
    }

    @Override
    public String getCategory(){
         String className = getActivityClass();
         String module = className.substring(0,className.lastIndexOf('.'));
         return module.substring(module.lastIndexOf('.')+1);
     }

    @Override
    public Map<String, Object> getInput() {
        return getSchema().getValue();
    }

    public Boolean shouldRun(Map<String,Object> input) {
        return true;
    }

    @Override
    public Map<String, Object> getOutput() {
        HashMap<String,Object> ret = new HashMap<>();
        try {
            return run(getInput());
        } catch (Exception e) {
           ret.put("error",e.getMessage());
        }
        return ret;
    }

    @Override
    public Boolean fireAndForget() {
        return false;
    }
}