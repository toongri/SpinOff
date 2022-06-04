package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.SearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieFirstDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtHashtagPostDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllFirstDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.search.IncorrectLengthRelatedKeywordException;
import com.nameless.spin_off.service.query.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"검색 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/search")
public class SearchApiController {
    private final SearchQueryService searchQueryService;
    private final CollectionQueryService collectionQueryService;
    private final PostQueryService postQueryService;
    private final MemberQueryService memberQueryService;
    private final MovieQueryService movieQueryService;

    @ApiOperation(value = "키워드 연관 컨텐츠 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "123"),
            @ApiImplicitParam(
                    name = "length",
                    value = "검색 결과 갯수",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123")
    })
    @GetMapping("/related/all/{keyword}")
    public SingleApiResult<RelatedSearchAllDto> readRelatedContentByKeyword(
            @PathVariable String keyword, @RequestParam int length)
            throws IncorrectLengthRelatedKeywordException {

        log.info("readRelatedContentByKeyword");
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(searchQueryService.getRelatedSearchAllByKeyword(keyword, length));
    }

    @ApiOperation(value = "키워드 연관 해시태그 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "123"),
            @ApiImplicitParam(
                    name = "length",
                    value = "검색 결과 갯수",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123")
    })
    @GetMapping("/related/hashtag/{keyword}")
    public SingleApiResult<List<RelatedSearchHashtagDto>> readRelatedHashtagByKeyword(
            @PathVariable String keyword, @RequestParam int length)
            throws IncorrectLengthRelatedKeywordException {

        log.info("readRelatedHashtagByKeyword");
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(
                searchQueryService.getRelatedSearchHashtagByKeyword(keyword, length));
    }

    @ApiOperation(value = "키워드 연관 멤버 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "123"),
            @ApiImplicitParam(
                    name = "length",
                    value = "검색 결과 갯수",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123")
    })
    @GetMapping("/related/member/{keyword}")
    public SingleApiResult<List<RelatedSearchMemberDto>> readRelatedMemberByKeyword
            (@PathVariable String keyword, @RequestParam int length)
            throws IncorrectLengthRelatedKeywordException {

        log.info("readRelatedMemberByKeyword");
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(
                searchQueryService.getRelatedSearchMemberByKeyword(keyword, length));
    }

    @ApiOperation(value = "키워드 전체 검색 최초 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "123"),
            @ApiImplicitParam(
                    name = "length",
                    value = "연관 해시태그 갯수 제한",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "post_page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "post_size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "post_sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc"),
            @ApiImplicitParam(
                    name = "collection_page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "collection_size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "collection_sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc"),
            @ApiImplicitParam(
                    name = "member_page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "member_size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "member_sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc"),
            @ApiImplicitParam(
                    name = "movie_page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "movie_size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "movie_sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc")
    })
    @GetMapping("/all/{keyword}/first")
    public SingleApiResult<SearchFirstDto<SearchAllFirstDto>> readContentByKeywordFirst(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword, @RequestParam int length,
            @Qualifier("post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable postPageable,
            @Qualifier("collection") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable,
            @Qualifier("member") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("movie") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable moviePageable)
            throws NotExistMemberException {

        log.info("readContentByKeywordFirst");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("memberPageable.getPageNumber() : {}", memberPageable.getPageNumber());
        log.info("memberPageable.getPageSize() : {}", memberPageable.getPageSize());
        log.info("memberPageable.getSort() : {}", memberPageable.getSort());

        log.info("postPageable.getPageNumber() : {}", postPageable.getPageNumber());
        log.info("postPageable.getPageSize() : {}", postPageable.getPageSize());
        log.info("postPageable.getSort() : {}", postPageable.getSort());

        log.info("moviePageable.getPageNumber() : {}", moviePageable.getPageNumber());
        log.info("moviePageable.getPageSize() : {}", moviePageable.getPageSize());
        log.info("moviePageable.getSort() : {}", moviePageable.getSort());

        log.info("collectionPageable.getPageNumber() : {}", collectionPageable.getPageNumber());
        log.info("collectionPageable.getPageSize() : {}", collectionPageable.getPageSize());
        log.info("collectionPageable.getSort() : {}", collectionPageable.getSort());

        return getResult(
                searchQueryService.getSearchPageDataAtAllFirst(keyword, getMemberId(currentMember), length,
                        postPageable, collectionPageable, memberPageable, moviePageable));
    }

    @ApiOperation(value = "키워드 전체 검색 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "123"),
            @ApiImplicitParam(
                    name = "post_page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "post_size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "post_sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc"),
            @ApiImplicitParam(
                    name = "member_page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "member_size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "member_sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc"),
            @ApiImplicitParam(
                    name = "movie_page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "movie_size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "movie_sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc")
    })
    @GetMapping("/all/{keyword}")
    public SingleApiResult<SearchAllDto> readContentByKeyword(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword,
            @Qualifier("post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable postPageable,
            @Qualifier("member") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("movie") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable moviePageable)
            throws NotExistMemberException {

        log.info("readContentByKeyword");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("memberPageable.getPageNumber() : {}", memberPageable.getPageNumber());
        log.info("memberPageable.getPageSize() : {}", memberPageable.getPageSize());
        log.info("memberPageable.getSort() : {}", memberPageable.getSort());

        log.info("postPageable.getPageNumber() : {}", postPageable.getPageNumber());
        log.info("postPageable.getPageSize() : {}", postPageable.getPageSize());
        log.info("postPageable.getSort() : {}", postPageable.getSort());

        log.info("moviePageable.getPageNumber() : {}", moviePageable.getPageNumber());
        log.info("moviePageable.getPageSize() : {}", moviePageable.getPageSize());
        log.info("moviePageable.getSort() : {}", moviePageable.getSort());

        return getResult(
                searchQueryService.getSearchPageDataAtAll(
                        keyword, getMemberId(currentMember),
                        postPageable,
                        memberPageable,
                        moviePageable));
    }

    @ApiOperation(value = "해시태그 검색 최초 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "length",
                    value = "해시태그 검색 제한",
                    required = true,
                    paramType = "query",
                    dataType = "int",
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
            @ApiImplicitParam(
                    name = "hashtagContents",
                    value = "해시태그 리스트",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "해시태그",
                    allowMultiple = true)
    })
    @GetMapping("/all/hashtag/first")
    public SingleApiResult<SearchFirstDto<Slice<SearchPageAtHashtagPostDto>>> readContentByHashtagFirst(
            @LoginMember MemberDetails currentMember,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam List<String> hashtagContents, @RequestParam int length) {

        log.info("readContentByHashtagFirst");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("hashtagContents : {}", hashtagContents.toString());
        log.info("length : {}", length);

        return getResult(postQueryService
                .getPostsByHashtagsSlicedForSearchPageFirst(pageable, hashtagContents, getMemberId(currentMember), length));
    }

    @ApiOperation(value = "해시태그 검색 조회", notes = "")
    @ApiImplicitParams({
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
            @ApiImplicitParam(
                    name = "hashtagContents",
                    value = "해시태그 리스트",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "해시태그",
                    allowMultiple = true)
    })
    @GetMapping("/all/hashtag")
    public SingleApiResult<Slice<SearchPageAtHashtagPostDto>> readContentByHashtag(
            @LoginMember MemberDetails currentMember,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam List<String> hashtagContents) {

        log.info("readContentByHashtag");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("hashtagContents : {}", hashtagContents.toString());

        return getResult(postQueryService
                .getPostsByHashtagsSlicedForSearchPage(pageable, hashtagContents, getMemberId(currentMember)));
    }

    @ApiOperation(value = "키워드 영화 검색 최초 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "123"),
            @ApiImplicitParam(
                    name = "length",
                    value = "해시태그 검색 제한",
                    required = true,
                    paramType = "query",
                    dataType = "int",
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
                    example = "popularity,desc")
    })
    @GetMapping("/movie/{keyword}/first")
    public SingleApiResult<SearchFirstDto<SearchMovieFirstDto>> readMovieByKeywordFirst(
            @PathVariable String keyword, @RequestParam int length,
            @PageableDefault(sort = "popularity", direction  = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMovieException {

        log.info("readMovieByKeywordFirst");
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(movieQueryService.getSearchPageMovieAtMovieSlicedFirst(keyword, pageable, length));
    }

    @ApiOperation(value = "키워드 영화 검색 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
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
                    example = "popularity,desc")
    })
    @GetMapping("/movie/{keyword}")
    public SingleApiResult<Slice<SearchMovieDto>> readMovieByKeyword(
            @PathVariable String keyword,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("readMovieByKeyword");
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);

        return getResult(
                movieQueryService.getSearchPageMovieAtMovieSliced(keyword, pageable));
    }

    @ApiOperation(value = "키워드 멤버 검색 최초 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "123"),
            @ApiImplicitParam(
                    name = "length",
                    value = "해시태그 검색 제한",
                    required = true,
                    paramType = "query",
                    dataType = "int",
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
                    example = "popularity,desc")
    })
    @GetMapping("/member/{keyword}/first")
    public SingleApiResult<SearchFirstDto<Slice<SearchMemberDto>>> readMemberByKeywordFirst(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword, @RequestParam int length,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("readMemberByKeywordFirst");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(
                memberQueryService.getSearchPageMemberAtMemberSlicedFirst(keyword, pageable, getMemberId(currentMember), length));
    }

    @ApiOperation(value = "키워드 멤버 검색 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
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
                    example = "popularity,desc")
    })
    @GetMapping("/member/{keyword}")
    public SingleApiResult<Slice<SearchMemberDto>> readMemberByKeyword(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("readMemberByKeyword");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);

        return getResult(
                memberQueryService.getSearchPageMemberAtMemberSliced(keyword, pageable, getMemberId(currentMember)));
    }

    @ApiOperation(value = "키워드 컬렉션 검색 최초 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
                    example = "123"),
            @ApiImplicitParam(
                    name = "length",
                    value = "해시태그 검색 제한",
                    required = true,
                    paramType = "query",
                    dataType = "int",
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
                    example = "popularity,desc")
    })
    @GetMapping("/collection/{keyword}/first")
    public SingleApiResult<SearchFirstDto<Slice<SearchCollectionDto>>> readCollectionByKeywordFirst(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword, @RequestParam int length,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("readCollectionByKeywordFirst");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(collectionQueryService
                .getSearchPageCollectionAtCollectionSlicedFirst(keyword, pageable, getMemberId(currentMember), length));
    }

    @ApiOperation(value = "키워드 컬렉션 검색 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "keyword",
                    value = "검색 키워드",
                    required = true,
                    paramType = "path",
                    dataType = "string",
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
                    example = "popularity,desc")
    })
    @GetMapping("/collection/{keyword}")
    public SingleApiResult<Slice<SearchCollectionDto>> readCollectionByKeyword(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("readCollectionByKeyword");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);

        return getResult(
                collectionQueryService.getSearchPageCollectionAtCollectionSliced(keyword, pageable, getMemberId(currentMember)));
    }

    public Long getMemberId(MemberDetails memberDetails) {
        return memberDetails == null ? null : memberDetails.getId();
    }
}
