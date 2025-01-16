package com.bluntsoftware.ludwig.conduit.activities.files;


import com.bluntsoftware.ludwig.conduit.impl.ActivityImpl;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.controller.AssetController;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bluntsoftware.ludwig.conduit.schema.PropertyFormat.FOLDER_CHOOSER;

/**
 * Created by Alex Mcknight on 1/3/2017.
 *
 */
@Service
public class CopyFileActivity extends ActivityImpl {

    private final AssetController assetService;

    public CopyFileActivity(AssetController assetService, ActivityConfigRepository activityConfigRepository) {
        super(activityConfigRepository);
        this.assetService = assetService;
    }

    @Override
    public JsonSchema getSchema() {
        JsonSchema schema = JsonSchema.builder().title("Copy File").build();
        schema.addString("fileInputPath","/working/uploads/somefile.txt");
        schema.addString("outputFolder","/",FOLDER_CHOOSER);
        schema.addString("filename","somefile.txt");

        List<String> method = new ArrayList<>();
        method.add("MoveFile");
        method.add("CopyFile");
        schema.addEnum("Method","copyMethod",method,"CopyFile");

        List<String> overwrite = new ArrayList<>();
        overwrite.add("true");
        overwrite.add("false");
        schema.addEnum("Overwrite","overwrite",overwrite,"false");

        return schema;
    }

    @Override
    public Map<String, Object> run(Map<String, Object> input)throws Exception {
        File fromfile = assetService.getFolder(input.get("fileInputPath").toString());
        File tofile = new File(assetService.getFolder(input.get("outputFolder").toString()),input.get("filename").toString());
        FileUtils.copyFile(fromfile,tofile);
        String folder =  input.get("outputFolder").toString();
        String filename = tofile.getName();
        Map<String,Object> fileInfo = new HashMap<>();
        fileInfo.put("filename",filename);
        fileInfo.put("folder",folder);
        if(folder.charAt(folder.length()- 1) == '/'){
            fileInfo.put("filepath",folder + filename);
        }else{
            fileInfo.put("filepath",folder + "/" + filename);
        }
        return fileInfo;
    }

    @Override
    public String getIcon() {
        return "fa-file-o";
    }

}
