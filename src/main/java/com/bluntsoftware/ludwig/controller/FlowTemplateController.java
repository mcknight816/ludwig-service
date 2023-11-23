package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.domain.Flow;
import com.bluntsoftware.ludwig.domain.FlowTemplate;
import com.bluntsoftware.ludwig.service.FlowTemplateService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meta/template")
@CrossOrigin
@AllArgsConstructor
public class FlowTemplateController {

    private final FlowTemplateService flowTemplateService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<FlowTemplate> findAll(){
        return this.flowTemplateService.findAll();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flow createFlow(@RequestBody FlowTemplate flowTemplate){
        return this.flowTemplateService.createFlow(flowTemplate);
    }
}
