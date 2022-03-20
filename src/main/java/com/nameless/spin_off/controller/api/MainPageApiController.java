package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/main-page")
public class MainPageApiController {

    private final MainPageQueryService mainPageService;

    @GetMapping("/discovery")
    public MainPageResult<MainPageDiscoveryDto> getDiscoveryData(
            @LoginMember MemberDetails currentMember,
            @Qualifier("popular_post") @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable popularPostPageable,
            @Qualifier("latest_post") @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                    Pageable latestPostPageable,
            @Qualifier("collection")  @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC)
                    Pageable collectionPageable)
            throws NotExistMemberException {

        log.info("getDiscoveryData");
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

    @GetMapping("/following")
    public MainPageResult<MainPageFollowDto> getFollowData(
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

        log.info("getFollowData");
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

    @Data
    @AllArgsConstructor
    public static class MainPageResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> MainPageResult<T> getResult(T data) {
        return new MainPageResult<>(data, true, "0", "성공");
    }

    public Long getMemberId(MemberDetails memberDetails) {
        return memberDetails == null ? null : memberDetails.getId();
    }

}
