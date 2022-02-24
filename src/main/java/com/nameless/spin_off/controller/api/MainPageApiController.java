package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionOrderByCollectedDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.query.MainPageQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/main-page")
public class MainPageApiController {

    private final MainPageQueryService mainPageService;

    @GetMapping("/discovery/post/id")
    public MainPageResult<Slice<MainPagePostDto>> getMainPagePostsOrderById(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam(value = "memberId", required = false) Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService.getPostsOrderById(PageRequest.of(page, size), memberId);
        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/discovery/post/popularity")
    public MainPageResult<Slice<MainPagePostDto>> getPostsOrderByPopularityBySlicingAfterLocalDateTime(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam(value = "id", required = false) Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService
                .getPostsOrderByPopularityBySlicing(
                        PageRequest.of(page, size), memberId);
        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/discovery/collection/popularity")
    public MainPageResult<Slice<MainPageCollectionDto>> getCollectionsOrderByPopularityBySlicingAfterLocalDateTime(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam(value = "id", required = false) Long memberId) throws NotExistMemberException {

        Slice<MainPageCollectionDto> slice = mainPageService
                .getCollectionsOrderByPopularityBySlicing(
                        PageRequest.of(page, size), memberId);
        return new MainPageResult<Slice<MainPageCollectionDto>>(slice);
    }

    @GetMapping("/following/post/hashtag")
    public MainPageResult<Slice<MainPagePostDto>> getPostsByFollowedHashtagOrderByIdSliced(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService
                .getPostsByFollowedHashtagOrderByIdSliced(PageRequest.of(page, size), memberId);

        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/following/post/member")
    public MainPageResult<Slice<MainPagePostDto>> getPostsByFollowingMemberOrderByIdSliced(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService
                .getPostsByFollowingMemberOrderByIdSliced(PageRequest.of(page, size), memberId);

        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/following/collection/member")
    public MainPageResult<Slice<MainPageCollectionDto>> getCollectionsByFollowedMemberOrderByIdSliced(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPageCollectionDto> slice = mainPageService
                .getCollectionsByFollowedMemberOrderByIdSliced(PageRequest.of(page, size), memberId);

        return new MainPageResult<Slice<MainPageCollectionDto>>(slice);
    }

    @GetMapping("/following/post/movie")
    public MainPageResult<Slice<MainPagePostDto>> getPostsByFollowedMovieOrderByIdSliced(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService
                .getPostsByFollowedMovieOrderByIdSliced(PageRequest.of(page, size), memberId);

        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/following/collection/collection")
    public MainPageResult<Slice<MainPageCollectionOrderByCollectedDto>> getCollectionsByFollowedCollectionsOrderByIdSliced(
            @RequestParam("page") Integer page, @RequestParam("size") Integer size,
            @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPageCollectionOrderByCollectedDto> slice = mainPageService
                .getCollectionsByFollowedCollectionsOrderByIdSliced(PageRequest.of(page, size), memberId);

        return new MainPageResult<Slice<MainPageCollectionOrderByCollectedDto>>(slice);
    }

    @Data
    @AllArgsConstructor
    public static class MainPageResult<T> {
        private T data;
    }
}
