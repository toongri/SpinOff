package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.MainPageDto.MainPageDiscoveryDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageFollowDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.query.MainPageQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"메인페이지 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/main-page")
public class MainPageApiController {

    private final MainPageQueryService mainPageService;

    @ApiOperation(value = "발견 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "popular_post_page",
                    value = "인기순 글 페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "popular_post_size",
                    value = "인기순 글 페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "popular_post_sort",
                    value = "인기순 글 페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popular,asc"),
            @ApiImplicitParam(
                    name = "latest_post_page",
                    value = "최신순 글 페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "latest_post_size",
                    value = "최신순 글 페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "latest_post_sort",
                    value = "최신순 글 페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "id,desc"),
            @ApiImplicitParam(
                    name = "collection_page",
                    value = "컬렉션 페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "collection_size",
                    value = "컬렉션 페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "collection_sort",
                    value = "컬렉션 페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "id,desc")
    })
    @GetMapping("/discovery")
    public SingleApiResult<MainPageDiscoveryDto> readDiscoveryMainPage(
            @LoginMember MemberDetails currentMember,
            @Qualifier("popular_post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable popularPostPageable,
            @Qualifier("latest_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable latestPostPageable,
            @Qualifier("collection")  @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable)
            throws NotExistMemberException {

        log.info("readDiscoveryMainPage");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("popularPostPageable.getPageNumber() : {}", popularPostPageable.getPageNumber());
        log.info("popularPostPageable.getPageSize() : {}", popularPostPageable.getPageSize());
        log.info("popularPostPageable.getSort() : {}", popularPostPageable.getSort());

        log.info("latestPostPageable.getPageNumber() : {}", latestPostPageable.getPageNumber());
        log.info("latestPostPageable.getPageSize() : {}", latestPostPageable.getPageSize());
        log.info("latestPostPageable.getSort() : {}", latestPostPageable.getSort());

        log.info("collectionPageable.getPageNumber() : {}", collectionPageable.getPageNumber());
        log.info("collectionPageable.getPageSize() : {}", collectionPageable.getPageSize());
        log.info("collectionPageable.getSort() : {}", collectionPageable.getSort());

        return getResult(
                mainPageService
                        .getDiscoveryData(
                                popularPostPageable, latestPostPageable, collectionPageable, getMemberId(currentMember)));
    }

    @ApiOperation(value = "팔로우 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "member_post_page",
                    value = "팔로우멤버 글 페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "member_post_size",
                    value = "팔로우멤버 글 페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "member_post_sort",
                    value = "팔로우멤버 글 페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popular,asc"),
            @ApiImplicitParam(
                    name = "hashtag_post_page",
                    value = "팔로우해시태그 글 페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "hashtag_post_size",
                    value = "팔로우해시태그 글 페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "hashtag_post_sort",
                    value = "팔로우해시태그 글 페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "id,desc"),
            @ApiImplicitParam(
                    name = "movie_post_page",
                    value = "팔로우영화 글 페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "movie_post_size",
                    value = "팔로우영화 글 페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "movie_post_sort",
                    value = "팔로우영화 글 페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "id,desc"),
            @ApiImplicitParam(
                    name = "collection_page",
                    value = "컬렉션 페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "collection_size",
                    value = "컬렉션 페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "collection_sort",
                    value = "컬렉션 페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "id,desc")
    })
    @GetMapping("/following")
    public SingleApiResult<MainPageFollowDto> readFollowMainPage(
            @LoginMember MemberDetails currentMember,
            @Qualifier("member_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("hashtag_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable hashtagPageable,
            @Qualifier("movie_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable moviePageable,
            @Qualifier("collection") @PageableDefault(sort = "lastModifiedDate", direction = Sort.Direction.DESC)
                    Pageable collectionPageable)
            throws NotExistMemberException {

        log.info("readFollowMainPage");
        log.info("memberId : {}", currentMember.getId());
        log.info("memberPageable.getPageNumber() : {}", memberPageable.getPageNumber());
        log.info("memberPageable.getPageSize() : {}", memberPageable.getPageSize());
        log.info("memberPageable.getSort() : {}", memberPageable.getSort());

        log.info("hashtagPageable.getPageNumber() : {}", hashtagPageable.getPageNumber());
        log.info("hashtagPageable.getPageSize() : {}", hashtagPageable.getPageSize());
        log.info("hashtagPageable.getSort() : {}", hashtagPageable.getSort());

        log.info("moviePageable.getPageNumber() : {}", moviePageable.getPageNumber());
        log.info("moviePageable.getPageSize() : {}", moviePageable.getPageSize());
        log.info("moviePageable.getSort() : {}", moviePageable.getSort());

        log.info("collectionPageable.getPageNumber() : {}", collectionPageable.getPageNumber());
        log.info("collectionPageable.getPageSize() : {}", collectionPageable.getPageSize());
        log.info("collectionPageable.getSort() : {}", collectionPageable.getSort());

        return getResult(
                mainPageService.getFollowData(memberPageable, hashtagPageable, moviePageable,
                        collectionPageable, currentMember.getId()));
    }

    public Long getMemberId(MemberDetails memberDetails) {
        return memberDetails == null ? null : memberDetails.getId();
    }

}
