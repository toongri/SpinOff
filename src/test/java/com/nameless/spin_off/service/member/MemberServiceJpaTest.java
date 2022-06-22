package com.nameless.spin_off.service.member;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.MemberDto.MemberInfoRequestDto;
import com.nameless.spin_off.dto.MemberDto.MemberProfileRequestDto;
import com.nameless.spin_off.entity.member.EmailAuth;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.exception.member.AlreadyBlockedMemberException;
import com.nameless.spin_off.exception.member.AlreadyFollowedMemberException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.sign.IncorrectAccountIdException;
import com.nameless.spin_off.exception.sign.IncorrectAccountPwException;
import com.nameless.spin_off.exception.sign.IncorrectNicknameException;
import com.nameless.spin_off.repository.member.EmailAuthRepository;
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
    @Autowired EmailAuthRepository emailAuthRepository;

    @Test
    public void 멤버_팔로우_멤버() throws Exception{

        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        Member followedM = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        Member followedMember = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        Member blockedMember = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long blockedMemberId = memberRepository.save(blockedMember).getId();
        Long aLong = collectionService
                .insertCollection(new CollectionDto.CollectionRequestDto("aaa", "", A), member.getId());

        Long aLong1 = collectionService
                .insertCollection(new CollectionDto.CollectionRequestDto("aaa", "", A), blockedMember.getId());

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
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        Member blockedMember = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
    public void 멤버_프로필_수정() throws Exception{
        //given

        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01000000000")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        em.flush();
        
        //when
        Long count = memberService.updateMemberProfile(memberId, new MemberProfileRequestDto(
                "mename1",
                "memberAcc2",
                "4",
                "5"), null);
        em.flush();

        Long count2 = memberService.updateMemberProfile(memberId, new MemberProfileRequestDto(
                "mename2",
                "memberAcc3",
                "3",
                "5"), null);
        em.flush();

        Long count3 = memberService.updateMemberProfile(memberId, new MemberProfileRequestDto(
                "mename2",
                "memberAcc3",
                "3",
                "5"), null);
        em.flush();

        //then
        assertThat(count).isEqualTo(4L);
        assertThat(count2).isEqualTo(3L);
        assertThat(count3).isEqualTo(0L);

        assertThatThrownBy(() -> memberService.updateMemberProfile(memberId, new MemberProfileRequestDto(
                "a",
                "memberAcc3",
                "3",
                "5"), null)).isInstanceOf(IncorrectNicknameException.class);

        assertThatThrownBy(() -> memberService.updateMemberProfile(memberId, new MemberProfileRequestDto(
                "mename2",
                "and",
                "3",
                "5"), null)).isInstanceOf(IncorrectAccountIdException.class);
    }

    @Test
    public void 멤버_정보_수정() throws Exception{
        //given

        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setPhoneNumber("01011111111")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();


        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email("jhkimkkk@naver.com")
                .expired(true)
                .provider(EmailAuthProviderStatus.C)
                .build());
        em.flush();

        //when
        Long count = memberService.updateMemberInfo(memberId, new MemberInfoRequestDto(
                "abc",
                "jhkimkkk@naver.com",
                "0101111111",
                LocalDate.now().minusDays(1)));
        em.flush();

        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email("jhkimkkk2323@naver.com")
                .expired(true)
                .provider(EmailAuthProviderStatus.C)
                .build());
        em.flush();

        Long count2 = memberService.updateMemberInfo(memberId, new MemberInfoRequestDto(
                "abc",
                "jhkimkkk2323@naver.com",
                "01011111112",
                LocalDate.now().minusDays(1)));
        em.flush();

        Long count3 = memberService.updateMemberInfo(memberId, new MemberInfoRequestDto(
                "abc",
                "jhkimkkk2323@naver.com",
                "01011111112",
                LocalDate.now().minusDays(1)));
        em.flush();


        //then
        assertThat(count).isEqualTo(3L);
        assertThat(count2).isEqualTo(2L);
        assertThat(count3).isEqualTo(0L);
    }

    @Test
    public void 비밀번호_수정() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setNickname("memcname")
                .setPhoneNumber("01011111111")
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