package com.myproject.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL_SERVICE")
public class EmailService {

    private final SendGrid sendGrid;

    @Value("${spring.sendgrid.from-email}")
    private String fromEmail;

    @Value("${spring.sendgrid.template-id}")
    private String templateId;

    @Value("${spring.sendgrid.verification-link}")
    private String verificationLink;

    /**
     * Send email by SendGrid
     * @param toEmail send email to someone
     * @param subject subject of email
     * @param body content of email
     * @return
     */
    public String sendEmail(String toEmail, String subject, String body) {
        Email from = new Email(fromEmail); // Email của bạn
        Email to = new Email(toEmail);

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);

            // Kiểm tra kết quả phản hồi từ SendGrid
            if (response.getStatusCode() == 202) {
                return "Email sent successfully!";
            } else {
                return "Failed to send email: " + response.getBody();
            }

        } catch (IOException e) {
            return "Error occurred while sending email: " + e.getMessage();
        }
    }

    /**
     * Send email verification
     * @param toEmail
     * @param name
     * @throws IOException
     */
    public void emailVerification(String toEmail, String name) throws IOException {
        log.info("Sending email verification to {}", toEmail);

        Email from = new Email(fromEmail, "Mr.D"); // Email của bạn
        Email to = new Email(toEmail);

        String subject = "Email Verification";

        String secretCode = String.format("?secretCode=%s", UUID.randomUUID());

        // TODO generate secret code and save to database

        // Define template
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("verification_link", verificationLink + secretCode);

        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        // Add to dynamic data
        map.forEach(personalization::addDynamicTemplateData);

        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if (response.getStatusCode() == 202) {
            log.info("Verification sent successfully!");
        } else {
            log.error("Failed to send verification: {}", response.getBody());
        }
    }
}
