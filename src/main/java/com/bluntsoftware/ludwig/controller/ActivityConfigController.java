package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.conduit.config.ActivityConfig;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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
        ActivityConfig config =  activityRepository.getByKlass(cls);
        return config;
    }

}
