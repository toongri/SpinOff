package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.PopularityRelatedHashtagDto;

import java.util.List;

public interface HashtagQueryService {

    List<PopularityRelatedHashtagDto> getHashtagsByPostIds(int length, List<Long> postIds);

}
