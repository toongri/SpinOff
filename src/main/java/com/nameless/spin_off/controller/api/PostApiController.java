package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.post.NoSuchPostException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.query.PostQueryRepository;
import com.nameless.spin_off.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostApiController {

    private final PostService postService;

    @PostMapping("")
    public PostApiResult createPostOne(@RequestBody PostDto.CreatePostVO createPost) throws NoSuchMemberException {

        Long postId = postService.savePostByPostVO(createPost);

        return new PostApiResult(postId);
    }

    @PostMapping("/like")
    public PostApiResult createLikeOne(@RequestBody Long memberId, @RequestBody Long postId) throws NoSuchMemberException, NoSuchPostException {
        Post post = postService.saveLikedPostByMemberIdAndPostId(memberId, postId);

        return new PostApiResult(post);
    }

    @Data
    @AllArgsConstructor
    public static class PostApiResult<T> {
        private T data;
    }
}
