package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.conduit.Activity;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/meta/activity")
public class ActivityController {
    private final ActivityRepository activityRepository;
    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<Activity> findAll(){
        return activityRepository.findAll();
    }
    @GetMapping(value = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    Activity findById(@PathVariable("id") String id){
        return activityRepository.getByKlass(id);
    }
}
