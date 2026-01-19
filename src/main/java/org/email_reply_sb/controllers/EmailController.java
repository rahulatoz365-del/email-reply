package org.email_reply_sb.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.email_reply_sb.dto.EmailRequest;
import org.email_reply_sb.dto.EmailResponse;
import org.email_reply_sb.service.OpenRouterAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        health.put("service", "Email AI Service");
        health.put("aiAvailable", openRouterAIService.isAvailable());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/tones")
    public ResponseEntity<String[]> getAvailableTones() {
        return ResponseEntity.ok(new String[]{
                "casual", "professional", "friendly", "aggressive"
        });
    }

    @PostMapping("/generate")
    public ResponseEntity<EmailResponse> generateReply(@RequestBody @Valid EmailRequest emailRequest) {
        try {
            log.info("Generating reply for tone: {}", emailRequest.getTone());
            String reply = openRouterAIService.generateEmailReply(emailRequest);
            return ResponseEntity.ok(EmailResponse.success(reply, emailRequest.getTone()));
        } catch (Exception e) {
            log.error("Error generating reply: {}", e.getMessage());
            return ResponseEntity.ok(EmailResponse.error(e.getMessage()));
        }
    }
}
