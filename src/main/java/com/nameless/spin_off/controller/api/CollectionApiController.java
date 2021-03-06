package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.*;
import com.nameless.spin_off.dto.CommentDto.CommentInCollectionRequestDto;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto.CollectedPostDto;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.enums.EnumMapper;
import com.nameless.spin_off.enums.EnumMapperValue;
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
                    name = "collectionRequestDto",
                    value = "컬렉션 정보",
                    required = true,
                    paramType = "body",
                    dataType = "CollectionRequestDto")
    })
    @PostMapping("")
    public SingleApiResult<Long> createCollection(
            @LoginMember MemberDetails currentMember, @RequestBody CollectionRequestDto collectionRequestDto)
            throws NotExistMemberException, IncorrectTitleOfCollectionException, IncorrectContentOfCollectionException {

        log.info("**** POST :: /collection");
        log.info("title : {}", collectionRequestDto.getTitle());
        log.info("content : {}", collectionRequestDto.getContent());
        log.info("publicOfCollectionStatus : {}", collectionRequestDto.getPublicOfCollectionStatus());

        return getResult(collectionService.insertCollection(collectionRequestDto, currentMember.getId()));
    }

    @ApiOperation(value = "컬렉션 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "ip",
                    value = "ip주소",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "192.168.0.1")
    })
    @GetMapping("/{collectionId}")
    public SingleApiResult<ReadCollectionDto> readCollection(
            @LoginMember MemberDetails currentMember, @PathVariable Long collectionId, @RequestParam String ip) {

        log.info("**** GET :: /collection/{id}");
        log.info("collectionId : {}", collectionId);

        ReadCollectionDto collection = collectionQueryService.getCollectionForRead(currentMember, collectionId);
        collectionService.insertViewedCollectionByIp(ip, collectionId);
        return getResult(collection);
    }

    @ApiOperation(value = "컬렉션 수정", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "collectionRequestDto",
                    value = "컬렉션 정보",
                    required = true,
                    paramType = "body",
                    dataType = "CollectionRequestDto")
    })
    @PutMapping("/{collectionId}")
    public SingleApiResult<Long> updateCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long collectionId,
            @RequestBody CollectionRequestDto collectionRequestDto)
            throws NotExistMemberException, IncorrectTitleOfCollectionException, IncorrectContentOfCollectionException {

        log.info("**** PUT :: /collection/{id}");
        log.info("title : {}", collectionRequestDto.getTitle());
        log.info("content : {}", collectionRequestDto.getContent());
        log.info("publicOfCollectionStatus : {}", collectionRequestDto.getPublicOfCollectionStatus());

        return getResult(collectionService.updateCollection(collectionRequestDto, collectionId, currentMember.getId()));
    }

    @ApiOperation(value = "컬렉션 글 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "글 id 리스트",
                    required = true,
                    paramType = "body",
                    dataType = "CollectionRequestDto")
    })
    @PostMapping("/{collectionId}/post")
    public SingleApiResult<Integer> createCollectedPost(@LoginMember MemberDetails currentMember,
                                                        @PathVariable Long collectionId,
                                                        @RequestBody CollectedPostsRequestDto requestDto) {

        log.info("**** POST :: /collection/{id}/post");
        log.info("collectionId : {}", collectionId);
        log.info("postIds : {}", requestDto.getPostIds().toString());

        return getResult(collectionService.insertCollectedPost(currentMember.getId(), collectionId, requestDto.getPostIds()));
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
    public SingleApiResult<Slice<CollectedPostDto>> readCollectedPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long collectionId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("**** GET :: /collection/{collectionId}/post");
        log.info("collectionId : {}", collectionId);
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
    public SingleApiResult<Long> createLikeCollection(@LoginMember MemberDetails currentMember, @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        log.info("**** POST :: /collection/{collectionId}/like");
        log.info("collectionId : {}", collectionId);

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
    public SingleApiResult<List<MembersByContentDto>> readLikeCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyLikedCollectionException, NotExistCollectionException {

        log.info("**** GET :: /collection/{collectionId}/like");
        log.info("collectionId : {}", collectionId);

        return getResult(collectionQueryService.getLikeCollectionMembers(getCurrentMemberId(currentMember), collectionId));
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
    public SingleApiResult<Long> createFollowCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyFollowedCollectionException, NotExistCollectionException,
            CantFollowOwnCollectionException {

        log.info("**** POST :: /collection/{collectionId}/follow");
        log.info("collectionId : {}", collectionId);

        return getResult(collectionService.insertFollowedCollectionByMemberId(currentMember.getId(), collectionId));
    }

    @ApiOperation(value = "컬렉션 팔로우 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "collectionId",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{collectionId}/follow")
    public SingleApiResult<List<MembersByContentDto>> readFollowCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        log.info("**** GET :: /collection/{collectionId}/follow");
        log.info("collectionId : {}", collectionId);

        return getResult(collectionQueryService.getFollowCollectionMembers(getCurrentMemberId(currentMember), collectionId));
    }

    @ApiOperation(value = "컬렉션 댓글 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "commentRequestDto",
                    value = "댓글 정보",
                    required = true,
                    paramType = "body",
                    dataType = "CommentInCollectionRequestDto")
    })
    @PostMapping("/{collectionId}/comment")
    public SingleApiResult<Long> createCommentInCollection(
            @LoginMember MemberDetails currentMember,
            @RequestBody CommentInCollectionRequestDto commentRequestDto,
            @PathVariable Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, NotExistCommentInCollectionException {

        log.info("**** POST :: /collection/{collectionId}/comment");
        log.info("collectionId : {}", collectionId);
        log.info("parentId : {}", commentRequestDto.getParentId());
        log.info("content : {}", commentRequestDto.getContent());

        Long commentId = commentInCollectionService.insertCommentInCollectionByCommentVO(
                commentRequestDto, currentMember.getId(), collectionId);
        return getResult(commentId);
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
    public SingleApiResult<List<ContentCommentDto>> readCommentsInCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long collectionId)
            throws NotExistMemberException, AlreadyFollowedCollectionException,
            NotExistCollectionException, CantFollowOwnCollectionException {

        log.info("**** GET :: /collection/{collectionId}/comment");
        log.info("collectionId : {}", collectionId);

        return getResult(commentInCollectionQueryService.getCommentsByCollectionId(currentMember, collectionId));
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
    public SingleApiResult<Long> createLikeCommentInCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long commentId)
            throws NotExistMemberException, AlreadyLikedCommentInCollectionException,
            NotExistCommentInCollectionException {

        log.info("**** POST :: /collection/comment/{commentId}/like");
        log.info("commentId : {}", commentId);

        return getResult(commentInCollectionService.insertLikedCommentByMemberId(currentMember.getId(), commentId));
    }

    @ApiOperation(value = "컬렉션 댓글 좋아요 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "commentId",
                    value = "댓글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/comment/{commentId}/like")
    public SingleApiResult<List<MembersByContentDto>> readLikeCommentInCollection(
            @LoginMember MemberDetails currentMember, @PathVariable Long commentId)
            throws NotExistMemberException, AlreadyLikedCommentInCollectionException,
            NotExistCommentInCollectionException {

        log.info("**** GET :: /collection/comment/{commentId}/like");
        log.info("commentId : {}", commentId);

        return getResult(commentInCollectionQueryService.getLikeCommentMembers(getCurrentMemberId(currentMember), commentId));
    }

    @ApiOperation(value = "컬렉션 리스트 조회", notes = "로그인 된 멤버의 컬렉션 리스트 조회")
    @ApiImplicitParams({
    })
    @GetMapping("/all")
    public SingleApiResult<List<PostInCollectionDto>> readCollectionsByMember(
            @LoginMember MemberDetails currentMember) {

        log.info("**** GET :: /collection/all");

        return getResult(collectionQueryService.getCollectionsByMemberId(currentMember.getId()));
    }

    @ApiOperation(value = "최근 수정 컬렉션 조회", notes = "로그인 된 멤버의 가장 최근 수정 컬렉션 조회")
    @ApiImplicitParams({
    })
    @GetMapping("/one")
    public SingleApiResult<QuickPostInCollectionDto> readLatestCollectionByMember(
            @LoginMember MemberDetails currentMember) {

        log.info("**** GET :: /collection/one");

        return getResult(collectionQueryService.getCollectionNameByMemberId(currentMember.getId()));
    }

    @ApiOperation(value = "컬렉션 공개 설정 리스트 조회", notes = "")
    @ApiImplicitParams({
    })
    @GetMapping("/public-categories")
    public List<EnumMapperValue> readCollectionPublicCategories() {

        log.info("**** GET :: /collection/public-categories");

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
