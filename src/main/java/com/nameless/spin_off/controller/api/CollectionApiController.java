package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.dto.CollectionDto.PostInCollectionDto;
import com.nameless.spin_off.entity.enums.EnumMapper;
import com.nameless.spin_off.entity.enums.EnumMapperValue;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.query.CollectionQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/collection")
public class CollectionApiController {

    private final CollectionService collectionService;
    private final CollectionQueryService collectionQueryService;
    private final EnumMapper enumMapper;

    @PostMapping("")
    public CollectionApiResult<Long> createOne(
            @LoginMember MemberDetails currentMember, @RequestBody CreateCollectionVO collectionVO)
            throws NotExistMemberException, IncorrectTitleOfCollectionException, IncorrectContentOfCollectionException {

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

    @GetMapping("/list/name")
    public CollectionApiResult<List<PostInCollectionDto>> getCollectionNamesById(
            @LoginMember MemberDetails currentMember) {

        log.info("getCollectionNamesById");
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionQueryService.getCollectionNamesByMemberId(currentMember.getId()));
    }

    @GetMapping("/public-categories")
    public List<EnumMapperValue> getCollectionPublicCategories() {

        log.info("getCollectionPublicCategories");

        return enumMapper.get("PublicOfCollectionStatus");
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
