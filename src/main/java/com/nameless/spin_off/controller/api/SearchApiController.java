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
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;
import com.nameless.spin_off.service.query.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/search")
public class SearchApiController {
    private final SearchQueryService searchQueryService;
    private final CollectionQueryService collectionQueryService;
    private final PostQueryService postQueryService;
    private final MemberQueryService memberQueryService;
    private final MovieQueryService movieQueryService;

    @GetMapping("/related/all/{keyword}")
    public SearchApiResult<RelatedSearchAllDto> getRelatedSearchAllByKeyword(
            @PathVariable String keyword, @RequestParam int length)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return getResult(
                searchQueryService.getRelatedSearchAllByKeyword(keyword, length));
    }

    @GetMapping("/related/hashtag/{keyword}")
    public SearchApiResult<List<RelatedSearchHashtagDto>> getRelatedSearchHashtagByKeyword(
            @PathVariable String keyword, @RequestParam int length)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return getResult(
                searchQueryService.getRelatedSearchHashtagByKeyword(keyword, length));
    }

    @GetMapping("/related/member/{keyword}")
    public SearchApiResult<List<RelatedSearchMemberDto>> getRelatedSearchMemberByKeyword
            (@PathVariable String keyword, @RequestParam int length)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return getResult(
                searchQueryService.getRelatedSearchMemberByKeyword(keyword, length));
    }

    @GetMapping("/all/{keyword}/first")
    public SearchApiResult<SearchFirstDto<SearchAllDto>> getLastSearchesByMemberFirst(
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

        return getResult(
                searchQueryService.getSearchPageDataAtAllFirst(keyword, getMemberId(currentMember), length,
                        postPageable, collectionPageable, memberPageable, moviePageable));
    }

    @GetMapping("/all/{keyword}")
    public SearchApiResult<SearchAllDto> getLastSearchesByMember(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword,
            @Qualifier("post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable postPageable,
            @Qualifier("collection") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable,
            @Qualifier("member") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("movie") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable moviePageable)
            throws NotExistMemberException {

        return getResult(
                searchQueryService.getSearchPageDataAtAll(
                        keyword, getMemberId(currentMember),
                        postPageable,
                        collectionPageable,
                        memberPageable,
                        moviePageable));
    }

    @GetMapping("/all/hashtag/first")
    public SearchApiResult<SearchFirstDto<Slice<SearchPageAtHashtagPostDto>>> getAllByHashtagFirst(
            @LoginMember MemberDetails currentMember,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam List<String> hashtagContents, @RequestParam int length) {

        log.info("getAllByHashtagFirst");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("hashtagContents : {}", hashtagContents);
        log.info("length : {}", length);


        return getResult(postQueryService
                .getPostsByHashtagsSlicedForSearchPageFirst(pageable, hashtagContents, getMemberId(currentMember), length));
    }

    @GetMapping("/all/hashtag")
    public SearchApiResult<Slice<SearchPageAtHashtagPostDto>> getAllByHashtag(
            @LoginMember MemberDetails currentMember,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam List<String> hashtagContents) {

        log.info("getAllByHashtag");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("hashtagContents : {}", hashtagContents);

        return getResult(postQueryService
                .getPostsByHashtagsSlicedForSearchPage(pageable, hashtagContents, getMemberId(currentMember)));
    }

    @GetMapping("/movie/{keyword}/first")
    public SearchApiResult<SearchFirstDto<SearchMovieFirstDto>> getMovieByKeyword(
            @PathVariable String keyword, @RequestParam int length,
            @PageableDefault(sort = "popularity", direction  = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMovieException {

        log.info("getMovieByKeyword");
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(movieQueryService.getSearchPageMovieAtMovieSlicedFirst(keyword, pageable, length));
    }

    @GetMapping("/movie/{keyword}")
    public SearchApiResult<Slice<SearchMovieDto>> getMovieByKeyword(
            @PathVariable String keyword,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("getMovieByKeyword");
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);

        return getResult(
                movieQueryService.getSearchPageMovieAtMovieSliced(keyword, pageable));
    }

    @GetMapping("/member/{keyword}/first")
    public SearchApiResult<SearchFirstDto<Slice<SearchMemberDto>>> getMemberByKeywordFirst(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword, @RequestParam int length,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("getMemberByKeywordFirst");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(
                memberQueryService.getSearchPageMemberAtMemberSlicedFirst(keyword, pageable, getMemberId(currentMember), length));
    }

    @GetMapping("/member/{keyword}")
    public SearchApiResult<Slice<SearchMemberDto>> getMemberByKeyword(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("getMemberByKeyword");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);

        return getResult(
                memberQueryService.getSearchPageMemberAtMemberSliced(keyword, pageable, getMemberId(currentMember)));
    }

    @GetMapping("/collection/{keyword}/first")
    public SearchApiResult<SearchFirstDto<Slice<SearchCollectionDto>>> getCollectionByKeywordFirst(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword, @RequestParam int length,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("getCollectionByKeywordFirst");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return getResult(collectionQueryService
                .getSearchPageCollectionAtCollectionSlicedFirst(keyword, pageable, getMemberId(currentMember), length));
    }

    @GetMapping("/collection/{keyword}")
    public SearchApiResult<Slice<SearchCollectionDto>> getCollectionByKeyword(
            @LoginMember MemberDetails currentMember, @PathVariable String keyword,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("getCollectionByKeyword");
        log.info("memberId : {}", getMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);

        return getResult(
                collectionQueryService.getSearchPageCollectionAtCollectionSliced(keyword, pageable, getMemberId(currentMember)));
    }

    @Data
    @AllArgsConstructor
    public static class SearchApiResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> SearchApiResult<T> getResult(T data) {
        return new SearchApiResult<>(data, true, "0", "성공");
    }

    public Long getMemberId(MemberDetails memberDetails) {
        return memberDetails == null ? null : memberDetails.getId();
    }
}
