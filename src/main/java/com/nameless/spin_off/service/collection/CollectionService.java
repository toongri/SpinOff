package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.NotSearchPostException;

import java.time.LocalDateTime;
import java.util.List;

public interface CollectionService {
    Long insertCollectionByCollectionVO(CreateCollectionVO postVO) throws NotSearchMemberException;

    Collection insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotSearchMemberException, NotSearchCollectionException,
            OverSearchLikedCollectionException, AlreadyLikedCollectionException;

    Collection insertViewedCollectionByIp(
            String ip, Long collectionId, LocalDateTime timeNow, Long minuteDuration)
            throws NotSearchCollectionException, OverSearchViewedCollectionByIpException;

    Collection insertFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotSearchMemberException, NotSearchCollectionException,
            OverSearchFollowedCollectionException, AlreadyFollowedCollectionException;

    List<Collection> insertCollectedPosts(Long memberId, Long postId, List<Long> collectionIds)
            throws NotSearchMemberException, NotSearchCollectionException,
            NotSearchPostException, OverSearchCollectedPostException, AlreadyCollectedPostException;
}
