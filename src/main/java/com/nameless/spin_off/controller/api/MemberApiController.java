package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.MemberDto.CreateMemberVO;
import com.nameless.spin_off.entity.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.member.*;
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
    public MemberApiResult<Long> createOne(@RequestBody CreateMemberVO createMemberVO)
            throws AlreadyAccountIdException, AlreadyNicknameException {

        Long memberId = memberService.insertMemberByMemberVO(createMemberVO);

        return new MemberApiResult<Long>(memberId);
    }

    @PostMapping("/follow")
    public MemberApiResult<Long> createFollowOne(@RequestParam Long memberId, @RequestParam Long followedMemberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {
        Long followedId = memberService.insertFollowedMemberByMemberId(memberId, followedMemberId);

        return new MemberApiResult<Long>(followedId);
    }

    @PostMapping("/block")
    public MemberApiResult<Long> createBlockOne(
            @RequestParam Long memberId, @RequestParam Long blockedMemberId,
            @RequestParam BlockedMemberStatus blockedMemberStatus)
            throws AlreadyFollowedMemberException, NotExistMemberException, AlreadyBlockedMemberException {

        Long blockId = memberService.insertBlockedMemberByMemberId(memberId, blockedMemberId, blockedMemberStatus);

        return new MemberApiResult<Long>(blockId);
    }


    @Data
    @AllArgsConstructor
    public static class MemberApiResult<T> {
        private T data;
    }
}
