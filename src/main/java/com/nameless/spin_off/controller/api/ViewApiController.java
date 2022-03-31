package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.hashtag.HashtagService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.movie.MovieService;
import com.nameless.spin_off.service.post.PostService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = {"조회 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/view")
public class ViewApiController {

    private final PostService postService;
    private final CollectionService collectionService;
    private final HashtagService hashtagService;
    private final MovieService movieService;
    private final MemberService memberService;

    @PostMapping("/contents/popularity")
    public ViewApiResult<Boolean> updatePopularity() {

        log.info("updatePopularity");
        memberService.updateAllPopularity();
        hashtagService.updateAllPopularity();
        collectionService.updateAllPopularity();
        movieService.updateAllPopularity();
        postService.updateAllPopularity();

        return getResult(true);
    }

    @Data
    @AllArgsConstructor
    public static class ViewApiResult<T> {
        private T data;
        Boolean isSuccess;
        String code;
        String message;
    }
    public <T> ViewApiResult<T> getResult(T data) {
        return new ViewApiResult<>(data, true, "0", "성공");
    }
}
