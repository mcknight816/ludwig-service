package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.dto.FlowConfigDto;
import com.bluntsoftware.ludwig.mapping.FlowConfigMapper;
import com.bluntsoftware.ludwig.service.FlowConfigService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/core/config")
public class FlowConfigController {
    private final FlowConfigService configService;
    public FlowConfigController(FlowConfigService configService) {
        this.configService = configService;
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<FlowConfigDto> save(@RequestBody FlowConfigDto dto){
        return this.configService.save(FlowConfigMapper.MAPPER.flowConfigDtoToFlowConfig(dto)).map(FlowConfigMapper.MAPPER::flowConfigToFlowConfigDto);
    }
    @GetMapping(value = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<FlowConfigDto> findById(@PathVariable("id") String id ){
        return this.configService.findById(String.valueOf(id)).map(FlowConfigMapper.MAPPER::flowConfigToFlowConfigDto);
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<FlowConfigDto> findAll(){
        return this.configService.findAll().map(FlowConfigMapper.MAPPER::flowConfigToFlowConfigDto);
    }
    @DeleteMapping(value = "{id}")
    public Mono<FlowConfigDto> deleteById(@PathVariable("id") String id ){
        return this.configService.deleteById(String.valueOf(id)).map(FlowConfigMapper.MAPPER::flowConfigToFlowConfigDto);
    }
}
