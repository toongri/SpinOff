package com.nameless.spin_off.controller.api;

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
import com.nameless.spin_off.service.query.PostQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostApiController {

    private final PostService postService;
    private final EnumMapper enumMapper;
    private final PostQueryService postQueryService;

    @PostMapping("")
    public PostApiResult<Long> createOne(@RequestBody CreatePostVO createPost) throws
            NotExistMemberException, NotExistMovieException, NotExistCollectionException, InCorrectHashtagContentException, AlreadyPostedHashtagException, AlreadyCollectedPostException, AlreadyPAuthorityOfPostStatusException, OverTitleOfPostException, OverContentOfPostException, NotMatchCollectionException {

        Long postId = postService.insertPostByPostVO(createPost);

        return new PostApiResult<Long>(postId);
    }

    @PostMapping("/like")
    public PostApiResult<Long> createLikeOne(@RequestParam Long memberId, @RequestParam Long postId)
            throws NotExistMemberException, NotExistPostException, AlreadyLikedPostException {

        Long resultId = postService.insertLikedPostByMemberId(memberId, postId);

        return new PostApiResult<Long>(resultId);
    }

    @PostMapping("/view")
    public PostApiResult<Long> createViewOne(@RequestParam String ip, @RequestParam Long postId)
            throws NotExistPostException {

        Long resultId = postService.insertViewedPostByIp(ip, postId);

        return new PostApiResult<Long>(resultId);
    }

    @PostMapping("/collections")
    public PostApiResult<List<Long>> createCollectedAll(
            @RequestParam Long memberId, @RequestParam Long postId, @RequestParam List<Long> collectionIds)
            throws NotExistMemberException,
            NotExistPostException, AlreadyCollectedPostException, NotMatchCollectionException {

        List<Long> resultId = postService.insertCollectedPosts(memberId, postId, collectionIds);

        return new PostApiResult<List<Long>>(resultId);
    }

    @GetMapping("/post-public-categories")
    public List<EnumMapperValue> getPostPublicCategories() {
        return enumMapper.get("PublicOfPostStatus");
    }

    @GetMapping("/search/hashtag")
    public PostApiSearchResult getfd(Pageable pageable, @RequestParam Long memberId,
                                     @RequestParam List<String> hashtagContents) {
        return postQueryService.getPostsByHashtagsSlicedForSearchPage(pageable, hashtagContents, memberId);
    }

    @Data
    @AllArgsConstructor
    public static class PostApiResult<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    public static class PostApiSearchResult<T, F> {
        private T data;
        private F hashtags;
    }
}
