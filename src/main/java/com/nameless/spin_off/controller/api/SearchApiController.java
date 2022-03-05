package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.SearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieFirstDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtHashtagPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
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

        return new SearchApiResult<>(
                searchQueryService.getRelatedSearchAllByKeyword(keyword, length));
    }

    @GetMapping("/related/hashtag/{keyword}")
    public SearchApiResult<List<RelatedSearchHashtagDto>> getRelatedSearchHashtagByKeyword(
            @PathVariable String keyword, @RequestParam int length)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return new SearchApiResult<>(
                searchQueryService.getRelatedSearchHashtagByKeyword(keyword, length));
    }

    @GetMapping("/related/member/{keyword}")
    public SearchApiResult<List<RelatedSearchMemberDto>> getRelatedSearchMemberByKeyword
            (@PathVariable String keyword, @RequestParam int length)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return new SearchApiResult<>(
                searchQueryService.getRelatedSearchMemberByKeyword(keyword, length));
    }

    @GetMapping("/member-latest/{memberId}")
    public SearchApiResult<List<LastSearchDto>> getLastSearchesByMemberFirst(
            @PathVariable Long memberId, @RequestParam int length)
            throws NotExistMemberException {

        return new SearchApiResult<>(searchQueryService.getLastSearchesByMemberLimit(memberId, length));
    }

    @GetMapping("/all/{keyword}/first")
    public SearchApiResult<SearchFirstDto<SearchAllDto>> getLastSearchesByMemberFirst(
            @PathVariable String keyword, @RequestParam(required = false) Long memberId, @RequestParam int length,
            @Qualifier("post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable postPageable,
            @Qualifier("collection") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable,
            @Qualifier("member") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("movie") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable moviePageable)
            throws NotExistMemberException {

        return new SearchApiResult<>(
                searchQueryService.getSearchPageDataAtAllFirst(keyword, memberId, length,
                        postPageable, collectionPageable, memberPageable, moviePageable));
    }

    @GetMapping("/all/{keyword}")
    public SearchApiResult<SearchAllDto> getLastSearchesByMember(
            @PathVariable String keyword, @RequestParam(required = false) Long memberId,
            @Qualifier("post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable postPageable,
            @Qualifier("collection") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable,
            @Qualifier("member") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("movie") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable moviePageable)
            throws NotExistMemberException {

        return new SearchApiResult<>(
                searchQueryService.getSearchPageDataAtAll(
                        keyword, memberId,
                        postPageable,
                        collectionPageable,
                        memberPageable,
                        moviePageable));
    }

    @GetMapping("/all/hashtag/first")
    public SearchApiResult<SearchFirstDto<Slice<SearchPageAtHashtagPostDto>>> getAllByHashtagFirst(
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long memberId,
            @RequestParam List<String> hashtagContents, @RequestParam int length) {

        log.info("getAllByHashtagFirst");
        log.info("memberId : {}", memberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("hashtagContents : {}", hashtagContents);
        log.info("length : {}", length);


        return new SearchApiResult<>(postQueryService
                .getPostsByHashtagsSlicedForSearchPageFirst(pageable, hashtagContents, memberId, length));
    }

    @GetMapping("/all/hashtag")
    public SearchApiResult<Slice<SearchPageAtHashtagPostDto>> getAllByHashtag(
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long memberId, @RequestParam List<String> hashtagContents) {

        log.info("getAllByHashtag");
        log.info("memberId : {}", memberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("hashtagContents : {}", hashtagContents);

        return new SearchApiResult<>(postQueryService
                .getPostsByHashtagsSlicedForSearchPage(pageable, hashtagContents, memberId));
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

        return new SearchApiResult<>(movieQueryService.getSearchPageMovieAtMovieSlicedFirst(keyword, pageable, length));
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

        return new SearchApiResult<>(
                movieQueryService.getSearchPageMovieAtMovieSliced(keyword, pageable));
    }

    @GetMapping("/member/{keyword}/first")
    public SearchApiResult<SearchFirstDto<Slice<SearchMemberDto>>> getMemberByKeywordFirst(
            @PathVariable String keyword, @RequestParam(required = false) Long memberId, @RequestParam int length,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("getMemberByKeywordFirst");
        log.info("memberId : {}", memberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return new SearchApiResult<>(
                memberQueryService.getSearchPageMemberAtMemberSlicedFirst(keyword, pageable, memberId, length));
    }

    @GetMapping("/member/{keyword}")
    public SearchApiResult<Slice<SearchMemberDto>> getMemberByKeyword(
            @PathVariable String keyword, @RequestParam(required = false) Long memberId,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable)
            throws NotExistMemberException {

        log.info("getMemberByKeyword");
        log.info("memberId : {}", memberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);

        return new SearchApiResult<>(
                memberQueryService.getSearchPageMemberAtMemberSliced(keyword, pageable, memberId));
    }

    @GetMapping("/collection/{keyword}/first")
    public SearchApiResult<SearchFirstDto<Slice<SearchCollectionDto>>> getCollectionByKeywordFirst(
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable String keyword, @RequestParam(required = false) Long memberId, @RequestParam int length)
            throws NotExistMemberException {

        log.info("getCollectionByKeywordFirst");
        log.info("memberId : {}", memberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);
        log.info("length : {}", length);

        return new SearchApiResult<>(collectionQueryService
                .getSearchPageCollectionAtCollectionSlicedFirst(keyword, pageable, memberId, length));
    }

    @GetMapping("/collection/{keyword}")
    public SearchApiResult<Slice<SearchCollectionDto>> getCollectionByKeyword(
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @PathVariable String keyword, @RequestParam(required = false) Long memberId) throws NotExistMemberException {

        log.info("getCollectionByKeyword");
        log.info("memberId : {}", memberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());
        log.info("keyword : {}", keyword);

        return new SearchApiResult<>(
                collectionQueryService.getSearchPageCollectionAtCollectionSliced(keyword, pageable, memberId));
    }

    @Data
    @AllArgsConstructor
    public static class SearchApiResult<T> {
        private T data;
    }
}
