package com.bluntsoftware.saasy.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodDto {
    String imageUrl;
    boolean isDefault;
    String token;
    private Map<String, Object> methodInfo;
    @JsonAnySetter
    public void addOtherInfo(String key, Object value) {
        if (this.methodInfo == null) {
            this.methodInfo = new HashMap<>();
        }
        this.methodInfo.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getMethodInfo() {
        return methodInfo;
    }
}
