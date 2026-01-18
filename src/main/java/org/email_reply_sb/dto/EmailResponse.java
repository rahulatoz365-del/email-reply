package org.email_reply_sb.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailResponse {
    private String reply;
    private String tone;
    private boolean success;
    private String errorMsg;

    public static EmailResponse success(String reply, String tone) {
        return new EmailResponse(reply,tone,true,null);
    }
    public static EmailResponse error(String message) {
        return new EmailResponse(null,null,false,message);
    }
}
