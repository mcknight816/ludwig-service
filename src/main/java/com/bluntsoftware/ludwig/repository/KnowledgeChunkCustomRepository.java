package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.domain.KnowledgeChunk;
import reactor.core.publisher.Flux;

import java.util.List;

public interface KnowledgeChunkCustomRepository {
    Flux<KnowledgeChunk> findSimilarChunks(List<Double> queryVector, int limit);
}
