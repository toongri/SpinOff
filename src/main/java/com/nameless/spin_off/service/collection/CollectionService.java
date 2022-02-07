package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.exception.collection.AlreadyLikedCollectionException;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchFollowedCollectionException;
import com.nameless.spin_off.exception.collection.OverSearchViewedCollectionByIpException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;

import java.time.LocalDateTime;

public interface CollectionService {
    Long saveCollectionByCollectionVO(CreateCollectionVO postVO) throws NotSearchMemberException;

    Collection updateLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotSearchMemberException, NotSearchCollectionException, OverSearchViewedCollectionByIpException, AlreadyLikedCollectionException;

    Collection updateViewedCollectionByIp(
            String ip, Long collectionId, LocalDateTime timeNow, Long minuteDuration)
            throws NotSearchCollectionException, OverSearchViewedCollectionByIpException;

    Collection updateFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotSearchMemberException, NotSearchCollectionException, OverSearchFollowedCollectionException, AlreadyLikedCollectionException;
}
