package com.bluntsoftware.saasy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaasySubscription {
    @Id
    private String id;
    private String appId;
    private String firstName;
    private String lastName;
    private String email;
    private String companyName;
    private String nonce;
    private BigDecimal chargeAmount;
    private String status;
    private String planId;
}
