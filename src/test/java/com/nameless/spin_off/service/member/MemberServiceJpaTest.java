package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.MemberDto.MemberInfoDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.member.AlreadyBlockedMemberException;
import com.nameless.spin_off.exception.member.AlreadyFollowedMemberException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.sign.IncorrectAccountPwException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.collection.PublicOfCollectionStatus.A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class MemberServiceJpaTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    public void 멤버_팔로우_멤버() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Member followedM = Member.buildMember().build();
        Long followedMemberId = memberRepository.save(followedM).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        memberService.insertFollowedMemberByMemberId(memberId, followedMemberId);

        em.flush();

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(memberId);
        Member newFollowedMember = memberRepository.getById(followedMemberId);
        newFollowedMember.updatePopularity();

        //then
        assertThat(newMember.getId()).isEqualTo(memberId);
        assertThat(newMember.getFollowedMembers().size()).isEqualTo(1);
        assertThat(newMember.getFollowedMembers().iterator().next().getMember().getId()).isEqualTo(followedMemberId);
        assertThat(newFollowedMember.getFollowingMembers().size()).isEqualTo(1);
        assertThat(newFollowedMember.getPopularity()).isEqualTo(1.0);
    }

    @Test
    public void 멤버_팔로우_멤버_예외처리() throws Exception{
        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Member followedMember = Member.buildMember().build();
        Long followedMemberId = memberRepository.save(followedMember).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long aLong = memberService.insertFollowedMemberByMemberId(memberId, followedMemberId);

        //then
        assertThatThrownBy(() -> memberService.insertFollowedMemberByMemberId(memberId, followedMemberId))
                .isInstanceOf(AlreadyFollowedMemberException.class);
        assertThatThrownBy(() -> memberService.insertFollowedMemberByMemberId(memberId, -1L))
                .isInstanceOf(NotExistMemberException.class);
    }

    @Test
    public void 멤버_블락_멤버() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Member blockedMember = Member.buildMember().build();
        Long blockedMemberId = memberRepository.save(blockedMember).getId();
        Long aLong = collectionService
                .insertCollectionByCollectionVO(new CollectionDto.CreateCollectionVO("", "", A), member.getId());

        Long aLong1 = collectionService
                .insertCollectionByCollectionVO(new CollectionDto.CreateCollectionVO("", "", A), blockedMember.getId());

        memberService.insertFollowedMemberByMemberId(memberId, blockedMemberId);
        collectionService.insertFollowedCollectionByMemberId(memberId, aLong1);
        collectionService.insertFollowedCollectionByMemberId(blockedMemberId, aLong);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        memberService.insertBlockedMemberByMemberId(memberId, blockedMemberId, BlockedMemberStatus.A);

        em.flush();

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(memberId);
        Member newBlockedMember = memberRepository.getById(blockedMemberId);
        newBlockedMember.updatePopularity();

        //then
        assertThat(newMember.getId()).isEqualTo(memberId);
        assertThat(newMember.getFollowedMembers().size()).isEqualTo(0);
        assertThat(newMember.getFollowedCollections().size()).isEqualTo(0);
        assertThat(newBlockedMember.getFollowedCollections().size()).isEqualTo(0);
        assertThat(newMember.getBlockedMembers().size()).isEqualTo(1);
        assertThat(newMember.getBlockedMembers().iterator().next().getMember().getId()).isEqualTo(blockedMemberId);
        assertThat(newBlockedMember.getBlockingMembers().size()).isEqualTo(1);
        assertThat(newBlockedMember.getPopularity()).isEqualTo(1);
    }

    @Test
    public void 멤버_블락_멤버_예외처리() throws Exception{
        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();
        Member blockedMember = Member.buildMember().build();
        Long blockedMemberId = memberRepository.save(blockedMember).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        memberService.insertBlockedMemberByMemberId(memberId, blockedMemberId, BlockedMemberStatus.A);

        System.out.println("멤버함수");
        Member newMember = memberRepository.getById(memberId);

        //then
        assertThatThrownBy(() -> memberService.insertBlockedMemberByMemberId(memberId, blockedMemberId, BlockedMemberStatus.A))
                .isInstanceOf(AlreadyBlockedMemberException.class);
        assertThatThrownBy(() -> memberService.insertBlockedMemberByMemberId(memberId, -1L, BlockedMemberStatus.A))
                .isInstanceOf(NotExistMemberException.class);
    }
    
    @Test
    public void 멤버_정보_수정() throws Exception{
        //given

        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        em.flush();
        
        //when
        Long count = memberService.updateMemberInfo(memberId, new MemberInfoDto("1", "member/2", "3", "4", "5"));
        em.flush();

        Long count2 = memberService.updateMemberInfo(memberId, new MemberInfoDto("0", "member/2", "2", "3", "5"));
        em.flush();

        Long count3 = memberService.updateMemberInfo(memberId, new MemberInfoDto("0", "member/2", "2", "3", "5"));
        em.flush();


        //then
        assertThat(count).isEqualTo(5L);
        assertThat(count2).isEqualTo(3L);
        assertThat(count3).isEqualTo(0L);
    }

    @Test
    public void 비밀번호_수정() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("abcd12@gmail.com")
                .setNickname("abcdd")
                .setAccountId("abcd232")
                .setAccountPw(passwordEncoder.encode("abc")).build();
        Long memberId = memberRepository.save(member).getId();
        em.flush();

        //when


        //then
        assertThat(memberService.isMatchedPassword(
                MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),"abc")).isTrue();

        assertThat(memberService.updateMemberPassword(memberId, "abcd1234")).isTrue();
        assertThat(memberService.isMatchedPassword(
                MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),"abcd1234")).isTrue();

        assertThatThrownBy(() -> memberService.updateMemberPassword(memberId, "bad"))
                .isInstanceOf(IncorrectAccountPwException.class);

    }
}