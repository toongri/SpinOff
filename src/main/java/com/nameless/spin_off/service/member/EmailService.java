package com.nameless.spin_off.service.member;

public interface EmailService {
    void sendForRegister(String email, String authToken);
    void sendForLinkageEmail(String email, String authToken, String accountId);
}
