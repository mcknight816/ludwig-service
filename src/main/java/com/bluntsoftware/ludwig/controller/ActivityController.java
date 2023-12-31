package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.conduit.impl.ActivityProperties;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meta/activity")
public class ActivityController {
    private final ActivityRepository activityRepository;
    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<ActivityProperties> findAll(){
        List<ActivityProperties> activities =  activityRepository.findAll().stream().map(Activity::getActivityProperties).collect(Collectors.toList());
        return activities;
    }
    @GetMapping(value = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    ActivityProperties findById(@PathVariable("id") String id){
        return activityRepository.getByKlass(id).getActivityProperties();
    }
}
