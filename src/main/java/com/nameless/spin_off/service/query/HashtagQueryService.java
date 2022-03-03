package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;

import java.util.List;

public interface HashtagQueryService {

    List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(int length, List<Long> postIds);
    List<RelatedMostTaggedHashtagDto> getHashtagsByMemberIds(int length, List<Long> memberIds);
    List<RelatedMostTaggedHashtagDto> getHashtagsByCollectionIds(int length, List<Long> collectionIds);

}
