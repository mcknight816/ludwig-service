package com.bluntsoftware.saasy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Calendar;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    Calendar        createdAt;
    String          processorResponseText;
    BigDecimal      amount;
    PaymentMethodDto creditCard;
}
