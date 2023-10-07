package com.bluntsoftware.ludwig.utils;

import java.util.HashMap;
import java.util.Map;

public enum FieldType {
  NOTFOUND(null,null,null),
  ENTITY( "Entity", "object","any"),
  STRING( "String", "string","string"),
  BOOL( "Boolean", "boolean","boolean"),
  INT( "Integer", "integer","number"),
  LIST( "List", "array","array"),
  BIG_DECIMAL( "BigDecimal", "number","number"),
  DATE( "Date", "date","string"),
  INSTANT( "Instant", "date-time","string"),
  LONG( "Long", "long","number"),
  DOUBLE( "Double", "double","number");

  private static final Map<String, FieldType> SCHEMA_TYPE_MAP = new HashMap<>();
  private static final Map<String, FieldType> JAVA_TYPE_MAP = new HashMap<>();
  private static final Map<String, FieldType> TYPESCRIPT_TYPE_MAP = new HashMap<>();
  static {
    for (FieldType type: FieldType.values()){
      if(type != FieldType.NOTFOUND){
        SCHEMA_TYPE_MAP.put(type.schemaType.toLowerCase(), type);
        JAVA_TYPE_MAP.put(type.javaType.toLowerCase(),type);
        TYPESCRIPT_TYPE_MAP.put(type.tsType.toLowerCase(),type);
      }
    }
  }

  private final String schemaType;
  private final String javaType;
  private final String tsType;

  FieldType(String javaType, String schemaType, String typeScriptType) {
    this.javaType = javaType;
    this.schemaType = schemaType;
    this.tsType = typeScriptType;
  }

  public static FieldType bySchemaType(String string){
    return SCHEMA_TYPE_MAP.getOrDefault(string.toLowerCase(), FieldType.NOTFOUND);
  }

  public static FieldType byJavaType(String javaType){
    return  JAVA_TYPE_MAP.getOrDefault(javaType.toLowerCase(), FieldType.NOTFOUND);
  }

  public static FieldType byTypeScriptType(String tsType){
    return  TYPESCRIPT_TYPE_MAP.getOrDefault(tsType.toLowerCase(), FieldType.NOTFOUND);
  }

  public String getSchemaType(){ return this.schemaType;}
  public String getJavaType(){return this.javaType;}
  public String getTypescriptType(){return this.tsType;}
}
