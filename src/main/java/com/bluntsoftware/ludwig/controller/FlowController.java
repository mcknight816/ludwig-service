package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.service.FlowService;
import com.bluntsoftware.ludwig.dto.FlowDto;
import com.bluntsoftware.ludwig.mapping.FlowMapper;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.PageRequest;

@RestController
@RequestMapping("/core/flow")
public class FlowController {

  private final  FlowService service;

  public  FlowController(FlowService service) {
    this.service = service;
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<FlowDto> save(@RequestBody FlowDto dto){
    return this.service.save(FlowMapper.MAPPER.flowDtoToFlow(dto)).map(FlowMapper.MAPPER::flowToFlowDto);
  }

  @GetMapping(value = "{id}",produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<FlowDto> findById(@PathVariable("id") String id ){
    return this.service.findById(String.valueOf(id)).map(FlowMapper.MAPPER::flowToFlowDto);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<FlowDto> findAll(){
    return this.service.findAll().map(FlowMapper.MAPPER::flowToFlowDto);
  }

  @DeleteMapping(value = "{id}")
  public Mono<Void> deleteById(@PathVariable("id") String id ){
    return this.service.deleteById(String.valueOf(id));
  }

  @ResponseBody
  @GetMapping(value = {"/search"}, produces = { "application/json" })
  public Flux<FlowDto> search(@RequestParam(value = "term",  defaultValue = "") String searchTerm,
                             @RequestParam(value = "page",  defaultValue = "0") Integer page,
                             @RequestParam(value = "limit", defaultValue = "50") Integer limit){
          return this.service.search(searchTerm,PageRequest.of(page,limit)).map(FlowMapper.MAPPER::flowToFlowDto);
  }

}
