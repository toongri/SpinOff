package com.nameless.spin_off.config.auth;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.enums.member.AuthorityOfMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public class MemberAdapter extends MemberDetails {

    private MemberDetails memberDetails;

    public MemberAdapter(Member member) {
        super(member.getId(), member.getAccountId(), member.getAccountPw(), authorities(member.getRoles()));
        this.memberDetails = new
                MemberDetails(
                        member.getId(), member.getAccountId(), member.getAccountPw(), authorities(member.getRoles()));
    }

    private static Set<GrantedAuthority> authorities(Set<AuthorityOfMemberStatus> roles) {
        return roles.stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                .collect(Collectors.toSet());
    }

    public MemberDetails getMemberDetails() {
        return memberDetails;
    }

}
