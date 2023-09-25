package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.dto.ApplicationDto;
import com.bluntsoftware.ludwig.mapping.ApplicationMapper;
import com.bluntsoftware.ludwig.service.ApplicationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/core/application")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApplicationDto> save(@RequestBody ApplicationDto dto){
        return this.applicationService.save(ApplicationMapper.MAPPER.applicationDtoToApplication(dto)).map(ApplicationMapper.MAPPER::applicationToApplicationDto);
    }

    @GetMapping(value = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ApplicationDto> findById(@PathVariable("id") String id ){
        return this.applicationService.findById(String.valueOf(id)).map(ApplicationMapper.MAPPER::applicationToApplicationDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ApplicationDto> findAll(){
        return this.applicationService.findAll().map(ApplicationMapper.MAPPER::applicationToApplicationDto);
    }

    @DeleteMapping(value = "{id}")
    public Mono<ApplicationDto> deleteById(@PathVariable("id") String id ){
        return this.applicationService.deleteById(String.valueOf(id)).map(ApplicationMapper.MAPPER::applicationToApplicationDto);
    }
}
