package com.se1933g01.steam_clone_backend.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author Loc Phan
 */

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public void sendOtpEmail(String email, String otp, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText("Your OTP code is: " + otp + "\nIt will expire in 10 minutes.");
        mailSender.send(message);
    }
    
}
