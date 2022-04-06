package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.ReadMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import com.nameless.spin_off.repository.query.SearchQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public Slice<MemberDto.SearchAllMemberDto> getSearchPageMemberAtAllSliced(
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
        List<Long> blockingIds = getAllIdByBlockedMemberId(currentMemberId);
        List<Long> blockedIds = getAllIdByBlockingMemberId(currentMemberId);
        ReadMemberDto memberDto = getReadMemberDto(targetMemberId, blockingIds, blockedIds);

        if (!memberDto.isBlocked() && currentMemberId != null) {
            memberDto.setBlocking(isExistBlockedMember(blockedIds, targetMemberId));
            memberDto.setAdmin(currentMemberId, isCurrentMemberAdmin(currentMember));
            if (!memberDto.isBlocking()) {
                memberDto.setFollowed(getExistFollowedMember(currentMemberId, targetMemberId));
            }
        }

        return memberDto;
    }

    private List<Long> getAllIdByBlockedMemberId(Long currentMemberId) {
        if (currentMemberId != null) {
            return memberRepository.findAllIdByBlockedMemberId(currentMemberId, BlockedMemberStatus.A);
        } else {
            return new ArrayList<>();
        }
    }

    private List<Long> getAllIdByBlockingMemberId(Long currentMemberId) {
        if (currentMemberId != null) {
            return memberRepository.findAllIdByBlockingMemberId(currentMemberId, BlockedMemberStatus.A);
        } else {
            return new ArrayList<>();
        }
    }

    private ReadMemberDto getReadMemberDto(Long targetMemberId, List<Long> blockingIds, List<Long> blockedIds) {
        if (isExistBlockingMember(blockingIds, targetMemberId)) {
            return new ReadMemberDto();
        } else {
            return getByIdForRead(targetMemberId, blockingIds, blockedIds);
        }
    }

    private boolean isExistBlockingMember(List<Long> blockingIds, Long targetMemberId) {
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

    private ReadMemberDto getByIdForRead(Long targetMemberId,
                                         List<Long> blockingIds, List<Long> blockedIds) {
        blockingIds.addAll(blockedIds);
        return memberQueryRepository
                .findByIdForRead(targetMemberId, blockingIds.stream().distinct().collect(Collectors.toList()))
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
            return new ArrayList<>();
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
