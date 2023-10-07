package com.bluntsoftware.ludwig.utils.converter;



import com.bluntsoftware.ludwig.domain.Model;

import java.util.Map;

public interface Converter {
  Model convert(Map<String,Object> data);
  Map<String,Object> convert(Model model);
}
