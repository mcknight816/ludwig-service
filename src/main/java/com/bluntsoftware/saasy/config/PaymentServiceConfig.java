package com.bluntsoftware.saasy.config;

import com.bluntsoftware.saasy.repository.AppRepo;
import com.bluntsoftware.saasy.repository.SubscriptionRepository;
import com.bluntsoftware.saasy.repository.TenantRepo;
import com.bluntsoftware.saasy.repository.TenantUserRepo;
import com.bluntsoftware.saasy.service.TenantUserInfoService;
import com.bluntsoftware.saasy.service.payment.PaymentService;
import com.bluntsoftware.saasy.service.payment.impl.GenericPaymentServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentServiceConfig {
    private final TenantRepo tenantRepo;
    private final TenantUserRepo tenantUserRepo;
    private final AppRepo appRepo;
    private final TenantUserInfoService tenantUserInfoService;
    private final SubscriptionRepository subscriptionRepo;

    public PaymentServiceConfig(TenantRepo tenantRepo, TenantUserRepo tenantUserRepo, AppRepo appRepo, TenantUserInfoService tenantUserInfoService, SubscriptionRepository subscriptionRepo) {
        this.tenantRepo = tenantRepo;
        this.tenantUserRepo = tenantUserRepo;
        this.appRepo = appRepo;
        this.tenantUserInfoService = tenantUserInfoService;
        this.subscriptionRepo = subscriptionRepo;
    }

    @Bean
    public PaymentService paymentService() {
        return new GenericPaymentServiceImpl(tenantRepo, tenantUserRepo, appRepo, tenantUserInfoService, subscriptionRepo);
    }

}
