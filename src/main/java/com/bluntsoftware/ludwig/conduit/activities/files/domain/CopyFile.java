package com.bluntsoftware.ludwig.conduit.activities.files.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.EntitySchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat.FOLDER_CHOOSER;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyFile implements EntitySchema {
    String fileInputPath;
    String outputFolder;
    String filename;
    String copyMethod;
    String overwrite;

    @Override
    public JsonSchema getJsonSchema() {
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
}
