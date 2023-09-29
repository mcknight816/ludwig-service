package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.service.OpenApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping(value = "/api/swagger")
public class OpenApiController {

    private final OpenApiService openAPiService;

    public OpenApiController(OpenApiService openAPiService) {
        this.openAPiService = openAPiService;
    }

    @RequestMapping(
            value = "/{appId}",
            method = { RequestMethod.GET},
            produces = "application/json")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Map<String,Object> api(@PathVariable("appId") String id){
        Map<String,Object> info = new HashMap<>();
        Map<String,Object> openApi = new HashMap<>();
        info.put("description","Conduit rest api based on Ludwig flows.");
        info.put("version","1.0.3");
        info.put("title","Ludwig Conduit API");
        openApi.put("openapi","3.0.0");

        openApi.put("info",info);
        openApi.put("url","http://localhost:8088");
        openApi.put("paths",openAPiService.paths(id));

        return openApi;
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
            byte[] dataBuffer =  mapper.writeValueAsBytes(api(id));
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
