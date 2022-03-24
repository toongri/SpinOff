package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.SearchDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
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
                        getFollowedMemberByMemberId(memberId), getBlockedMemberByMemberId(memberId));
    }

    @Override
    public SearchFirstDto<Slice<SearchMemberDto>> getSearchPageMemberAtMemberSlicedFirst(
            String keyword, Pageable pageable, Long memberId, int length) throws NotExistMemberException {

        Slice<SearchMemberDto> members = memberQueryRepository
                .findAllSlicedForSearchPageAtMember(keyword, pageable,
                        getFollowedMemberByMemberId(memberId), getBlockedMemberByMemberId(memberId));

        return new SearchFirstDto<>(members, getHashtagsByPostIds(length, members.getContent()));
    }

    @Override
    public Slice<MemberDto.SearchAllMemberDto> getSearchPageMemberAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return memberQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable,
                getBlockedMemberByMemberId(memberId));
    }

    @Override
    public List<SearchDto.LastSearchDto> getLastSearchesByMemberLimit(Long memberId, int length) {
        return searchQueryRepository.findLastSearchesByMemberIdLimit(memberId, length);
    }

    private List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(int length, List<SearchMemberDto> data) {
        return hashtagQueryRepository.findAllByPostIds(
                length,
                data.stream().map(SearchMemberDto::getMemberId).collect(Collectors.toList()));
    }

    private List<Long> getBlockedMemberByMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByBlockingMemberId(memberId);
        } else{
            return new ArrayList<>();
        }
    }

    private List<Long> getFollowedMemberByMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else{
            return new ArrayList<>();
        }
    }
}
