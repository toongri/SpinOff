package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
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

import static com.nameless.spin_off.StaticVariable.VIEWED_BY_IP_TIME;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/collection")
public class CollectionApiController {

    private final CollectionService collectionService;

    @PostMapping("")
    public CollectionApiResult<Long> createCollectionOne(@RequestBody CreateCollectionVO collectionVO)
            throws NotExistMemberException {
        Long aLong = collectionService.insertCollectionByCollectionVO(collectionVO);

        return new CollectionApiResult<Long>(aLong);
    }

    @PostMapping("/like")
    public CollectionApiResult<Collection> createLikeOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException, OverSearchLikedCollectionException {

        Collection collection = collectionService.insertLikedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Collection>(collection);
    }

    @PostMapping("/view")
    public CollectionApiResult<Collection> viewPostByIp(@RequestBody String ip, @RequestBody Long postId)
            throws NotExistCollectionException, OverSearchViewedCollectionByIpException {

        Collection collection = collectionService
                .insertViewedCollectionByIp(ip, postId, LocalDateTime.now(), VIEWED_BY_IP_TIME);

        return new CollectionApiResult<Collection>(collection);
    }

    @PostMapping("/follow")
    public CollectionApiResult<Collection> createFollowOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotExistMemberException, AlreadyFollowedCollectionException,
            OverSearchFollowedCollectionException, NotExistCollectionException {

        Collection collection = collectionService.insertFollowedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Collection>(collection);
    }

    @PostMapping("/post")
    public CollectionApiResult<List<Collection>> addPostInCollections(
            @RequestBody Long memberId, @RequestBody Long postId, @RequestBody List<Long> collectionIds)
            throws OverSearchCollectedPostException, NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotExistCollectionException {

        List<Collection> collections = collectionService.insertCollectedPosts(memberId, postId, collectionIds);

        return new CollectionApiResult<List<Collection>>(collections);
    }


    @Data
    @AllArgsConstructor
    public static class CollectionApiResult<T> {
        private T data;
    }
}
