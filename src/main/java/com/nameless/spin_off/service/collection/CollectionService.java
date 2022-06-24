package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.CollectionRequestDto;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;

import java.util.List;

public interface CollectionService {
    Long insertCollection(CollectionRequestDto collectionVO, Long memberId)
            throws NotExistMemberException, IncorrectTitleOfCollectionException, IncorrectContentOfCollectionException;
    Long updateCollection(CollectionRequestDto collectionVO, Long collectionId, Long memberId);
    Long insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, AlreadyLikedCollectionException;
    Long insertViewedCollectionByIp(String ip, Long collectionId) throws NotExistCollectionException;
    Long insertFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException,
            AlreadyFollowedCollectionException, CantFollowOwnCollectionException;
    int insertCollectedPost(Long currentMemberId, Long collectionId, List<Long> postIds);
    Long deleteCollection(MemberDetails currentMember, Long collectionId);
    int updateAllPopularity();
}
