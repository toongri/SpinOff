package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.dto.CollectionDto.PostInCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.QuickPostInCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.ReadCollectionDto;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.dto.CommentDto.CreateCommentInCollectionVO;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto.CollectedPostDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.entity.enums.EnumMapper;
import com.nameless.spin_off.entity.enums.EnumMapperValue;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotExistCommentInCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.query.CollectionQueryService;
import com.nameless.spin_off.service.query.CommentInCollectionQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final CommentInCollectionQueryService commentInCollectionQueryService;
    private final CommentInCollectionService commentInCollectionService;
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

    @ApiOperation(value = "컬렉션 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{collectionId}")
    public SingleApiResult<ReadCollectionDto> readOne(
            @LoginMember MemberDetails currentMember, @PathVariable Long collectionId) {

        log.info("readOne");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", getCurrentMemberId(currentMember));
        return getResult(collectionQueryService.getCollectionForRead(currentMember, collectionId));
    }

    @ApiOperation(value = "컬렉션 글 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "page",
                    value = "페이지 번호",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "size",
                    value = "페이지 크기",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "sort",
                    value = "페이지 정렬",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc")
    })
    @GetMapping("/{collectionId}/post")
    public SingleApiResult<Slice<CollectedPostDto>> readCollectedPostAll(
            @LoginMember MemberDetails currentMember, @PathVariable Long collectionId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("readCollectedPostAll");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", getCurrentMemberId(currentMember));
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());

        return getResult(collectionQueryService.getCollectedPostsSliced(currentMember, collectionId, pageable));
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

    @ApiOperation(value = "컬렉션 좋아요 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{collectionId}/like")
    public SingleApiResult<List<MembersByContentDto>> readLikeAll(@LoginMember MemberDetails currentMember,
                                                                  @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readLikeAll");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", currentMemberId);

        return getResult(collectionQueryService.getLikeCollectionMembers(currentMemberId, collectionId));
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

    @ApiOperation(value = "컬렉션 댓글 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{collectionId}/comment")
    public SingleApiResult<List<ContentCommentDto>> readCommentsInCollection(@LoginMember MemberDetails currentMember,
                                                                             @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyFollowedCollectionException,
            NotExistCollectionException, CantFollowOwnCollectionException {

        log.info("readCommentsInCollection");
        log.info("collectionId : {}", collectionId);
        log.info("memberId : {}", getCurrentMemberId(currentMember));

        return getResult(commentInCollectionQueryService.getCommentsByCollectionId(currentMember, collectionId));
    }

    @ApiOperation(value = "컬렉션 댓글 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "commentVO",
                    value = "댓글 정보",
                    required = true,
                    paramType = "body",
                    dataType = "CreateCommentInCollectionVO")
    })
    @PostMapping("/{collectionId}/comment")
    public SingleApiResult<Long> createCommentInCollection(
            @LoginMember MemberDetails currentMember, @RequestBody CreateCommentInCollectionVO commentVO,
            @PathVariable Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {

        log.info("createCommentInCollection");
        log.info("memberId : {}", currentMember.getId());
        log.info("collectionId : {}", collectionId);
        log.info("parentId : {}", commentVO.getParentId());
        log.info("content : {}", commentVO.getContent());

        Long commentId = commentInCollectionService.insertCommentInCollectionByCommentVO(
                commentVO, currentMember.getId(), collectionId);
        return getResult(commentId);
    }

    @ApiOperation(value = "컬렉션 댓글 좋아요 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "commentId",
                    value = "댓글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/comment/{commentId}/like")
    public SingleApiResult<Long> createCommentLikeOneInCollection(
            @LoginMember MemberDetails currentMember, @PathVariable Long commentId)
            throws NotExistMemberException, AlreadyLikedCommentInCollectionException,
            NotExistCommentInCollectionException {

        log.info("createCommentLikeOneInCollection");
        log.info("memberId : {}", currentMember.getId());
        log.info("commentId : {}", commentId);

        return getResult(commentInCollectionService.insertLikedCommentByMemberId(currentMember.getId(), commentId));
    }

    @ApiOperation(value = "컬렉션 리스트 조회", notes = "로그인 된 멤버의 컬렉션 리스트 조회")
    @ApiImplicitParams({
    })
    @GetMapping("/all")
    public SingleApiResult<List<PostInCollectionDto>> getCollectionsById(
            @LoginMember MemberDetails currentMember) {

        log.info("getCollectionsById");
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionQueryService.getCollectionsByMemberId(currentMember.getId()));
    }

    @ApiOperation(value = "최근 수정 컬렉션 조회", notes = "로그인 된 멤버의 가장 최근 수정 컬렉션 조회")
    @ApiImplicitParams({
    })
    @GetMapping("/one")
    public SingleApiResult<QuickPostInCollectionDto> getLatestCollectionNameById(
            @LoginMember MemberDetails currentMember) {

        log.info("getLatestCollectionNameById");
        log.info("memberId : {}", currentMember.getId());

        return getResult(collectionQueryService.getCollectionNameByMemberId(currentMember.getId()));
    }

    @ApiOperation(value = "컬렉션 공개 설정 리스트 조회", notes = "")
    @ApiImplicitParams({
    })
    @GetMapping("/public-categories")
    public List<EnumMapperValue> getCollectionPublicCategories() {

        log.info("getCollectionPublicCategories");

        return enumMapper.get("PublicOfCollectionStatus");
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }
}
