package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.MyPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MyPagePostDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.AlreadyBlockedMemberException;
import com.nameless.spin_off.exception.member.AlreadyFollowedMemberException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.query.CollectionQueryService;
import com.nameless.spin_off.service.query.MemberQueryService;
import com.nameless.spin_off.service.query.PostQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"멤버 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberApiController {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final CollectionQueryService collectionQueryService;

    @ApiOperation(value = "멤버 마이페이지", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{memberId}")
    public SingleApiResult<Long> readOne(
            @LoginMember MemberDetails currentMember, @PathVariable Long memberId) {
        Long currentMemberId = getCurrentMemberId(currentMember);

        log.info("readOne");
        log.info("currentMemberId : {}", currentMemberId);
        log.info("memberId : {}", memberId);

        return getResult(memberService.getMemberForRead(currentMemberId, memberId));
    }

    @ApiOperation(value = "멤버 마이페이지 글", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc"),
    })
    @GetMapping("/{memberId}/post")
    public SingleApiResult<Slice<MyPagePostDto>> readMemberPosts(
            @LoginMember MemberDetails currentMember, @PathVariable Long memberId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("readMemberPosts");
        log.info("currentMemberId : {}", getCurrentMemberId(currentMember));
        log.info("memberId : {}", memberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());

        return getResult(postQueryService.getPostsByMemberIdSliced(currentMember, memberId, pageable));
    }

    @ApiOperation(value = "멤버 마이페이지 컬렉션", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc"),
    })
    @GetMapping("/{memberId}/collection")
    public SingleApiResult<Slice<MyPageCollectionDto>> readMemberCollections(
            @LoginMember MemberDetails currentMember, @PathVariable Long memberId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("readMemberCollections");
        log.info("currentMemberId : {}", getCurrentMemberId(currentMember));
        log.info("memberId : {}", memberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());

        return getResult(collectionQueryService.getCollectionsByMemberIdSliced(currentMember, memberId, pageable));
    }

    @ApiOperation(value = "멤버 팔로우 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "followedMemberId",
                    value = "팔로우할 멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{followedMemberId}/follow")
    public SingleApiResult<Long> createFollowOne(
            @LoginMember MemberDetails currentMember, @PathVariable Long followedMemberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("createFollowOne");
        log.info("memberId : {}", currentMember.getId());
        log.info("followedMemberId : {}", followedMemberId);

        return getResult(memberService.insertFollowedMemberByMemberId(currentMember.getId(), followedMemberId));
    }

    @ApiOperation(value = "멤버 차단 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "blockedMemberId",
                    value = "차단할 멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{blockedMemberId}/block")
    public SingleApiResult<Long> createBlockOne(
            @LoginMember MemberDetails currentMember, @PathVariable Long blockedMemberId,
            @RequestParam BlockedMemberStatus blockedMemberStatus)
            throws AlreadyFollowedMemberException, NotExistMemberException, AlreadyBlockedMemberException {

        log.info("createBlockOne");
        log.info("memberId : {}", currentMember.getId());
        log.info("blockedMemberId : {}", blockedMemberId);
        log.info("blockedMemberStatus : {}", blockedMemberStatus);

        return getResult(memberService
                .insertBlockedMemberByMemberId(currentMember.getId(), blockedMemberId, blockedMemberStatus));
    }

    @ApiOperation(value = "검색 기록 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "첼시"),
            @ApiImplicitParam(
                    name = "searchedByMemberStatus",
                    value = "검색 속성",
                    required = true,
                    paramType = "query",
                    dataType = "SearchedByMemberStatus",
                    example = "A")
    })
    @PostMapping("/search")
    public SingleApiResult<Long> createSearchByKeyword(
            @LoginMember MemberDetails currentMember, @RequestParam String keyword,
            @RequestParam SearchedByMemberStatus searchedByMemberStatus)
            throws NotExistMemberException {

        log.info("createBlockOne");
        log.info("memberId : {}", currentMember.getId());
        log.info("keyword : {}", keyword);
        log.info("searchedByMemberStatus : {}", searchedByMemberStatus);

        return getResult(memberService.insertSearch(currentMember.getId(), keyword, searchedByMemberStatus));
    }

    @ApiOperation(value = "검색 기록 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "length",
                    value = "검색 기록 조회 갯수 제한",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123")
    })
    @GetMapping("/search")
    public SingleApiResult<List<LastSearchDto>> getLastSearchesByMemberLimit(
            @LoginMember MemberDetails currentMember, @RequestParam int length)
            throws NotExistMemberException {

        return getResult(memberQueryService.getLastSearchesByMemberLimit(currentMember.getId(), length));
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }
}
