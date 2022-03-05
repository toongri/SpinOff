package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.MainPageDto.MainPageDiscoveryDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageFollowDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.query.MainPageQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/main-page")
public class MainPageApiController {

    private final MainPageQueryService mainPageService;

    @GetMapping("/discovery")
    public MainPageResult<MainPageDiscoveryDto> getDiscoveryData(
            @RequestParam(required = false) Long memberId,
            @Qualifier("popular_post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable popularPostPageable,
            @Qualifier("latest_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable latestPostPageable,
            @Qualifier("collection")  @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable)
            throws NotExistMemberException {

        log.info("getDiscoveryData");
        log.info("memberId : {}", memberId);
        log.info("popularPostPageable.getPageNumber() : {}", popularPostPageable.getPageNumber());
        log.info("popularPostPageable.getPageSize() : {}", popularPostPageable.getPageSize());
        log.info("popularPostPageable.getSort() : {}", popularPostPageable.getSort());

        log.info("latestPostPageable.getPageNumber() : {}", latestPostPageable.getPageNumber());
        log.info("latestPostPageable.getPageSize() : {}", latestPostPageable.getPageSize());
        log.info("latestPostPageable.getSort() : {}", latestPostPageable.getSort());

        log.info("collectionPageable.getPageNumber() : {}", collectionPageable.getPageNumber());
        log.info("collectionPageable.getPageSize() : {}", collectionPageable.getPageSize());
        log.info("collectionPageable.getSort() : {}", collectionPageable.getSort());

        return new MainPageResult<>(
                mainPageService.getDiscoveryData(popularPostPageable, latestPostPageable, collectionPageable, memberId));
    }

    @GetMapping("/following")
    public MainPageResult<MainPageFollowDto> getFollowData(
            @RequestParam Long memberId,
            @Qualifier("member_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable memberPageable,
            @Qualifier("hashtag_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable hashtagPageable,
            @Qualifier("movie_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable moviePageable,
            @Qualifier("collection") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable collectionPageable)
            throws NotExistMemberException {

        log.info("getFollowData");
        log.info("memberId : {}", memberId);
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

        return new MainPageResult<>(
                mainPageService.getFollowData(memberPageable, hashtagPageable, moviePageable,
                        collectionPageable, memberId));
    }

    @Data
    @AllArgsConstructor
    public static class MainPageResult<T> {
        private T data;
    }

}
