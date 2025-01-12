package com.bluntsoftware.saasy.service;


import com.bluntsoftware.saasy.domain.TenantUser;
import com.bluntsoftware.saasy.exception.BadRequestException;
import com.bluntsoftware.saasy.repository.TenantRepo;
import com.bluntsoftware.saasy.repository.TenantUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Secured({"ROLE_SAASY_USER","ROLE_SAASY_ADMIN"})
public class TenantUserService {
    private final TenantUserRepo repo;
    private final UserInfoService userInfoService;
    private final TenantRepo tenantRepo;

    public TenantUserService(TenantUserRepo repo, UserInfoService userInfoService, TenantRepo tenantRepo) {
        this.repo = repo;
        this.userInfoService = userInfoService;
        this.tenantRepo = tenantRepo;
    }


    public Mono<TenantUser> save(TenantUser tenantUser) {

        String tenantId = tenantUser.getTenantId();
        String userEmail = tenantUser.getEmail();

        if(tenantId == null || tenantId.isEmpty()) {
            throw new BadRequestException( "Tenant ID is required");
        }

        if(userEmail == null || userEmail.isEmpty()) {
            throw new BadRequestException( "User Email is required");
        }

        TenantUser current = repo.findByTenantIdAndEmail(tenantId,userEmail).block();
        if(current != null && !current.getId().equalsIgnoreCase(tenantUser.getId())){
            throw new BadRequestException("Tenant User already exists");
        }

        return repo.save(tenantUser);
    }

    public Mono<Void> deleteById(String id) {

        return repo.deleteById(id);
    }

    public Mono<TenantUser> findById(String id) {
        return repo.findById(id);
    }

    @Secured({"ROLE_SAASY_ADMIN"})
    public Flux<TenantUser> findAll() {
        return repo.findAll();
    }

    @Secured({"ROLE_SAASY_ADMIN"})
    public Flux<TenantUser> search(String term, Pageable pageable) {
        log.info("create a filter in repo for search term {}",term);
        return repo.findAllBy(pageable);
    }

    public Flux<TenantUser> searchByTenant(String tenantId, Pageable pageable) {
        return repo.findAllByTenantId(tenantId,pageable);
    }


}
