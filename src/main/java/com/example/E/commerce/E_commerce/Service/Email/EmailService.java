package com.example.E.commerce.E_commerce.Service.Email;

import jakarta.validation.constraints.Email;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendResetPasswordEmail(@Email String email, String resetLink)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Email Password ");
        message.setText
                (
                        "Click the link below to reset your password:\n\n"
                                + resetLink +
                                "\n\nThis link will expire in 15 minutes."
                );
        javaMailSender.send(message);
    }
}
