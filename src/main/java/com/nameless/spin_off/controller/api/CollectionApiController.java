package com.nameless.spin_off.controller.api;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.AlreadyLikedCollectionException;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchFollowedCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchViewedCollectionByIpException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.AlreadyLikedPostException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
import com.nameless.spin_off.exception.post.OverSearchViewedPostByIpException;
import com.nameless.spin_off.service.collection.CollectionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/collection")
public class CollectionApiController {

    private final CollectionService collectionService;

    private final Long VIEWED_BY_IP_TIME = 1L;
    private final LocalDateTime currentTime = LocalDateTime.now();

    @PostMapping("")
    public CollectionApiResult<Long> createCollectionOne(@RequestBody CreateCollectionVO collectionVO)
            throws NotSearchMemberException {
        Long aLong = collectionService.saveCollectionByCollectionVO(collectionVO);

        return new CollectionApiResult<Long>(aLong);
    }

    @PostMapping("/like")
    public CollectionApiResult<Collection> createLikeOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotSearchMemberException, AlreadyLikedCollectionException,
            NotSearchCollectionException, OverSearchViewedCollectionByIpException {

        Collection collection = collectionService.updateLikedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Collection>(collection);
    }

    @PostMapping("/view")
    public CollectionApiResult<Collection> viewPostByIp(@RequestBody String ip, @RequestBody Long postId)
            throws NotSearchCollectionException, OverSearchViewedCollectionByIpException {

        Collection collection = collectionService
                .updateViewedCollectionByIp(ip, postId, currentTime, VIEWED_BY_IP_TIME);

        return new CollectionApiResult<Collection>(collection);
    }

    @PostMapping("/follow")
    public CollectionApiResult<Collection> createFollowOne(@RequestBody Long memberId, @RequestBody Long postId)
            throws NotSearchMemberException, AlreadyLikedCollectionException,
            OverSearchFollowedCollectionException, NotSearchCollectionException {

        Collection collection = collectionService.updateFollowedCollectionByMemberId(memberId, postId);

        return new CollectionApiResult<Collection>(collection);
    }

    @Data
    @AllArgsConstructor
    public static class CollectionApiResult<T> {
        private T data;
    }
}
