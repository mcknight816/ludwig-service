package com.bluntsoftware.saasy.service.payment.impl;


import com.bluntsoftware.saasy.domain.*;
import com.bluntsoftware.saasy.dto.PaymentMethodDto;
import com.bluntsoftware.saasy.dto.SubscriptionDto;
import com.bluntsoftware.saasy.dto.TransactionDto;
import com.bluntsoftware.saasy.repository.AppRepo;
import com.bluntsoftware.saasy.repository.BraintreePaymentRepository;
import com.bluntsoftware.saasy.repository.TenantRepo;
import com.bluntsoftware.saasy.repository.TenantUserRepo;
import com.bluntsoftware.saasy.service.TenantUserInfoService;
import com.bluntsoftware.saasy.service.payment.PaymentService;
import com.braintreegateway.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.security.access.annotation.Secured;


import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Log
@Secured({"ROLE_TENANT_USER"})
public class BraintreePaymentServiceImpl implements PaymentService {

    private final BraintreePaymentRepository braintreeRepository;
    private final TenantRepo tenantRepo;
    private final TenantUserRepo tenantUserRepo;
    private final AppRepo appRepo;
    private final TenantUserInfoService tenantUserInfoService;

    public BraintreePaymentServiceImpl(BraintreePaymentRepository braintreeRepository, TenantRepo tenantRepo, TenantUserRepo tenantUserRepo, AppRepo appRepo, TenantUserInfoService tenantUserInfoService) {
        this.braintreeRepository = braintreeRepository;
        this.tenantRepo = tenantRepo;
        this.tenantUserRepo = tenantUserRepo;
        this.appRepo = appRepo;
        this.tenantUserInfoService = tenantUserInfoService;
    }

    public String generateClientToken() {
        return braintreeRepository.generateClientToken();
    }

    private Optional<Customer> createCustomer(Tenant tenant, String nonce) {
        if(!this.tenantUserInfoService.isTenant(tenant.getId())){
            throw new RuntimeException("You dont have permission to do this");
        }
        String[] firstLast = tenant.getCustomer().getName().split( " ");

        CustomerRequest customerRequest = new CustomerRequest()
            .firstName(firstLast[0])
            .lastName(firstLast[1])
            .company(tenant.getDisplayName())
            .id(tenant.getId())
            .email(tenant.getCustomer().getEmail());

        if (nonce != null) {
            customerRequest.paymentMethodNonce(nonce);
        }

        Customer customer = null;
        try{
            Result<Customer> customerResult = braintreeRepository.gateway().customer().create(customerRequest);
            customer = customerResult.getTarget();
        }catch(Exception e){
            log.info(e.getMessage());
        }
        return Optional.ofNullable(customer);
    }

    Optional<Subscription> createSubscription(String tenantId,String planId, Customer customer){
        PaymentMethod defaultPaymentMethod = null;
        for(PaymentMethod paymentMethod:customer.getPaymentMethods()){
            if(paymentMethod.isDefault()){
                defaultPaymentMethod = paymentMethod;
            }
        }
        return Optional.ofNullable(createSubscription(tenantId,planId, defaultPaymentMethod));
    }

    private Subscription createSubscription(String tenantId,String planId, PaymentMethod defaultPaymentMethod) {
        if(!this.tenantUserInfoService.isTenant(tenantId)){
            throw new RuntimeException("You dont have permission to do this");
        }
        Subscription subscription = null;
        Integer days = this.calculateDaysForCanceledSubscriptions(tenantId);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE,days+1);

        if(defaultPaymentMethod != null){
            SubscriptionRequest subrequest = new SubscriptionRequest()
                .planId(planId)
                .paymentMethodToken(defaultPaymentMethod.getToken());
            if(days > 0){
                subrequest.firstBillingDate(c);
            }
            try{
                Result<Subscription> subscriptionResult = braintreeRepository.gateway().subscription().create(subrequest);
                subscription = subscriptionResult.getTarget();
            }catch(Exception e){
                log.info(e.getMessage());
            }
        }
        return subscription;
    }

    Optional<Subscription> getSubscriptionById(String subscriptionId){
        Subscription subscription = null;
            try{
                subscription = braintreeRepository.gateway().subscription().find(subscriptionId);
            }catch(Exception e){
                log.info(e.getMessage());
            }
        return Optional.ofNullable(subscription);
    }

    Optional<Customer> getCustomerByTenantId(String tenantId){
        Customer customer = null;
        try{
            customer = braintreeRepository.gateway().customer().find(tenantId);
        }catch(Exception e){
            log.info(e.getMessage());
        }
        return Optional.ofNullable(customer);
    }

    Optional<Customer> createCustomer(String tenantId){
        if(!this.tenantUserInfoService.isTenant(tenantId)){
            throw new RuntimeException("You dont have permission to do this");
        }
        Tenant tenant = this.tenantRepo.findById(tenantId).block();
        return createCustomer(tenant,null);
    }

    @Override
    public List<PaymentMethodDto> getPaymentMethods(String tenantId){
        Customer customer = getCustomerByTenantId(tenantId)
                .orElseGet(()-> createCustomer(tenantId)
                        .orElseThrow(()-> new RuntimeException("Cannot create customer account")));
        return new ArrayList<>(getPaymentMethods(customer));
    }

    private List<PaymentMethod> listPaymentMethods(String tenantId){
        Customer customer = getCustomerByTenantId(tenantId)
                .orElseGet(()-> createCustomer(tenantId)
                        .orElseThrow(()-> new RuntimeException("Cannot create customer account")));
        return new ArrayList<>(customer.getPaymentMethods());
    }

    private List<PaymentMethodDto> getPaymentMethods(Customer customer){
        return customer.getPaymentMethods().stream().map(this::convert)
                .collect(Collectors.toList());
    }

    private PaymentMethodDto convert(PaymentMethod paymentMethod){
        ObjectMapper mapper = new ObjectMapper();
        PaymentMethodDto pmDto =  mapper.convertValue(paymentMethod,PaymentMethodDto.class);

        if(pmDto != null && pmDto.getMethodInfo().containsKey("subscriptions")){
            pmDto.getMethodInfo().remove("subscriptions");
        }

        if(pmDto != null && pmDto.getMethodInfo().containsKey("verification")){
            pmDto.getMethodInfo().remove("verification");
        }
        return pmDto;
    }

    public List<PaymentMethodDto> setDefaultPaymentMethod(String tenantId,String paymentMethodToken){
        if(!this.tenantUserInfoService.isTenant(tenantId)){
            throw new RuntimeException("You dont have permission to do this");
        }
        CustomerRequest cr = new CustomerRequest().defaultPaymentMethodToken(paymentMethodToken);
        braintreeRepository.gateway().customer().update(tenantId,cr);
        return getPaymentMethods(tenantId);
    }

    PaymentMethod createPaymentMethod(String tenantId, String nonce){
        if(!this.tenantUserInfoService.isTenant(tenantId)){
            throw new RuntimeException("You dont have permission to do this");
        }
        PaymentMethodRequest pmr = new PaymentMethodRequest();
        pmr.paymentMethodNonce(nonce);
        pmr.customerId(tenantId);
        Result<? extends PaymentMethod> pm = braintreeRepository.gateway().paymentMethod().create(pmr);
        return pm.getTarget();
    }
    public List<PaymentMethodDto> removePaymentMethod(String tenantId,String paymentMethodToken){
        if(!this.tenantUserInfoService.isTenant(tenantId)){
            throw new RuntimeException("You dont have permission to do this");
        }
        braintreeRepository.gateway().paymentMethod().delete(paymentMethodToken);
        Customer customer = getCustomerByTenantId(tenantId)
                .orElseGet(()-> createCustomer(tenantId)
                        .orElseThrow(()-> new RuntimeException("Cannot create customer account")));
        return new ArrayList<>(getPaymentMethods(customer));
    };

    private Result<Subscription> cancelSubscription(SubscriptionDto subscription){

        Result<Subscription> subscriptionResult = null;
        if(subscription != null){
            subscriptionResult = braintreeRepository.gateway().subscription().cancel(subscription.getId());
        }
        return subscriptionResult;
    }

    public SubscriptionDto cancelSubscription(String tenantId){
        if(!this.tenantUserInfoService.isTenant(tenantId)){
            throw new RuntimeException("You dont have permission to do this");
        }
        SubscriptionDto subscription = getSubscriptionByTenantId(tenantId);
        AtomicReference<Result<Subscription>> result = new AtomicReference<>();
        if(subscription != null ){
            result.set(cancelSubscription(subscription));
        }
        return SubscriptionDto.builder().build();
    }

    boolean hasActiveSubscription(String subscriptionId){
        Optional<Subscription> subscription = getSubscriptionById(subscriptionId);
        if(subscription.isPresent()){
            Subscription.Status status = subscription.get().getStatus();
            return status.toString().equalsIgnoreCase("active");
        }
        return false;
    }

    boolean hasPendingSubscription(String subscriptionId){
        Optional<Subscription> subscription = getSubscriptionById(subscriptionId);
        if(subscription.isPresent()){
            Subscription.Status status = subscription.get().getStatus();
            return status.toString().equalsIgnoreCase("pending");
        }
        return false;
    }

    public Map<String,Object> getSubscriptionStatus(String tenantId){
        Map<String,Object> ret = new HashMap<>();
        ret.put("status","Subscription Not Found");
        SubscriptionDto subscription = getSubscriptionByTenantId(tenantId);
        if(subscription != null){
            ret.put("status",subscription.getStatus());
        }
        return ret;
    }

    List<Subscription> subscriptionsByTenantId(String tenantId){
        List<Subscription> subscriptions = new ArrayList<>();
        listPaymentMethods(tenantId).forEach((pm)-> subscriptions.addAll(pm.getSubscriptions()));
        return subscriptions;
    }

    public SubscriptionDto getSubscriptionByTenantId(String tenantId){
        if(!this.tenantUserInfoService.isTenantUser(tenantId)){
            throw new RuntimeException("You dont have permission to do this");
        }
        List<Subscription> subscriptionList = subscriptionsByTenantId(tenantId);
        if(subscriptionList != null && subscriptionList.size() > 0){
            Optional<Subscription> sub =  subscriptionsByTenantId(tenantId).stream()
                    .filter((s)-> s.getStatus().equals(Subscription.Status.ACTIVE)
                               || s.getStatus().equals(Subscription.Status.PENDING)
                               || s.getStatus().equals(Subscription.Status.PAST_DUE))
                    .findFirst();
        }
        return SubscriptionDto.builder().build();
    }

    @Override
    public  SubscriptionDto createSubscription(SaasySubscription sr) {

        App app = this.appRepo.findById(sr.getAppId()).block();
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

        Optional<Customer> customerOptional = createCustomer(tenant,sr.getNonce());
        Optional<Subscription> subscriptionOptional = Optional.empty();
        if(customerOptional.isPresent()){
            subscriptionOptional = createSubscription(tenant.getId(),sr.getPlanId(),customerOptional.get());
        }
        if(subscriptionOptional.isPresent()){
            Subscription subscription  = subscriptionOptional.get();
            tenant.setSubscriptionId(subscription.getId());
            this.tenantRepo.save(tenant).subscribe();
        }
        return SubscriptionDto.builder().build();
    }

    @Override
    public  SubscriptionDto updateSubscription(String tenantId,SaasySubscription sr) {
        if(!this.tenantUserInfoService.isTenant(tenantId)){
            throw new RuntimeException("You don thave permission to do this");
        }

        if(tenantId == null || tenantId.equalsIgnoreCase("")){
            throw new RuntimeException("A Tenant Id is Required to update a subscription");
        }

        Tenant tenant = tenantRepo.findById(tenantId).blockOptional().orElseThrow(()->
            new RuntimeException("Tenant not found for Id " + tenantId)
        );
        PaymentMethod pm = null;
        if(sr.getNonce() != null && !sr.getNonce().isEmpty()){
            pm = createPaymentMethod(tenantId,sr.getNonce());
        }

        Optional<Customer> customerOptional = getCustomerByTenantId(tenantId);
        CustomerRequest customerUpdateRequest = null;
        if(!customerOptional.isPresent()){
            customerOptional = createCustomer(tenant,sr.getNonce());
        }else{
            customerUpdateRequest = new CustomerRequest()
                    .company(sr.getCompanyName())
                    .id(tenantId)
                    .email(sr.getEmail())
                    .firstName(sr.getFirstName())
                    .lastName(sr.getLastName());

            if(pm != null){
                customerUpdateRequest.defaultPaymentMethodToken(pm.getToken());
            }
        }

        Customer customer = customerOptional.get();
        if(customerUpdateRequest != null){
            customer = braintreeRepository.gateway().customer().update(customer.getId(),customerUpdateRequest).getTarget();
        }
        Subscription subscription = null;
        SubscriptionDto subscriptionDto = this.getSubscriptionByTenantId(tenantId);
        if(subscriptionDto != null){

            SubscriptionRequest req = new SubscriptionRequest();
            if(pm != null){
                req.paymentMethodToken(pm.getToken());
            }
            req.planId(sr.getPlanId());
            req.price(sr.getChargeAmount());
            subscription  = braintreeRepository.gateway().subscription().update(subscriptionDto.getId(), req).getTarget();
        }else{
            Optional<Subscription> subscriptionOptional = createSubscription(tenant.getId(),sr.getPlanId(),customer);
            subscription = subscriptionOptional.orElseThrow(()->new RuntimeException("Unable to create a subscription"));
        }

        return SubscriptionDto.builder().build();
    }

    public List<TransactionDto> getTransactionHistory(String tenantId){
        List<TransactionDto> ret = new ArrayList<>();
        TransactionSearchRequest query = new TransactionSearchRequest().customerId().is(tenantId);
        braintreeRepository.gateway().transaction().search(query).forEach(
                t-> ret.add( TransactionDto.builder()
                            .createdAt(t.getCreatedAt())
                            .processorResponseText(t.getProcessorResponseText())
                            .creditCard(convert(t.getCreditCard()))
                                .amount(t.getAmount())
                        .build())
        );
        return ret;
    }

    Integer calculateDaysForCanceledSubscriptions(String tenantId){
        AtomicReference<Long> adays = new AtomicReference<Long>(0L);
        this.subscriptionsByTenantId(tenantId).stream().filter((t)-> t.getStatus().equals(Subscription.Status.CANCELED))
                .forEach(c->{
           Calendar cal = c.getNextBillingDate();
           if(cal != null ){
               Calendar now = Calendar.getInstance();
               now.setTime(new Date());
               long dif = Duration.between(now.toInstant(), cal.toInstant()).toDays();
               if(dif > 0){
                   adays.set(dif);
               }
           }
        });
        int days = adays.get().intValue();
        return  days > 0 ? days : 0;
    }

}
