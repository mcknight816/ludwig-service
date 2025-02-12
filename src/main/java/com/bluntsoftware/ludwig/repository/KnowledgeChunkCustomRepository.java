package com.bluntsoftware.ludwig.repository;

import com.bluntsoftware.ludwig.domain.KnowledgeChunk;
import reactor.core.publisher.Flux;

import java.util.List;

public interface KnowledgeChunkCustomRepository {
    Flux<KnowledgeChunk> findSimilarChunks(String user,String knowledgeBaseName,List<Double> queryVector, int limit);
}
