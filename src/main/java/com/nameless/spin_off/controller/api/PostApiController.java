package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.PostDto.CreatePostVO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostApiController {

    private final PostService postService;

    @PostMapping("")
    public PostApiResult<Long> createPostOne(@RequestBody CreatePostVO createPost) throws
            NotExistMemberException, NotExistMovieException, NotExistCollectionException, InCorrectHashtagContentException, AlreadyPostedHashtagException, AlreadyCollectedPostException {

        Long postId = postService.insertPostByPostVO(createPost);

        return new PostApiResult<Long>(postId);
    }

    @PostMapping("/like")
    public PostApiResult<Long> createLikeOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException {

        Long resultId = postService.insertLikedPostByMemberId(memberId, postId);

        return new PostApiResult<Long>(resultId);
    }

    @PostMapping("/view")
    public PostApiResult<Long> viewPostByIp(@RequestBody String ip, @RequestBody Long postId)
            throws NotExistPostException {

        Long resultId = postService.insertViewedPostByIp(ip, postId);

        return new PostApiResult<Long>(resultId);
    }

    @PostMapping("/collections")
    public PostApiResult<Long> addPostInCollections(
            @RequestBody Long memberId, @RequestBody Long postId, @RequestBody List<Long> collectionIds)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        Long resultId = postService.insertCollectedPosts(memberId, postId, collectionIds);

        return new PostApiResult<Long>(resultId);
    }

    @Data
    @AllArgsConstructor
    public static class PostApiResult<T> {
        private T data;
    }
}
