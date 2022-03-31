package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.dto.CollectionDto.PostInCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.QuickPostInCollectionDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.entity.enums.EnumMapper;
import com.nameless.spin_off.entity.enums.EnumMapperValue;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.query.CollectionQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"컬렉션 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/collection")
public class CollectionApiController {

    private final CollectionService collectionService;
    private final CollectionQueryService collectionQueryService;
    private final EnumMapper enumMapper;

    @ApiOperation(value = "컬렉션 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionVO",
                    value = "컬렉션 정보",
                    required = true,
                    paramType = "body",
                    dataType = "CreateCollectionVO")
    })
    @PostMapping("")
    public SingleApiResult<Long> createOne(
            @LoginMember MemberDetails currentMember, @RequestBody CreateCollectionVO collectionVO)
            throws NotExistMemberException, IncorrectTitleOfCollectionException, IncorrectContentOfCollectionException {

        log.info("createOne");
        log.info("memberId : {}", currentMember.getId());
        log.info("title : {}", collectionVO.getTitle());
        log.info("content : {}", collectionVO.getContent());
        log.info("publicOfCollectionStatus : {}", collectionVO.getPublicOfCollectionStatus());

        return getResult(collectionService.insertCollectionByCollectionVO(collectionVO, currentMember.getId()));
    }

    @ApiOperation(value = "컬렉션 좋아요 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{collectionId}/like")
    public SingleApiResult<Long> createLikeOne(@LoginMember MemberDetails currentMember, @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        log.info("createLikeOne");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionService.insertLikedCollectionByMemberId(currentMember.getId(), collectionId));
    }

    @ApiOperation(value = "컬렉션 팔로우 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{collectionId}/follow")
    public SingleApiResult<Long> createFollowOne(@LoginMember MemberDetails currentMember, @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyFollowedCollectionException,
            NotExistCollectionException, CantFollowOwnCollectionException {

        log.info("createFollowOne");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionService.insertFollowedCollectionByMemberId(currentMember.getId(), collectionId));
    }

    @ApiOperation(value = "컬렉션 리스트 조회", notes = "로그인 된 멤버의 컬렉션 리스트 조회")
    @ApiImplicitParams({
    })
    @GetMapping("/all")
    public SingleApiResult<List<PostInCollectionDto>> getCollectionsById(
            @LoginMember MemberDetails currentMember) {

        log.info("getCollectionsById");
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionQueryService.getCollectionsById(currentMember.getId()));
    }

    @ApiOperation(value = "최근 수정 컬렉션 조회", notes = "로그인 된 멤버의 가장 최근 수정 컬렉션 조회")
    @ApiImplicitParams({
    })
    @GetMapping("/one")
    public SingleApiResult<QuickPostInCollectionDto> getLatestCollectionNameById(
            @LoginMember MemberDetails currentMember) {

        log.info("getLatestCollectionNameById");
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionQueryService.getLatestCollectionNameById(currentMember.getId()));
    }

    @ApiOperation(value = "컬렉션 공개 설정 리스트 조회", notes = "")
    @ApiImplicitParams({
    })
    @GetMapping("/public-categories")
    public List<EnumMapperValue> getCollectionPublicCategories() {

        log.info("getCollectionPublicCategories");

        return enumMapper.get("PublicOfCollectionStatus");
    }
}
