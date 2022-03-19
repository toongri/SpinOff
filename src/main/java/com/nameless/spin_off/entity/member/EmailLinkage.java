package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailLinkage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_linkage_id")
    private Long id;

    private String accountId;
    private String email;
    private String authToken;
    private Boolean expired;

    @Builder
    public EmailLinkage(String accountId, String email, String authToken, Boolean expired) {
        this.accountId = accountId;
        this.email = email;
        this.authToken = authToken;
        this.expired = expired;
    }

    public void useToken() {
        this.expired = true;
    }
}
