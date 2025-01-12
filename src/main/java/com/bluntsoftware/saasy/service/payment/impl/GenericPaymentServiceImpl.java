package com.bluntsoftware.saasy.service.payment.impl;

import com.bluntsoftware.saasy.domain.*;
import com.bluntsoftware.saasy.dto.PaymentMethodDto;
import com.bluntsoftware.saasy.dto.SubscriptionDto;
import com.bluntsoftware.saasy.dto.TransactionDto;
import com.bluntsoftware.saasy.repository.AppRepo;
import com.bluntsoftware.saasy.repository.SubscriptionRepository;
import com.bluntsoftware.saasy.repository.TenantRepo;
import com.bluntsoftware.saasy.repository.TenantUserRepo;
import com.bluntsoftware.saasy.service.TenantUserInfoService;
import com.bluntsoftware.saasy.service.payment.PaymentService;
import java.util.*;

public class GenericPaymentServiceImpl implements PaymentService {

    private final TenantRepo tenantRepo;
    private final TenantUserRepo tenantUserRepo;
    private final AppRepo appRepo;
    private final TenantUserInfoService tenantUserInfoService;
    private final SubscriptionRepository subscriptionRepo;

    public GenericPaymentServiceImpl(TenantRepo tenantRepo, TenantUserRepo tenantUserRepo, AppRepo appRepo, TenantUserInfoService tenantUserInfoService, SubscriptionRepository subscriptionRepo) {
        this.tenantRepo = tenantRepo;
        this.tenantUserRepo = tenantUserRepo;
        this.appRepo = appRepo;
        this.tenantUserInfoService = tenantUserInfoService;
        this.subscriptionRepo = subscriptionRepo;
    }

    @Override
    public String generateClientToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public SubscriptionDto createSubscription(SaasySubscription sr) {

        App app = this.appRepo.findById(sr.getAppId()).block();

        if(sr.getPlanId() == null || sr.getPlanId().isEmpty()) {
           sr.setPlanId(app.getPlans().get(0).getPlanId());
        }

        Tenant tenant = Tenant.builder()
                .customer(User.builder()
                        .email(sr.getEmail())
                        .name(sr.getFirstName() + " " + sr.getLastName())
                        .username(sr.getEmail())
                        .active(false)
                        .build())
                .owner(app.getOwner())
                .app(IdName.builder().id(app.getId()).name(app.getName()).build())
                .displayName(sr.getCompanyName())
                .planId(sr.getPlanId())
                .build();

        tenant = this.tenantRepo.save(tenant).block();

        TenantUser tenantUser =TenantUser.builder()
                .tenantId(tenant.getId())
                .roles(Arrays.asList("ADMIN"))
                .isCustomer(true)
                .name(sr.getFirstName() + " " + sr.getLastName())
                .active(true)
                .email(sr.getEmail())
                .build();
        this.tenantUserRepo.save(tenantUser).subscribe();

        SaasySubscription subscription = subscriptionRepo.save(sr).block();
        if(subscription != null && subscription.getId() != null) {
            subscription.setStatus("ACTIVE");
            tenant.setSubscriptionId(subscription.getId());
            this.tenantRepo.save(tenant).subscribe();
        }

        return SubscriptionDto.builder()
                .id(subscription.getId())
                .status(subscription.getStatus())
                .planId(subscription.getPlanId())
                .build();
    }

    @Override
    public SubscriptionDto updateSubscription(String tenantId, SaasySubscription subscription) {

        subscription = subscriptionRepo.save(subscription).block();

        return SubscriptionDto.builder()
                .id(subscription.getId())
                .status(subscription.getStatus())
                .planId(subscription.getPlanId())
                .build();
    }

    @Override
    public SubscriptionDto getSubscriptionByTenantId(String tenantId) {
        return null;
    }

    @Override
    public SubscriptionDto cancelSubscription(String tenantId) {
        return null;
    }

    @Override
    public List<PaymentMethodDto> setDefaultPaymentMethod(String tenantId, String paymentMethodToken) {
        return new ArrayList<>();
    }

    @Override
    public List<PaymentMethodDto> getPaymentMethods(String tenantId) {
        return new ArrayList<>();
    }

    @Override
    public List<PaymentMethodDto> removePaymentMethod(String tenantId, String paymentMethodToken) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getSubscriptionStatus(String tenantId) {
        return new HashMap<>();
    }

    @Override
    public List<TransactionDto> getTransactionHistory(String tenantId) {
        return new ArrayList<>();
    }

}
