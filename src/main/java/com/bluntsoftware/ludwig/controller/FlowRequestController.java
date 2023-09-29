package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.domain.FlowRequest;
import com.bluntsoftware.ludwig.service.FlowRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/core/flow-request")
@CrossOrigin
@AllArgsConstructor
public class FlowRequestController {
    private final FlowRequestService flowRequestService;
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<FlowRequest> findAll(){
        return this.flowRequestService.findAll();
    }
    @GetMapping(value = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<FlowRequest> findById(@PathVariable("id") String id ) {
        return this.flowRequestService.findById(id);
    }
    @PostMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<FlowRequest> save(@RequestBody FlowRequest model){
        return this.flowRequestService.save(model);
    }
    @DeleteMapping(value = "{id}")
    public Mono<FlowRequest> deleteById(@PathVariable("id") String id ){
        return this.flowRequestService.deleteById(id);
    }
}
