package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.enums.member.EmailLinkageProviderStatus;
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
public class EmailLinkage extends BaseTimeEntity {
    private static final Long MAX_EXPIRE_TIME = 5L;

    @Id
    @GeneratedValue()
    @Column(name = "email_linkage_id")
    private Long id;

    private String accountId;
    private String authToken;
    private Boolean expired;
    private LocalDateTime expireDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EmailLinkageProviderStatus provider;

    @Builder
    public EmailLinkage(String accountId, String authToken, Boolean expired, EmailLinkageProviderStatus provider) {
        this.accountId = accountId;
        this.authToken = authToken;
        this.expired = expired;
        this.provider = provider;
        this.expireDate = LocalDateTime.now().plusMinutes(MAX_EXPIRE_TIME);
    }

    public void useToken() {
        this.expired = true;
    }
}
