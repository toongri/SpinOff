package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberQueryRepositoryTest {

    @Autowired MemberQueryRepository memberQueryRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberService memberService;

    @Test
    public void 블록_체크() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Member member2 = Member.buildMember().build();
        memberRepository.save(member2);

        memberService.insertBlockedMemberByMemberId(member.getId(), member2.getId(), BlockedMemberStatus.A);

        //when
        Boolean bool1 = memberQueryRepository.isBlockedOrBlockingAboutAll(member.getId(), member2.getId());
        Boolean bool2 = memberQueryRepository.isBlockedOrBlockingAboutAll(member2.getId(), member.getId());

        //then
        assertThat(bool1).isTrue();
        assertThat(bool2).isTrue();
    }

}