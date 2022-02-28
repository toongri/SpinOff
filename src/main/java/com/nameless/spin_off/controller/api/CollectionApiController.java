package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.collection.CollectionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/collection")
public class CollectionApiController {

    private final CollectionService collectionService;

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

    @Data
    @AllArgsConstructor
    public static class CollectionApiResult<T> {
        private T data;
    }
}
