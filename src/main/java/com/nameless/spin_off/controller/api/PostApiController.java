package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.*;
import com.nameless.spin_off.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.nameless.spin_off.StaticVariable.VIEWED_BY_IP_TIME;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostApiController {

    private final PostService postService;

    @PostMapping("")
    public PostApiResult<Long> createPostOne(@RequestBody CreatePostVO createPost) throws
            NotExistMemberException, NotExistMovieException, NotExistCollectionException, InCorrectHashtagContentException {

        Long postId = postService.insertPostByPostVO(createPost);

        return new PostApiResult<Long>(postId);
    }

    @PostMapping("/like")
    public PostApiResult<Post> createLikeOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotExistMemberException, NotExistPostException,
            OverSearchLikedPostException, AlreadyLikedPostException {

        Post post = postService.insertLikedPostByMemberId(memberId, postId);

        return new PostApiResult<Post>(post);
    }

    @PostMapping("/view")
    public PostApiResult<Post> viewPostByIp(@RequestBody String ip, @RequestBody Long postId)
            throws NotExistPostException, OverSearchViewedPostByIpException {

        Post post = postService.insertViewedPostByIp(ip, postId, LocalDateTime.now(), VIEWED_BY_IP_TIME);

        return new PostApiResult<Post>(post);
    }

    @Data
    @AllArgsConstructor
    public static class PostApiResult<T> {
        private T data;
    }
}
