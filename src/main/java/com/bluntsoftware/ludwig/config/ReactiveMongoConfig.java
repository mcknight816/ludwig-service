package com.bluntsoftware.ludwig.config;

import com.bluntsoftware.ludwig.tenant.TenantResolver;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.autoconfigure.mongo.ReactiveMongoClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import reactor.core.publisher.Mono;
import javax.validation.constraints.NotNull;
import java.util.stream.Collectors;

@Configuration
public class ReactiveMongoConfig {
    @Value("${spring.data.mongodb.database}")
    String dbName;
    @Bean
    SimpleReactiveMongoDatabaseFactory mongoDbFactory(MongoClient client){
        return new SimpleReactiveMongoDatabaseFactory(client,dbName){
            @NotNull
            @Override
            public Mono<MongoDatabase> getMongoDatabase() throws DataAccessException {
                return super.getMongoDatabase(dbName + "_" + TenantResolver.resolve());
            }
        };
    }
    @Bean
    public MongoClient mongo(ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers,
                             MongoClientSettings settings) {
        return new ReactiveMongoClientFactory(builderCustomizers.orderedStream().collect(Collectors.toList()))
                .createMongoClient(settings);
    }

    @Bean
    MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder().build();
    }
}

