package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MemberQueryRepositoryTest {

    @Autowired MemberQueryRepository memberQueryRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;

    @Test
    public void 블록_체크() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);
        Member member2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member2);

        memberService.insertBlockedMemberByMemberId(member.getId(), member2.getId(), BlockedMemberStatus.A);

        //when
        Boolean bool1 = memberQueryRepository.isBlockedOrBlockingAndStatus(member.getId(), member2.getId(), BlockedMemberStatus.A);
        Boolean bool2 = memberQueryRepository.isBlockedOrBlockingAndStatus(member2.getId(), member.getId(), BlockedMemberStatus.A);

        //then
        assertThat(bool1).isTrue();
        assertThat(bool2).isTrue();
    }

    @Test
    public void readMember체크() throws Exception{
        //given

        //when
        assertThatThrownBy(() -> memberQueryRepository.findByIdForRead(0L, Collections.emptyList())
                .orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER)))
                .isInstanceOf(NotExistMemberException.class);
        //then

    }

}