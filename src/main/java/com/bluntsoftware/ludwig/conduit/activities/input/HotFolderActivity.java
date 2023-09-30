package com.bluntsoftware.ludwig.conduit.activities.input;


import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.controller.AssetController;
import com.bluntsoftware.ludwig.repository.FlowConfigRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
/**
 * Created by Alex Mcknight on 1/12/2017.
 */
@Service
public class HotFolderActivity extends TimerActivity {

    private final AssetController assetService;

    public HotFolderActivity(AssetController assetService, FlowConfigRepository flowConfigRepository) {
        super(flowConfigRepository);
        this.assetService = assetService;
    }

    @Override
    public JsonSchema getSchema() {

        JsonSchema schema =  super.getSchema();
        schema.setTitle("Hot Folder Properties");
        schema.addString("folderLocation","/hot","folderChooser");
        schema.addString("include","*.*",null);
        return schema;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input) throws Exception{
        Map<String, Object> out =  super.run(input);
        File hotFolder = assetService.getFolder(input.get("folderLocation").toString());
        File doneFolder = new File(hotFolder, "done");
        if(!doneFolder.exists()){
            doneFolder.mkdirs();
        }
        List<Path> files = listFiles(input.get("folderLocation").toString(),input.get("include").toString());
        File foundFile = null;
        for(Path file:files){
            foundFile = file.toFile();
            if(foundFile.isFile()){
                try {
                    Files.copy(file, Paths.get(doneFolder.getAbsolutePath()).resolve(foundFile.getName()), REPLACE_EXISTING);
                    foundFile.delete();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(foundFile != null){
            Map<String,Object> fileInfo = new HashMap<>();
            String originalFilename = foundFile.getName();
            String extension = "";
            int i = originalFilename.lastIndexOf('.');
            if (i > 0) {
                extension = originalFilename.substring(i);
            }
            fileInfo.put("originalFilename",foundFile.getName());
            fileInfo.put("name",foundFile.getName());
            fileInfo.put("size",foundFile.getTotalSpace());
            fileInfo.put("extension",extension);
            fileInfo.put("path",doneFolder.getAbsolutePath());
            out.put("fileInfo",fileInfo);
        }

        return out;
    }

    @Override
    public Map<String, Object> getOutput() {
        Map<String, Object> out =  super.getOutput();
        Map<String,Object> fileInfo = new HashMap<>();
        fileInfo.put("originalFilename","");
        fileInfo.put("name","");
        fileInfo.put("size",0);
        fileInfo.put("path","");
        fileInfo.put("extension","");
        out.put("fileInfo",fileInfo);
        return super.getOutput();
    }
    private List<Path> listFiles(String folder) throws IOException {
        return listFiles(folder,"*.*");
    }
    private List<Path> listFiles(String folder, String filter) throws IOException {
        File hotFolder = assetService.getFolder(folder);
        return Files.list(Paths.get(hotFolder.getAbsolutePath())).collect(Collectors.toList());
    }
    @Override
    public Boolean shouldRun(Map<String,Object> input) {
        try {
            List<Path> files = listFiles(input.get("folderLocation").toString(),input.get("include").toString());
            for(Path file:files){
                File foundFile = file.toFile();
                if(foundFile.isFile() && foundFile.getAbsolutePath().contains(".")){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getIcon() {
        return "fa-folder-open-o";
    }
}
