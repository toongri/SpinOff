package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.FollowCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MyPageCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.FollowHashtagDto;
import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.dto.MovieDto.FollowMovieDto;
import com.nameless.spin_off.dto.PostDto.MyPagePostDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.member.SearchedByMemberStatus;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @ApiOperation(value = "멤버 조회", notes = "")
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
    public SingleApiResult<ReadMemberDto> readMember(
            @LoginMember MemberDetails currentMember, @PathVariable Long memberId) {
        Long currentMemberId = getCurrentMemberId(currentMember);

        log.info("readMember");
        log.info("currentMemberId : {}", currentMemberId);
        log.info("memberId : {}", memberId);

        return getResult(memberQueryService.getMemberForRead(currentMember, memberId));
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
    public SingleApiResult<Long> createFollowMember(
            @LoginMember MemberDetails currentMember, @PathVariable Long followedMemberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("createFollowMember");
        log.info("memberId : {}", currentMember.getId());
        log.info("followedMemberId : {}", followedMemberId);

        return getResult(memberService.insertFollowedMemberByMemberId(currentMember.getId(), followedMemberId));
    }

    @ApiOperation(value = "멤버 팔로워 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{memberId}/follower")
    public SingleApiResult<List<MembersByContentDto>> readFollowerMember(
            @LoginMember MemberDetails currentMember, @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {
        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readFollowerMember");
        log.info("memberId : {}", currentMemberId);
        log.info("targetMemberId : {}", memberId);

        return getResult(memberQueryService.getFollowingMembersByMemberId(currentMemberId, memberId));
    }

    @ApiOperation(value = "멤버 팔로잉 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{memberId}/following")
    public SingleApiResult<List<MembersByContentDto>> readFollowingMember(
            @LoginMember MemberDetails currentMember, @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readFollowingMember");
        log.info("memberId : {}", currentMemberId);
        log.info("targetMemberId : {}", memberId);

        return getResult(memberQueryService.getFollowedMembersByMemberId(currentMemberId, memberId));
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

    @ApiOperation(value = "멤버 팔로우 영화 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{memberId}/follow/movie")
    public SingleApiResult<List<FollowMovieDto>> readFollowMovie(
            @LoginMember MemberDetails currentMember, @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readFollowMovie");
        log.info("memberId : {}", currentMemberId);
        log.info("targetMemberId : {}", memberId);

        return getResult(memberQueryService.getFollowMoviesByMemberId(currentMemberId, memberId));
    }

    @ApiOperation(value = "멤버 팔로우 해시태그 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{memberId}/follow/hashtag")
    public SingleApiResult<List<FollowHashtagDto>> readFollowHashtag(@LoginMember MemberDetails currentMember,
                                                                     @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readFollowHashtag");
        log.info("memberId : {}", currentMemberId);
        log.info("targetMemberId : {}", memberId);

        return getResult(memberQueryService.getFollowHashtagsByMemberId(currentMemberId, memberId));
    }

    @ApiOperation(value = "멤버 팔로우 컬렉션 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{memberId}/follow/collection")
    public SingleApiResult<List<FollowCollectionDto>> readFollowCollection(@LoginMember MemberDetails currentMember,
                                                                           @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readFollowCollection");
        log.info("memberId : {}", currentMemberId);
        log.info("targetMemberId : {}", memberId);

        return getResult(memberQueryService.getFollowCollectionsByMemberId(currentMember, memberId));
    }

    @ApiOperation(value = "멤버 프로필 조회", notes = "")
    @ApiImplicitParams({
    })
    @GetMapping("/profile")
    public SingleApiResult<MemberProfileResponseDto> readMemberProfile(@LoginMember MemberDetails currentMember) {
        Long currentMemberId = getCurrentMemberId(currentMember);

        log.info("readMemberInfo");
        log.info("currentMemberId : {}", currentMemberId);

        return getResult(memberQueryService.getMemberForProfile(currentMemberId));
    }

    @ApiOperation(value = "멤버 프로필 수정", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberProfileRequestDto",
                    value = "{" +
                            "\"nickname\":\"스프링부트\"," +
                            " \"profileUrl\":\"www.naver.profile.com\"," +
                            " \"accountId\":spinoff033," +
                            " \"website\": \"www.naver.com\"," +
                            " \"bio\" : \"스프링부트와 aws로 혼자 구현하는 웹 서비스\"," +
                            "}",
                    required = true,
                    paramType = "formData",
                    dataType = "MemberProfileRequestDto")
    })
    @PatchMapping("/profile")
    public SingleApiResult<Long> updateMemberProfile(@LoginMember MemberDetails currentMember,
                                                     @RequestPart MemberProfileRequestDto memberProfileRequestDto,
                                                     @RequestPart("image") MultipartFile multipartFile) throws IOException {
        Long currentMemberId = getCurrentMemberId(currentMember);

        log.info("updateMemberInfo");
        log.info("currentMemberId : {}", currentMemberId);
        log.info("accountId : {}", memberProfileRequestDto.getAccountId());
        log.info("nickname : {}", memberProfileRequestDto.getNickname());
        log.info("bio : {}", memberProfileRequestDto.getBio());
        log.info("website : {}", memberProfileRequestDto.getWebsite());

        return getResult(memberService.updateMemberProfile(currentMemberId, memberProfileRequestDto, multipartFile));
    }

    @ApiOperation(value = "멤버 비밀번호 여부 확인", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "password",
                    value = "비밀번호",
                    required = true,
                    paramType = "query",
                    dataType = "String")
    })
    @GetMapping("/password/check")
    public SingleApiResult<Boolean> readIsMatchedPassword(
            @LoginMember MemberDetails currentMember, @RequestParam String password) {
        Long currentMemberId = getCurrentMemberId(currentMember);

        log.info("readIsMatchedPassword");
        log.info("currentMemberId : {}", currentMemberId);
        log.info("password : {}", password);

        return getResult(memberService.isMatchedPassword(currentMember, password));
    }

    @ApiOperation(value = "멤버 비밀번호 변경", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "password",
                    value = "비밀번호",
                    required = true,
                    paramType = "query",
                    dataType = "String")
    })
    @PatchMapping("/password")
    public SingleApiResult<Boolean> updatePassword(
            @LoginMember MemberDetails currentMember, @RequestParam String password) {
        Long currentMemberId = getCurrentMemberId(currentMember);

        log.info("updatePassword");
        log.info("currentMemberId : {}", currentMemberId);
        log.info("password : {}", password);

        return getResult(memberService.updateMemberPassword(currentMemberId, password));
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
    public SingleApiResult<Long> createBlockMember(@LoginMember MemberDetails currentMember,
                                                   @PathVariable Long blockedMemberId,
                                                   @RequestParam BlockedMemberStatus blockedMemberStatus)
            throws AlreadyFollowedMemberException, NotExistMemberException, AlreadyBlockedMemberException {

        log.info("createBlockMember");
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
    public SingleApiResult<Long> createSearch(
            @LoginMember MemberDetails currentMember, @RequestParam String keyword,
            @RequestParam SearchedByMemberStatus searchedByMemberStatus)
            throws NotExistMemberException {

        log.info("createSearch");
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
    public SingleApiResult<List<LastSearchDto>> readLastSearch(
            @LoginMember MemberDetails currentMember, @RequestParam int length)
            throws NotExistMemberException {

        log.info("readLastSearch");
        log.info("memberId : {}", currentMember.getId());
        log.info("length : {}", length);

        return getResult(memberQueryService.getLastSearchesByMemberLimit(currentMember.getId(), length));
    }

    @ApiOperation(value = "이메일 인증 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "email",
                    value = "이메일 정보",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "spinoff232@gmail.com")
    })
    @PostMapping("/auth-email")
    public SingleApiResult<Boolean> createAuthEmail(@LoginMember MemberDetails currentMember,
                                                    @RequestParam String email) {
        log.info("createAuthEmail");
        log.info("memberId : {}", currentMember.getId());
        log.info("email : {}", email);
        return getResult(memberService.sendEmailForAuth(currentMember.getId(), email));
    }

    @ApiOperation(value = "이메일 인증 확인", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "이메일 인증 정보",
                    required = true,
                    paramType = "body",
                    dataType = "EmailAuthRequestDto")
    })
    @PatchMapping("/auth-email")
    public SingleApiResult<Boolean> confirmAuthEmail(@RequestBody EmailAuthRequestDto requestDto) {

        log.info("confirmAuthEmail");
        log.info("email : {}", requestDto.getEmail());
        log.info("authToken : {}", requestDto.getAuthToken());

        return getResult(memberService.confirmEmailForAuth(requestDto));
    }


    @ApiOperation(value = "이메일 변경 인증 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "email",
                    value = "이메일 정보",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "spinoff232@gmail.com")
    })
    @PostMapping("/update-email")
    public SingleApiResult<Boolean> createUpdateEmail(@LoginMember MemberDetails currentMember,
                                                    @RequestParam String email) {
        log.info("createUpdateEmail");
        log.info("memberId : {}", currentMember.getId());
        log.info("email : {}", email);
        return getResult(memberService.sendEmailForUpdateEmail(currentMember.getId(), email));
    }

    @ApiOperation(value = "이메일 변경 인증 확인", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "이메일 인증 정보",
                    required = true,
                    paramType = "body",
                    dataType = "EmailAuthRequestDto")
    })
    @PatchMapping("/update-email")
    public SingleApiResult<Boolean> confirmUpdateEmail(@RequestBody EmailAuthRequestDto requestDto) {

        log.info("confirmUpdateEmail");
        log.info("email : {}", requestDto.getEmail());
        log.info("authToken : {}", requestDto.getAuthToken());

        return getResult(memberService.confirmEmailForUpdateEMail(requestDto));
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }
}
