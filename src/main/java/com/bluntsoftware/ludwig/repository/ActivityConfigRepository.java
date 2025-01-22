package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfig;
import com.bluntsoftware.ludwig.conduit.config.ActivityConfigImpl;
import com.bluntsoftware.ludwig.conduit.config.ConfigProperties;
import com.bluntsoftware.ludwig.domain.FlowConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Repository
public class ActivityConfigRepository {

    private final FlowConfigRepository flowConfigRepository;

    public ActivityConfigRepository(FlowConfigRepository flowConfigRepository) {
        this.flowConfigRepository = flowConfigRepository;
    }

    public List<ConfigProperties> findAll(){
        return  ActivityConfigImpl.list();
    }

    public ActivityConfig getByKlass(String klass){
        return ActivityConfigImpl.getByConfigClass(klass);
    }

    public FlowConfig findByNameAndConfigClass(String name, String configCLass) {
        if(flowConfigRepository != null){
            Mono<FlowConfig> flowConfigMono = flowConfigRepository.findByNameAndConfigClass( name,configCLass);
            FlowConfig flowConfig = flowConfigMono.block();
            if(flowConfig != null){
                return flowConfig;
            }
        }
        ActivityConfig activityConfig = getByKlass(configCLass);
        ObjectMapper mapper = new ObjectMapper();
        return FlowConfig.builder()
                .configClass(configCLass)
                .name(name)
                .config(mapper.convertValue(activityConfig, Map.class))
                .build();
    }

    public <T extends ActivityConfig> T getActivityConfigByNameAs(String name,Class<T> clazz) throws IllegalArgumentException {
        ActivityConfig activityConfig = getByKlass(clazz.getName());
        FlowConfig flowConfig = this.findByNameAndConfigClass(name,clazz.getName());

        return (T) activityConfig ;
    }

}
