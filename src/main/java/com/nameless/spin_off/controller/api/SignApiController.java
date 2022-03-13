package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum;
import com.nameless.spin_off.exception.member.NotCorrectEmailRequest;
import com.nameless.spin_off.service.member.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sign")
public class SignApiController {

    private final MemberService memberService;

    @PostMapping("/register")
    public SignResult<MemberRegisterResponseDto> register(@RequestBody MemberRegisterRequestDto requestDto) {
        MemberRegisterResponseDto responseDto = memberService.registerMember(requestDto);

        log.info("register");
        log.info("accountId : {}", requestDto.getAccountId());
        log.info("accountPw : {}", requestDto.getAccountPw());
        log.info("name : {}", requestDto.getName());
        log.info("nickname : {}", requestDto.getNickname());
        log.info("birth : {}", requestDto.getBirth());
        log.info("email : {}", requestDto.getEmail());
        return getResult(responseDto);
    }

    @PostMapping("/login")
    public SignResult<MemberLoginResponseDto> login(@RequestBody MemberLoginRequestDto requestDto) {
        MemberLoginResponseDto responseDto = memberService.loginMember(requestDto);
        return getResult(responseDto);
    }

    @PostMapping("/reissue")
    public SignResult<TokenResponseDto> reIssue(@RequestBody TokenRequestDto tokenRequestDto) {
        TokenResponseDto responseDto = memberService.reIssue(tokenRequestDto);
        return getResult(responseDto);
    }

    @GetMapping("/confirm-email")
    public SignResult<String> confirmEmail(@ModelAttribute EmailAuthRequestDto requestDto) {
        memberService.confirmEmail(requestDto);
        return getResult("인증이 완료되었습니다.");
    }

    @GetMapping("/linkage-email/check")
    public SignResult<String> linkageEmail(@ModelAttribute EmailLinkageCheckRequestDto requestDto) {
        memberService.emailLinkageCheck(requestDto);
        return getResult("연동이 완료되었습니다.");
    }

    @GetMapping("/linkage-email/naver")
    public SignResult<String> linkageEmailNaver(@RequestBody EmailLinkageUpdateRequestDto requestDto) {
        if (isNotCorrectEmail(requestDto.getEmail(), NAVER)) {
            throw new NotCorrectEmailRequest();
        }
        memberService.emailLinkageUpdate(requestDto);
        return getResult("메일을 확인하여 주시기 바랍니다.");
    }

    @GetMapping("/linkage-email/kakao")
    public SignResult<String> linkageEmailKakao(@RequestBody EmailLinkageUpdateRequestDto requestDto) {
        if (isNotCorrectEmail(requestDto.getEmail(), KAKAO)) {
            throw new NotCorrectEmailRequest();
        }
        memberService.emailLinkageUpdate(requestDto);
        return getResult("메일을 확인하여 주시기 바랍니다.");
    }

    @GetMapping("/linkage-email/google")
    public SignResult<String> linkageEmailGoogle(@RequestBody EmailLinkageUpdateRequestDto requestDto) {
        if (isNotCorrectEmail(requestDto.getEmail(), GOOGLE)) {
            throw new NotCorrectEmailRequest();
        }
        memberService.emailLinkageUpdate(requestDto);
        return getResult("메일을 확인하여 주시기 바랍니다.");
    }

    private Boolean isNotCorrectEmail(String email, EmailLinkageServiceEnum provider) {
        return !getProviderByEmail(email).equals(provider.getValue());
    }

    private String getProviderByEmail(String email) {
        return email.substring(email.indexOf("@") + 1, email.indexOf("."));
    }

    @Data
    @AllArgsConstructor
    public static class SignResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> SignResult<T> getResult(T data) {
        return new SignResult<>(data, true, "0", "성공");
    }
}
