package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.MemberDto.MemberRegisterRequestDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.entity.member.EmailAuth;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.sign.*;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.EmailAuthRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus.A;
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
    @Autowired EmailAuthRepository emailAuthRepository;

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
                new MemberRegisterRequestDto(accountId, accountPw, name, nickname, birth, email, null);
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
        String accountId = "bdfdfd";
        String accountPw = "dfdfdf";
        String name = "fdfdf";
        String nickname = "fdfdf";
        LocalDate birth = LocalDate.now();
        String email = "cc";
        String profileImg = null;

        MemberRegisterRequestDto memberRegisterRequestDto =
                new MemberRegisterRequestDto(accountId, accountPw, name, nickname, birth, email, "abc");

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
    public void 멤버_회원가입_형식_예외처리() throws Exception{
        //given
        String accountId = "aaaaa";
        String accountPw = "dfdfdf231";
        String name = "fdfdf";
        String nickname = "fdfdff";
        LocalDate birth = LocalDate.now();
        String email = "cc@naver.com";
        String profileImg = null;

        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email(email)
                .expired(true)
                .provider(EmailAuthProviderStatus.A)
                .build());

        MemberRegisterRequestDto memberRegisterRequestDto =
                new MemberRegisterRequestDto(accountId, accountPw, name, nickname, birth, email, "abc");

        //when
        //then
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountIdException.class);

        memberRegisterRequestDto.setAccountId("aaaaaaaaaaaaa");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountIdException.class);

        memberRegisterRequestDto.setAccountId("aaaaa%aaa");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountIdException.class);

        memberRegisterRequestDto.setAccountId("가나다라마바사아");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountIdException.class);

        memberRegisterRequestDto.setAccountId("abcdefgt");
        memberRegisterRequestDto.setAccountPw("가나다라마바사아");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("125123223");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("asbdsddd");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("!@%!@#%@@#@");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("ds23!");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("abdfsdf231!");
        memberRegisterRequestDto.setNickname("가");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectNicknameException.class);

        memberRegisterRequestDto.setNickname("rkskekfkfkfk");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectNicknameException.class);

        memberRegisterRequestDto.setNickname("rksk!");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectNicknameException.class);

        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email("@naver.com")
                .expired(true)
                .provider(EmailAuthProviderStatus.A)
                .build());
        memberRegisterRequestDto.setNickname("퉁그리_");
        memberRegisterRequestDto.setEmail("@naver.com");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectEmailException.class);

        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email("abc@naver")
                .expired(true)
                .provider(EmailAuthProviderStatus.A)
                .build());
        memberRegisterRequestDto.setEmail("abc@naver");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectEmailException.class);
        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email("abc@")
                .expired(true)
                .provider(EmailAuthProviderStatus.A)
                .build());
        memberRegisterRequestDto.setEmail("abc@");
        assertThatThrownBy(() -> memberService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(InCorrectEmailException.class);
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
}