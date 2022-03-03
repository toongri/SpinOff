package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.HashtagDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto.CreateMemberVO;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtMemberMemberDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.query.HashtagQueryService;
import com.nameless.spin_off.service.query.MemberQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberApiController {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final HashtagQueryService hashtagQueryService;

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

    @PostMapping("/search")
    public MemberApiResult<Long> insertSearchByKeyword(@RequestParam String keyword, @RequestParam Long id,
                                                       @RequestParam("status") SearchedByMemberStatus searchedByMemberStatus)
            throws NotExistMemberException {

        return new MemberApiResult<Long>(memberService.insertSearch(id, keyword, searchedByMemberStatus));
    }

    @GetMapping("/search")
    public MemberApiResult<Slice<SearchPageAtMemberMemberDto>> getSearchPageMemberAtMemberSliced(
            @RequestParam String keyword, @RequestParam Long memberId,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {
        return new MemberApiResult<Slice<SearchPageAtMemberMemberDto>>(
                memberQueryService.getSearchPageMemberAtMemberSliced(keyword, pageable, memberId));
    }

    @GetMapping("/search/first")
    public MemberApiSearchResult getSearchPageMemberAtMemberSlicedFirst(
            @RequestParam String keyword, @RequestParam Long memberId, @RequestParam int hashtagLength,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)Pageable pageable)
            throws NotExistMemberException {

        Slice<SearchPageAtMemberMemberDto> members =
                memberQueryService.getSearchPageMemberAtMemberSliced(keyword, pageable, memberId);
        return new MemberApiSearchResult<Slice<SearchPageAtMemberMemberDto>, List<HashtagDto.RelatedMostTaggedHashtagDto>>(
                members, getHashtagsByPostIds(hashtagLength, members.getContent()));
    }

    @Data
    @AllArgsConstructor
    public static class MemberApiResult<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    public static class MemberApiSearchResult<T, F> {
        private T data;
        private F hashtags;
    }
    private List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(int length, List<SearchPageAtMemberMemberDto> data) {
        return hashtagQueryService.getHashtagsByPostIds(
                length,
                data.stream().map(SearchPageAtMemberMemberDto::getMemberId).collect(Collectors.toList()));
    }
}
