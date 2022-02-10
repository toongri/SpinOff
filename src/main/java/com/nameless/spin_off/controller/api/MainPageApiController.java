package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.service.member.JpaMemberService;
import com.nameless.spin_off.service.query.MainPageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/main-page")
public class MainPageApiController {

    private final MainPageService mainPageService;
    private final JpaMemberService memberService;
    private final Long POPULARITY_DATE_DURATION = 2L;
    private final LocalDateTime currentTime = LocalDateTime.now();

    @GetMapping("/discovery/post/id")
    public MainPageResult<Slice<MainPagePostDto>> getMainPagePostsOrderById(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size) {

        Slice<MainPagePostDto> slice = mainPageService.getPostsOrderById(PageRequest.of(page, size));
        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/discovery/post/popularity")
    public MainPageResult<Slice<MainPagePostDto>> getPostsOrderByPopularityBySlicingAfterLocalDateTime(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size) {

        LocalDateTime startTime = currentTime.minusDays(POPULARITY_DATE_DURATION);
//        LocalDateTime startTime = LocalDateTime
//                .of(2022, 2, 9, 21, 58, 25, 390);
        Slice<MainPagePostDto> slice = mainPageService
                .getPostsOrderByPopularityBySlicingAfterLocalDateTime(
                        PageRequest.of(page, size), startTime, currentTime);
        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/discovery/collection/popularity")
    public MainPageResult<Slice<MainPageCollectionDto>> getCollectionsOrderByPopularityBySlicingAfterLocalDateTime(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size) {

        LocalDateTime startTime = currentTime.minusDays(POPULARITY_DATE_DURATION);
//        LocalDateTime startTime = LocalDateTime
//                .of(2022, 2, 9, 21, 58, 25, 390);
        Slice<MainPageCollectionDto> slice = mainPageService
                .getCollectionsOrderByPopularityBySlicingAfterLocalDateTime(
                        PageRequest.of(page, size), startTime, currentTime);
        return new MainPageResult<Slice<MainPageCollectionDto>>(slice);
    }

    @GetMapping("/following/post/hashtag")
    public MainPageResult<Slice<MainPagePostDto>> dfd(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam("id") Long memberId) throws NotSearchMemberException {

        Slice<MainPagePostDto> slice = mainPageService
                .getPostsByFollowedHashtagOrderByIdSliced(PageRequest.of(page, size), memberId);

        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @Data
    @AllArgsConstructor
    public static class MainPageResult<T> {
        private T data;
    }
}
