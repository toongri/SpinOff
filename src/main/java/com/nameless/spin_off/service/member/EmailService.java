package com.nameless.spin_off.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String userEmail;

    @Async
    public void sendForRegister(String email, String authToken) {

        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(userEmail);
        smm.setTo(email);
        smm.setSubject("회원가입 이메일 인증");
        smm.setText("http://localhost:8080/api/sign/confirm-email?email="+email+"&authToken="+authToken);

        javaMailSender.send(smm);
    }
}