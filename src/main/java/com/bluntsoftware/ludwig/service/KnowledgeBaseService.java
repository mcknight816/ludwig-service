package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.domain.Knowledge;
import com.bluntsoftware.ludwig.domain.KnowledgeBase;
import com.bluntsoftware.ludwig.repository.KnowledgeBaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
@Slf4j
@Service
public class KnowledgeBaseService {
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final KnowledgeService knowledgeService;

    public KnowledgeBaseService(KnowledgeBaseRepository knowledgeBaseRepository, KnowledgeService knowledgeService) {
        this.knowledgeBaseRepository = knowledgeBaseRepository;
        this.knowledgeService = knowledgeService;
    }

    public Mono<KnowledgeBase> save(KnowledgeBase knowledgeBase) {
        return knowledgeBaseRepository.save(knowledgeBase);
    }

    public Mono<KnowledgeBase> findById(String id) {
        return knowledgeBaseRepository.findById(id);
    }

    public Flux<KnowledgeBase> findAll() {
        return knowledgeBaseRepository.findAll();
    }

    public Mono<Void> deleteById(String id) {
        knowledgeService
                .deleteAllByKnowledgeBaseId(id)
                .doOnSubscribe(s -> log.info("Deleting Knowledge Chunk {}", id)).block();
        knowledgeBaseRepository.deleteById(id).block();
        return Mono.empty();
    }


    public Mono<Knowledge> saveKnowledge(Knowledge knowledge) {
        if (knowledge.getBaseId() != null) {
            return knowledgeService.save(knowledge);
        }

        if (knowledge.getCategory() != null) {
            return knowledgeBaseRepository.findFirstByCategory(knowledge.getCategory())
                    .switchIfEmpty(
                            knowledgeBaseRepository.save(
                                    KnowledgeBase.builder()
                                            .category(knowledge.getCategory())
                                            .description(knowledge.getCategory())
                                            .name(knowledge.getCategory())
                                            .build()
                            )
                    )
                    .map(kb -> {
                        knowledge.setBaseId(kb.getId());
                        return knowledge;
                    })
                    .flatMap(knowledgeService::save);
        } else if (knowledge.getUserId() != null) {
            return knowledgeBaseRepository.findFirstByName(knowledge.getUserId())
                    .switchIfEmpty(
                            knowledgeBaseRepository.save(
                                    KnowledgeBase.builder()
                                            .category("User Knowledge")
                                            .description("Knowledge for User " + knowledge.getUserId())
                                            .name(knowledge.getUserId())
                                            .build()
                            )
                    )
                    .map(kb -> {
                        knowledge.setBaseId(kb.getId());
                        return knowledge;
                    })
                    .flatMap(knowledgeService::save);
        } else {
            return Mono.error(new IllegalStateException("Knowledge Base is required"));
        }
    }

    public List<String> getRelevantKnowledge(Knowledge request) {
        //Check the request for a category, user id or base id to get the knowledge base
        return knowledgeService.getRelevantKnowledge(request);
    }

    public KnowledgeBase findFirstByCategoryAndUserId(String category, String userId) {
        return knowledgeBaseRepository.findFirstByCategoryAndUserId(category,userId).block();
    }

    public String processRequest(Knowledge request, String role) {
        List<String> relevantKnowledge =  getRelevantKnowledge(request);
        // Construct context using the relevant history
        String context = role + String.join("\n", relevantKnowledge); // Combine Relevant Knowledge
        // Generate AI response based on the constructed context
        String aiResponse = knowledgeService.processRequest(request,context);
        // Save the current user message and AI response (with embeddings) into the mongo aiEmbeddingRepository
        if(request.getText().toLowerCase().contains("remember")){
             saveKnowledge(Knowledge.builder()
                     .baseId(request.getBaseId())
                     .userId(request.getUserId())
                     .text(request.getText())
                     .category(request.getCategory()).build()).block();
        }
        return aiResponse;
    }
}
