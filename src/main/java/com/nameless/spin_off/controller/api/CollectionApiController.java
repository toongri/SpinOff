package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
import com.nameless.spin_off.service.collection.CollectionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/collection")
public class CollectionApiController {

    private final CollectionService collectionService;

    private final Long VIEWED_BY_IP_TIME = 1L;
    private final LocalDateTime currentTime = LocalDateTime.now();

    @PostMapping("")
    public CollectionApiResult<Long> createCollectionOne(@RequestBody CreateCollectionVO collectionVO)
            throws NotSearchMemberException {
        Long aLong = collectionService.insertCollectionByCollectionVO(collectionVO);

        return new CollectionApiResult<Long>(aLong);
    }

    @PostMapping("/like")
    public CollectionApiResult<Collection> createLikeOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotSearchMemberException, AlreadyLikedCollectionException,
            NotSearchCollectionException, OverSearchLikedCollectionException {

        Collection collection = collectionService.insertLikedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Collection>(collection);
    }

    @PostMapping("/view")
    public CollectionApiResult<Collection> viewPostByIp(@RequestBody String ip, @RequestBody Long postId)
            throws NotSearchCollectionException, OverSearchViewedCollectionByIpException {

        Collection collection = collectionService
                .insertViewedCollectionByIp(ip, postId, currentTime, VIEWED_BY_IP_TIME);

        return new CollectionApiResult<Collection>(collection);
    }

    @PostMapping("/follow")
    public CollectionApiResult<Collection> createFollowOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotSearchMemberException, AlreadyFollowedCollectionException,
            OverSearchFollowedCollectionException, NotSearchCollectionException {

        Collection collection = collectionService.insertFollowedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Collection>(collection);
    }

    @PostMapping("/post")
    public CollectionApiResult<List<Collection>> addPostInCollections(
            @RequestBody Long memberId, @RequestBody Long postId, @RequestBody List<Long> collectionIds)
            throws OverSearchCollectedPostException, NotSearchMemberException,
            NotSearchPostException, AlreadyCollectedPostException, NotSearchCollectionException {

        List<Collection> collections = collectionService.insertCollectedPosts(memberId, postId, collectionIds);

        return new CollectionApiResult<List<Collection>>(collections);
    }


    @Data
    @AllArgsConstructor
    public static class CollectionApiResult<T> {
        private T data;
    }
}
