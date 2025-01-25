package com.bluntsoftware.ludwig.conduit.activities;

import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract implementation of the Activity interface.
 * Incorporates improvements for thread safety, better lifecycle management, and logging.
 * Created by Alex Mcknight on 1/4/2017 and refactored for best practices.
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class  ActivityImpl implements Activity {

    private static final Logger logger = LoggerFactory.getLogger(ActivityImpl.class);

    // Thread-safe map for storing activities
    private static final Map<String, Activity> activities = new ConcurrentHashMap<>();

    // Repository dependency, must be injected
    private final ActivityConfigRepository activityConfigRepository;

    // Constructor with null validation for dependency
    public ActivityImpl(ActivityConfigRepository activityConfigRepository) {
        if (activityConfigRepository == null) {
            throw new IllegalArgumentException("ActivityConfigRepository must not be null");
        }
        this.activityConfigRepository = activityConfigRepository;

        // Register activity if annotated as a Service
        if (getClass().isAnnotationPresent(Service.class)) {
            activities.put(getClass().getTypeName(), this);
        }
    }

    public enum Category{
        Conduit,Input,FileAndFolders
    }
    // Getter for activity repository, ensuring it is available
    protected ActivityConfigRepository getActivityConfigRepository() {
        return activityConfigRepository;
    }

    /**
     * Retrieves external configuration by its name and type.
     *
     * @param configName The name of the configuration to retrieve.
     * @param clazz      The expected class of the configuration.
     * @param <T>        The type parameter for the configuration.
     * @return A map of configuration values, or null if not found.
     */
    public <T> Map<String, Object> getExternalConfigByName(Object configName, Class<T> clazz) {
        if (configName != null && activityConfigRepository != null) {
            var config = activityConfigRepository.findByNameAndConfigClass(configName.toString(), clazz.getName());
            if (config != null) {
                return config.getConfig();
            }
        }
        logger.warn("Configuration not found for name: {} and class: {}", configName, clazz.getName());
        return null;
    }

    /**
     * Retrieves an activity by its class name, using reflection only when necessary.
     *
     * @param className The fully qualified name of the class.
     * @return The activity instance, or null if it cannot be instantiated.
     */
    public static Activity getByClassName(String className) {
        Activity activity = activities.get(className);
        if (activity == null) {
            try {
                activity = (Activity) Class.forName(className).getDeclaredConstructor().newInstance();
                activities.put(className, activity);
            } catch (Exception e) {
                logger.error("Error instantiating class: {}", className, e);
                return null;
            }
        }
        return activity;
    }

    /**
     * Lists all registered activities.
     *
     * @return An unmodifiable map of all activities.
     */
    public static Map<String, Activity> list() {
        return Map.copyOf(activities);
    }

    @Override
    public ActivityProperties getActivityProperties() {
        return this.getActivityProperties(this);
    }

    /**
     * Returns the ActivityProperties for the provided activity instance.
     *
     * @param activity The activity to retrieve properties for.
     * @return The activity properties.
     */
    public ActivityProperties getActivityProperties(Activity activity) {
        return ActivityProperties.builder()
                .name(activity.getName())
                .activityClass(activity.getActivityClass())
                .output(activity.getOutput())
                .icon(activity.getIcon())
                .category(activity.getCategory())
                .fireAndForget(activity.fireAndForget())
                .input(activity.getInput())
                .schema(activity.getJsonSchema())
                .build();
    }

    /**
     * Executes the activity by combining input configurations and running the logic.
     *
     * @param input The provided activity input values.
     * @return The result of the activity logic.
     * @throws Exception If an error occurs during execution.
     */
    public Map<String, Object> execute(Map<String, Object> input) throws Exception {
        Map<String, Object> activityInput = new ConcurrentHashMap<>(getInput());
        activityInput.putAll(input);
        return run(activityInput);
    }

    @Override
    public String getName() {
        String name = this.getClass().getSimpleName();
        if (name.contains("Activity")) {
            return name.substring(0, name.indexOf("Activity"));
        }
        return name;
    }

    @Override
    public String getIcon() {
        return "fa-university"; // Default icon, can be overridden
    }

    @Override
    public String getActivityClass() {
        return getClass().getTypeName();
    }

    @Override
    public String getCategory() {
        String className = getActivityClass();
        String module = className.substring(0, className.lastIndexOf('.'));
        return module.substring(module.lastIndexOf('.') + 1);
    }

    @Override
    public Map<String, Object> getInput() {
        JsonSchema schema = getJsonSchema();
        if (schema == null) {
            logger.warn("getSchema() returned null; returning empty input map");
            return Map.of();
        }
        return schema.getValue();
    }

    public Boolean shouldRun(Map<String, Object> input) {
        // Business logic can be added here
        return true;
    }

    @Override
    public Map<String, Object> getOutput() {
        try {
            return run(getInput());
        } catch (Exception e) {
            logger.error("Error running activity: {}", getName(), e);
            Map<String,Object> output = new ConcurrentHashMap<>();
             output.put("error", e.getMessage() != null ? e.getMessage() : e.toString());
             return output;
        }
    }

    @Override
    public Boolean fireAndForget() {
        return false;
    }

    // Abstract methods to be implemented by subclasses
    public abstract Map<String, Object> run(Map<String, Object> input) throws Exception;
    public abstract JsonSchema getJsonSchema();
}