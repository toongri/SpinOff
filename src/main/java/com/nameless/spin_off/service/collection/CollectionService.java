package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NoSuchCollectionException;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.post.NoSuchPostException;

import java.time.LocalDateTime;

public interface CollectionService {
    Long saveCollectionByCollectionVO(CreateCollectionVO postVO) throws NoSuchMemberException;
    Collection updateLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NoSuchMemberException, NoSuchCollectionException;
    Collection updateViewedCollectionByIp(
            String ip, Long collectionId, LocalDateTime startTime, LocalDateTime endTime)
            throws NoSuchCollectionException;
    Collection updateFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NoSuchMemberException, NoSuchCollectionException;
}
