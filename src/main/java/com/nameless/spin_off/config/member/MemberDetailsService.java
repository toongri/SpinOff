package com.nameless.spin_off.config.member;

import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        log.info("accountId : {}", accountId);
        Member member = memberRepository.findByAccountIdWithRoles(accountId).orElseThrow(NotExistMemberException::new);
        log.info("member.getRoles() : {}", member.getRoles().toString());
        return MemberDetails.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .accountPw(member.getAccountPw())
                .authorities(member.getRoles().stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                        .collect(Collectors.toList()))
                .build();
    }
}
