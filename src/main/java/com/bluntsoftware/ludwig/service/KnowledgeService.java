package com.bluntsoftware.ludwig.service;


import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIMessage;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.OpenAiModel;
import com.bluntsoftware.ludwig.conduit.utils.ParagraphSplitter;

import com.bluntsoftware.ludwig.domain.Knowledge;
import com.bluntsoftware.ludwig.domain.KnowledgeBase;
import com.bluntsoftware.ludwig.domain.KnowledgeChunk;

import com.bluntsoftware.ludwig.repository.KnowledgeBaseRepository;
import com.bluntsoftware.ludwig.repository.KnowledgeChunkRepository;
import com.bluntsoftware.ludwig.repository.KnowledgeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class KnowledgeService {
    private final UserService userService;
    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final KnowledgeBaseRepository knowledgeBaseRepository;
    private final OpenAiService openAiService;
    public KnowledgeService(UserService userService, KnowledgeRepository knowledgeRepository, KnowledgeChunkRepository knowledgeChunkRepository, KnowledgeBaseRepository knowledgeBaseRepository, OpenAiService openAiService) {
        this.userService = userService;
        this.knowledgeRepository = knowledgeRepository;
        this.knowledgeChunkRepository = knowledgeChunkRepository;
        this.knowledgeBaseRepository = knowledgeBaseRepository;
        this.openAiService = openAiService;
    }
    public Mono<Knowledge> save(Knowledge knowledge) {
         if(knowledge.getId() == null || knowledge.getId().isEmpty()) {
             knowledge.setId(UUID.randomUUID().toString());
         }

         if(knowledge.getUserId() == null || knowledge.getUserId().isEmpty()) {
            knowledge.setUserId("system");
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
         knowledgeChunkRepository
                .deleteAllByKnowledgeId(id)
                .doOnSubscribe(s -> log.info("Deleting Knowledge Chunk {}", id)).block();
          knowledgeRepository.deleteById(id).block();
         return Mono.empty();
    }

    public Flux<Knowledge> findAllByBaseId(String s) {
        return knowledgeRepository.findAllByBaseId(s);
    }

    //Create Embedding for Ai
    void createKnowledgeEmbeddings(Knowledge knowledge){
        KnowledgeBase kb = knowledgeBaseRepository.findById(knowledge.getBaseId()).block();
        if(kb != null){
            knowledgeChunkRepository.deleteAllByKnowledgeId(knowledge.getId()).block();
            AIService aiService = openAiService.getOpenAiService(kb.getOpenAiConfig());
            List<String> vectorText = ParagraphSplitter.chunkText(knowledge.getText(),500);
            vectorText.forEach(text -> {
                try {
                    List<Double> embeddings = aiService.getEmbedding(knowledge.getText());
                    knowledgeChunkRepository.save(KnowledgeChunk.builder()
                                    .text(text)
                                    .knowledgeBaseName(kb.getName())
                                    .userId(knowledge.getUserId())
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

    public Mono<Knowledge> findAllByBaseIdAndUserId(String id, String user) {
        return knowledgeRepository.findAllByBaseIdAndUserId(id, user);
    }



    public List<String> getRelevantKnowledge(Knowledge request){
        return getRelevantKnowledge(request,getUserId());
    }
    public List<String> getRelevantKnowledge(Knowledge request,String userId)  {
        KnowledgeBase kb = knowledgeBaseRepository.findById(request.getBaseId()).block();
        assert kb != null;
        AIService aiService = openAiService.getOpenAiService(kb.getOpenAiConfig());

        List<Double> queryVector = null;
        try {
            queryVector = aiService.getEmbedding(request.getText());
            List<KnowledgeChunk> knowledgeChunks =  knowledgeChunkRepository
                    .findSimilarChunks(userId,kb.getName(),queryVector,50)
                    .collectList()
                    .block();

            if(knowledgeChunks != null) {
                return knowledgeChunks.stream().map(KnowledgeChunk::getText).toList();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }

    public String getUserId(){
        Map<String,Object> userDetails = userService.getAuthenticatedUserDetails();
        return userDetails.containsKey("email") ? (String)userDetails.get("email") : (String)userDetails.get("sub");
    }

    public String processRequest(Knowledge request, String context) {
        KnowledgeBase kb = knowledgeBaseRepository.findById(request.getBaseId()).block();
        assert kb != null;
        AIService aiService = openAiService.getOpenAiService(kb.getOpenAiConfig());
        return aiService.completions(AICompletionRequest.builder()
                .message(AIMessage.builder().role("system").content(context).build())
                .message(AIMessage.builder().role("user").content(request.getText()).build())
                .model(OpenAiModel.GPT_4_MINI.getValue())
                .build()).getChoices().get(0).getMessage().getContent();
    }


    public Mono<Void>  deleteAllByKnowledgeBaseId(String id) {
        findAllByBaseId(id).collectList().block().forEach(k -> deleteById(k.getId()).block());
        return Mono.empty();
    }
}
