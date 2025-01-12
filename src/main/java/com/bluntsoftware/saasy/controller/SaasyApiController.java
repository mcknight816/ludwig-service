package com.bluntsoftware.saasy.controller;

import com.bluntsoftware.saasy.domain.IdName;
import com.bluntsoftware.saasy.domain.Tenant;
import com.bluntsoftware.saasy.domain.TenantUser;
import com.bluntsoftware.saasy.domain.User;
import com.bluntsoftware.saasy.dto.AppDto;
import com.bluntsoftware.saasy.service.SaasyApiService;
import com.bluntsoftware.saasy.service.UserInfoService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/saasy/v1/{appId}")
public class SaasyApiController {

    private final SaasyApiService saasyApiService;

    public SaasyApiController(SaasyApiService saasyApiService) {
        this.saasyApiService = saasyApiService;

    }
    /********************************************************************************************
     *
     *      TENANT
     *
     ********************************************************************************************/
    @ResponseBody
    @PostMapping(value = {"/my-tenants"}, produces = { "application/json" })
    public Flux<IdName> findMyTenants(@PathVariable("appId") String appId){
        log.info("findMyTenants appId={}", appId);

        return this.saasyApiService.findMyTenants(appId)
                .map(t->IdName.builder().id(t.getId()).name(t.getDisplayName()).build());
    }

    @PostMapping(value="/tenant",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Tenant> save(@RequestBody Map<String,Object> dto){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Tenant tenant = mapper.convertValue(dto,Tenant.class);
        return this.saasyApiService.updateTenant(tenant);
    }

    @GetMapping(value = "/tenant/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Tenant> findById(@PathVariable("appId") String appId,@PathVariable("id") String id ){
        return this.saasyApiService.findTenantById(appId,String.valueOf(id));
    }
    /********************************************************************************************
     *
     *      TENANT USER
     *
     ********************************************************************************************/
    @DeleteMapping(value = "/tenant-user/{id}")
    public Mono<Void> deleteTenantUserById(@PathVariable("appId") String appId,@PathVariable("id") String id ){
        return this.saasyApiService.deleteTenantUserById(appId,String.valueOf(id));
    }

    @ResponseBody
    @PostMapping(value = { "/me/{tenantId}"}, produces = { "application/json" })
    public Mono<TenantUser> me(@PathVariable("appId") String appId, @PathVariable("tenantId") String tenantId){
        log.info("findMe appId={} tenantId={}", appId, tenantId);
        return this.saasyApiService.findMe(appId,tenantId);
    }

    @ResponseBody
    @PostMapping(value = { "/me"}, produces = { "application/json" })
    public Mono<TenantUser> me(@PathVariable("appId") String appId ){
        return this.saasyApiService.findMe(appId,null);
    }

    @ResponseBody
    @PatchMapping(value = {"/tenant-user"}, produces = { "application/json" })
    public Mono<TenantUser> update(@RequestBody Map<String,Object> dto){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TenantUser tenantUser = mapper.convertValue(dto,TenantUser.class);
        return this.saasyApiService.updateTenantUser(tenantUser);
    }

    @ResponseBody
    @PostMapping(value = {"/{tenantId}/tenant-user"}, produces = { "application/json" })
    public Mono<TenantUser> saveTenantUser(@PathVariable("appId") String appId,
                                           @PathVariable("tenantId") String tenantId,
                                           @RequestBody Map<String,Object> dto){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TenantUser tenantUser = mapper.convertValue(dto,TenantUser.class);
        tenantUser.setTenantId(tenantId);
        return this.saasyApiService.saveTenantUser(appId,tenantUser);
    }

    @ResponseBody
    @GetMapping(value = {"/{tenantId}/tenant-user/search"}, produces = { "application/json" })
    public Flux<TenantUser> search(@PathVariable("appId") String appId,
                                   @PathVariable("tenantId") String tenantId,
                                   @RequestParam(value = "term",  defaultValue = "") String searchTerm,
                                   @RequestParam(value = "page",  defaultValue = "0") Integer page,
                                   @RequestParam(value = "limit", defaultValue = "50") Integer limit){

        return this.saasyApiService.searchTenantUserByTenant(appId,tenantId, PageRequest.of(page,limit));
    }

    /********************************************************************************************
     *
     *      APP
     *
     ********************************************************************************************/

    @ResponseBody
    @GetMapping(value = {"/app/{id}"}, produces = { "application/json" })
    public Mono<AppDto> findAppById(@PathVariable("id") String id){
        return  saasyApiService.findAppById(id);
    }

    @ResponseBody
    @GetMapping(value = {"/headers"}, produces = { "application/json" })
    public Map<String,String>  headerCheck(@RequestHeader Map<String,String> headers){
        return headers;
    }

}
