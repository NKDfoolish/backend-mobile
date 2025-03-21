package com.myproject.controller;

import com.myproject.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL_CONTROLLER")
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/send-email")
    public void sendEmail(@RequestParam String toEmail, @RequestParam String subject, @RequestParam String body) {
        log.info("Sending email to: {}", toEmail);
        emailService.sendEmail(toEmail, subject, body);
        log.info("Email sent successfully!");
    }
}