package com.bluntsoftware.saasy.service;


import com.bluntsoftware.saasy.domain.App;
import com.bluntsoftware.saasy.domain.Plan;
import com.bluntsoftware.saasy.domain.User;
import com.bluntsoftware.saasy.dto.AppDto;
import com.bluntsoftware.saasy.repository.AppRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
@Service
@Secured({"ROLE_SAASY_USER","ROLE_SAASY_ADMIN"})
public class AppService{

  private final AppRepo repo;
  private final UserInfoService userService;

  @PostConstruct
  public void init(){
     String appId = "64ebec74fb5e3453f79e65a3";
      App  app = repo.findById(appId).block();
     if(app == null){
       ArrayList<String> roles = new ArrayList<>();
       roles.add("ADMIN");
       roles.add("USER");

       ArrayList<Plan> plans = new ArrayList<>();
       plans.add(Plan.builder().planId("Bronze").name("Bronze").description("Basic Plan").monthly(BigDecimal.valueOf(22.00)).build());
       plans.add(Plan.builder().planId("Silver").name("Silver").description("Business Plan").monthly(BigDecimal.valueOf(45.00)).build());
       plans.add(Plan.builder().planId("Gold").name("Gold").description("Best Plan").monthly(BigDecimal.valueOf(250.00)).build());

       repo.save(App.builder()
                       .id(appId)
                       .name("Ludwig")
                       .owner("mcknight816@gmail.com")
                       .roles(roles)
                       .plans(plans)
                       .jwkSetUri("https://keycloak.bluntsoftware.com/auth/realms/ludwig/protocol/openid-connect/certs")
               .build()).block();
     }
  }

  public AppService(AppRepo repo, UserInfoService userService) {
    this.repo = repo;
    this.userService = userService;
  }

  void isOwner(User user, String id){
    App current = !StringUtils.isEmpty(id) ? repo.findById(id).block() : null;
    if (current != null && !userService.isAdmin() && !user.getUsername().equalsIgnoreCase(current.getOwner())) {
      throw new RuntimeException("Unauthorized to edit this request");
    }
  }
  @Secured({"ROLE_SAASY_ADMIN","ROLE_SAASY_USER"})
  public Mono<App> save(App item) {
    User user = userService.getLoggedInUser();
    String id = item.getId();
    isOwner(user,id);
    if(!userService.isAdmin() || id == null){
      item.setOwner(user.getUsername());
    }
    return repo.save(item);
  }

  public Mono<Void> deleteById(String id) {
    isOwner(userService.getLoggedInUser(),id);
    return repo.deleteById(id);
  }

  public Mono<App> findById(String id) {
    isOwner(userService.getLoggedInUser(),id);
    return repo.findById(id);
  }

  @Secured({"ROLE_SAASY_ADMIN"})
  public Flux<App> findAll() {
    return repo.findAll();
  }

  @Secured({"ROLE_SAASY_ADMIN","ROLE_SAASY_USER"})
  public Flux<App> search(String term,Pageable pageable) {
    User user = userService.getLoggedInUser();
    log.info("create a filter for search term {}",term);
    if(userService.isAdmin()){
      return repo.findAllBy(pageable);
    }
    return repo.findAllByOwner(user.getUsername(),pageable);
  }

}
