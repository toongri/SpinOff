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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/hashtag")
public class HashtagApiController {

    private final HashtagService hashtagService;
    private final SearchQueryService searchQueryService;

    @PostMapping("/{hashtagId}/view/{ip}")
    public HashtagResult<Long> createViewOne(@PathVariable String ip, @PathVariable Long hashtagId)
            throws NotExistHashtagException {

        log.info("createViewOne");
        log.info("hashtagId : {}", hashtagId);
        log.info("ip : {}", ip);

        return new HashtagResult<>(hashtagService.insertViewedHashtagByIp(ip, hashtagId));
    }

    @PostMapping("/{hashtagId}/follow/{memberId}")
    public HashtagResult<Long> createFollowOne(@PathVariable Long memberId, @PathVariable Long hashtagId)
            throws AlreadyFollowedHashtagException, NotExistMemberException, NotExistHashtagException {

        log.info("createFollowOne");
        log.info("hashtagId : {}", hashtagId);
        log.info("memberId : {}", memberId);

        return new HashtagResult<>(hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId));
    }


    @GetMapping("/most-popular")
    public HashtagResult<List<MostPopularHashtag>> getMostPopularHashtag(@RequestParam int length) {

        log.info("getMostPopularHashtag");
        log.info("length : {}", length);

        return new HashtagResult<>(searchQueryService.getMostPopularHashtagLimit(length));
    }

    @Data
    @AllArgsConstructor
    public static class HashtagResult<T> {
        private T data;
    }
}
