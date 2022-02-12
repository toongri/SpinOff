package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;

import java.time.LocalDateTime;
import java.util.List;

public interface CollectionService {
    Long insertCollectionByCollectionVO(CreateCollectionVO postVO) throws NotExistMemberException;

    Collection insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException,
            OverSearchLikedCollectionException, AlreadyLikedCollectionException;

    Collection insertViewedCollectionByIp(
            String ip, Long collectionId, LocalDateTime timeNow, Long minuteDuration)
            throws NotExistCollectionException, OverSearchViewedCollectionByIpException;

    Collection insertFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException,
            OverSearchFollowedCollectionException, AlreadyFollowedCollectionException;

    List<Collection> insertCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotExistMemberException, NotExistCollectionException,
            NotExistPostException, OverSearchCollectedPostException, AlreadyCollectedPostException;
}
