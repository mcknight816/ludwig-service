package com.bluntsoftware.ludwig.utils.converter;



import com.bluntsoftware.ludwig.domain.Entity;
import com.bluntsoftware.ludwig.utils.converter.impl.JsonConverter;
import com.bluntsoftware.ludwig.utils.converter.impl.JsonSchemaConverter;
import com.bluntsoftware.ludwig.utils.converter.impl.SwaggerConverter;

import java.util.List;
import java.util.Map;

public final class ConverterFactory {
  private ConverterFactory(){}
  public static List<Entity> buildEntities(String name, Map<String, Object> json) {
    Converter converter = new JsonConverter(name);
    if(json.containsKey("openapi") || json.containsKey("swagger")){
      converter = new SwaggerConverter();
    }
    if(json.containsKey("$schema")){
      converter = new JsonSchemaConverter(name);
    }
    return converter.convert(json).getEntities();
  }

}
