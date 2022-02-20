package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.SearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchDto;
import com.nameless.spin_off.repository.query.SearchQueryRepository;
import com.nameless.spin_off.service.member.SearchService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/search")
public class SearchApiController {
    private final SearchService searchService;

    @GetMapping
    public SearchApiResult<RelatedSearchDto> getRelatedSearchResult(@RequestParam String keyword) {

        return new SearchApiResult<RelatedSearchDto>(searchService.getRelatedSearchByKeyword(keyword));
    }

    @Data
    @AllArgsConstructor
    public static class SearchApiResult<T> {
        private T data;
    }
}
