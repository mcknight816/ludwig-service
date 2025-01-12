package com.bluntsoftware.saasy.controller;

import com.bluntsoftware.saasy.domain.SaasySubscription;
import com.bluntsoftware.saasy.dto.PaymentMethodDto;
import com.bluntsoftware.saasy.dto.SubscriptionDto;
import com.bluntsoftware.saasy.dto.TransactionDto;
import com.bluntsoftware.saasy.service.payment.PaymentService;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/saasy/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/token")
    public Map<String,Object> getToken(){
        Map<String,Object> token = new HashMap<>();
        token.put("token",paymentService.generateClientToken());
        return token;
    }

    @PostMapping(value="/subscribe",consumes= MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> subscribe(@RequestBody  Map<String,Object> subscriptionRequest){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SaasySubscription sub  = mapper.convertValue(subscriptionRequest,SaasySubscription.class);
        this.paymentService.createSubscription(sub);
       return Mono.empty();
    }

    @DeleteMapping("/cancel/{tenantId}")
    public SubscriptionDto cancelSubscription(@PathVariable("tenantId") String tenantId){
        return this.paymentService.cancelSubscription(tenantId);
    }

    @PostMapping(value="/update/{tenantId}",consumes= MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public SubscriptionDto updateSubscriptionPaymentMethod(@PathVariable("tenantId") String tenantId,@RequestBody  Map<String,Object> subscriptionRequest){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SaasySubscription sub  = mapper.convertValue(subscriptionRequest,SaasySubscription.class);
        return this.paymentService.updateSubscription(tenantId,sub);
    }

    @GetMapping(value="/subscription/{tenantId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public SubscriptionDto getSubscription(@PathVariable("tenantId") String tenantId ){
        return this.paymentService.getSubscriptionByTenantId(tenantId);
    }

    @GetMapping(value="/subscription/status/{tenantId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> subscriptionStatus(@PathVariable("tenantId") String tenantId ){
        return this.paymentService.getSubscriptionStatus(tenantId);
    }

    @GetMapping("/cards/{tenantId}")
    public List<PaymentMethodDto> getPaymentMethods(@PathVariable("tenantId") String tenantId){
          return this.paymentService.getPaymentMethods(tenantId);
    }

    @GetMapping("/default/{tenantId}/{token}")
    public List<PaymentMethodDto> defaultPaymentMethods(@PathVariable("tenantId") String tenantId,@PathVariable("token") String token){
        return this.paymentService.setDefaultPaymentMethod(tenantId,token);
    }

    @DeleteMapping("/cards/{tenantId}/{token}")
    public List<PaymentMethodDto> getPaymentMethods(@PathVariable("tenantId") String tenantId,@PathVariable("token") String token){
        return this.paymentService.removePaymentMethod(tenantId,token);
    }
    @GetMapping("/transaction/history/{tenantId}")
    public List<TransactionDto> getTranasctionHistory(@PathVariable("tenantId") String tenantId){
        return this.paymentService.getTransactionHistory(tenantId);
    }
}
