package com.bluntsoftware.saasy.controller;


import com.bluntsoftware.saasy.domain.TenantUser;
import com.bluntsoftware.saasy.service.TenantUserService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/rest/tenant-user")
public class TenantUserController {

    private final TenantUserService service;

    public TenantUserController(TenantUserService service) {
        this.service = service;
    }

    @PostMapping(value="",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TenantUser> save(@RequestBody Map<String,Object> dto){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TenantUser tenantUser = mapper.convertValue(dto,TenantUser.class);
        return this.service.save(tenantUser);
    }

    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<TenantUser> findById(@PathVariable("id") String id ){
        return this.service.findById(String.valueOf(id));
    }

    @GetMapping(value = "",produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<TenantUser> findAll(){
        return this.service.findAll();
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteById(@PathVariable("id") String id ){
        return this.service.deleteById(String.valueOf(id));
    }

    @ResponseBody
    @GetMapping(value = {"/search"}, produces = { "application/json" })
    public Flux<TenantUser> search(@RequestParam(value = "tenantId",required = false) String tenantId,
                                   @RequestParam(value = "term",  defaultValue = "") String searchTerm,
                               @RequestParam(value = "page",  defaultValue = "0") Integer page,
                               @RequestParam(value = "limit", defaultValue = "50") Integer limit){
        return this.service.searchByTenant(tenantId, PageRequest.of(page,limit));
    }

}
