package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.exception.member.AlreadyFollowedHashtagException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.service.hashtag.HashtagService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/hashtag")
public class HashtagApiController {

    private final HashtagService hashtagService;

    @PostMapping("/view")
    public HashtagResult<Long> createViewOne(@RequestBody String ip, @RequestBody Long postId) throws NotExistHashtagException {
        Long aLong = hashtagService.insertViewedHashtagByIp(ip, postId);

        return new HashtagResult<Long>(aLong);
    }

    @PostMapping("/follow")
    public HashtagResult<Long> createFollowOne(@RequestParam Long memberId, @RequestParam Long hashtagId) throws AlreadyFollowedHashtagException, NotExistMemberException, NotExistHashtagException {
        Long likedHashtagId = hashtagService.insertFollowedHashtagByHashtagId(memberId, hashtagId);

        return new HashtagResult<Long>(likedHashtagId);
    }

    @Data
    @AllArgsConstructor
    public static class HashtagResult<T> {
        private T data;
    }
}
