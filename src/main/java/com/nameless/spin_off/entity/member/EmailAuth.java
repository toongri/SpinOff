package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuth extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_auth_id")
    private Long id;
    private String email;
    private String authToken;
    private Boolean expired;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EmailAuthProviderStatus provider;

    @Builder
    public EmailAuth(String email, String authToken, Boolean expired, EmailAuthProviderStatus provider) {
        this.email = email;
        this.authToken = authToken;
        this.expired = expired;
        this.provider = provider;
    }

    public void useToken() {
        this.expired = true;
    }
}
