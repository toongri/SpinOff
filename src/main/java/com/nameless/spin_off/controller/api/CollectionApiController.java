package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMemberId;
import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.collection.CollectionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/collection")
public class CollectionApiController {

    private final CollectionService collectionService;

    @PostMapping("")
    public CollectionApiResult<Long> createOne(
            @LoginMemberId Long memberId, @RequestBody CreateCollectionVO collectionVO)
            throws NotExistMemberException, OverTitleOfCollectionException, OverContentOfCollectionException {

        log.info("createOne");
        log.info("memberId : {}", memberId);
        log.info("title : {}", collectionVO.getTitle());
        log.info("content : {}", collectionVO.getContent());
        log.info("publicOfCollectionStatus : {}", collectionVO.getPublicOfCollectionStatus());

        return getResult(collectionService.insertCollectionByCollectionVO(collectionVO, memberId));
    }

    @PostMapping("/{collectionId}/like")
    public CollectionApiResult<Long> createLikeOne(@PathVariable Long collectionId, @LoginMemberId Long memberId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        log.info("createLikeOne");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", memberId);

        return getResult(collectionService.insertLikedCollectionByMemberId(memberId, collectionId));
    }

    @PostMapping("/{collectionId}/view/{ip}")
    public CollectionApiResult<Long> createViewOne(@PathVariable String ip, @PathVariable Long collectionId)
            throws NotExistCollectionException {

        log.info("createViewOne");
        log.info("collectionId : {}", collectionId);
        log.info("ip : {}", ip);

        return getResult(collectionService.insertViewedCollectionByIp(ip, collectionId));
    }

    @PostMapping("/{collectionId}/follow")
    public CollectionApiResult<Long> createFollowOne(@LoginMemberId Long memberId, @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyFollowedCollectionException,
            NotExistCollectionException, CantFollowOwnCollectionException {

        log.info("createFollowOne");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", memberId);

        return getResult(collectionService.insertFollowedCollectionByMemberId(memberId, collectionId));
    }

    @Data
    @AllArgsConstructor
    public static class CollectionApiResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> CollectionApiResult<T> getResult(T data) {
        return new CollectionApiResult<>(data, true, "0", "성공");
    }
}
