package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.dto.CollectionDto.SearchPageAtCollectionCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.query.CollectionQueryService;
import com.nameless.spin_off.service.query.HashtagQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/collection")
public class CollectionApiController {

    private final CollectionService collectionService;
    private final CollectionQueryService collectionQueryService;
    private final HashtagQueryService hashtagQueryService;

    @PostMapping("")
    public CollectionApiResult<Long> createCollectionOne(@RequestBody CreateCollectionVO collectionVO)
            throws NotExistMemberException, OverTitleOfCollectionException, OverContentOfCollectionException {
        Long aLong = collectionService.insertCollectionByCollectionVO(collectionVO);

        return new CollectionApiResult<Long>(aLong);
    }

    @PostMapping("/like")
    public CollectionApiResult<Long> createLikeOne(@RequestParam Long memberId, @RequestParam Long postId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        Long collectionId = collectionService.insertLikedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Long>(collectionId);
    }

    @PostMapping("/view")
    public CollectionApiResult<Long> createViewOne(@RequestParam String ip, @RequestParam Long postId)
            throws NotExistCollectionException {

        Long collectionId = collectionService
                .insertViewedCollectionByIp(ip, postId);

        return new CollectionApiResult<Long>(collectionId);
    }

    @PostMapping("/follow")
    public CollectionApiResult<Long> createFollowOne(@RequestParam Long memberId, @RequestParam Long postId)
            throws NotExistMemberException, AlreadyFollowedCollectionException,
            NotExistCollectionException, CantFollowOwnCollectionException {

        Long collectionId = collectionService.insertFollowedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Long>(collectionId);
    }

    @GetMapping("/search")
    public CollectionApiResult getSearchPageCollectionAtCollectionSliced(
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam String keyword, @RequestParam Long memberId) throws NotExistMemberException {
        return new CollectionApiResult(
                collectionQueryService.getSearchPageCollectionAtCollectionSliced(keyword, pageable, memberId));
    }

    @GetMapping("/search/first")
    public CollectionApiSearchResult getSearchPageCollectionAtCollectionSlicedFirst(
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam String keyword, @RequestParam Long memberId, @RequestParam int length)
            throws NotExistMemberException {

        Slice<SearchPageAtCollectionCollectionDto> data =
                collectionQueryService.getSearchPageCollectionAtCollectionSliced(keyword, pageable, memberId);
        return new CollectionApiSearchResult(
                data,
                getHashtagsByPostIds(length, data.getContent()));
    }

    @Data
    @AllArgsConstructor
    public static class CollectionApiResult<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    public static class CollectionApiSearchResult<T, F> {
        private T data;
        private F hashtags;
    }

    private List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(
            int length, List<SearchPageAtCollectionCollectionDto> data) {
        return hashtagQueryService.getHashtagsByCollectionIds(
                length,
                data.stream().map(SearchPageAtCollectionCollectionDto::getCollectionId).collect(Collectors.toList()));
    }
}
