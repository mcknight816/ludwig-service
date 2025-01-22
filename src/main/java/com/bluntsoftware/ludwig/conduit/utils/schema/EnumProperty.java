package com.bluntsoftware.ludwig.conduit.utils.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Created by Alex Mcknight on 1/26/2017.
 *
 */

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EnumProperty extends StringProperty {
    @JsonProperty("enum")
    private String[] enumeration;
    @JsonIgnore
    public void set(List<String> enumeration){
        setEnumeration(enumeration.toArray(new String[0]));
    }
}
