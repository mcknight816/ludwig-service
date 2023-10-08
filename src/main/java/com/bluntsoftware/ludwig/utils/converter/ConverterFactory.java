package com.bluntsoftware.ludwig.utils.converter;



import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.utils.converter.impl.JsonToModel;
import com.bluntsoftware.ludwig.utils.converter.impl.JsonSchemaToModel;
import com.bluntsoftware.ludwig.utils.converter.impl.SwaggerToModel;

import java.util.List;
import java.util.Map;

public final class ConverterFactory {
  private ConverterFactory(){}
  public static List<Entity> buildEntities(String name, Map<String, Object> json) {
    Converter converter = new JsonToModel(name);
    if(json.containsKey("openapi") || json.containsKey("swagger")){
      converter = new SwaggerToModel();
    }
    if(json.containsKey("$schema")){
      converter = new JsonSchemaToModel(name);
    }
    return converter.convert(json).getEntities();
  }

}
