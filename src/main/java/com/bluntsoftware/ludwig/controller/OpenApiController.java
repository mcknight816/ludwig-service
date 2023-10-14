package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.service.OpenApiService;
import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
@RestController
@RequestMapping(value = "/api/swagger")
public class OpenApiController {

    private final OpenApiService openAPiService;

    public OpenApiController(OpenApiService openAPiService) {
        this.openAPiService = openAPiService;
    }

    @RequestMapping(value = {"/{appId}","/{appId}/{tenantId}"},
            method = { RequestMethod.GET},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Map<String,Object> api(@PathVariable("appId") String id,@PathVariable(value = "tenantId",required= false) String tenantId){
        if(tenantId != null){
            TenantResolver.setCurrentTenant(tenantId);
        }
        return openAPiService.openApi(id);
    }

    @RequestMapping(
            value = "/{appId}/ludwig.json",
            method = { RequestMethod.GET},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<InputStreamResource> apiJson(@PathVariable("appId") String id){
        InputStreamResource isr = null;
        HttpHeaders respHeaders = new HttpHeaders();
        try {
            ObjectMapper mapper = new ObjectMapper();
            byte[] dataBuffer =  mapper.writeValueAsBytes(openAPiService.openApi(id));
            isr = new InputStreamResource(new ByteArrayInputStream(dataBuffer));
            respHeaders.setContentType(MediaType.valueOf("application/json"));
            respHeaders.setContentLength(dataBuffer.length);
            respHeaders.setContentDispositionFormData("attachment", "ludwig.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
    }
}
