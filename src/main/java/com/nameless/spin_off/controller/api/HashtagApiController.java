package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.hashtag.HashtagService;
import com.nameless.spin_off.service.query.SearchQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/hashtag")
public class HashtagApiController {

    private final HashtagService hashtagService;
    private final SearchQueryService searchQueryService;

    @PostMapping("/view")
    public HashtagResult<Long> createViewOne(@RequestBody String ip, @RequestBody Long postId)
            throws NotExistHashtagException {
        Long aLong = hashtagService.insertViewedHashtagByIp(ip, postId);

        return new HashtagResult<Long>(aLong);
    }

    @PostMapping("/follow")
    public HashtagResult<Long> createFollowOne(@RequestParam Long memberId, @RequestParam Long hashtagId)
            throws AlreadyFollowedHashtagException, NotExistMemberException, NotExistHashtagException {
        Long likedHashtagId = hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId);

        return new HashtagResult<Long>(likedHashtagId);
    }


    @GetMapping("/most-popular")
    public HashtagResult<List<MostPopularHashtag>> getMostPopularHashtag(@RequestParam int length) {

        return new HashtagResult<List<MostPopularHashtag>>(searchQueryService.getMostPopularHashtagLimit(length));
    }

    @Data
    @AllArgsConstructor
    public static class HashtagResult<T> {
        private T data;
    }
}
