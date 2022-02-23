package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;
import com.nameless.spin_off.service.query.SearchQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/search")
public class SearchApiController {
    private final SearchQueryService searchQueryService;

    @GetMapping("/related/keyword/all")
    public SearchApiResult<RelatedSearchAllDto> getRelatedSearchAllByKeyword(@RequestParam String keyword)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return new SearchApiResult<RelatedSearchAllDto>(searchQueryService.getRelatedSearchAllByKeyword(keyword));
    }

    @GetMapping("/related/keyword/hashtag")
    public SearchApiResult<List<RelatedSearchHashtagDto>> getRelatedSearchHashtagByKeyword(@RequestParam String keyword)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return new SearchApiResult<List<RelatedSearchHashtagDto>>(searchQueryService.getRelatedSearchHashtagByKeyword(keyword));
    }

    @GetMapping("/related/keyword/member")
    public SearchApiResult<List<RelatedSearchMemberDto>> getRelatedSearchMemberByKeyword(@RequestParam String keyword)
            throws UnderLengthRelatedKeywordException, OverLengthRelatedKeywordException {

        return new SearchApiResult<List<RelatedSearchMemberDto>>(searchQueryService.getRelatedSearchMemberByKeyword(keyword));
    }


    @GetMapping("/most-popular")
    public SearchApiResult<List<MostPopularHashtag>> getMostPopularHashtag() {

        return new SearchApiResult<List<MostPopularHashtag>>(searchQueryService.getMostPopularHashtag());
    }

    @GetMapping("/member-last")
    public SearchApiResult<List<LastSearchDto>> getLastSearchesByMember(@RequestParam Long id) throws NotExistMemberException {

        return new SearchApiResult<List<LastSearchDto>>(searchQueryService.getLastSearchesByMember(id));
    }

    @Data
    @AllArgsConstructor
    public static class SearchApiResult<T> {
        private T data;
    }
}
