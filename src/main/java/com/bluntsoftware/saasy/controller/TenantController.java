package com.bluntsoftware.saasy.controller;


import com.bluntsoftware.saasy.domain.Tenant;
import com.bluntsoftware.saasy.service.TenantService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/rest/tenant")
public class TenantController {

  private final TenantService service;

  public  TenantController(TenantService service) {
    this.service = service;
  }

  @PostMapping(value="",produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Tenant> save(@RequestBody Map<String,Object> dto){
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    Tenant tenant = mapper.convertValue(dto,Tenant.class);
    return this.service.save(tenant);
  }

  @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Tenant> findById(@PathVariable("id") String id ){
    return this.service.findById(String.valueOf(id));
  }

  @GetMapping(value = "",produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<Tenant> findAll(){
    return this.service.findAll();
  }

  @DeleteMapping(value = "/{id}")
  public Mono<Void> deleteById(@PathVariable("id") String id ){
    return this.service.deleteById(String.valueOf(id));
  }

  @ResponseBody
  @GetMapping(value = {"/search"}, produces = { "application/json" })
  public Flux<Tenant> search(@RequestParam(value = "term",  defaultValue = "") String searchTerm,
                             @RequestParam(value = "page",  defaultValue = "0") Integer page,
                             @RequestParam(value = "limit", defaultValue = "50") Integer limit){
          return this.service.search(searchTerm,PageRequest.of(page,limit));
  }


}
