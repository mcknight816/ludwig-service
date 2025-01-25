package com.bluntsoftware.ludwig.conduit.config;

import com.bluntsoftware.ludwig.conduit.utils.AES;
import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonPath;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.StringProperty;
import com.bluntsoftware.ludwig.domain.Config;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class ActivityConfigImpl<T extends EntitySchema> implements ActivityConfig<T> {
    private final Class<T> type;
    private final static Map<String, ActivityConfig> configs = new ConcurrentHashMap<>();

    public abstract ConfigTestResult testConfig(T config);



    public static List<ConfigProperties> list(){
        return configs.values().stream().map(a -> ConfigProperties.builder()
                .configClass(a.getConfigClass())
                .name(a.getName())
                .category(a.getCategory())
                .schema(a.getSchema())
                .build()
        ).collect(Collectors.toList());
    }

    public static ActivityConfig getByConfigClass(String configClass){
        return configs.get(configClass);
    }

    public ConfigTestResult test(Map<String, Object> config) {
        return testConfig(getConfig(config));
    }

    @SuppressWarnings("unchecked")
    public ActivityConfigImpl() {
        this.type = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];

        if(getClass().isAnnotationPresent(Service.class)){
            configs.put(getClass().getTypeName(),this);
        }
    }

    public static ActivityConfig<?> getByClassName(String className){
        ActivityConfig<?> configSchema = configs.get(className);
        try {
            if(configSchema == null){
                configSchema =  (ActivityConfig<?>)Class.forName(className).getDeclaredConstructor().newInstance();
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
        return getRecord();
    }

    public JsonSchema getRecord() {
        try {
            return (JsonSchema) type.getMethod("getEntitySchema").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPropertyName() {
        return getName().toLowerCase().replace(" ","");
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

    protected Map<String,Object> getDefaults(){
        return getSchema().getValue();
    }

    public T getDefaultConfig() {
       return this.getConfig(this.getDefaults());
    }

    public T getConfig(Map<String,Object> config) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.convertValue(config,type);
    }
}
