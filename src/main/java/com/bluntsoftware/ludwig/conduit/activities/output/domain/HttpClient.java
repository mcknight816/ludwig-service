package com.bluntsoftware.ludwig.conduit.activities.output.domain;

import com.bluntsoftware.ludwig.conduit.utils.schema.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpClient implements EntitySchema {
    String name;
    String url;
    String username;
    String password;
    String payload;
    String file;
    String contentType;

    @Override
    public JsonSchema getJsonSchema() {
        JsonSchema schema = JsonSchema.builder().title(this.getName()).build();
        List<String> type = new ArrayList<String>();
        type.add("post");
        type.add("get");
        type.add("delete");
        type.add("put");
        type.add("upload");

        schema.addEnum("type",type ,"json");
        schema.addString("url");
        schema.addString("username");
        schema.addString("password", StringProperty.builder().defaultValue("").format(PropertyFormat.PASSWORD).build());
        schema.addString("payload",StringProperty.builder().defaultValue("{}").type(PropertyType.HIDDEN.getValue()).build());
        schema.addString("file",StringProperty.builder().defaultValue("http://someserver/someFile.jpg").format(PropertyFormat.IMAGE_CHOOSER).build());
        schema.addString("content-type");
        return schema;
    }
}
