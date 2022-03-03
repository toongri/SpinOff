package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.PopularityRelatedHashtagDto;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagQueryServiceJpa implements HashtagQueryService {

    private final HashtagQueryRepository hashtagQueryRepository;

    @Override
    public List<PopularityRelatedHashtagDto> getHashtagsByPostIds(int length, List<Long> postIds) {
        return hashtagQueryRepository.findAllByPostIds(length, postIds);
    }
}
