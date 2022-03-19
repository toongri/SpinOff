package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.auth.LoginMemberId;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.AlreadyBlockedMemberException;
import com.nameless.spin_off.exception.member.AlreadyFollowedMemberException;
import com.nameless.spin_off.exception.member.NotCorrectEmailRequest;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.query.MemberQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nameless.spin_off.entity.enums.member.EmailLinkageServiceEnum.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberApiController {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;

    @PostMapping("/{followedMemberId}/follow")
    public MemberApiResult<Long> createFollowOne(
            @LoginMemberId Long memberId, @PathVariable Long followedMemberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("createFollowOne");
        log.info("memberId : {}", memberId);
        log.info("followedMemberId : {}", followedMemberId);

        return getResult(memberService.insertFollowedMemberByMemberId(memberId, followedMemberId));
    }

    @PostMapping("/{blockedMemberId}/block")
    public MemberApiResult<Long> createBlockOne(
            @LoginMemberId Long memberId, @PathVariable Long blockedMemberId,
            @RequestParam BlockedMemberStatus blockedMemberStatus)
            throws AlreadyFollowedMemberException, NotExistMemberException, AlreadyBlockedMemberException {

        log.info("createBlockOne");
        log.info("memberId : {}", memberId);
        log.info("blockedMemberId : {}", blockedMemberId);
        log.info("blockedMemberStatus : {}", blockedMemberStatus);

        return getResult(memberService
                .insertBlockedMemberByMemberId(memberId, blockedMemberId, blockedMemberStatus));
    }

    @PostMapping("/search/{keyword}")
    public MemberApiResult<Long> insertSearchByKeyword(
            @PathVariable String keyword, @LoginMemberId Long memberId,
            @RequestParam("status") SearchedByMemberStatus searchedByMemberStatus)
            throws NotExistMemberException {

        log.info("createBlockOne");
        log.info("memberId : {}", memberId);
        log.info("keyword : {}", keyword);
        log.info("searchedByMemberStatus : {}", searchedByMemberStatus);

        return getResult(memberService.insertSearch(memberId, keyword, searchedByMemberStatus));
    }

    @PostMapping("/linkage-email/naver")
    public MemberApiResult<String> linkageEmailNaver(@RequestParam String email, @LoginMember MemberDetails member) {
        if (isNotCorrectEmail(email, NAVER)) {
            throw new NotCorrectEmailRequest();
        }
        memberService.updateEmailLinkage(email, member.getUsername());
        return getResult("메일을 확인하여 주시기 바랍니다.");
    }

    @PostMapping("/linkage-email/kakao")
    public MemberApiResult<String> linkageEmailKakao(@RequestParam String email, @LoginMember MemberDetails member) {
        if (isNotCorrectEmail(email, KAKAO)) {
            throw new NotCorrectEmailRequest();
        }
        memberService.updateEmailLinkage(email, member.getUsername());
        return getResult("메일을 확인하여 주시기 바랍니다.");
    }

    @PostMapping("/linkage-email/google")
    public MemberApiResult<String> linkageEmailGoogle(@RequestParam String email, @LoginMember MemberDetails member) {
        if (isNotCorrectEmail(email, GOOGLE)) {
            throw new NotCorrectEmailRequest();
        }
        memberService.updateEmailLinkage(email, member.getUsername());
        return getResult("메일을 확인하여 주시기 바랍니다.");
    }

    @GetMapping("/member-latest")
    public MemberApiResult<List<LastSearchDto>> getLastSearchesByMemberFirst(
            @LoginMemberId Long memberId, @RequestParam int length)
            throws NotExistMemberException {

        return getResult(memberQueryService.getLastSearchesByMemberLimit(memberId, length));
    }

    private Boolean isNotCorrectEmail(String email, EmailLinkageServiceEnum provider) {
        return !getProviderByEmail(email).equals(provider.getValue());
    }

    private String getProviderByEmail(String email) {
        return email.substring(email.indexOf("@") + 1, email.indexOf("."));
    }

    @Data
    @AllArgsConstructor
    public static class MemberApiResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> MemberApiResult<T> getResult(T data) {
        return new MemberApiResult<>(data, true, "0", "성공");
    }
}
