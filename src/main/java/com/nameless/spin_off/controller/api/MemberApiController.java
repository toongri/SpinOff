package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.MemberDto.MemberRegisterRequestDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.service.member.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberApiController {

    private final MemberService memberService;

    @PostMapping("")
    public MemberApiResult<Long> createOne(@RequestBody MemberRegisterRequestDto memberRegisterRequestDto)
            throws AlreadyAccountIdException, AlreadyNicknameException {

        log.info("createOne");
        log.info("accountId : {}", memberRegisterRequestDto.getAccountId());
        log.info("accountPw : {}", memberRegisterRequestDto.getAccountPw());
        log.info("name : {}", memberRegisterRequestDto.getName());
        log.info("nickname : {}", memberRegisterRequestDto.getNickname());
        log.info("birth : {}", memberRegisterRequestDto.getBirth());
        log.info("email : {}", memberRegisterRequestDto.getEmail());

        return new MemberApiResult<>(memberService.insertMemberByMemberVO(memberRegisterRequestDto));
    }

    @PostMapping("/{followedMemberId}/follow/{memberId}")
    public MemberApiResult<Long> createFollowOne(
            @PathVariable Long memberId, @PathVariable Long followedMemberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("createFollowOne");
        log.info("memberId : {}", memberId);
        log.info("followedMemberId : {}", followedMemberId);

        return new MemberApiResult<>(memberService.insertFollowedMemberByMemberId(memberId, followedMemberId));
    }

    @PostMapping("/{blockedMemberId}/block/{memberId}")
    public MemberApiResult<Long> createBlockOne(
            @PathVariable Long memberId, @PathVariable Long blockedMemberId,
            @RequestParam BlockedMemberStatus blockedMemberStatus)
            throws AlreadyFollowedMemberException, NotExistMemberException, AlreadyBlockedMemberException {

        log.info("createBlockOne");
        log.info("memberId : {}", memberId);
        log.info("blockedMemberId : {}", blockedMemberId);
        log.info("blockedMemberStatus : {}", blockedMemberStatus);

        return new MemberApiResult<>(memberService
                .insertBlockedMemberByMemberId(memberId, blockedMemberId, blockedMemberStatus));
    }

    @PostMapping("/{memberId}/search/{keyword}")
    public MemberApiResult<Long> insertSearchByKeyword(
            @PathVariable String keyword, @PathVariable Long memberId,
            @RequestParam("status") SearchedByMemberStatus searchedByMemberStatus)
            throws NotExistMemberException {

        log.info("createBlockOne");
        log.info("memberId : {}", memberId);
        log.info("keyword : {}", keyword);
        log.info("searchedByMemberStatus : {}", searchedByMemberStatus);

        return new MemberApiResult<>(memberService.insertSearch(memberId, keyword, searchedByMemberStatus));
    }

    @Data
    @AllArgsConstructor
    public static class MemberApiResult<T> {
        private T data;
    }
}
