package com.nameless.spin_off.config.auth.dto;

import com.nameless.spin_off.entity.member.Member;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionMember implements Serializable {
    private Long memberId;
    private String nickname;

    public SessionMember(Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
    }
}
