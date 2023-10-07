package com.bluntsoftware.ludwig.controller;

import com.bluntsoftware.ludwig.conduit.activities.files.FileDownloader;
import com.bluntsoftware.ludwig.conduit.activities.output.HttpResponseActivity;
import com.bluntsoftware.ludwig.domain.FlowActivity;
import com.bluntsoftware.ludwig.service.FlowRunnerService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/{appPath}")
public class FlowRunnerController {

    private final FlowRunnerService flowRunnerService;
    private final AssetController assetService;
    public FlowRunnerController(FlowRunnerService flowRunnerService, AssetController assetService) {
        this.flowRunnerService = flowRunnerService;
        this.assetService = assetService;
    }

    @PostMapping(value = {"/{flowName}","/{flowName}/action/{context}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Object> postToFlow(@PathVariable String appPath,@PathVariable String flowName, @PathVariable(required = false) String context, @RequestBody Map<String, Object> object)  {
        return response(flowRunnerService.handlePost(appPath,flowName,context,object));
    }

    @GetMapping(value = {"/{flowName}","/{flowName}/action/{context}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Object> get(@PathVariable String appPath, @PathVariable String flowName, @PathVariable(required = false) String context, HttpServletRequest request){
        Map<String,Object> payload = new HashMap<>();
        for(String key:request.getParameterMap().keySet()){
            payload.put(key,request.getParameter(key));
        }
        return response(flowRunnerService.handleGet(appPath,flowName,context,payload));
    }
    @GetMapping(value = {"/{flowName}/{id}","/{flowName}/action/{context}/{id}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    Object getById(@PathVariable String appPath,@PathVariable String flowName, @PathVariable String id, @PathVariable(required=false) String context, HttpServletRequest request) {
        return response(flowRunnerService.handelGetById(appPath,flowName,context,id)) ;
    }

    @DeleteMapping (value = {"/{flowName}/{id}","/{flowName}/action/{context}/{id}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    Object deleteById(@PathVariable String appPath,@PathVariable String flowName, @PathVariable String id, @PathVariable(required=false) String context, HttpServletRequest request) {
        return response(flowRunnerService.handelDeleteById(appPath,flowName,context,id)) ;
    }
    @GetMapping( value = {"/{flowName}/columns","/{flowName}/columns/action/{context}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    Object listColumns(@PathVariable String appPath, @PathVariable("flowName") String flowName, @PathVariable(required=false) String context) {
        return response(flowRunnerService.handleGetColumns(appPath,flowName,context));
    }

    public ResponseEntity<Object> response(List<FlowActivity> out ){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(out != null){
            for(FlowActivity flowActivity:out){
                if(flowActivity.getHasError() != null && flowActivity.getHasError()){
                    return new ResponseEntity<>(flowActivity.getOutput(),headers, HttpStatus.BAD_REQUEST);
                }
                if(flowActivity.getActivityClass().equalsIgnoreCase(HttpResponseActivity.class.getName())){
                    Map<String,Object> in = flowActivity.getInput();
                    boolean download = false;
                    if(in.containsKey("output_method")){
                        download = in.get("output_method").toString().equalsIgnoreCase("download");
                    }
                    if(in.containsKey("output_type")){
                        String contentType = in.get("output_type").toString();
                        if(contentType.equalsIgnoreCase("html")){
                            headers.setContentType(MediaType.TEXT_HTML);
                        }else if(contentType.equalsIgnoreCase("xml")){
                            headers.setContentType(MediaType.TEXT_XML);
                        }else if(contentType.equalsIgnoreCase("file")){
                            String file = (String)in.get("file");
                            return download(file,download);
                        }
                        if(download){
                            headers.set("Content-Disposition", "attachment; filename=" + "data." + contentType);
                        }
                    }
                    return new ResponseEntity<>(in.get("data"),headers, HttpStatus.OK);
                }
            }
        }
        Map<String,Object> result = new HashMap<>();
        result.put("result",out);
        return new ResponseEntity<>(result,headers, HttpStatus.OK);
    }
    ResponseEntity<Object> downloadFileByUrl(String url,Boolean download)  {
        try{
            FileDownloader fd = new FileDownloader() {
                @Override
                public ResponseEntity<Object> download(InputStream is, String contentType, String filename, int contentLength, String contentDisposition) {

                    String disposition = "";
                    if(download){
                        if(contentDisposition == null || contentDisposition.equalsIgnoreCase("")){
                            disposition =  "attachment; filename=" + filename;
                        }else{
                            disposition = contentDisposition;
                        }
                    }

                    return  ResponseEntity.ok()
                            .contentLength(contentLength)
                            .contentType(MediaType.valueOf(contentType))
                            .header("Content-Disposition", disposition)
                            .body(new InputStreamResource(is));
                }
            };
            return fd.downloadFile(url);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    ResponseEntity<Object> downloadFile(String file,Boolean download)   {
        try{
            File asset = assetService.getFolder(file);
            String disposition = "";
            if(download) {
                disposition = "attachment; filename=" + asset.getName();
            }
            MediaType mediaType;
            String fileType = Files.probeContentType(asset.toPath());
            mediaType = MediaType.parseMediaType(fileType);
            FileInputStream fs = new FileInputStream(asset);
            return ResponseEntity.ok()
                    .contentLength(asset.length())
                    .contentType(mediaType)
                    .header("Content-Disposition", disposition)
                    .body(new InputStreamResource(fs));
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    ResponseEntity<Object> download(String file,Boolean download){
        if(file.contains("http")){
            return downloadFileByUrl(file,download);
        }else{
            return downloadFile(file,download);
        }
    }
}
