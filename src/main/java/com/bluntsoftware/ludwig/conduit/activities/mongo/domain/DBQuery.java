package com.bluntsoftware.ludwig.conduit.activities.mongo.domain;

import com.bluntsoftware.ludwig.conduit.schema.EnumProperty;
import com.bluntsoftware.ludwig.conduit.schema.JsonSchema;
import com.bluntsoftware.ludwig.conduit.schema.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DBQuery {
    String filter = "";
    String page = "1";
    String rows = "20";
    String sord = "ASC";
    String sidx = "_id";
    public static JsonSchema getSchema() {
        String[] sortOrder = {"ASC", "DESC"};
        JsonSchema schema = JsonSchema.builder().title("Query").build();
        schema.getProperties().put("filter", StringProperty.builder().title("Filter").format("json").defaultValue("").build());
        schema.getProperties().put("page", StringProperty.builder().title("Page").defaultValue("1").build());
        schema.getProperties().put("rows", StringProperty.builder().title("Rows").defaultValue("20").build());
        schema.getProperties().put("sord", EnumProperty.builder().title("Sort Order").enumeration(sortOrder).defaultValue("ASC").build());
        schema.getProperties().put("sidx", StringProperty.builder().title("Sort Index").defaultValue("_id").build());
        return schema;
    }
}
