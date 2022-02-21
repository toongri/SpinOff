package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchDto;
import com.nameless.spin_off.entity.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.member.SearchService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/search")
public class SearchApiController {
    private final SearchService searchService;

    @GetMapping("/related/keyword")
    public SearchApiResult<RelatedSearchDto> getRelatedSearchResult(@RequestParam String keyword) {

        return new SearchApiResult<RelatedSearchDto>(searchService.getRelatedSearchByKeyword(keyword));
    }

    @PostMapping("")
    public SearchApiResult<Long> insertSearchByKeyword(@RequestParam String keyword, @RequestParam Long id,
                                              @RequestParam("status")SearchedByMemberStatus searchedByMemberStatus)
            throws NotExistMemberException {

        return new SearchApiResult<Long>(searchService.insertSearch(id, keyword, searchedByMemberStatus));
    }


    @GetMapping("/last-search")
    public SearchApiResult<List<LastSearchDto>> getLastSearchesByMember(@RequestParam Long id) throws NotExistMemberException {

        return new SearchApiResult<List<LastSearchDto>>(searchService.getLastSearchesByMember(id));
    }


    @GetMapping("/most-popular")
    public SearchApiResult<List<MostPopularHashtag>> getMostPopularHashtag() {

        return new SearchApiResult<List<MostPopularHashtag>>(searchService.getMostPopularHashtag());
    }


    @Data
    @AllArgsConstructor
    public static class SearchApiResult<T> {
        private T data;
    }
}
