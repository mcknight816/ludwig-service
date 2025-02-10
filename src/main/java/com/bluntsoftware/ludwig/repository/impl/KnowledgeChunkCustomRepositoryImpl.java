package com.bluntsoftware.ludwig.repository.impl;

import com.bluntsoftware.ludwig.domain.KnowledgeChunk;
import com.bluntsoftware.ludwig.repository.KnowledgeChunkCustomRepository;
import org.bson.Document;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public class KnowledgeChunkCustomRepositoryImpl implements KnowledgeChunkCustomRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    public KnowledgeChunkCustomRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<KnowledgeChunk> findSimilarChunks(List<Double> queryVector, int limit) {
        AggregationOperation vectorSearch = context -> new Document("$vectorSearch",
                new Document("index", "vector") // Use the correct index name
                        .append("path", "vector") // Field to compare against
                        .append("queryVector", queryVector) // Input vector for similarity search
                        .append("numCandidates", 100) // Larger values improve accuracy
                        .append("limit", limit) // Max results
        );

        TypedAggregation<KnowledgeChunk> aggregation = Aggregation.newAggregation(KnowledgeChunk.class, vectorSearch);

        return mongoTemplate.aggregate(aggregation, KnowledgeChunk.class);
    }

}
