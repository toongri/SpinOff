package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.query.MainPageQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
            Pageable pageable,
            @RequestParam(value = "memberId", required = false) Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService.getPostsSliced(pageable, memberId);
        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/discovery/collection/popularity")
    public MainPageResult<Slice<MainPageCollectionDto>> getCollectionsSliced(
            Pageable pageable,
            @RequestParam(value = "id", required = false) Long memberId) throws NotExistMemberException {

        Slice<MainPageCollectionDto> slice = mainPageService.getCollectionsSliced(pageable, memberId);
        return new MainPageResult<Slice<MainPageCollectionDto>>(slice);
    }

    @GetMapping("/following/post/member")
    public MainPageResult<Slice<MainPagePostDto>> getPostsByFollowingMemberSliced(
            Pageable pageable, @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService.getPostsByFollowingMemberSliced(pageable, memberId);

        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/following/post/hashtag")
    public MainPageResult<Slice<MainPagePostDto>> getPostsByFollowedHashtagSliced(
            Pageable pageable, @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService.getPostsByFollowedHashtagSliced(pageable, memberId);

        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/following/post/movie")
    public MainPageResult<Slice<MainPagePostDto>> getPostsByFollowedMovieSliced(
            Pageable pageable, @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPagePostDto> slice = mainPageService
                .getPostsByFollowedMovieSliced(pageable, memberId);

        return new MainPageResult<Slice<MainPagePostDto>>(slice);
    }

    @GetMapping("/following/collection/member")
    public MainPageResult<Slice<MainPageCollectionDto>> getCollectionsByFollowedMemberSliced(
            Pageable pageable, @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPageCollectionDto> slice = mainPageService
                .getCollectionsByFollowedMemberSliced(pageable, memberId);

        return new MainPageResult<Slice<MainPageCollectionDto>>(slice);
    }

    @GetMapping("/following/collection/collection")
    public MainPageResult<Slice<MainPageCollectionDto>> getCollectionsByFollowedCollectionsSliced(
            Pageable pageable, @RequestParam("id") Long memberId) throws NotExistMemberException {

        Slice<MainPageCollectionDto> slice = mainPageService
                .getCollectionsByFollowedCollectionsSliced(pageable, memberId);

        return new MainPageResult<Slice<MainPageCollectionDto>>(slice);
    }

    @Data
    @AllArgsConstructor
    public static class MainPageResult<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    public static class MainPageDiscoveryResult<T> {
        private T data;
    }
}
