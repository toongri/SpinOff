package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.movie.NoSuchMovieException;
import com.nameless.spin_off.exception.post.NoSuchPostException;
import com.nameless.spin_off.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostApiController {

    private final PostService postService;

    private final Long VIEWED_BY_IP_TIME = 1L;
    private final LocalDateTime currentTime = LocalDateTime.now();


    @PostMapping("")
    public PostApiResult createPostOne(@RequestBody PostDto.CreatePostVO createPost) throws NoSuchMemberException, NoSuchMovieException {
        Long postId = postService.savePostByPostVO(createPost);

        return new PostApiResult(postId);
    }

    @PostMapping("/like")
    public PostApiResult createLikeOne(@RequestBody Long memberId, @RequestBody Long postId) throws NoSuchMemberException, NoSuchPostException {
        Post post = postService.updateLikedPostByMemberId(memberId, postId);

        return new PostApiResult(post);
    }

    @Data
    @AllArgsConstructor
    public static class PostApiResult<T> {
        private T data;
    }
}
