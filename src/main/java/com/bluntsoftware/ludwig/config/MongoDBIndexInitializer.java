package com.bluntsoftware.ludwig.config;


import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.bluntsoftware.saasy.repository.TenantRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class MongoDBIndexInitializer {

    private final ReactiveMongoTemplate mongoTemplate;
    private final TenantRepo tenantRepository;
    public MongoDBIndexInitializer(ReactiveMongoTemplate mongoTemplate, TenantRepo tenantRepository) {
        this.mongoTemplate = mongoTemplate;
        this.tenantRepository = tenantRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createVectorIndex() {
        String currentTenantId = TenantResolver.resolve();
        Objects.requireNonNull(this.tenantRepository.findAll().collectList().block())
                .forEach(tenant ->{
                    TenantResolver.setCurrentTenant(tenant.getId());
                    // Define vector-based index options
                    IndexDefinition indexDefinition = new Index()
                            .on("vector", Sort.Direction.ASC)  // Replace 'vector' with the field
                            .named("vectorSearch")
                            .partial(null)
                            .background();
                    // Create index in the 'knowledgeChunk' collection
                    mongoTemplate.indexOps("knowledgeChunk")
                            .ensureIndex(indexDefinition)
                            .doOnSuccess(indexName -> log.info("Vector index successfully created : {} for tenant {}", indexName, tenant.getDisplayName()))
                            .doOnError(e -> log.error("Failed to create vector index for tenant {}", tenant.getDisplayName(), e))
                            .then().block();
                });
        TenantResolver.setCurrentTenant(currentTenantId);
    }
}
