package com.vin.VinSystem.Auth.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MailService {

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Value("${mail.from}")
    private String mailFrom;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public String sendOtp(String toEmail) {
        String otp = generateOtp();
        sendEmail(toEmail, "VinSystem - Mã OTP", "Mã OTP của bạn là: " + otp);
        return otp;
    }

    @Async
    public void sendAccountInfo(String toEmail, String username, String password) {
        String text = "Xin chào!\n\nTài khoản VinSystem đã được tạo.\n\n" +
                "Tên đăng nhập: " + username + "\nMật khẩu: " + password +
                "\n\nVui lòng đổi mật khẩu sau khi đăng nhập.\n\nTrân trọng,\nVinCar";
        sendEmail(toEmail, "VinSystem - Thông tin tài khoản", text);
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + sendgridApiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("personalizations", List.of(
                Map.of("to", List.of(Map.of("email", to)))
            ));
            body.put("from", Map.of("email", mailFrom));
            body.put("subject", subject);
            body.put("content", List.of(
                Map.of("type", "text/plain", "value", text)
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(
                "https://api.sendgrid.com/v3/mail/send",
                request,
                String.class
            );
        } catch (Exception e) {
            System.err.println("Lỗi gửi mail SendGrid: " + e.getMessage());
        }
    }
    @Async
public void sendEmailWithPdf(String to, String subject, String text,
                              byte[] pdfBytes, String filename) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + sendgridApiKey);

        String base64Pdf = java.util.Base64.getEncoder().encodeToString(pdfBytes);

        Map<String, Object> body = new HashMap<>();
        body.put("personalizations", List.of(
            Map.of("to", List.of(Map.of("email", to)))
        ));
        body.put("from", Map.of("email", mailFrom));
        body.put("subject", subject);
        body.put("content", List.of(
            Map.of("type", "text/plain", "value", text)
        ));
        body.put("attachments", List.of(Map.of(
            "content",     base64Pdf,
            "type",        "application/pdf",
            "filename",    filename,
            "disposition", "attachment"
        )));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(
            "https://api.sendgrid.com/v3/mail/send",
            request,
            String.class
        );
    } catch (Exception e) {
        System.err.println("Lỗi gửi mail+PDF SendGrid: " + e.getMessage());
    }
}
}