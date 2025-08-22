package com.bluntsoftware.ludwig.service;

import com.bluntsoftware.ludwig.conduit.service.ai.AIService;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AICompletionRequest;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.AIMessage;
import com.bluntsoftware.ludwig.conduit.service.ai.domain.OpenAiModel;
import com.bluntsoftware.ludwig.config.AppConfig;
import com.bluntsoftware.ludwig.domain.AiEmbedding;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OpenAiService {

    private final AIService aiService;
    public OpenAiService(AppConfig appConfig) {
        this.aiService = new AIService(appConfig);
    }

    public String callOpenAi(String prompt) {
        return this.aiService.completions(AICompletionRequest.builder()
                        .message(AIMessage.builder().role("user").content(prompt).build())
                        .model(OpenAiModel.GPT_4.getValue())
                .build()).getChoices().get(0).getMessage().getContent();
    }

    public List<Double> getEmbeddings(String text) throws IOException {
        return aiService.getEmbedding(text);
    }

    public List<String> getRelevantHistory( List<Double> userEmbedding,List<AiEmbedding> history) {
        return getRelevantHistory(userEmbedding,history,5);
    }

    public List<String> getRelevantHistory( List<Double> userEmbedding,List<AiEmbedding> history,int limit) {
        // Filter by session id and calculate similarity
        return history.stream()
                .sorted((entry1, entry2) -> Double.compare(
                        calculateCosineSimilarity(entry2.getVector(), userEmbedding),
                        calculateCosineSimilarity(entry1.getVector(), userEmbedding)
                ))
                .limit(limit)
                .map(AiEmbedding::getText)
                .collect(Collectors.toList());
    }

    public Map<AiEmbedding, Double> computeSimilarities(String prompt, List<AiEmbedding> aiEmbeddings) throws IOException {
        List<Double> promptEmbedding = getEmbeddings(prompt);
        return computeSimilarities(promptEmbedding, aiEmbeddings);
    }

    private Map<AiEmbedding, Double> computeSimilarities(List<Double> promptEmbedding, List<AiEmbedding> aiEmbeddings) {
        Map<AiEmbedding, Double> similarityScores = new HashMap<>();

        for (AiEmbedding aiEmbedding :aiEmbeddings) {
            List<Double> activityEmbedding = aiEmbedding.getVector(); // Fetch stored embedding
            double similarity = calculateCosineSimilarity(promptEmbedding, activityEmbedding);
            similarityScores.put(aiEmbedding, similarity);
        }

        return similarityScores;
    }

    private double calculateCosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        // Validate inputs
        if (vectorA == null || vectorB == null || vectorA.isEmpty() || vectorB.isEmpty()) {
            throw new IllegalArgumentException("Input vectors must be non-null and non-empty.");
        }
        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException("Input vectors must have the same size.");
        }

        // Compute dot product
        double dotProduct = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
        }

        // Compute magnitudes using a helper method
        double magnitudeA = computeVectorMagnitude(vectorA);
        double magnitudeB = computeVectorMagnitude(vectorB);

        // Prevent division by zero if one of the vectors has zero magnitude
        if (magnitudeA == 0.0 || magnitudeB == 0.0) {
            throw new IllegalArgumentException("Vector magnitude should not be zero.");
        }

        // Calculate and return cosine similarity
        return dotProduct / (magnitudeA * magnitudeB);
    }

    private double computeVectorMagnitude(List<Double> vector) {
        double sumOfSquares = 0.0;
        for (Double value : vector) {
            sumOfSquares += value * value;
        }
        return Math.sqrt(sumOfSquares);
    }
}
