package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfig;
import com.bluntsoftware.ludwig.conduit.config.ConfigProperties;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.dto.FlowConfigDto;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meta/config")
public class ActivityConfigController {

    private final ActivityConfigRepository activityConfigRepository;

    public ActivityConfigController(ActivityConfigRepository activityConfigRepository) {
        this.activityConfigRepository = activityConfigRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<ConfigProperties> findAll(){
         return activityConfigRepository.findAll();
    }

    @GetMapping(value = "{class}",produces = MediaType.APPLICATION_JSON_VALUE)
    ConfigProperties findById(@PathVariable("class") String cls) {
        return activityConfigRepository.getPropsByKlass(cls);
    }

    @PostMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConfigTestResult test(@RequestBody Map<String,Object> dto){
        ObjectMapper mapper = new ObjectMapper();
        FlowConfigDto flowConfigDto = mapper.convertValue(dto,FlowConfigDto.class);
        ActivityConfig<?> configActivity = activityConfigRepository.getByKlass(flowConfigDto.getConfigClass());
        return configActivity.test(flowConfigDto.getConfig());
    }
}
