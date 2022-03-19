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
public class EmailServiceJpa implements EmailService{
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String userEmail;

    @Value("${spring.domain}")
    private String domain;

    @Async
    public void sendForRegister(String email, String authToken) {

        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(userEmail);
        smm.setTo(email);
        smm.setSubject("회원가입 이메일 인증");
        smm.setText("인증 코드는 " + authToken + " 입니다. 노출에 유의하십시오.");

        javaMailSender.send(smm);
    }

    @Async
    public void sendForLinkageEmail(String email, String authToken, String accountId) {

        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(userEmail);
        smm.setTo(email);
        smm.setSubject("이메일 연동 인증");
        smm.setText(domain+"/api/sign/linkage-email/check?email="
                + email + "&authToken=" + authToken + "&accountId=" + accountId);

        javaMailSender.send(smm);
    }
}