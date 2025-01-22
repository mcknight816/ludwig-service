package com.bluntsoftware.ludwig.config;

import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import reactor.core.publisher.Mono;
import javax.validation.constraints.NotNull;
@Slf4j
@Configuration
public class MultiTenantMongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    String dbName;

    @Bean
    SimpleReactiveMongoDatabaseFactory mongoDbFactory(MongoClient client){
        return new SimpleReactiveMongoDatabaseFactory(client,dbName){
            @org.jetbrains.annotations.NotNull
            @NotNull
            @Override
            public Mono<MongoDatabase> getMongoDatabase() throws DataAccessException {
                String tenant = TenantResolver.resolve() != null && !TenantResolver.resolve().equalsIgnoreCase("") ?  "_" + TenantResolver.resolve() :"";
                String tenantDb = dbName + tenant;
                return super.getMongoDatabase(tenantDb);
            }
        };
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(uri);
    }

}

