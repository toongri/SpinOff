package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryServiceJpa implements MemberQueryService {
    private final MemberQueryRepository memberQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagQueryRepository hashtagQueryRepository;
    List<Member> followedMembers;
    List<Member> blockedMembers;

    @Override
    public Slice<SearchMemberDto> getSearchPageMemberAtMemberSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);

        followedMembers = getFollowedMemberByMember(member);
        blockedMembers = getBlockedMemberByMember(member);

        return memberQueryRepository
                .findAllSlicedForSearchPageAtMember(keyword, pageable, followedMembers, blockedMembers);
    }

    @Override
    public SearchFirstDto<Slice<SearchMemberDto>> getSearchPageMemberAtMemberSlicedFirst(
            String keyword, Pageable pageable, Long memberId, int length) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);

        followedMembers = getFollowedMemberByMember(member);
        blockedMembers = getBlockedMemberByMember(member);

        Slice<SearchMemberDto> members = memberQueryRepository
                .findAllSlicedForSearchPageAtMember(keyword, pageable, followedMembers, blockedMembers);

        return new SearchFirstDto<>(members, getHashtagsByPostIds(length, members.getContent()));
    }

    @Override
    public Slice<MemberDto.SearchAllMemberDto> getSearchPageMemberAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);
        blockedMembers = getBlockedMemberByMember(member);

        return memberQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable, blockedMembers);
    }

    private List<Member> getFollowedMemberByMember(Member member) {
        if (member != null) {
            return member.getFollowedMembers().stream()
                    .map(FollowedMember::getMember).collect(Collectors.toList());
        } else{
            return new ArrayList<>();
        }
    }

    private List<Member> getBlockedMemberByMember(Member member) {
        if (member != null) {
            return member.getBlockedMembers().stream()
                    .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.A))
                    .map(BlockedMember::getMember).collect(Collectors.toList());
        } else{
            return new ArrayList<>();
        }
    }

    private Member getMemberByIdWithFollowedMemberAndBlockedMember(Long memberId) throws NotExistMemberException {
        if (memberId == null) {
            return null;
        }

        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMemberAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private List<RelatedMostTaggedHashtagDto> getHashtagsByPostIds(int length, List<SearchMemberDto> data) {
        return hashtagQueryRepository.findAllByPostIds(
                length,
                data.stream().map(SearchMemberDto::getMemberId).collect(Collectors.toList()));
    }
}
