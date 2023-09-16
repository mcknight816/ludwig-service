package com.bluntsoftware.ludwig.conduit.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by Alex Mcknight on 1/26/2017.
 *
 */
class EnumProperty extends StringProperty{
    private String[] enumeration;
    @JsonProperty("enum")
    public String[] getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(String[] enumeration) {
        this.enumeration = enumeration;
    }
    @JsonIgnore
    public void set(List<String> enumeration){
        setEnumeration(enumeration.toArray(new String[0]));
    }

}
