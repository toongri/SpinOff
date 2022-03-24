package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMember;
import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.enums.EnumMapper;
import com.nameless.spin_off.entity.enums.EnumMapperValue;
import com.nameless.spin_off.exception.collection.AlreadyCollectedPostException;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.hashtag.InCorrectHashtagContentException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;
import com.nameless.spin_off.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostApiController {

    private final PostService postService;
    private final EnumMapper enumMapper;

    @PostMapping("")
    public PostApiResult<Long> createOne(@LoginMember MemberDetails currentMember,
                                         @RequestPart CreatePostVO createPostVO,
                                         @RequestPart("images") List<MultipartFile> multipartFiles) throws
            NotExistMemberException, NotExistMovieException, NotExistCollectionException,
            InCorrectHashtagContentException, AlreadyPostedHashtagException,
            AlreadyCollectedPostException, AlreadyAuthorityOfPostStatusException,
            OverTitleOfPostException, OverContentOfPostException, NotMatchCollectionException, IOException {

        log.info("createOne");
        log.info("memberId : {}", currentMember.getId());
        log.info("title : {}", createPostVO.getTitle());
        log.info("content : {}", createPostVO.getContent());
        log.info("movieId : {}", createPostVO.getMovieId());
        log.info("publicOfPostStatus : {}", createPostVO.getPublicOfPostStatus());
        log.info("hashtagContents : {}", createPostVO.getHashtagContents());
        log.info("collectionIds : {}", createPostVO.getCollectionIds());

        return getResult(postService.insertPostByPostVO(createPostVO, currentMember.getId(), multipartFiles));
    }

    @PostMapping("/{postId}/like")
    public PostApiResult<Long> createLikeOne(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException {

        log.info("createLikeOne");
        log.info("memberId : {}", currentMember.getId());
        log.info("postId : {}", postId);

        return getResult(postService.insertLikedPostByMemberId(currentMember.getId(), postId));
    }

//    @GetMapping("/{postId}/collections")
//    public PostApiResult<List<PostInCollectionDto>> getCollectionCheckPost(
//            @LoginMember MemberDetails currentMember, @PathVariable Long postId) {
//
//        log.info("getCollectionCheckPost");
//        log.info("memberId : {}", currentMember.getId());
//        log.info("postId : {}", postId);
//
//        return getResult(collectionQueryService.getCollectionNamesByMemberId(currentMember.getId()));
//    }

    @PostMapping("/{postId}/collections")
    public PostApiResult<List<Long>> createCollectedAll(
            @LoginMember MemberDetails currentMember, @PathVariable Long postId, @RequestParam List<Long> collectionIds)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        log.info("createCollectedAll");
        log.info("postId : {}", postId);
        log.info("memberId : {}", currentMember.getId());
        log.info("collectionIds : {}", collectionIds);

        return getResult(postService.insertCollectedPosts(currentMember.getId(), postId, collectionIds));
    }

    @GetMapping("/public-categories")
    public List<EnumMapperValue> getPostPublicCategories() {

        log.info("getPostPublicCategories");

        return enumMapper.get("PublicOfPostStatus");
    }

    @Data
    @AllArgsConstructor
    public static class PostApiResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> PostApiResult<T> getResult(T data) {
        return new PostApiResult<>(data, true, "0", "성공");
    }
}
