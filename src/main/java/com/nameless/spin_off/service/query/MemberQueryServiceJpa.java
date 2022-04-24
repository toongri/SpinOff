package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.MemberDto.ReadMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchAllMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import com.nameless.spin_off.repository.query.MovieQueryRepository;
import com.nameless.spin_off.repository.query.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceJpa implements MemberQueryService {
    private final MemberQueryRepository memberQueryRepository;
    private final SearchQueryRepository searchQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagQueryRepository hashtagQueryRepository;
    private final MovieQueryRepository movieQueryRepository;
    List<Long> blockingIds;
    List<Long> blockedIds;

    @Override
    public Slice<SearchMemberDto> getSearchPageMemberAtMemberSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return memberQueryRepository
                .findAllSlicedForSearchPageAtMember(keyword, pageable,
                        memberId, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId));
    }

    @Override
    public SearchFirstDto<Slice<SearchMemberDto>> getSearchPageMemberAtMemberSlicedFirst(
            String keyword, Pageable pageable, Long memberId, int length) throws NotExistMemberException {

        Slice<SearchMemberDto> members = memberQueryRepository
                .findAllSlicedForSearchPageAtMember(keyword, pageable,
                        memberId, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId));

        return new SearchFirstDto<>(members, getHashtagsByPostIds(length, members.getContent()));
    }

    @Override
    public Slice<SearchAllMemberDto> getSearchPageMemberAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return memberQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable,
                getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId));
    }

    @Override
    public List<LastSearchDto> getLastSearchesByMemberLimit(Long memberId, int length) {
        return searchQueryRepository.findLastSearchesByMemberIdLimit(memberId, length);
    }

    @Override
    public ReadMemberDto getMemberForRead(MemberDetails currentMember, Long targetMemberId) {
        Long currentMemberId = getCurrentMemberId(currentMember);
        blockingIds = getAllIdByBlockedMemberId(currentMemberId);
        blockedIds = getAllIdByBlockingMemberId(currentMemberId);
        ReadMemberDto memberDto = getReadMemberDto(targetMemberId);

        if (!memberDto.isBlocked() && currentMemberId != null) {
            memberDto.setBlocking(isExistBlockedMember(blockedIds, targetMemberId));
            memberDto.setAdmin(currentMemberId, isCurrentMemberAdmin(currentMember));
            if (!memberDto.isBlocking()) {
                memberDto.setFollowed(getExistFollowedMember(currentMemberId, targetMemberId));
            }
        }

        return memberDto;
    }

    @Override
    public List<MembersByContentDto> getFollowedMembersByMemberId(Long currentMemberId, Long targetMemberId) {
        isExistMember(targetMemberId);
        blockingIds = getAllIdByBlockedMemberId(currentMemberId);
        blockedIds = getAllIdByBlockingMemberId(currentMemberId);
        isBlockingMember(targetMemberId);
        return getMembersByContentDtos(findAllFollowedMemberByMemberId(targetMemberId), currentMemberId);
    }

    @Override
    public List<MembersByContentDto> getFollowingMembersByMemberId(Long currentMemberId, Long targetMemberId) {
        isExistMember(targetMemberId);
        blockingIds = getAllIdByBlockedMemberId(currentMemberId);
        blockedIds = getAllIdByBlockingMemberId(currentMemberId);
        isBlockingMember(targetMemberId);

        return getMembersByContentDtos(findAllFollowingMemberByMemberId(targetMemberId), currentMemberId);
    }

    @Override
    public List<MembersByContentDto> getFollowHashtagsByMemberId(Long currentMemberId, Long targetMemberId) {
        isExistMember(targetMemberId);
        blockingIds = getAllIdByBlockedMemberId(currentMemberId);
        blockedIds = getAllIdByBlockingMemberId(currentMemberId);
        isBlockingMember(targetMemberId);
    }

    @Override
    public List<MembersByContentDto> getFollowMoviesByMemberId(Long currentMemberId, Long targetMemberId) {
        isExistMember(targetMemberId);
        blockingIds = getAllIdByBlockedMemberId(currentMemberId);
        blockedIds = getAllIdByBlockingMemberId(currentMemberId);
        isBlockingMember(targetMemberId);
    }

    private void isBlockingMember(Long targetMemberId) {
        if (blockingIds.contains(targetMemberId)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private List<Long> getAllIdByBlockedMemberId(Long currentMemberId) {
        if (currentMemberId != null) {
            return memberRepository.findAllIdByBlockedMemberId(currentMemberId, BlockedMemberStatus.A);
        } else {
            return Collections.emptyList();
        }
    }

    private List<Long> getAllIdByBlockingMemberId(Long currentMemberId) {
        if (currentMemberId != null) {
            return memberRepository.findAllIdByBlockingMemberId(currentMemberId, BlockedMemberStatus.A);
        } else {
            return Collections.emptyList();
        }
    }

    private void isExistMember(Long targetMemberId) {
        if (!memberQueryRepository.isExist(targetMemberId)) {
            throw new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER);
        }
    }

    private ReadMemberDto getReadMemberDto(Long targetMemberId) {
        if (isExistBlockingMember(targetMemberId)) {
            return new ReadMemberDto();
        } else {
            return getByIdForRead(targetMemberId);
        }
    }

    private List<MembersByContentDto> findAllFollowedMemberByMemberId(Long targetMemberId) {
        return memberQueryRepository.findAllFollowedMemberByMemberId(targetMemberId, getBlockedAndBlockingIds());
    }

    private List<MembersByContentDto> findAllFollowingMemberByMemberId(Long targetMemberId) {
        return memberQueryRepository.findAllFollowingMemberByMemberId(targetMemberId, getBlockedAndBlockingIds());
    }

    private List<Long> getBlockedAndBlockingIds() {
        List<Long> blockIds = new ArrayList<>(blockingIds);
        blockIds.addAll(blockedIds);
        return blockIds.stream().distinct().collect(Collectors.toList());
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

    private List<Long> getAllIdByFollowingMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else {
            return Collections.emptyList();
        }
    }

    private boolean isExistBlockingMember(Long targetMemberId) {
        return blockingIds.stream().anyMatch(m -> m.equals(targetMemberId));
    }

    private Boolean isExistBlockedMember(List<Long> blockedIds, Long targetMemberId) {
        return blockedIds.stream().anyMatch(m -> m.equals(targetMemberId));
    }

    private Boolean getExistFollowedMember(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId == null) {
            return false;
        } else {
            return memberQueryRepository.isExistFollowedMember(currentMemberId, targetMemberId);
        }
    }

    private ReadMemberDto getByIdForRead(Long targetMemberId) {
        List<Long> blockIds = new ArrayList<>(blockedIds);
        blockIds.addAll(blockedIds);
        return memberQueryRepository
                .findByIdForRead(targetMemberId, blockIds.stream().distinct().collect(Collectors.toList()))
                .orElseGet(() -> memberQueryRepository.findByIdForRead(targetMemberId)
                        .orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER)));
    }

    private List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(int length, List<SearchMemberDto> data) {
        return hashtagQueryRepository.findAllByPostIds(
                length,
                data.stream().map(SearchMemberDto::getMemberId).collect(Collectors.toList()));
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
        } else{
            return Collections.emptyList();
        }
    }

    private boolean isCurrentMemberAdmin(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.isAdmin();
        } else {
            return false;
        }
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }
}
