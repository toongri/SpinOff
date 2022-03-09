package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.MemberDto.MemberLoginRequestDto;
import com.nameless.spin_off.dto.MemberDto.MemberLoginResponseDto;
import com.nameless.spin_off.dto.MemberDto.MemberRegisterRequestDto;
import com.nameless.spin_off.dto.MemberDto.MemberRegisterResponseDto;
import com.nameless.spin_off.service.member.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/login")
    public SignResult<MemberLoginResponseDto> login(@RequestBody MemberLoginRequestDto requestDto) {
        MemberLoginResponseDto responseDto = memberService.loginMember(requestDto);
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
