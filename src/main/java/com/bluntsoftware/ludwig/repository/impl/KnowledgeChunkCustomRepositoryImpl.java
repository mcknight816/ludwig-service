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
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

@Repository
public class KnowledgeChunkCustomRepositoryImpl implements KnowledgeChunkCustomRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    public KnowledgeChunkCustomRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Flux<KnowledgeChunk> findSimilarChunks(List<Double> queryVector, int limit) {
        boolean isAtlasOrVectorSearchSupported = checkVectorSearchSupport();

        // If $vectorSearch is supported, use it for similarity search
        if (isAtlasOrVectorSearchSupported) {
            return findSimilarChunksWithVectorSearch(queryVector, limit);
        }

        // Fallback to application-level vector similarity for standard MongoDB
        return findSimilarChunksWithApplicationFallback(queryVector, limit);
    }

    //TODO : Make this more efficient by querying the db with user && knowledgeBaseName vs filter
    @Override
    public Flux<KnowledgeChunk> findSimilarChunks(String user, String knowledgeBaseName, List<Double> queryVector, int limit) {
        return findSimilarChunks(queryVector, limit)
                .filter(kc -> knowledgeBaseName.equalsIgnoreCase(kc.getKnowledgeBaseName()))
                .filter(kc-> user.equalsIgnoreCase(kc.getUserId()));
    }

    private boolean checkVectorSearchSupport() {
        try {
            // Attempt to execute a simple command that requires $vectorSearch
            Document command = new Document("buildInfo", 1);
          //  Document result = mongoTemplate.getMongoDatabase().run(command);
            // Check version or Atlas-specific features here if needed
            return false; // Add further checks for Atlas features if required
        } catch (Exception e) {
            // Log the unsupported $vectorSearch error
            return false;
        }
    }

    private Flux<KnowledgeChunk> findSimilarChunksWithVectorSearch(List<Double> queryVector, int limit) {
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

    private Flux<KnowledgeChunk> findSimilarChunksWithApplicationFallback(List<Double> queryVector, int limit) {
        // Fallback logic for local MongoDB (no $vectorSearch support)
        return mongoTemplate.findAll(KnowledgeChunk.class)
                .filter(kc -> kc.getVector() != null && !kc.getVector().isEmpty())
                .flatMap(kc -> {
                    // Compute similarity manually
                    double similarity = calculateCosineSimilarity(queryVector, kc.getVector());
                    kc.setSimilarity(similarity); // Add similarity to the object
                    return Mono.just(kc);
                })
                .sort(Comparator.comparingDouble(KnowledgeChunk::getSimilarity).reversed()) // Sort by similarity
                .take(limit); // Limit results
    }

    private double calculateCosineSimilarity(List<Double> queryVector, List<Double> documentVector) {
        if (queryVector.size() != documentVector.size()) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        double dotProduct = 0.0;
        double queryMagnitude = 0.0;
        double documentMagnitude = 0.0;

        for (int i = 0; i < queryVector.size(); i++) {
            double queryElement = queryVector.get(i);
            double documentElement = documentVector.get(i);

            dotProduct += queryElement * documentElement;
            queryMagnitude += queryElement * queryElement;
            documentMagnitude += documentElement * documentElement;
        }

        if (queryMagnitude == 0 || documentMagnitude == 0) {
            return 0; // Handle zero vectors
        }

        return dotProduct / (Math.sqrt(queryMagnitude) * Math.sqrt(documentMagnitude));
    }


}
