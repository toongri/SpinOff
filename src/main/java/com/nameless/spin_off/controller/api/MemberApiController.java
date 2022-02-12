package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.MemberDto.CreateMemberVO;
import com.nameless.spin_off.exception.member.AlreadyAccountIdException;
import com.nameless.spin_off.exception.member.AlreadyNicknameException;
import com.nameless.spin_off.service.member.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("")
    public MemberApiResult<Long> createMemberOne(@RequestBody CreateMemberVO createMemberVO)
            throws AlreadyAccountIdException, AlreadyNicknameException {

        Long memberId = memberService.insertMemberByMemberVO(createMemberVO);

        return new MemberApiResult<Long>(memberId);
    }

    @Data
    @AllArgsConstructor
    public static class MemberApiResult<T> {
        private T data;
    }
}
