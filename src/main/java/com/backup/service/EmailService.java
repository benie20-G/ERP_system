package com.backup.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true indicates HTML content
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    
    public void sendVerificationEmail(String to, String name, String token) {
        String subject = "Account Verification";
        String body = "<html><body>"
                + "<h2>Hello " + name + ",</h2>"
                + "<p>Please click on the link below to verify your account:</p>"
                + "<a href='http://localhost:8080/api/auth/verify?token=" + token + "'>Verify Account</a>"
                + "<p>This link will expire in 24 hours.</p>"
                + "<p>Thank you,<br/>ERP System Team</p>"
                + "</body></html>";
        
        sendEmail(to, subject, body);
    }
    
    public void sendPasswordResetEmail(String to, String name, String token) {
        String subject = "Password Reset Request";
        String body = "<html><body>"
                + "<h2>Hello " + name + ",</h2>"
                + "<p>You have requested to reset your password. Please click on the link below to reset your password:</p>"
                + "<a href='http://localhost:8080/api/auth/reset-password?token=" + token + "'>Reset Password</a>"
                + "<p>This link will expire in 1 hour.</p>"
                + "<p>If you did not request a password reset, please ignore this email.</p>"
                + "<p>Thank you,<br/>ERP System Team</p>"
                + "</body></html>";
        
        sendEmail(to, subject, body);
    }
    
    public void sendSalaryNotification(String to, String name, String month, String year, String institution, Double amount, String employeeId) {
        String subject = "Salary Notification";
        String body = "<html><body>"
                + "<h2>Dear " + name + ",</h2>"
                + "<p>Your salary of " + month + "/" + year + " from " + institution 
                + " <strong>" + String.format("%.2f", amount) + "</strong> has been credited to your <strong>" 
                + employeeId + "</strong> account successfully.</p>"
                + "<p>Thank you,<br/>ERP System Team</p>"
                + "</body></html>";
        
        sendEmail(to, subject, body);
    }
}