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
import static com.nameless.spin_off.enums.ContentsTimeEnum.MEMBER_DELETE_MONTH;

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
            @LoginMember MemberDetails currentMember,
            @PathVariable Long memberId) {

        log.info("**** GET :: /member/{memberId}");
        log.info("memberId : {}", memberId);

        return getResult(memberQueryService.getMemberForRead(currentMember, memberId));
    }

    @ApiOperation(value = "멤버 팔로우 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberId",
                    value = "팔로우할 멤버 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{memberId}/follow")
    public SingleApiResult<Long> createFollowMember(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("**** POST :: /member/{memberId}/follow");
        log.info("memberId : {}", memberId);

        return getResult(memberService.insertFollowedMemberByMemberId(currentMember.getId(), memberId));
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
    @GetMapping("/{memberId}/follow/follower")
    public SingleApiResult<List<MembersByContentDto>> readFollowerMember(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("**** GET :: /member/{memberId}/follow/follower");
        log.info("memberId : {}", memberId);

        return getResult(memberQueryService.getFollowingMembersByMemberId(getCurrentMemberId(currentMember), memberId));
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
    @GetMapping("/{memberId}/follow/following")
    public SingleApiResult<List<MembersByContentDto>> readFollowingMember(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("**** GET :: /member/{memberId}/follow/following");
        log.info("memberId : {}", memberId);

        return getResult(memberQueryService.getFollowedMembersByMemberId(getCurrentMemberId(currentMember), memberId));
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
            @LoginMember MemberDetails currentMember,
            @PathVariable Long memberId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {


        log.info("**** GET :: /member/{memberId}/post");
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

        log.info("**** GET :: /member/{memberId}/collection");
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

        log.info("**** GET :: /member/{memberId}/follow/movie");
        log.info("targetMemberId : {}", memberId);

        return getResult(memberQueryService.getFollowMoviesByMemberId(getCurrentMemberId(currentMember), memberId));
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
    public SingleApiResult<List<FollowHashtagDto>> readFollowHashtag(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("**** GET :: /member/{memberId}/follow/hashtag");
        log.info("targetMemberId : {}", memberId);

        return getResult(memberQueryService.getFollowHashtagsByMemberId(getCurrentMemberId(currentMember), memberId));
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
    public SingleApiResult<List<FollowCollectionDto>> readFollowCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long memberId)
            throws AlreadyFollowedMemberException, NotExistMemberException {

        log.info("**** GET :: /member/{memberId}/follow/collection");
        log.info("targetMemberId : {}", memberId);

        return getResult(memberQueryService.getFollowCollectionsByMemberId(currentMember, memberId));
    }

    @ApiOperation(value = "멤버 삭제시간 업데이트", notes = "")
    @ApiImplicitParams({
    })
    @PatchMapping("/delete")
    public SingleApiResult<Boolean> updateDeleteDate(@LoginMember MemberDetails currentMember) {

        log.info("**** PATCH :: /member/delete");

        return getResult(memberService.updateMemberDeleteDate(currentMember.getId(), MEMBER_DELETE_MONTH.getDatePlusMonths()));
    }

    @ApiOperation(value = "멤버 삭제 취소", notes = "")
    @ApiImplicitParams({
    })
    @PatchMapping("/delete/cancel")
    public SingleApiResult<Boolean> updateDeleteDateCancel(@LoginMember MemberDetails currentMember) {

        log.info("**** DELETE :: /member/delete/cancel");

        return getResult(memberService.updateMemberDeleteDateToNull(currentMember.getId()));
    }

    @ApiOperation(value = "멤버 프로필 조회", notes = "")
    @ApiImplicitParams({
    })
    @GetMapping("/profile")
    public SingleApiResult<MemberProfileResponseDto> readMemberProfile(@LoginMember MemberDetails currentMember) {

        log.info("**** GET :: /member/profile");

        return getResult(memberQueryService.getMemberForProfile(getCurrentMemberId(currentMember)));
    }

    @ApiOperation(value = "멤버 프로필 수정", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberProfileRequestDto",
                    value = "{" +
                            "\"nickname\":\"스프링부트\"," +
                            " \"accountId\":\"spinoff033\"," +
                            " \"website\": \"www.naver.com\"," +
                            " \"bio\" : \"스프링부트와 aws로 혼자 구현하는 웹 서비스\"" +
                            "}",
                    required = true,
                    paramType = "formData",
                    dataType = "MemberProfileRequestDto")
    })
    @PatchMapping("/profile")
    public SingleApiResult<Long> updateMemberProfile(
            @LoginMember MemberDetails currentMember,
            @RequestPart MemberProfileRequestDto memberProfileRequestDto,
            @RequestPart(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        Long currentMemberId = getCurrentMemberId(currentMember);

        log.info("**** PATCH :: /member/profile");
        log.info("accountId : {}", memberProfileRequestDto.getAccountId());
        log.info("nickname : {}", memberProfileRequestDto.getNickname());
        log.info("bio : {}", memberProfileRequestDto.getBio());
        log.info("website : {}", memberProfileRequestDto.getWebsite());

        return getResult(memberService.updateMemberProfile(currentMemberId, memberProfileRequestDto, multipartFile));
    }

    @ApiOperation(value = "멤버 개인정보 조회", notes = "")
    @ApiImplicitParams({
    })
    @GetMapping("/info")
    public SingleApiResult<MemberInfoResponseDto> readMemberInfo(@LoginMember MemberDetails currentMember) {

        log.info("**** GET :: /member/info");

        return getResult(memberQueryService.getMemberForInfo(getCurrentMemberId(currentMember)));
    }

    @ApiOperation(value = "멤버 개인정보 수정", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "memberInfoRequestDto",
                    required = true,
                    paramType = "body",
                    dataType = "MemberInfoRequestDto")
    })
    @PatchMapping("/info")
    public SingleApiResult<Long> updateMemberInfo(
            @LoginMember MemberDetails currentMember,
            @RequestBody MemberInfoRequestDto memberInfoRequestDto)
            throws IOException {

        log.info("**** PATCH :: /member/info");
        log.info("authToken : {}", memberInfoRequestDto.getAuthToken());
        log.info("email : {}", memberInfoRequestDto.getEmail());
        log.info("birth : {}", memberInfoRequestDto.getBirth());
        log.info("phoneNumber : {}", memberInfoRequestDto.getPhoneNumber());

        return getResult(memberService.updateMemberInfo(getCurrentMemberId(currentMember), memberInfoRequestDto));
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
            @LoginMember MemberDetails currentMember,
            @RequestParam String password) {

        log.info("**** GET :: /member/password/check");
        log.info("password : {}", password);

        return getResult(memberService.isMatchedPassword(currentMember, password));
    }

    @ApiOperation(value = "멤버 비밀번호 변경", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "비밀번호",
                    required = true,
                    paramType = "body",
                    dataType = "PasswordRequestDto")
    })
    @PatchMapping("/password")
    public SingleApiResult<Boolean> updatePassword(
            @LoginMember MemberDetails currentMember,
            @RequestBody PasswordRequestDto requestDto) {

        log.info("**** PATCH :: /member/password");
        log.info("password : {}", requestDto.getPassword());

        return getResult(memberService.updateMemberPassword(getCurrentMemberId(currentMember), requestDto.getPassword()));
    }

    @ApiOperation(value = "멤버 차단 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "차단할 멤버 id",
                    required = true,
                    paramType = "body",
                    dataType = "BlockRequestDto")
    })
    @PostMapping("/{memberId}/block")
    public SingleApiResult<Long> createBlockMember(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long memberId,
            @RequestBody BlockRequestDto requestDto)
            throws AlreadyFollowedMemberException, NotExistMemberException, AlreadyBlockedMemberException {

        log.info("**** POST :: /member/{memberId}/block");
        log.info("blockedMemberId : {}", memberId);
        log.info("blockedMemberStatus : {}", requestDto.getBlockedMemberStatus());

        return getResult(memberService
                .insertBlockedMemberByMemberId(currentMember.getId(), memberId, requestDto.getBlockedMemberStatus()));
    }

    @ApiOperation(value = "멤버 차단 목록 조회", notes = "")
    @ApiImplicitParams({
    })
    @GetMapping("/block")
    public SingleApiResult<List<BlockedMemberDto>> readBlockMember(@LoginMember MemberDetails currentMember)
            throws AlreadyFollowedMemberException, NotExistMemberException, AlreadyBlockedMemberException {

        log.info("**** GET :: /member/block");

        return getResult(memberQueryService.getBlockedMembersByMemberId(currentMember.getId()));
    }

    @ApiOperation(value = "검색 기록 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "searchMemberRequestDto",
                    value = "검색 dto",
                    required = true,
                    paramType = "body",
                    dataType = "SearchMemberRequestDto")
    })

    @PostMapping("/search")
    public SingleApiResult<Long> createSearch(
            @LoginMember MemberDetails currentMember, @RequestBody SearchMemberRequestDto searchMemberRequestDto)
            throws NotExistMemberException {

        log.info("**** POST :: /member/search");
        log.info("keyword : {}", searchMemberRequestDto.getKeyword());
        log.info("searchedByMemberStatus : {}", searchMemberRequestDto.getSearchedByMemberStatus());

        return getResult(memberService.insertSearch(
                currentMember.getId(),
                searchMemberRequestDto.getKeyword(),
                searchMemberRequestDto.getSearchedByMemberStatus()));
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
            @LoginMember MemberDetails currentMember,
            @RequestParam int length)
            throws NotExistMemberException {

        log.info("**** GET :: /member/search");
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
    public SingleApiResult<Boolean> createAuthEmail(
            @LoginMember MemberDetails currentMember,
            @RequestParam String email) {
        log.info("**** POST :: /member/auth-email");
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

        log.info("**** PATCH :: /member/auth-email");
        log.info("email : {}", requestDto.getEmail());
        log.info("authToken : {}", requestDto.getAuthToken());

        return getResult(memberService.confirmEmailForAuth(requestDto));
    }

    @ApiOperation(value = "이메일 변경 인증 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "이메일 정보",
                    required = true,
                    paramType = "body",
                    dataType = "EmailRequestDto")
    })
    @PostMapping("/update-email")
    public SingleApiResult<Boolean> createUpdateEmail(
            @LoginMember MemberDetails currentMember,
            @RequestBody EmailRequestDto requestDto) {

        log.info("**** POST :: /member/update-email");
        log.info("email : {}", requestDto.getEmail());
        return getResult(memberService.sendEmailForUpdateEmail(currentMember.getId(), requestDto.getEmail()));
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

        log.info("**** PATCH :: /member/update-email");
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
