package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    private String email;
    private String authToken;
    private Boolean expired;
    private LocalDateTime expireDate;

    @Builder
    public EmailLinkage(String accountId, String email, String authToken, Boolean expired) {
        this.accountId = accountId;
        this.email = email;
        this.authToken = authToken;
        this.expired = expired;
        this.expireDate = LocalDateTime.now().plusMinutes(MAX_EXPIRE_TIME);
    }

    public void useToken() {
        this.expired = true;
    }
}
