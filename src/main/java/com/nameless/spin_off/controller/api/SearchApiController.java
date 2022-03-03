package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.dto.SearchDto.SearchAllDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;
import com.nameless.spin_off.service.query.HashtagQueryService;
import com.nameless.spin_off.service.query.SearchQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/search")
public class SearchApiController {
    private final SearchQueryService searchQueryService;
    private final HashtagQueryService hashtagQueryService;

    @GetMapping("/related/keyword/all")
    public SearchApiResult<RelatedSearchAllDto> getRelatedSearchAllByKeyword(
            @RequestParam String keyword, @RequestParam int length)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return new SearchApiResult<RelatedSearchAllDto>(
                searchQueryService.getRelatedSearchAllByKeyword(keyword, length));
    }

    @GetMapping("/related/keyword/hashtag")
    public SearchApiResult<List<RelatedSearchHashtagDto>> getRelatedSearchHashtagByKeyword(
            @RequestParam String keyword, @RequestParam int length)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return new SearchApiResult<List<RelatedSearchHashtagDto>>(
                searchQueryService.getRelatedSearchHashtagByKeyword(keyword, length));
    }

    @GetMapping("/related/keyword/member")
    public SearchApiResult<List<RelatedSearchMemberDto>> getRelatedSearchMemberByKeyword
            (@RequestParam String keyword, @RequestParam int length)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return new SearchApiResult<List<RelatedSearchMemberDto>>(
                searchQueryService.getRelatedSearchMemberByKeyword(keyword, length));
    }

    @GetMapping("/member-latest")
    public SearchApiResult<List<LastSearchDto>> getLastSearchesByMemberFirst(@RequestParam Long id, @RequestParam int length)
            throws NotExistMemberException {

        return new SearchApiResult<List<LastSearchDto>>(searchQueryService.getLastSearchesByMemberLimit(id, length));
    }

    @GetMapping("/all/first")
    public SearchApiResultFirst<SearchAllDto, List<RelatedMostTaggedHashtagDto>> getLastSearchesByMemberFirst(
            @RequestParam String keyword, @RequestParam Long id, @RequestParam int length,
            @Qualifier("post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable postPageable,
            @Qualifier("collection") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable,
            @Qualifier("member") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("movie") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable moviePageable)
            throws NotExistMemberException {

        SearchAllDto data = searchQueryService.getSearchPageDataAtAll(keyword, id,
                postPageable, collectionPageable, memberPageable, moviePageable);

        return new SearchApiResultFirst<SearchAllDto, List<RelatedMostTaggedHashtagDto>>(
                data,getHashtagsByPostIds(length, data.getPosts().getContent()));
    }

    @GetMapping("/all/")
    public SearchApiResult<SearchAllDto> getLastSearchesByMember(
            @RequestParam String keyword, @RequestParam Long id,
            @Qualifier("post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable postPageable,
            @Qualifier("collection") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable,
            @Qualifier("member") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("movie") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable moviePageable)
            throws NotExistMemberException {

        return new SearchApiResult<SearchAllDto>(
                searchQueryService.getSearchPageDataAtAll(
                        keyword, id,
                        postPageable,
                        collectionPageable,
                        memberPageable,
                        moviePageable));
    }

    @Data
    @AllArgsConstructor
    public static class SearchApiResult<T> {
        private T data;
    }
    @Data
    @AllArgsConstructor
    public static class SearchApiResultFirst<T, F> {
        private T data;
        private F hashtags;
    }

    private List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(int length, List<SearchPageAtAllPostDto> data) {
        return hashtagQueryService.getHashtagsByPostIds(
                length,
                data.stream().map(SearchPageAtAllPostDto::getPostId).collect(Collectors.toList()));
    }
}
