package com.bluntsoftware.saasy.service;


import com.bluntsoftware.saasy.domain.*;
import com.bluntsoftware.saasy.repository.TenantRepo;
import com.bluntsoftware.saasy.repository.TenantUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

import java.util.UUID;

@Slf4j
@Service
@Secured({"ROLE_SAASY_USER","ROLE_SAASY_ADMIN"})
public class TenantService{

  private final TenantRepo repo;
  private final UserInfoService userInfoService;
  private final TenantUserRepo tenantUserRepo;

  @PostConstruct
  public void init(){
    /*String tenantId = "ludwig-trial";
    String ownerEmail = "mcknight816@gmail.com";
    Tenant tenant = repo.findById(tenantId).block();
    if(tenant == null){

      ArrayList<String> roles = new ArrayList<>();
      roles.add("ROLE_ADMIN");
      roles.add("ROLE_USER");
      roles.add("ROLE_SAASY_ADMIN");
      roles.add("ROLE_SAASY_USER");

      User user = User.builder()
              .name("Alexander Mcknight")
              .username(ownerEmail)
              .active(true)
              .roles(roles)
              .email(ownerEmail).build();

      ArrayList<User> users = new ArrayList<>();
      users.add(user);

      tenant = repo.save(Tenant.builder()
              .id(tenantId)
              .app(IdName.builder().name("ludwig").id(UUID.randomUUID().toString()).build())
              .owner(ownerEmail)
              .customer(user)
              .displayName("Ludwig Trial")
              .planId("Bronze")
              .displayName("Ludwig Trial")
              .users(users)
              .build()).block();

      log.info("Registered new tenant: {}", tenant);

      TenantUser tu = TenantUser.builder()
              .tenantId(tenantId)
              .email(user.getEmail())
              .name(user.getName())
              .active(user.getActive())
              .isCustomer(true)
              .roles(user.getRoles()).build();

      tu = tenantUserRepo.save(tu).block();
      log.info("Registered new tenant user: {}", tu);

    }*/
  }

  public TenantService(TenantRepo repo, UserInfoService userService, TenantUserRepo tenantUserRepo) {
    this.repo = repo;
    this.userInfoService = userService;
    this.tenantUserRepo = tenantUserRepo;
  }

  void isOwner(User user, String id){
    Tenant current = !StringUtils.isEmpty(id) ? repo.findById(id).block() : null;
    if (current != null){
      //is tenant owner
      if(current.getCustomer().getEmail().equalsIgnoreCase(user.getEmail())){
        return;
      }else if(userInfoService.hasRole(Roles.TENANT_USER)){
        throw new RuntimeException("Unauthorized to edit this request");
      }
      if (!userInfoService.isAdmin() && !user.getUsername().equalsIgnoreCase(current.getOwner())) {
        throw new RuntimeException("Unauthorized to edit this request");
      }
    }
  }

  @Secured({"ROLE_SAASY_ADMIN","ROLE_SAASY_USER"})
  public Mono<Tenant> save(Tenant tenant) {
    User user= userInfoService.getLoggedInUser();
    String id = tenant.getId();
    isOwner(user,id);
    if(!userInfoService.isAdmin() || id == null){
      tenant.setOwner(user.getUsername());
    }
    Tenant t = repo.save(tenant).block();
    User customer = t.getCustomer();
    TenantUser tenantCustomer = tenantUserRepo.findByTenantIdAndEmail(t.getId(),customer.getEmail()).block();
    if(tenantCustomer == null){
      TenantUser tu = TenantUser.builder()
              .tenantId(t.getId())
              .email(customer.getEmail())
              .name(customer.getName())
              .active(customer.getActive())
              .isCustomer(true)
              .roles(customer.getRoles()).build();
      tu = tenantUserRepo.save(tu).block();
    }
    return Mono.just(t);
  }
  @Secured({"ROLE_SAASY_ADMIN","ROLE_SAASY_USER"})
  public Mono<Void> deleteById(String id) {
     isOwner(userInfoService.getLoggedInUser(),id);
    return repo.deleteById(id);
  }

  public Mono<Tenant> findById(String id) {
     isOwner(userInfoService.getLoggedInUser(),id);
    return repo.findById(id);
  }

  @Secured({"ROLE_SAASY_ADMIN"})
  public Flux<Tenant> findAll() {
    return repo.findAll();
  }

  @Secured({"ROLE_SAASY_ADMIN","ROLE_SAASY_USER"})
  public Flux<Tenant> search(String term,Pageable pageable) {
    User user = userInfoService.getLoggedInUser();
    if(userInfoService.isAdmin()){
      return repo.findAllBy(pageable);
    }
    return repo.findAllByOwner(user.getUsername(),pageable);
  }
}
