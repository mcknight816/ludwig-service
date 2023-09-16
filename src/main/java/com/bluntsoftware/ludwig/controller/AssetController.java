package com.bluntsoftware.ludwig.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by Alex Mcknight on 4/8/2017.
 *
 */
@Controller("AssetService")
@RequestMapping(value = "/assets")
public class AssetController {


    //String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    @RequestMapping(value = "/**",method = { RequestMethod.GET})
    @ResponseBody
    Object list(HttpServletRequest request) throws IOException {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String restOfTheUrl = path.replace("/assets","");
        restOfTheUrl = restOfTheUrl.replace("//","/");
        File asset = new File( getAssetFolder(),restOfTheUrl);
        if(!asset.exists()){
                 return "";
        }
            if(asset.isDirectory()){
                if(!restOfTheUrl.endsWith("/")){
                    restOfTheUrl += "/";
                }
                File[] files = asset.listFiles();
                List<HashMap<String, Object>> ret = new ArrayList<>();
                if(files != null){
                    for(File file:files){
                        HashMap<String, Object> folderInfo = new HashMap<>();
                        folderInfo.put("name",file.getName());
                        folderInfo.put("path",restOfTheUrl + file.getName());
                        folderInfo.put("isDirectory",file.isDirectory());
                        ret.add(folderInfo);
                    }
                }
                return ret;
            }else{
                MediaType mediaType =  MediaType.IMAGE_PNG;
                try{
                    String fileType = Files.probeContentType(asset.toPath());
                    mediaType = MediaType.parseMediaType(fileType);
                }catch(Exception e){
                    e.printStackTrace();
                }
                FileInputStream fs = new FileInputStream(asset);
                return ResponseEntity.ok()
                        .contentLength(asset.length())
                        .contentType(mediaType)
                        .body(new InputStreamResource(fs));
            }


    }

    @RequestMapping(
            value = "/get",
            method = { RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    ResponseEntity<InputStreamResource> get(@RequestParam("file") String file, RedirectAttributes redirectAttrs ) throws Exception {
        File asset = new File( getAssetFolder(),file);
        MediaType mediaType =  MediaType.IMAGE_PNG;
        try{
            String fileType = Files.probeContentType(asset.toPath());
            mediaType = MediaType.parseMediaType(fileType);
        }catch(Exception e){
           // e.printStackTrace();
        }
        FileInputStream fs = new FileInputStream(asset);
        return ResponseEntity.ok()
                .contentLength(asset.length())
                .contentType(mediaType)
                .body(new InputStreamResource(fs));
    }

    @RequestMapping(
            value = "/remove",
            method = { RequestMethod.POST})
    @ResponseBody
    Map remove(@RequestParam("file") String file ) throws Exception {
        File asset = new File( getAssetFolder(),file);

         if(asset.isDirectory()){
             FileUtils.deleteDirectory(asset);
         }else{
             asset.delete();
         }
        Map ret = new HashMap();
        ret.put("status","success");
        ret.put("deletefile",asset.getAbsolutePath());
        return ret;
    }


    //@RequestParam("file") MultipartFile[] files
    @RequestMapping(
            value = "/multi_upload",
            method = { RequestMethod.POST},
            produces = "application/json;charset=utf8")
    @ResponseBody
    Map multi_upload(@RequestParam("file") MultipartFile[] files, @RequestParam("dir") String dir, RedirectAttributes redirectAttributes) throws IOException {
        File destinationFolder = getFolder(dir);
        for (MultipartFile file : files) {
            file.transferTo(new File(destinationFolder, file.getOriginalFilename()));
        }
        Map<String, String> ret = new HashMap<>();
        ret.put("status","success");
        return ret;

    }
    @RequestMapping(
            value = "/upload",
            method = { RequestMethod.POST})
    @ResponseBody
    Map upload(@RequestParam("file") MultipartFile file, @RequestParam("dir") String dir, RedirectAttributes redirectAttributes) throws IOException {
        File destinationFolder = getFolder(dir);
        file.transferTo(new File(destinationFolder,file.getOriginalFilename()));
        Map<String, String> ret = new HashMap<>();
        ret.put("status","success");
        return ret;
    }

    @RequestMapping(
            value = "/mkdirs",
            method = { RequestMethod.POST},
            produces = "application/json",
            consumes = "application/json")
    @ResponseBody
    File newFolder(@RequestBody Map<String, Object> json) throws IOException {

        if(json != null && json.containsKey("dir")){
            File folder = getFolder(json.get("dir").toString());
            if(!folder.exists()){
                folder.mkdirs();
            }
            return folder;
        }
      return null;

    }

    @RequestMapping(value = {"/filetree"}, method = { RequestMethod.GET, RequestMethod.POST}, produces = "text/html;charset=UTF-8")
    void list(@RequestParam(value = "dir", required = false) String dir, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream out = response.getOutputStream();
        File assetFolder =  getFolder(dir);
            if (assetFolder != null && assetFolder.exists()) {
                String[] files = assetFolder.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.charAt(0) != '.';
                    }
                });

                Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
                out.print("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
                // All dirs
                for (String file : files) {
                    if (new File(assetFolder, file).isDirectory()) {
                        out.print("<li class=\"directory collapsed\"><a   rel=\"" + dir + file + "/\">"
                                + file + "</a></li>");
                    }
                }
                // All files
                for (String file : files) {
                    if (!new File(assetFolder, file).isDirectory()) {
                        int dotIndex = file.lastIndexOf('.');
                        String ext = dotIndex > 0 ? file.substring(dotIndex + 1) : "";
                        out.print("<li class=\"file ext_" + ext + "\"><a   rel=\"" + dir + file + "\">"
                                + file + "</a></li>");
                    }
                }
                out.print("</ul>");
            }else{
                out.print("<ul class=\"jqueryFileTree\" style=\"display: none;\">");
                out.print("<li class=\"directory collapsed\"><a rel=\"/\">" + "assets</a></li>");
                out.print("</ul>");
            }

    }
    @RequestMapping(value = {"/folders"}, method = { RequestMethod.GET, RequestMethod.POST}, produces = "text/html;charset=UTF-8")
    void folders(@RequestParam(value = "dir", required = false) String dir, HttpServletResponse response) throws Exception {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        ServletOutputStream out = response.getOutputStream();

        File assetFolder =  getFolder(dir);

        if (assetFolder != null && assetFolder.exists()) {
            String[] files = assetFolder.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.charAt(0) != '.';
                }
            });
            Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
            out.print("<ul class=\"jqueryFileTree\" style=\"display: none;\">");

            // All dirs
            for (String file : files) {
                if (new File(assetFolder, file).isDirectory()) {
                    out.print("<li class=\"directory collapsed\"><a  rel=\"" + dir + file + "/\">"
                            + file + "</a></li>");
                }
            }
            out.print("</ul>");
        }
    }

    public File getAssetFolder() {
        File userAppFolder = new File(System.getProperty("java.io.tmpdir"),"ludwig");
        File assetFolder = new File(userAppFolder,"assets");
        assetFolder.mkdirs();
        return assetFolder;
    }

    public File getFolder(String dir) throws IOException {
        File assetFolder =  getAssetFolder();
        if (dir != null && !dir.equalsIgnoreCase("")) {
            if (dir.charAt(dir.length()-1) == '\\') {
                dir = dir.substring(0, dir.length()-1) + "/";
            } else if (dir.charAt(dir.length()-1) != '/') {
                dir += "/";
            }
            dir = java.net.URLDecoder.decode(dir, "UTF-8");

        }else{
            return null;
        }
        return new File(assetFolder,dir);
    }

}
