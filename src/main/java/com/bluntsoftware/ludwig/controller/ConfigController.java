package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.dto.ConfigDto;
import com.bluntsoftware.ludwig.mapping.ConfigMapper;
import com.bluntsoftware.ludwig.service.ConfigService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping("/core/config")
public class ConfigController {
    private final ConfigService configService;
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ConfigDto> save(@RequestBody ConfigDto dto){
        return this.configService.save(ConfigMapper.MAPPER.configDtoToConfig(dto)).map(ConfigMapper.MAPPER::configToConfigDto);
    }
    @GetMapping(value = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ConfigDto> findById(@PathVariable("id") String id ){
        return this.configService.findById(String.valueOf(id)).map(ConfigMapper.MAPPER::configToConfigDto);
    }
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ConfigDto> findAll(){
        return this.configService.findAll().map(ConfigMapper.MAPPER::configToConfigDto);
    }
    @DeleteMapping(value = "{id}")
    public Mono<ConfigDto> deleteById(@PathVariable("id") String id ){
        return this.configService.deleteById(String.valueOf(id)).map(ConfigMapper.MAPPER::configToConfigDto);
    }
}
