package com.nameless.spin_off.service.member;

public interface EmailService {
    void sendForRegister(String email, String authToken);
    void sendForLinkageEmail(String email, String authToken, String accountId);
    void sendForForgetAccountId(String email, String accountId);
    void sendForForgetAccountPw(String email, String accountPw);
    void sendForAuthEmail(String email, String authToken);
    void sendForUpdateEmail(String email, String authToken);
}
