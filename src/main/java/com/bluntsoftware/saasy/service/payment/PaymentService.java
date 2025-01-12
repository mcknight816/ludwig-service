package com.bluntsoftware.saasy.service.payment;

import com.bluntsoftware.saasy.domain.SaasySubscription;
import com.bluntsoftware.saasy.dto.PaymentMethodDto;
import com.bluntsoftware.saasy.dto.SubscriptionDto;
import com.bluntsoftware.saasy.dto.TransactionDto;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    String                  generateClientToken();
    SubscriptionDto  createSubscription(SaasySubscription subscription);
    SubscriptionDto  updateSubscription(String tenantId,SaasySubscription subscription);
    SubscriptionDto  getSubscriptionByTenantId(String tenantId);
    SubscriptionDto    cancelSubscription(String tenantId);
    List<PaymentMethodDto>  setDefaultPaymentMethod(String tenantId, String paymentMethodToken);
    List<PaymentMethodDto>  getPaymentMethods(String tenantId);
    List<PaymentMethodDto>  removePaymentMethod(String tenantId,String paymentMethodToken);
    Map<String,Object>      getSubscriptionStatus(String tenantId);
    List<TransactionDto>    getTransactionHistory(String tenantId);
}
