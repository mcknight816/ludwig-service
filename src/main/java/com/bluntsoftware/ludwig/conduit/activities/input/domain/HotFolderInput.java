package com.bluntsoftware.ludwig.conduit.activities.input.domain;


import com.bluntsoftware.ludwig.conduit.utils.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.utils.schema.PropertyFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class HotFolderInput extends TimerInput {
    String folderLocation;
    String include;

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema = super.getJsonSchema();
        schema.setTitle("Hot Folder Properties");
        schema.addString("folderLocation","/hot", PropertyFormat.FOLDER_CHOOSER);
        schema.addString("include","*.*",null);
        return schema;
    }
}
