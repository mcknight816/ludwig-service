package com.bluntsoftware.saasy.repository;


import com.bluntsoftware.saasy.domain.SaasySubscription;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends ReactiveMongoRepository<SaasySubscription, String> {
}
