package com.bluntsoftware.saasy.domain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class TenantUser {
    @Id
    private String id;
    private String tenantId;
    private String email;
    private String name;
    private Boolean active;
    private Boolean isCustomer;
    private String avatar;
    private List<String> roles;
    private Map<String, Object> otherInfo;

    @JsonAnySetter
    public void addOtherInfo(String key, Object value) {
        if (this.otherInfo == null) {
            this.otherInfo = new HashMap<>();
        }
        this.otherInfo.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getOtherInfo() {
        return otherInfo;
    }
}
