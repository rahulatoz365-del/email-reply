package org.email_reply_sb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.email_reply_sb.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.*;

@Service
@Slf4j
public class OpenRouterAIService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${openrouter.api.key}")
    private String apikey;

    @Value("${openrouter.model}")
    private String model;

    private final String baseurl="https://openrouter.ai/api/v1";

    public OpenRouterAIService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String generateEmailReply(EmailRequest emailRequest) {
    String fullUrl = baseurl + "/chat/completions";
    String prompt = buildPrompt(emailRequest);
    Map<String, Object> requestBody = buildRequestBody(prompt);
    requestBody.put("stream", false); // Non-streaming

    try {
        String response = webClient.post()
                .uri(fullUrl)
                .header("Authorization", "Bearer " + apikey)
                .header("Content-Type", "application/json")
                .header("HTTP-Referer", "http://localhost:8080")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseFullResponse(response);
    } catch (Exception e) {
        log.error("Error calling OpenRouter: {}", e.getMessage());
        throw new RuntimeException("Failed to generate email reply: " + e.getMessage());
    }
}
    private String buildPrompt(EmailRequest emailRequest){
        String tone= getToneDesc(emailRequest.getTone());
        StringBuilder prompt=new StringBuilder();
        prompt.append("You are an email assistant>Generate a reply to the following email.\n\n");
        prompt.append("Instructions.\n");
        prompt.append("- Write only the email reply body,no subject line.\n");
        prompt.append("- Tone: ").append(tone).append("\n");
        prompt.append("- Keep it a bit lengthy dont put unnecessary words but keep it relevant\n");
        prompt.append("- Do not include any explanations,metadata or phrases like 'Here's your reply'\n");
        prompt.append("- Start Directly with the greeting or response.\n");
        prompt.append("- Match the language of the original email.\n\n");
        if(emailRequest.getSubject()!=null && !emailRequest.getSubject().isEmpty()){
            prompt.append("Subject: ").append(emailRequest.getSubject()).append("\n\n");
        }
        prompt.append("Original Email Body:\n");
        prompt.append(emailRequest.getMessage()).append("\n\n");
        if(emailRequest.getSenderName()!=null && !emailRequest.getSenderName().isEmpty()){
            prompt.append("Sender Name: ").append(emailRequest.getSenderName()).append("\n\n");
        }
        if(emailRequest.getAdditionalContext()!=null && !emailRequest.getAdditionalContext().isEmpty()){
            prompt.append("Additional Context: ").append(emailRequest.getAdditionalContext()).append("\n\n");
        }
        prompt.append("Generate Reply Body:");
        return prompt.toString();
    }
    private String getToneDesc(String tone){
        return switch (tone.toLowerCase()){
            case "casual" -> "Casual and relaxed. Use informal language, " +
                    "contractions, and a friendly conversational style. Keep it light.";
            case "professional" -> "Professional and formal. Use proper grammar, " +
                    "business-appropriate language, and maintain a respectful, polished tone.";
            case "friendly" -> "Warm and friendly. Be personable, positive, " +
                    "and approachable while remaining respectful. Show genuine interest.";
            case "aggressive" -> "Direct and assertive. Be firm and to the point, " +
                    "but remain professional. Express urgency or strong conviction without being rude.";
            default -> "Professional and balanced.";
        };
    }
    private Map<String,Object> buildRequestBody(String prompt){
        Map<String,String> systemMessage=new HashMap<>();
        systemMessage.put("role","system");
        systemMessage.put("content","You are an email assistant that generates appropriate email replies.Always,respond with just the email content no explanations or metadata");

        Map<String,String> userMessage=new HashMap<>();
        userMessage.put("role","user");
        userMessage.put("content",prompt);

        List<Map<String,String>> messages =new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);

        Map<String,Object> requestBody=new HashMap<>();
        requestBody.put("model",model);
        requestBody.put("messages",messages);
        requestBody.put("temperature",0.7);
        requestBody.put("max_tokens",1024);
        requestBody.put("top_p",0.95);
        return requestBody;
    }
    private String parseFullResponse(String response) {
    try {
        JsonNode root = objectMapper.readTree(response);
        JsonNode choices = root.get("choices");
        
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode message = choices.get(0).get("message");
            if (message != null && message.has("content")) {
                return message.get("content").asText();
            }
        }
        
        return "Could not generate reply.";
    } catch (Exception e) {
        log.error("Error parsing response: {}", e.getMessage());
        return "Error parsing response.";
    }
    public boolean isAvailable(){return true;}
}
