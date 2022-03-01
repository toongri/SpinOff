package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.MainPageDto.MainPageDiscoveryDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageFollowDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.query.MainPageQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/main-page")
public class MainPageApiController {

    private final MainPageQueryService mainPageService;

    @GetMapping("/discovery")
    public MainPageResult<MainPageDiscoveryDto> getDiscoveryData(
            @RequestParam Long memberId,
            @Qualifier("post") Pageable postPageable, @Qualifier("collection") Pageable collectionPageable)
            throws NotExistMemberException {

        return new MainPageResult<MainPageDiscoveryDto>(
                mainPageService.getDiscoveryData(postPageable, collectionPageable, memberId));
    }

    @GetMapping("/following")
    public MainPageResult<MainPageFollowDto> getDiscoveryData(
            @RequestParam Long memberId,
            @Qualifier("member") Pageable memberPageable, @Qualifier("hashtag") Pageable hashtagPageable,
            @Qualifier("movie") Pageable moviePageable, @Qualifier("collection") Pageable collectionPageable)
            throws NotExistMemberException {

        return new MainPageResult<MainPageFollowDto>(
                mainPageService.getFollowData(memberPageable, hashtagPageable, moviePageable, collectionPageable, memberId));
    }


    @Data
    @AllArgsConstructor
    public static class MainPageResult<T> {
        private T data;
    }

}
