package org.email_reply_sb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String subject;
    @NotBlank(message = "Message is required")
    private String message;
    @NotBlank(message = "Tone is required")
    private String tone;
    private String senderName;
    private String additionalContext;
}
