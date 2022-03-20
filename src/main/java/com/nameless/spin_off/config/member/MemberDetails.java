package com.nameless.spin_off.config.member;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class MemberDetails implements UserDetails {

    private Long id;
    private String accountId;
    private String accountPw;
    Set<GrantedAuthority> authorities;

    @Builder
    public MemberDetails(Long id, String accountId, String accountPw, Set<GrantedAuthority> authorities) {
        this.id = id;
        this.accountId = accountId;
        this.accountPw = accountPw;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return accountPw;
    }

    @Override
    public String getUsername() {
        return accountId;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
