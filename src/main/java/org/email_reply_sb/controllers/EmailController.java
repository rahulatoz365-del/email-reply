package org.email_reply_sb.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.email_reply_sb.dto.EmailRequest;
import org.email_reply_sb.dto.EmailResponse;
import org.email_reply_sb.service.OpenRouterAIService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class EmailController {
    private final OpenRouterAIService openRouterAIService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "running");
        health.put("service","Email AI Service");
        health.put("provider", "OpenRouter");
        health.put("model", "GLM-4.5-Air");
        health.put("aiAvailable", openRouterAIService.isAvailable());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/tone")
    public ResponseEntity<String[]> getAvailableTone() {
        return ResponseEntity.ok(new String[]{
                "casual","professional","friendly","aggressive"
        });
    }

    @PostMapping(value="/stream", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamReply(@RequestBody EmailRequest emailRequest) {
        return openRouterAIService.streamEmailReply(emailRequest);
    }
}
