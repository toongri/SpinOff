package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.service.member.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/sign")
public class SignApiController {

    private final MemberService memberService;

    @PostMapping("/register")
    public SignResult<MemberRegisterResponseDto> register(@RequestBody MemberRegisterRequestDto requestDto) {
        MemberRegisterResponseDto responseDto = memberService.registerMember(requestDto);

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

    @Data
    @AllArgsConstructor
    public static class SignResult<T> {
        private T data;
    }
    public <T> SignResult<T> getResult(T data) {

        return new SignResult<>(data);
    }
}
