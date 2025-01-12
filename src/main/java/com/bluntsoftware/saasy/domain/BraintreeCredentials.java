package com.bluntsoftware.saasy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BraintreeCredentials {
    private String env;
    private String owner;
    private String merchantId;
    private String publicKey;
    private String privateKey;
}
