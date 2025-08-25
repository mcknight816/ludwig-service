package com.bluntsoftware.ludwig.service.ai;


import com.bluntsoftware.ludwig.conduit.activities.ActivityProperties;
import com.bluntsoftware.ludwig.domain.AiEmbedding;
import com.bluntsoftware.ludwig.repository.ActivityRepository;
import com.bluntsoftware.ludwig.repository.AiEmbeddingRepository;
import com.bluntsoftware.ludwig.service.OpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ActivityRegistry {

    private final ActivityRepository activityRepository;
    private final AiEmbeddingRepository aiEmbeddingRepository;
    private final OpenAiService openAiService;
    private List<ActivityProperties> activities;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final static String CATEGORY = "ActivityRegistry";
    List<AiEmbedding> embeddings = new ArrayList<>();
    public ActivityRegistry(ActivityRepository activityRepository, AiEmbeddingRepository aiEmbeddingRepository, OpenAiService openAiService) {
        this.activityRepository = activityRepository;
        this.aiEmbeddingRepository = aiEmbeddingRepository;
        this.openAiService = openAiService;
    }

    @PostConstruct
    public void init() {
     //   this.registerActivities();
    }

    public void registerActivities(){

        log.info("Registering activities");
        this.activities =  activityRepository.findAll()
                .stream()
                .map(activity -> {
                    try {
                       return activity.getActivityProperties();
                    } catch (Exception e){
                      log.error(activity.getName() + " activity had issues" );
                      return ActivityProperties.builder().name(activity.getName()).build();
                    }
                }).collect(Collectors.toList());

        log.info("Registered {} activities", activities.size());
       // activities.forEach(this::registerActivity);
    }

    public void registerActivity(ActivityProperties activity){



        String text = String.format("%s: Keywords=%s. Description: %s. Input JSON Schema: %s",
                activity.getName(),
                activity.getKeywords().toString(),
                activity.getCategory(),
                activity.getSchema().getJson()
        );


        try {

            List<Double> vector = openAiService.getEmbeddings(text);
            AiEmbedding embedding = AiEmbedding.builder()
                    .id(activity.getName())
                    .category(CATEGORY)
                    .vector(vector)
                    .text(text)
                    .description(activity.getCategory())
                    .build();


           // aiEmbeddingRepository.save(embedding).block();
            embeddings.add(embedding);
        } catch (IOException e) {
            throw new RuntimeException("Error generating embeddings for activity: " + activity.getName(), e);
        }
    }

    public String flowBuilder(String prompt){
        try {

            Map<AiEmbedding, Double>  similar  = openAiService.computeSimilarities(prompt,embeddings);
            List<AiEmbedding> relaventActivities = similar.entrySet().stream()
                    .filter(entry -> entry.getValue() > 0.75) // Check similarity score
                    .map(Map.Entry::getKey)                  // Extract the embedding (key)
                    .collect(Collectors.toList());

            List<String> activities = relaventActivities.stream().map(AiEmbedding::getId).collect(Collectors.toList());
            return objectMapper.writeValueAsString(activities);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
