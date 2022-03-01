package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtMemberMemberDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
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

    @Override
    public Slice<SearchPageAtMemberMemberDto> getSearchPageMemberAtMemberSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);

        List<Member> followedMembers = getFollowedMemberByMember(member);
        List<Member> blockedMembers = getBlockedMemberByMember(member);

        return memberQueryRepository
                .findAllSlicedForSearchPageAtMember(keyword, pageable, followedMembers, blockedMembers);
    }

    @Override
    public Slice<SearchPageAtAllMemberDto> getSearchPageMemberAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);
        List<Member> blockedMembers = getBlockedMemberByMember(member);

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
}
