package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.MainPageDto.MainPageDiscoveryDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageFollowDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;

public interface MainPageQueryService {
    MainPageDiscoveryDto getDiscoveryData(Pageable popularPostPageable,
                                          Pageable latestPostPageable,
                                          Pageable collectionPageable, Long memberId)
            throws NotExistMemberException;
    MainPageFollowDto getFollowData(
            Pageable memberPageable, Pageable hashtagPageable,
            Pageable moviePageable, Pageable collectionPageable, Long memberId)
            throws NotExistMemberException;
}
