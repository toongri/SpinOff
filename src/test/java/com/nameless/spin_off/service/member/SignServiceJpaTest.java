package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.MemberDto.MemberRegisterRequestDto;
import com.nameless.spin_off.entity.enums.member.EmailAuthProviderStatus;
import com.nameless.spin_off.entity.member.EmailAuth;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.sign.IncorrectAccountIdException;
import com.nameless.spin_off.exception.sign.IncorrectAccountPwException;
import com.nameless.spin_off.exception.sign.IncorrectEmailException;
import com.nameless.spin_off.exception.sign.IncorrectNicknameException;
import com.nameless.spin_off.repository.member.EmailAuthRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SignServiceJpaTest {
    @Autowired SignService signService;
    @Autowired EntityManager em;
    @Autowired MemberRepository memberRepository;
    @Autowired EmailAuthRepository emailAuthRepository;

    @Test
    public void 멤버_회원가입() throws Exception{
        //given
        String accountId = "abcdefgt";
        String accountPw = "dfdfdf231";
        String name = "fdfdf";
        String nickname = "fdfdff";
        LocalDate birth = LocalDate.now();
        String email = "cc@naver.com";

        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email(email)
                .expired(true)
                .provider(EmailAuthProviderStatus.A)
                .build());

        MemberRegisterRequestDto memberRegisterRequestDto =
                new MemberRegisterRequestDto(accountId, accountPw, name, nickname, birth, email, "abc");
        //when
        signService.registerMember(memberRegisterRequestDto);

        em.flush();
        //then
        Member member = memberRepository.findOneByAccountId(accountId).get();

        assertThat(member.getAccountId()).isEqualTo(accountId);
        assertThat(member.getName()).isEqualTo(name);
        assertThat(member.getNickname()).isEqualTo(nickname);
        assertThat(member.getBirth()).isEqualTo(birth);
        assertThat(member.getEmail()).isEqualTo(email);
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
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountIdException.class);

        memberRegisterRequestDto.setAccountId("aaaaaaaaaaaaa");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountIdException.class);

        memberRegisterRequestDto.setAccountId("aaaaa%aaa");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountIdException.class);

        memberRegisterRequestDto.setAccountId("가나다라마바사아");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountIdException.class);

        memberRegisterRequestDto.setAccountId("abcdefgt");
        memberRegisterRequestDto.setAccountPw("가나다라마바사아");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("125123223");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("asbdsddd");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("!@%!@#%@@#@");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("ds23!");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectAccountPwException.class);

        memberRegisterRequestDto.setAccountPw("abdfsdf231!");
        memberRegisterRequestDto.setNickname("가");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectNicknameException.class);

        memberRegisterRequestDto.setNickname("rkskekfkfkfk");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectNicknameException.class);

        memberRegisterRequestDto.setNickname("rksk!");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectNicknameException.class);

        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email("@naver.com")
                .expired(true)
                .provider(EmailAuthProviderStatus.A)
                .build());
        memberRegisterRequestDto.setNickname("퉁그리_");
        memberRegisterRequestDto.setEmail("@naver.com");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectEmailException.class);

        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email("abc@naver")
                .expired(true)
                .provider(EmailAuthProviderStatus.A)
                .build());
        memberRegisterRequestDto.setEmail("abc@naver");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectEmailException.class);
        emailAuthRepository.save(EmailAuth.builder()
                .authToken("abc")
                .email("abc@")
                .expired(true)
                .provider(EmailAuthProviderStatus.A)
                .build());
        memberRegisterRequestDto.setEmail("abc@");
        assertThatThrownBy(() -> signService.registerMember(memberRegisterRequestDto))
                .isInstanceOf(IncorrectEmailException.class);
    }
}