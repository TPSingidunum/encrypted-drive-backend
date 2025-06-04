package com.singidunum.encrypted_drive_backend.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;

    @Async("mailExecutor")
    public void sendEmail(String to, String subject, String templateName, Map<String, Object> model) {
        logger.info("Starting sendEmail task. recipient={}, subject={}, template={}", to, subject, templateName);

        // Start timing
        long startNanos = System.nanoTime();

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setFrom("no-replay@masofino.net");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mimeMessage);

            long endNanos = System.nanoTime();

            logger.info("Email sent successfully to {} in {}", to, generateTimeString(startNanos, endNanos) );
        } catch (IOException | TemplateException | MessagingException e) {
            logger.error("Error while sending email to {}", to, e);
            throw new RuntimeException(e);
        }
    }


    public String generateTimeString(long startNanos, long stopNanos) {
        // Stop timing
        long endNanos = System.nanoTime();
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(endNanos - startNanos);

        // Compute minutes and seconds
        long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) - TimeUnit.MINUTES.toSeconds(minutes);

        String timeString;
        if (minutes > 0) {
            timeString = String.format("%dm %ds", minutes, seconds);
        } else {
            timeString = String.format("%ds", seconds);
        }

        return timeString;
    }
}