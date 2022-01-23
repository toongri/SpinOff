package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.entity.ApiResult;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/test")
public class TestApiController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

    @GetMapping("/member")
    public ApiResult member() {
        return new ApiResult(memberRepository.findAll());
    }

    @GetMapping("/post")
    public ApiResult post() {
        List<Post> all = postQueryRepository.findAll();
        for (Post post : all) {
            System.out.println("post = " + post);
        }
        return new ApiResult(all);
    }

    @GetMapping("/post/{id}")
    public ApiResult postOne(@PathVariable(value="id") Long id) {
        Post post = postQueryRepository.findOne(id);
        System.out.println("post = " + post);
        return new ApiResult(post);
    }

}
