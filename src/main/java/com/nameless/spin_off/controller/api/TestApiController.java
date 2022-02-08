package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.movie.NotSearchMovieException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.query.PostQueryRepository;
import com.nameless.spin_off.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/test")
public class TestApiController {

    private final MemberRepository memberRepository;
    private final PostQueryRepository postQueryRepository;
    private final PostService postService;

    @GetMapping("/member")
    public TestApiResult member() {
        return new TestApiResult(memberRepository.findAll());
    }

    @GetMapping("/post")
    public TestApiResult post() {
        List<Post> all = postQueryRepository.testFindAll();
        for (Post post : all) {
            System.out.println("post = " + post);
        }
        return new TestApiResult(all);
    }

    @GetMapping("/post/{id}")
    public TestApiResult postOne(@PathVariable(value = "id") Long id) {
        Post post = postQueryRepository.testFindOne(id);
        System.out.println("post = " + post);
        return new TestApiResult(post);
    }

    @PostMapping("/{id}/post")
    public TestApiResult createPostOne(@RequestBody PostDto.CreatePostVO createPost) throws NotSearchMemberException, NotSearchMovieException, NotSearchCollectionException {

        Long postId = postService.insertPostByPostVO(createPost);

        return new TestApiResult(postId);
    }


    @Data
    @AllArgsConstructor
    public static class TestApiResult<T> {
        private T data;
    }
}