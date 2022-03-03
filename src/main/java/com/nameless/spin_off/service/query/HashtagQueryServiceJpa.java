package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
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
    public List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(int length, List<Long> postIds) {
        return hashtagQueryRepository.findAllByPostIds(length, postIds);
    }

    @Override
    public List<HashtagDto.RelatedMostTaggedHashtagDto> getHashtagsByMemberIds(int length, List<Long> memberIds) {
        return hashtagQueryRepository.findAllByMemberIds(length, memberIds);
    }
}
