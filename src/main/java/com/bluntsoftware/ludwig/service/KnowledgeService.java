package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.config.ai.domain.OpenAiConfig;
import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.conduit.utils.ParagraphSplitter;
import com.bluntsoftware.ludwig.domain.Knowledge;
import com.bluntsoftware.ludwig.domain.KnowledgeBase;
import com.bluntsoftware.ludwig.domain.KnowledgeChunk;
import com.bluntsoftware.ludwig.repository.ActivityConfigRepository;
import com.bluntsoftware.ludwig.repository.KnowledgeBaseRepository;
import com.bluntsoftware.ludwig.repository.KnowledgeChunkRepository;
import com.bluntsoftware.ludwig.repository.KnowledgeRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class KnowledgeService {
    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final ActivityConfigRepository activityConfigRepository;
    public KnowledgeService(KnowledgeRepository knowledgeRepository, KnowledgeChunkRepository knowledgeChunkRepository, KnowledgeBaseRepository knowledgeBaseRepository, ActivityConfigRepository activityConfigRepository) {
        this.knowledgeRepository = knowledgeRepository;
        this.knowledgeChunkRepository = knowledgeChunkRepository;
        this.knowledgeBaseRepository = knowledgeBaseRepository;
        this.activityConfigRepository = activityConfigRepository;
    }
    public Mono<Knowledge> save(Knowledge knowledge) {
         if(knowledge.getId() == null || knowledge.getId().isEmpty()) {
             knowledge.setId(UUID.randomUUID().toString());
         }
        createKnowledgeEmbeddings(knowledge);
        return knowledgeRepository.save(knowledge);
    }

    public Mono<Knowledge> findById(String id) {
        return knowledgeRepository.findById(id);
    }

    public Flux<Knowledge> findAll() {
        return knowledgeRepository.findAll();
    }

    public Mono<Void> deleteById(String id) {
        return knowledgeRepository.deleteById(id);
    }

    public Flux<Knowledge> findAllByBaseId(String s) {
        return knowledgeRepository.findAllByBaseId(s);
    }

    //Create Embedding for Ai
    void createKnowledgeEmbeddings(Knowledge knowledge){
        KnowledgeBase kb = knowledgeBaseRepository.findById(knowledge.getBaseId()).block();
        if(kb != null){
            knowledgeChunkRepository.deleteAllByKnowledgeId(knowledge.getId()).block();
            OpenAiConfig config = this.activityConfigRepository.getConfigByNameAs(kb.getOpenAiConfig(),OpenAiConfig.class);
            AIService aiService = new AIService(config.getSecret());
            List<String> vectorText = ParagraphSplitter.chunkText(knowledge.getText(),500);
            vectorText.forEach(text -> {
                try {
                    List<Double> embeddings = aiService.getEmbedding(knowledge.getText());
                    knowledgeChunkRepository.save(KnowledgeChunk.builder()
                                    .text(text)
                                    .knowledgeId(knowledge.getId())
                                    .vector(embeddings)
                            .build()).block();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            knowledge.setProcessed(true);
        }
    }
}
