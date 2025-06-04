package com.singidunum.encrypted_drive_backend.controllers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailController {
    private final JavaMailSender mailSender;

    @GetMapping("/send")
    public String sendEmail() {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("no-replay@masofino.net");
        mail.setSubject("Test");
        mail.setTo("tpetrovic@singidunum.ac.rs");
        try (var inputStream = EmailController.class.getResourceAsStream("/templates/mail/email-test.html")) {
            assert inputStream != null;
            mail.setText( new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mailSender.send(mail);

        return "Email sent";
    }

    @GetMapping("/send-with-attachment")
    public String sendEmailWithAttachment() throws MessagingException, URISyntaxException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("no-replay@masofino.net");
        helper.setSubject("Test");
        helper.setTo("tpetrovic@singidunum.ac.rs");
        try (var inputStream = EmailController.class.getResourceAsStream("/templates/mail/email-test.html")) {
            assert inputStream != null;
            helper.setText( new String(inputStream.readAllBytes(), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }        helper.addAttachment("atachment.sql", new File(Objects.requireNonNull(EmailController.class.getClassLoader().getResource("database/encrypted_drive.sql")).toURI()));

        mailSender.send(mimeMessage);
        return "Email sent";
    }
}
