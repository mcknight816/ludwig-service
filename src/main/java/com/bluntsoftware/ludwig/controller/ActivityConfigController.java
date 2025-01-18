package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfig;
import com.bluntsoftware.ludwig.conduit.config.ConfigTestResult;
import com.bluntsoftware.ludwig.dto.FlowConfigDto;
import com.bluntsoftware.ludwig.mapping.FlowConfigMapper;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meta/config")
public class ActivityConfigController {

    private final ActivityConfigRepository activityRepository;

    public ActivityConfigController(ActivityConfigRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<ActivityConfig> findAll(){
        return activityRepository.findAll();
    }

    @GetMapping(value = "{class}",produces = MediaType.APPLICATION_JSON_VALUE)
    ActivityConfig findById(@PathVariable("class") String cls){
        return activityRepository.getByKlass(cls);
    }

    @PostMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConfigTestResult test(@RequestBody Map<String,Object> dto){
        ObjectMapper mapper = new ObjectMapper();
        FlowConfigDto flowConfigDto = mapper.convertValue(dto,FlowConfigDto.class);
        ActivityConfig<?> configActivity = activityRepository.getByKlass(flowConfigDto.getConfigClass());
        return configActivity.test(flowConfigDto.getConfig());
    }
}
