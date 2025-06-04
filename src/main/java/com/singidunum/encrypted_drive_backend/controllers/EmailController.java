package com.singidunum.encrypted_drive_backend.controllers;
import com.singidunum.encrypted_drive_backend.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @GetMapping("/send")
    public String sendEmail() {
        emailService.sendEmail(
                "tpetrovic@singidunum.ac.rs",
                "Verify Account",
                "mail/verify.ftlh",
                Map.of("username", "User", "link", "https://example.com/verify", "year", "2025")
        );
        return "Email sent";
    }
}
