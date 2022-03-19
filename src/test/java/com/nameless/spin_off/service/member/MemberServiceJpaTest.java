package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.MemberDto.MemberRegisterRequestDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

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
    @Autowired CollectionRepository collectionRepository;
    @Autowired PostRepository postRepository;

    @Test
    public void 멤버_회원가입() throws Exception{
        //given
        String accountId = "aa";
        String accountPw = "aa";
        String name = "dd";
        String nickname = "ddd";
        LocalDate birth = LocalDate.now();
        String email = "cc";
        String profileImg = null;

        MemberRegisterRequestDto memberRegisterRequestDto =
                new MemberRegisterRequestDto(accountId, accountPw, name, nickname, birth, email);
        //when
        Long aLong = memberService.insertMemberByMemberVO(memberRegisterRequestDto);

        em.flush();
        //then
        Member member = memberRepository.getById(aLong);

        assertThat(member.getAccountId()).isEqualTo(accountId);
        assertThat(member.getAccountPw()).isEqualTo(accountPw);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getBirth()).isEqualTo(birth);
        assertThat(member.getEmail()).isEqualTo(email);
        assertThat(member.getProfileImg()).isEqualTo(profileImg);
    }

    @Test
    public void 멤버_회원가입_예외처리() throws Exception{
        //given
        String accountId = "aa";
        String accountPw = "aa";
        String name = "dd";
        String nickname = "ddd";
        LocalDate birth = LocalDate.now();
        String email = "cc";
        String profileImg = null;

        MemberRegisterRequestDto memberRegisterRequestDto =
                new MemberRegisterRequestDto(accountId, accountPw, name, nickname, birth, email);

        //when
        Long aLong = memberService.insertMemberByMemberVO(memberRegisterRequestDto);
        em.flush();

        //then
        assertThatThrownBy(() -> memberService.insertMemberByMemberVO(memberRegisterRequestDto))
                .isInstanceOf(AlreadyAccountIdException.class);

        memberRegisterRequestDto.setNickname("");
        assertThatThrownBy(() -> memberService.insertMemberByMemberVO(memberRegisterRequestDto))
                .isInstanceOf(AlreadyAccountIdException.class);

        memberRegisterRequestDto.setNickname(nickname);
        memberRegisterRequestDto.setAccountId("");
        assertThatThrownBy(() -> memberService.insertMemberByMemberVO(memberRegisterRequestDto))
                .isInstanceOf(AlreadyNicknameException.class);
    }

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
}