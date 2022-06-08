package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.PostInCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.QuickPostInCollectionDto;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.dto.CommentDto.CommentInPostRequestDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto;
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
@Api(tags = {"글 api"})
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

    @ApiOperation(value = "글 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "createPostVO",
                    value = "{" +
                            "\"title\":\"스프링부트와 aws로 혼자 구현하는 웹 서비스\"," +
                            " \"content\":\"스프링부트와 aws로 혼자 구현하는 웹 서비스\"," +
                            " \"movieId\":123," +
                            " \"publicOfPostStatus\": \"A\"," +
                            " \"hashtagContents\" : [\"네이버\", \"카카오\", \"구글\", \"네이트\"]," +
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

        log.info("createPost");
        log.info("memberId : {}", currentMember.getId());
        log.info("title : {}", createPostVO.getTitle());
        log.info("content : {}", createPostVO.getContent());
        log.info("movieId : {}", createPostVO.getMovieId());
        log.info("publicOfPostStatus : {}", createPostVO.getPublicOfPostStatus());
        log.info("hashtagContents : {}", createPostVO.getHashtagContents());
        log.info("collectionIds : {}", createPostVO.getCollectionIds());

        return getResult(postService.insertPostByPostVO(createPostVO, currentMember.getId(), multipartFiles));
    }

    @ApiOperation(value = "글 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
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
    @GetMapping("/{postId}")
    public SingleApiResult<ReadPostDto> readPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId, @RequestParam String ip) {

        log.info("readPost");
        log.info("postId : {}", postId);
        log.info("ip : {}", ip);
        log.info("memberId : {}", getCurrentMemberId(currentMember));

        ReadPostDto postForVisit = postQueryService.getPostForRead(currentMember, postId);
        postService.insertViewedPostByIp(ip, postId);
        return getResult(postForVisit);
    }

    @ApiOperation(value = "글 좋아요 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @PostMapping("/{postId}/like")
    public SingleApiResult<Long> createLikePost(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException {

        log.info("createLikePost");
        log.info("memberId : {}", currentMember.getId());
        log.info("postId : {}", postId);

        return getResult(postService.insertLikedPostByMemberId(currentMember.getId(), postId));
    }

    @ApiOperation(value = "컬렉션 좋아요 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{postId}/like")
    public SingleApiResult<List<MembersByContentDto>> readLikePost(@LoginMember MemberDetails currentMember,
                                                                   @PathVariable Long postId)
            throws NotExistMemberException, AlreadyLikedCollectionException,
            NotExistCollectionException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readLikePost");
        log.info("postId : {}", postId);
        log.info("memberId : {}", currentMemberId);

        return getResult(collectionQueryService.getLikeCollectionMembers(currentMemberId, postId));
    }

    @ApiOperation(value = "글 컬렉션 리스트 수정", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "컬렉션 id 리스트",
                    required = true,
                    paramType = "body",
                    dataType = "PostOnCollectionsRequestDto")
    })
    @PutMapping("/{postId}/collection/all")
    public SingleApiResult<List<Long>> updatePostInCollections(@LoginMember MemberDetails currentMember,
                                                               @PathVariable Long postId,
                                                               @RequestBody PostOnCollectionsRequestDto requestDto)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        log.info("updatePostInCollections");
        log.info("memberId : {}", currentMember.getId());
        log.info("postId : {}", postId);
        log.info("collectionIds : {}", requestDto.getCollectionIds().toString());

        return getResult(postService.updateCollectedPosts(currentMember.getId(), postId, requestDto.getCollectionIds()));
    }

    @ApiOperation(value = "글 컬렉션 리스트 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{postId}/collection/all")
    public SingleApiResult<List<PostInCollectionDto>> readPostInCollections(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId) {

        log.info("readPostInCollections");
        log.info("memberId : {}", currentMember.getId());
        log.info("postId : {}", postId);

        return getResult(collectionQueryService.getCollectionsByMemberIdAndPostId(currentMember.getId(), postId));
    }

    @ApiOperation(value = "글 컬렉션 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123"),
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "컬렉션 id",
                    required = true,
                    paramType = "body",
                    dataType = "PostOnCollectionRequestDto")
    })
    @PostMapping("/{postId}/collection/one")
    public SingleApiResult<Long> createPostInCollection(@LoginMember MemberDetails currentMember,
                                                        @PathVariable Long postId,
                                                        @RequestBody PostOnCollectionRequestDto requestDto)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        log.info("createPostInCollection");
        log.info("memberId : {}", currentMember.getId());
        log.info("postId : {}", postId);
        log.info("collectionId : {}", requestDto.getCollectionId());

        return getResult(postService.insertCollectedPost(currentMember.getId(), postId, requestDto.getCollectionId()));
    }

    @ApiOperation(value = "글 컬렉션 추천 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{postId}/collection/one")
    public SingleApiResult<QuickPostInCollectionDto> readPostInCollection(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        log.info("readPostInCollection");
        log.info("memberId : {}", currentMember.getId());
        log.info("postId : {}", postId);

        return getResult(collectionQueryService.getCollectionNameByMemberIdAndPostId(currentMember.getId(), postId));
    }

    @ApiOperation(value = "글 댓글 생성", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "requestDto",
                    value = "댓글 정보",
                    required = true,
                    paramType = "body",
                    dataType = "CommentInPostRequestDto")
    })
    @PostMapping("/{postId}/comment")
    public SingleApiResult<Long> createCommentInPost(
            @LoginMember MemberDetails currentMember, @RequestBody CommentInPostRequestDto requestDto,
            @PathVariable Long postId)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {

        log.info("createCommentInPost");
        log.info("memberId : {}", currentMember.getId());
        log.info("postId : {}", postId);
        log.info("parentId : {}", requestDto.getParentId());
        log.info("content : {}", requestDto.getContent());

        return getResult(commentInPostService.insertCommentInPostByCommentVO(
                requestDto, currentMember.getId(), postId));
    }

    @ApiOperation(value = "글 댓글 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "123")
    })
    @GetMapping("/{postId}/comment")
    public SingleApiResult<List<ContentCommentDto>> readCommentInPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId) {

        log.info("readCommentInPost");
        log.info("postId : {}", postId);
        log.info("memberId : {}", getCurrentMemberId(currentMember));

        return getResult(commentInPostQueryService.getCommentsByPostId(currentMember, postId));
    }

    @ApiOperation(value = "글 댓글 좋아요 생성", notes = "")
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
    public SingleApiResult<Long> createLikeCommentInPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        log.info("createLikeCommentInPost");
        log.info("memberId : {}", currentMember.getId());
        log.info("commentId : {}", commentId);

        return getResult(commentInPostService.insertLikedCommentByMemberId(currentMember.getId(), commentId));
    }

    @ApiOperation(value = "글 댓글 좋아요 조회", notes = "")
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
    public SingleApiResult<List<MembersByContentDto>> readLikeCommentInPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readLikeCommentInPost");
        log.info("memberId : {}", currentMemberId);
        log.info("commentId : {}", commentId);

        return getResult(commentInPostQueryService.getLikeCommentMembers(currentMemberId, commentId));
    }

    @ApiOperation(value = "연관 글 조회", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "postId",
                    value = "글 id",
                    required = true,
                    paramType = "path",
                    dataType = "long",
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
    @GetMapping("/{postId}/post")
    public SingleApiResult<Slice<RelatedPostDto>> readRelatedPost(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId,
            @PageableDefault(sort = "popularity", direction = Sort.Direction.DESC) Pageable pageable) {

        Long currentMemberId = getCurrentMemberId(currentMember);
        log.info("readRelatedPost");
        log.info("postId : {}", postId);
        log.info("memberId : {}", currentMemberId);
        log.info("pageable.getPageNumber() : {}", pageable.getPageNumber());
        log.info("pageable.getPageSize() : {}", pageable.getPageSize());
        log.info("pageable.getSort() : {}", pageable.getSort());

        return getResult(postQueryService.getRelatedPostsSliced(currentMemberId, postId, pageable));
    }

    @ApiOperation(value = "글 공개 설정 리스트 조회", notes = "")
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
