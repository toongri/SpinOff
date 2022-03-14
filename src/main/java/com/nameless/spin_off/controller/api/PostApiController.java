package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.config.auth.LoginMemberId;
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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostApiController {

    private final PostService postService;
    private final EnumMapper enumMapper;

    @PostMapping("")
    public PostApiResult<Long> createOne(@RequestBody CreatePostVO createPostVO, @LoginMemberId Long memberId) throws
            NotExistMemberException, NotExistMovieException, NotExistCollectionException,
            InCorrectHashtagContentException, AlreadyPostedHashtagException,
            AlreadyCollectedPostException, AlreadyAuthorityOfPostStatusException,
            OverTitleOfPostException, OverContentOfPostException, NotMatchCollectionException {

        log.info("createOne");
        log.info("memberId : {}", memberId);
        log.info("title : {}", createPostVO.getTitle());
        log.info("content : {}", createPostVO.getContent());
        log.info("movieId : {}", createPostVO.getMovieId());
        log.info("thumbnailUrl : {}", createPostVO.getThumbnailUrl());
        log.info("publicOfPostStatus : {}", createPostVO.getPublicOfPostStatus());
        log.info("hashtagContents : {}", createPostVO.getHashtagContents());
        log.info("mediaUrls : {}", createPostVO.getMediaUrls());
        log.info("collectionIds : {}", createPostVO.getCollectionIds());

        return getResult(postService.insertPostByPostVO(createPostVO, memberId));
    }

    @PostMapping("/{postId}/like")
    public PostApiResult<Long> createLikeOne(
            @LoginMemberId Long memberId, @PathVariable Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException {

        log.info("createLikeOne");
        log.info("memberId : {}", memberId);
        log.info("postId : {}", postId);

        return getResult(postService.insertLikedPostByMemberId(memberId, postId));
    }

    @PostMapping("/{postId}/view/{ip}")
    public PostApiResult<Long> createViewOne(
            @PathVariable String ip, @PathVariable Long postId)
            throws NotExistPostException {

        log.info("createViewOne");
        log.info("postId : {}", postId);
        log.info("ip : {}", ip);

        return getResult(postService.insertViewedPostByIp(ip, postId));
    }

    @PostMapping("/{postId}/collections")
    public PostApiResult<List<Long>> createCollectedAll(
            @LoginMemberId Long memberId, @PathVariable Long postId, @RequestParam List<Long> collectionIds)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        log.info("createCollectedAll");
        log.info("postId : {}", postId);
        log.info("memberId : {}", memberId);
        log.info("collectionIds : {}", collectionIds);

        return getResult(postService.insertCollectedPosts(memberId, postId, collectionIds));
    }

    @GetMapping("/post-public-categories")
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
