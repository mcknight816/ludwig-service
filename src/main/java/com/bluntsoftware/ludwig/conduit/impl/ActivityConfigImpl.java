package com.bluntsoftware.ludwig.conduit.impl;

import com.bluntsoftware.ludwig.conduit.AES;
import com.bluntsoftware.ludwig.conduit.ActivityConfig;
import com.bluntsoftware.ludwig.conduit.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.schema.JsonPath;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.StringProperty;
import com.bluntsoftware.ludwig.domain.Config;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class ActivityConfigImpl<T extends EntitySchema> implements ActivityConfig {

    private final static Map<String, ActivityConfig> configs = new HashMap<>();

    public static Map<String, ActivityConfig> list(){return configs;}

    public static ActivityConfig getByClassName(String className){
        ActivityConfig configSchema = configs.get(className);
        try {
            if(configSchema == null){
                configSchema =  (ActivityConfig)Class.forName(className).getDeclaredConstructor().newInstance();
                configs.put(className, configSchema);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return configSchema;
    }
    public JsonSchema getSchema(){
       /* JsonSchema schema = new JsonSchema(getName());
        JsonSchema recordProperty = getRecord();
        schema.addRecord(recordProperty.getTitle(),recordProperty);*/
        return getRecord();
    }

    @Override
    public String getPropertyName() {
        return getName().toLowerCase().replace(" ","");
    }

    @JsonIgnore
    public abstract JsonSchema getRecord();

    public abstract Map test();
    public ActivityConfigImpl() {
        if(getClass().isAnnotationPresent(Service.class)){
            configs.put(getClass().getTypeName(),this);
        }
    }
    @Override
    public String getConfigClass() {
        return getClass().getTypeName();
    }

    @Override
    public String getCategory(){
        String className = getConfigClass();
        String module = className.substring(0,className.lastIndexOf('.'));
        return module.substring(module.lastIndexOf('.')+1,module.length());
    }

    @Override
    public String getName() {
        String name = this.getClass().getSimpleName();
        if(name.contains("Config")){
            return name.substring(0,name.indexOf("Config"));
        }
        return name;
    }

    public Config getDefaults(){
        Config default_config =  new Config();
        default_config.setConfigClass(getConfigClass());
        default_config.putAll(getSchema().getValue());
        return default_config;
    }

    public static String encrypt(String value){
        return  AES.encrypt(value,System.getProperty("ludwig.encrypt.key","ludwig"));
    }

    public static String decrypt(String secret){
        return  AES.decrypt(secret,System.getProperty("ludwig.encrypt.key","ludwig"));
    }

    static JsonSchema getConfigSchema(Config config){
        String configClass = config.getConfigClass();
        ActivityConfig schema = ActivityConfigImpl.getByClassName(configClass);
        return schema.getSchema();
    }

    public static Config encrypt(Config config){
        Map<String, StringProperty> paths = getConfigSchema(config).getSecretStringProperties();
        JsonPath path = new JsonPath(config);
        for(String prop_path : paths.keySet()){
            Object val = path.getValue("payload." + prop_path);
            if(val != null && !val.toString().equalsIgnoreCase("") && !val.toString().startsWith("{cipher}")){
                String encrypted_value = "{cipher}" + encrypt(val.toString());
                path.setValue("payload." +prop_path,encrypted_value);
            }
        }
        return config;
    }

    public static Config decrypt(Config config){
        Map<String, StringProperty> paths = getConfigSchema(config).getSecretStringProperties();
        JsonPath path = new JsonPath(config);
        for(String prop_path : paths.keySet()){
            Object val = path.getValue("payload." + prop_path);
            if(val != null &&  val.toString().startsWith("{cipher}")){
                String decrypted_value = decrypt(val.toString().replace("{cipher}","" ));
                path.setValue("payload." +prop_path,decrypted_value);
            }
        }
        return config;
    }

}
