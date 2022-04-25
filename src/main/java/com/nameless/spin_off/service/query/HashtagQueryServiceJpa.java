package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.hashtag.NotExistHashtagException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagQueryServiceJpa implements HashtagQueryService {

    private final HashtagQueryRepository hashtagQueryRepository;
    private final MemberRepository memberRepository;
    private List<Long> blockedMemberIds;

    @Override
    public List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(int length, List<Long> postIds) {
        return hashtagQueryRepository.findAllByPostIds(length, postIds);
    }

    @Override
    public List<RelatedMostTaggedHashtagDto> getHashtagsByMemberIds(int length, List<Long> memberIds) {
        return hashtagQueryRepository.findAllByMemberIds(length, memberIds);
    }

    @Override
    public List<RelatedMostTaggedHashtagDto> getHashtagsByCollectionIds(int length, List<Long> collectionIds) {
        return hashtagQueryRepository.findAllByCollectionIds(length, collectionIds);
    }

    @Override
    public List<MembersByContentDto> getFollowHashtagMembers(Long memberId, Long hashtagId) {
        isExistHashtag(hashtagId);
        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        return getMembersByContentDtos(getFollowMembersByHashtagId(hashtagId), memberId);
    }

    private void isExistHashtag(Long hashtagId) {
        if (!hashtagQueryRepository.isExist(hashtagId)) {
            throw new NotExistHashtagException(ErrorEnum.NOT_EXIST_HASHTAG);
        }
    }

    private List<MembersByContentDto> getMembersByContentDtos(List<MembersByContentDto> members, Long memberId) {
        List<Long> followingMemberIds = getAllIdByFollowingMemberId(memberId);
        members.forEach(m -> m.setFollowedAndOwn(followingMemberIds.contains(m.getMemberId()), memberId));
        return members.stream()
                .sorted(
                        Comparator.comparing(MembersByContentDto::isOwn)
                                .thenComparing(MembersByContentDto::isFollowed).reversed())
                .collect(Collectors.toList());
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
        } else{
            return Collections.emptyList();
        }
    }

    private List<MembersByContentDto> getFollowMembersByHashtagId(Long hashtagId) {
        return hashtagQueryRepository.findAllFollowMemberByHashtagId(hashtagId, blockedMemberIds);
    }

    private List<Long> getAllIdByFollowingMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else {
            return Collections.emptyList();
        }
    }
}
