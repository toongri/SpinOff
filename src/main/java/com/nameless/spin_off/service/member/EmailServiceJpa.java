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

    @Override
    @Async
    public void sendForRegister(String email, String authToken) {

        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(userEmail);
        smm.setTo(email);
        smm.setSubject("회원가입 이메일 인증");
        smm.setText("인증 코드는 " + authToken + " 입니다. 노출에 유의하십시오.");

        javaMailSender.send(smm);
    }

    @Override
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

    @Override
    @Async
    public void sendForForgetAccountId(String email, String accountId) {
        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(userEmail);
        smm.setTo(email);
        smm.setSubject("아이디 찾기");
        smm.setText("accountId : " + accountId);

        javaMailSender.send(smm);
    }

    @Override
    @Async
    public void sendForForgetAccountPw(String email, String accountPw) {
        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(userEmail);
        smm.setTo(email);
        smm.setSubject("비밀번호 재생성");
        smm.setText("accountPw : " + accountPw);

        javaMailSender.send(smm);
    }

    @Override
    @Async
    public void sendForAuthEmail(String email, String authToken) {
        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(userEmail);
        smm.setTo(email);
        smm.setSubject("본인 이메일 인증");
        smm.setText("인증 코드는 " + authToken + " 입니다. 노출에 유의하십시오.");

        javaMailSender.send(smm);
    }

    @Override
    @Async
    public void sendForUpdateEmail(String email, String authToken) {
        SimpleMailMessage smm = new SimpleMailMessage();

        smm.setFrom(userEmail);
        smm.setTo(email);
        smm.setSubject("이메일 업데이트 인증");
        smm.setText("인증 코드는 " + authToken + " 입니다. 노출에 유의하십시오.");

        javaMailSender.send(smm);
    }
}