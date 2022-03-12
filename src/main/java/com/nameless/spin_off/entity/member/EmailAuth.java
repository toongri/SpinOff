package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailAuth extends BaseTimeEntity {

    private static final Long MAX_EXPIRE_TIME = 5L;

    @Id
    @GeneratedValue()
    @Column(name = "email_auth_id")
    private Long id;

    private String email;
    private String authToken;
    private Boolean expired;
    private LocalDateTime expireDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EmailAuthProviderStatus provider;

    @Builder
    public EmailAuth(String email, String authToken, Boolean expired, EmailAuthProviderStatus provider) {
        this.email = email;
        this.authToken = authToken;
        this.expired = expired;
        this.provider = provider;
        this.expireDate = LocalDateTime.now().plusMinutes(MAX_EXPIRE_TIME);
    }

    public void useToken() {
        this.expired = true;
    }
}
