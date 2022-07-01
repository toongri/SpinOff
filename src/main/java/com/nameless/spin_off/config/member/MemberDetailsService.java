package com.nameless.spin_off.config.member;

import com.nameless.spin_off.config.auth.MemberAdapter;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        log.debug("accountId : {}", accountId);
        Member member = memberRepository.findByAccountIdWithRoles(accountId)
                .orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER));

        log.info("currentMemberId : {}", member.getId());
        log.debug("currentMember.getRoles() : {}", member.getRoles().toString());

        return new MemberAdapter(member);
    }
}
