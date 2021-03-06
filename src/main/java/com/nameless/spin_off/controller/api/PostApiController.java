package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.PostInCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.QuickPostInCollectionDto;
import com.nameless.spin_off.dto.CommentDto.CommentInPostRequestDto;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto.*;
import com.nameless.spin_off.dto.ResultDto.SingleApiResult;
import com.nameless.spin_off.enums.EnumMapper;
import com.nameless.spin_off.enums.EnumMapperValue;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.collection.AlreadyLikedCollectionException;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.hashtag.IncorrectHashtagContentException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;
import com.nameless.spin_off.service.comment.CommentInPostService;
import com.nameless.spin_off.service.post.PostService;
import com.nameless.spin_off.service.query.CollectionQueryService;
import com.nameless.spin_off.service.query.CommentInPostQueryService;
import com.nameless.spin_off.service.query.PostQueryService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.nameless.spin_off.dto.ResultDto.SingleApiResult.getResult;

@Slf4j
@Api(tags = {"??? api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostApiController {

    private final PostService postService;
    private final PostQueryService postQueryService;
    private final CollectionQueryService collectionQueryService;
    private final CommentInPostQueryService commentInPostQueryService;
    private final CommentInPostService commentInPostService;
    private final EnumMapper enumMapper;

    @ApiOperation(value = "??? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "createPostVO",
                    value = "{" +
                            "\"title\":\"?????????????????? aws??? ?????? ???????????? ??? ?????????\"," +
                            " \"content\":\"?????????????????? aws??? ?????? ???????????? ??? ?????????\"," +
                            " \"movieId\":123," +
                            " \"publicOfPostStatus\": \"A\"," +
                            " \"hashtagContents\" : [\"?????????\", \"?????????\", \"??????\", \"?????????\"]," +
                            " \"collectionIds\":[123, 124, 125, 128]" +
                            "}",
                    required = true,
                    paramType = "formData",
                    dataType = "CreatePostVO")
    })
    @PostMapping("")
    public SingleApiResult<Long> createPost(@LoginMember MemberDetails currentMember,
                                            @RequestPart CreatePostVO createPostVO,
                                            @RequestPart("images") List<MultipartFile> multipartFiles) throws
            NotExistMemberException, NotExistMovieException, NotExistCollectionException,
            IncorrectHashtagContentException, AlreadyPostedHashtagException,
            AlreadyCollectedPostException,
            IncorrectTitleOfPostException, IncorrectContentOfPostException, NotMatchCollectionException, IOException {

        log.info("**** POST :: /post");
        log.info("title : {}", createPostVO.getTitle());
        log.info("content : {}", createPostVO.getContent());
        log.info("movieId : {}", createPostVO.getMovieId());
        log.info("publicOfPostStatus : {}", createPostVO.getPublicOfPostStatus());
        log.info("hashtagContents : {}", createPostVO.getHashtagContents());
        log.info("collectionIds : {}", createPostVO.getCollectionIds());

        return getResult(postService.insertPostByPostVO(createPostVO, currentMember.getId(), multipartFiles));
    }

    @ApiOperation(value = "??? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "ip",
                    value = "ip??????",
                    required = true,
                    paramType = "query",
                    dataType = "string",
                    example = "192.168.0.1")
    })
    @GetMapping("/{postId}")
    public SingleApiResult<ReadPostDto> readPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId, @RequestParam String ip) {

        log.info("**** GET :: /post/{postId}");
        log.info("postId : {}", postId);
        log.info("ip : {}", ip);

        ReadPostDto postForVisit = postQueryService.getPostForRead(currentMember, postId);
        postService.insertViewedPostByIp(ip, postId);
        return getResult(postForVisit);
    }

    @ApiOperation(value = "??? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @DeleteMapping("/{postId}")
    public SingleApiResult<Long> deletePost(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId) {

        log.info("**** DELETE :: /post/{postId}");
        log.info("postId : {}", postId);

        return getResult(postService.deletePost(currentMember, postId));
    }

    @ApiOperation(value = "??? ????????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{postId}/like")
    public SingleApiResult<Long> createLikePost(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException {

        log.info("**** POST :: /post/{postId}/like");
        log.info("postId : {}", postId);

        return getResult(postService.insertLikedPostByMemberId(currentMember.getId(), postId));
    }

    @ApiOperation(value = "????????? ????????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{postId}/like")
    public SingleApiResult<List<MembersByContentDto>> readLikePost(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long postId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        log.info("**** GET :: /post/{postId}/like");
        log.info("postId : {}", postId);

        return getResult(collectionQueryService.getLikeCollectionMembers(getCurrentMemberId(currentMember), postId));
    }

    @ApiOperation(value = "??? ????????? ????????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "????????? id ?????????",
                    required = true,
                    paramType = "body",
                    dataType = "PostOnCollectionsRequestDto")
    })
    @PutMapping("/{postId}/collection/all")
    public SingleApiResult<List<Long>> updatePostInCollections(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long postId,
            @RequestBody PostOnCollectionsRequestDto requestDto)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        log.info("**** PUT :: /post/{postId}/collection/all");
        log.info("postId : {}", postId);
        log.info("collectionIds : {}", requestDto.getCollectionIds().toString());

        return getResult(postService.updateCollectedPosts(currentMember.getId(), postId, requestDto.getCollectionIds()));
    }

    @ApiOperation(value = "??? ????????? ????????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{postId}/collection/all")
    public SingleApiResult<List<PostInCollectionDto>> readPostInCollections(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId) {

        log.info("**** GET :: /post/{postId}/collection/all");
        log.info("postId : {}", postId);

        return getResult(collectionQueryService.getCollectionsByMemberIdAndPostId(currentMember.getId(), postId));
    }

    @ApiOperation(value = "??? ????????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "????????? id",
                    required = true,
                    paramType = "body",
                    dataType = "PostOnCollectionRequestDto")
    })
    @PostMapping("/{postId}/collection/one")
    public SingleApiResult<Long> createPostInCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long postId,
            @RequestBody PostOnCollectionRequestDto requestDto)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        log.info("**** POST :: /post/{postId}/collection/one");
        log.info("postId : {}", postId);
        log.info("collectionId : {}", requestDto.getCollectionId());

        return getResult(postService.insertCollectedPost(currentMember.getId(), postId, requestDto.getCollectionId()));
    }

    @ApiOperation(value = "??? ????????? ?????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{postId}/collection/one")
    public SingleApiResult<QuickPostInCollectionDto> readPostInCollection(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long postId)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        log.info("**** GET :: /post/{postId}/collection/one");
        log.info("postId : {}", postId);

        return getResult(collectionQueryService.getCollectionNameByMemberIdAndPostId(currentMember.getId(), postId));
    }

    @ApiOperation(value = "??? ?????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "?????? ??????",
                    required = true,
                    paramType = "body",
                    dataType = "CommentInPostRequestDto")
    })
    @PostMapping("/{postId}/comment")
    public SingleApiResult<Long> createCommentInPost(
            @LoginMember MemberDetails currentMember, @RequestBody CommentInPostRequestDto requestDto,
            @PathVariable Long postId)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {

        log.info("**** POST :: /post/{postId}/comment");
        log.info("postId : {}", postId);
        log.info("parentId : {}", requestDto.getParentId());
        log.info("content : {}", requestDto.getContent());

        return getResult(commentInPostService.insertCommentInPostByCommentVO(
                requestDto, currentMember.getId(), postId));
    }

    @ApiOperation(value = "??? ?????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{postId}/comment")
    public SingleApiResult<List<ContentCommentDto>> readCommentInPost(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long postId) {

        log.info("**** GET :: /post/{postId}/comment");
        log.info("postId : {}", postId);

        return getResult(commentInPostQueryService.getCommentsByPostId(currentMember, postId));
    }

    @ApiOperation(value = "??? ?????? ????????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "commentId",
                    value = "?????? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/comment/{commentId}/like")
    public SingleApiResult<Long> createLikeCommentInPost(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        log.info("**** POST :: /post/comment/{commentId}/like");
        log.info("commentId : {}", commentId);

        return getResult(commentInPostService.insertLikedCommentByMemberId(currentMember.getId(), commentId));
    }

    @ApiOperation(value = "??? ?????? ????????? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "commentId",
                    value = "?????? id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/comment/{commentId}/like")
    public SingleApiResult<List<MembersByContentDto>> readLikeCommentInPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        log.info("**** GET :: /post/comment/{commentId}/like");
        log.info("commentId : {}", commentId);

        return getResult(commentInPostQueryService.getLikeCommentMembers(getCurrentMemberId(currentMember), commentId));
    }

    @ApiOperation(value = "?????? ??? ??????", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "??? id",
                    required = true,
                    paramType = "path",
                    dataType = "long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "page",
                    value = "????????? ??????",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123"),
            @ApiImplicitParam(
                    name = "size",
                    value = "????????? ??????",
                    required = true,
                    paramType = "query",
                    dataType = "int",
                    example = "123",
                    allowableValues = "range[0, 100]"),
            @ApiImplicitParam(
                    name = "sort",
                    value = "????????? ??????",
                    required = false,
                    paramType = "query",
                    dataType = "string",
                    example = "popularity,desc")
    })
    @GetMapping("/{postId}/post")
    public SingleApiResult<Slice<RelatedPostDto>> readRelatedPost(
            @LoginMember MemberDetails currentMember,
            @PathVariable Long postId,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("**** GET :: /post/{postId}/post");
        log.info("postId : {}", postId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());

        return getResult(postQueryService.getRelatedPostsSliced(getCurrentMemberId(currentMember), postId, pageable));
    }

    @ApiOperation(value = "??? ?????? ?????? ????????? ??????", notes = "")
    @ApiImplicitParams({
    })
    @GetMapping("/public-categories")
    public List<EnumMapperValue> readPostPublicCategories() {

        log.info("readPostPublicCategories");

        return enumMapper.get("PublicOfPostStatus");
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }
}
