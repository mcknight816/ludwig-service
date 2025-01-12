package com.bluntsoftware.saasy.repository;


import com.bluntsoftware.saasy.domain.Tenant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface TenantRepo extends ReactiveMongoRepository<Tenant, String> {
	Flux<Tenant> findAllBy(Pageable pageable);
	Flux<Tenant> findAllByOwner(String owner, Pageable pageable);
	Flux<Tenant> findAllByIdIn(List<String> ids);
}
