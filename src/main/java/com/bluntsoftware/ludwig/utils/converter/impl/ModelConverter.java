package com.bluntsoftware.ludwig.utils.converter.impl;

import com.bluntsoftware.ludwig.domain.Model;
import com.bluntsoftware.ludwig.utils.converter.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ModelConverter implements Converter {

  protected final ObjectMapper mapper;

  protected ModelConverter() {
    mapper = new ObjectMapper();
  }

  @Override
  public Model convert(Map<String, Object> data) {
    return mapper.convertValue(data,Model.class);
  }

  @Override
  public Map<String, Object> convert(Model model) {
    return convertToMap(model);
  }

  public String toJson(Model model) throws JsonProcessingException {
    return mapper.writeValueAsString(convert(model));
  }

  public Model fromJson(String json) throws JsonProcessingException {
    return convert(convertToMap(mapper.readValue(json,Map.class)));
  }

  protected Map<String,Object> convertToMap(Object map){
    return mapper.convertValue(map,Map.class);
  }

  public Model fromFile(File file) throws IOException {
    return  convert(convertToMap(mapper.readValue(file,Map.class)));
  }
}
