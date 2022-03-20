package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
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
            @LoginMember MemberDetails currentMember, @RequestBody CreateCollectionVO collectionVO)
            throws NotExistMemberException, OverTitleOfCollectionException, OverContentOfCollectionException {

        log.info("createOne");
        log.info("memberId : {}", currentMember.getId());
        log.info("title : {}", collectionVO.getTitle());
        log.info("content : {}", collectionVO.getContent());
        log.info("publicOfCollectionStatus : {}", collectionVO.getPublicOfCollectionStatus());

        return getResult(collectionService.insertCollectionByCollectionVO(collectionVO, currentMember.getId()));
    }

    @PostMapping("/{collectionId}/like")
    public CollectionApiResult<Long> createLikeOne(@LoginMember MemberDetails currentMember, @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        log.info("createLikeOne");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionService.insertLikedCollectionByMemberId(currentMember.getId(), collectionId));
    }

    @PostMapping("/{collectionId}/follow")
    public CollectionApiResult<Long> createFollowOne(@LoginMember MemberDetails currentMember, @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyFollowedCollectionException,
            NotExistCollectionException, CantFollowOwnCollectionException {

        log.info("createFollowOne");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionService.insertFollowedCollectionByMemberId(currentMember.getId(), collectionId));
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
