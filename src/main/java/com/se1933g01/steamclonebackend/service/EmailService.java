package com.se1933g01.steamclonebackend.service;

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

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // Trong thực tế, bạn nên log lỗi này
            System.err.println("Error while sending email: " + e.getMessage());
        }
    }
}
