package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;

public interface CollectionService {
    Long insertCollectionByCollectionVO(CreateCollectionVO postVO) throws NotExistMemberException, OverTitleOfCollectionException, OverContentOfCollectionException;
    Long insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, AlreadyLikedCollectionException;
    Long insertViewedCollectionByIp(String ip, Long collectionId) throws NotExistCollectionException;
    Long insertFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException,
            AlreadyFollowedCollectionException, CantFollowOwnCollectionException;
}
