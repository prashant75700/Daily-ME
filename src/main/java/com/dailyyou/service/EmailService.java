package com.dailyyou.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        
        System.out.println("==========================================");
        System.out.println("Status: MOCK EMAIL SENT");
        System.out.println("To: " + toEmail);
        System.out.println("Subject: Password Reset Request");
        System.out.println("Body:");
        System.out.println("Click the link to reset your password:");
        System.out.println(resetLink);
        System.out.println("==========================================");
    }
}
