package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.hashtag.HashtagService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.movie.MovieService;
import com.nameless.spin_off.service.post.PostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/view")
public class ViewApiController {

    private final PostService postService;
    private final CollectionService collectionService;
    private final HashtagService hashtagService;
    private final MovieService movieService;
    private final MemberService memberService;

    @PostMapping("/post/{postId}/view/{ip}")
    public ViewApiResult<Long> createPostOne(
            @PathVariable String ip, @PathVariable Long postId)
            throws NotExistPostException {

        log.info("createViewOne");
        log.info("postId : {}", postId);
        log.info("ip : {}", ip);

        return getResult(postService.insertViewedPostByIp(ip, postId));
    }

    @PostMapping("/movie/{movieId}/view/{ip}")
    public ViewApiResult<Long> createMovieOne(
            @PathVariable String ip, @PathVariable Long movieId) throws NotExistMovieException {

        log.info("createViewOne");
        log.info("movieId : {}", movieId);
        log.info("ip : {}", ip);

        return getResult(movieService.insertViewedMovieByIp(ip, movieId));
    }

    @PostMapping("/hashtag/{hashtagId}/view/{ip}")
    public ViewApiResult<Long> createHashtagOne(@PathVariable String ip, @PathVariable Long hashtagId)
            throws NotExistHashtagException {

        log.info("createViewOne");
        log.info("hashtagId : {}", hashtagId);
        log.info("ip : {}", ip);

        return getResult(hashtagService.insertViewedHashtagByIp(ip, hashtagId));
    }

    @PostMapping("/collection/{collectionId}/view/{ip}")
    public ViewApiResult<Long> createCollectionOne(@PathVariable String ip, @PathVariable Long collectionId)
            throws NotExistCollectionException {

        log.info("createViewOne");
        log.info("collectionId : {}", collectionId);
        log.info("ip : {}", ip);

        return getResult(collectionService.insertViewedCollectionByIp(ip, collectionId));
    }

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
