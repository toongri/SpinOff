package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.collection.CollectionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.nameless.spin_off.StaticVariable.VIEWED_BY_IP_MINUTE;

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
    public CollectionApiResult<Long> createLikeOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException, OverSearchLikedCollectionException {

        Long collectionId = collectionService.insertLikedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Long>(collectionId);
    }

    @PostMapping("/view")
    public CollectionApiResult<Long> viewPostByIp(@RequestBody String ip, @RequestBody Long postId)
            throws NotExistCollectionException, OverSearchViewedCollectionByIpException {

        Long collectionId = collectionService
                .insertViewedCollectionByIp(ip, postId);

        return new CollectionApiResult<Long>(collectionId);
    }

    @PostMapping("/follow")
    public CollectionApiResult<Long> createFollowOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotExistMemberException, AlreadyFollowedCollectionException,
            OverSearchFollowedCollectionException, NotExistCollectionException {

        Long collectionId = collectionService.insertFollowedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Long>(collectionId);
    }

    @Data
    @AllArgsConstructor
    public static class CollectionApiResult<T> {
        private T data;
    }
}
