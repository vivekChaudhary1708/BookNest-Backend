package com.booknest.authuserservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\n\nThis OTP is valid for 15 minutes.");
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            System.err.println("=====================================================");
            System.err.println("WARNING: Failed to send email via SMTP.");
            System.err.println("Because real SMTP credentials are not configured, we are printing the OTP here:");
            System.err.println("TO: " + to);
            System.err.println("OTP: " + otp);
            System.err.println("=====================================================");
        }
    }
}
