package com.nameless.spin_off.service.member;

import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.MemberScoreEnum;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.AlreadyBlockedMemberException;
import com.nameless.spin_off.exception.member.AlreadyFollowedMemberException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.FollowedCollectionRepository;
import com.nameless.spin_off.repository.member.BlockedMemberRepository;
import com.nameless.spin_off.repository.member.FollowedMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceJpa implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final FollowedMemberRepository followedMemberRepository;
    private final BlockedMemberRepository blockedMemberRepository;
    private final FollowedCollectionRepository followedCollectionRepository;

    @Transactional
    @Override
    public Long insertFollowedMemberByMemberId(Long memberId, Long followedMemberId)
            throws NotExistMemberException, AlreadyFollowedMemberException {
        isExistMember(followedMemberId);
        isBlockedOrBlockingAboutAll(memberId, followedMemberId);

        isExistFollowedMember(memberId, followedMemberId);

        return followedMemberRepository
                .save(
                        FollowedMember.createFollowedMember(
                                Member.createMember(memberId),
                                Member.createMember(followedMemberId)))
                .getId();
    }

    @Transactional
    @Override
    public Long insertBlockedMemberByMemberId(Long memberId, Long blockedMemberId,
                                              BlockedMemberStatus blockedMemberStatus)
            throws NotExistMemberException, AlreadyBlockedMemberException {

        isExistMember(blockedMemberId);
        if (BlockedMemberStatus.B.equals(blockedMemberStatus)) {
            isExistBlock(memberId, blockedMemberId);
        } else if (BlockedMemberStatus.A.equals(blockedMemberStatus)) {
            isExistBlockAndDeletePastBlocked(memberId, blockedMemberId);

            followedMemberRepository
                    .deleteAll(getFollowedMembersByFollowingMemberIdAndMemberId(memberId, blockedMemberId));

            followedCollectionRepository
                    .deleteAll(getFollowedCollectionsByFollowingMemberIdAndMemberId(memberId, blockedMemberId));
        }

        return blockedMemberRepository
                .save(
                        BlockedMember.createBlockedMember(
                                Member.createMember(memberId),
                                Member.createMember(blockedMemberId),
                                blockedMemberStatus))
                .getId();
    }

    @Transactional
    @Override
    public Long insertSearch(Long memberId, String content, SearchedByMemberStatus searchedByMemberStatus) throws NotExistMemberException {

        Member member = getMemberWithSearch(memberId);

        return member.addSearch(content, searchedByMemberStatus);
    }

    @Transactional
    @Override
    public int updateAllPopularity() {
        List<Member> members =
                memberQueryRepository.findAllByViewAfterTime(MemberScoreEnum.MEMBER_FOLLOW.getOldestDate());
        for (Member member : members) {
            member.updatePopularity();
        }
        return members.size();
    }

    private void isExistBlock(Long memberId, Long blockedMemberId) {
        if (memberQueryRepository.isExistBlockedMember(memberId, blockedMemberId)) {
            throw new AlreadyBlockedMemberException(ErrorEnum.ALREADY_BLOCKED_MEMBER);
        }
    }

    private void isExistBlockAndDeletePastBlocked(Long memberId, Long blockedMemberId) {
        Optional<BlockedMember> optional = 
                memberQueryRepository.findOneByBlockingIdAndBlockedId(memberId, blockedMemberId);
        if (optional.isPresent()) {
            BlockedMember blockedMember = optional.get();
            if (BlockedMemberStatus.A.equals(blockedMember.getBlockedMemberStatus())) {
                throw new AlreadyBlockedMemberException(ErrorEnum.ALREADY_BLOCKED_MEMBER);
            } else {
                blockedMemberRepository.delete(blockedMember);
            }
        }
    }

    private void isExistMember(Long memberId) {
        if (!memberQueryRepository.isExist(memberId)) {
            throw new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER);
        }
    }

    private Member getMemberWithSearch(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithSearch(memberId);

        return optionalMember.orElseThrow(() -> new NotExistMemberException(ErrorEnum.NOT_EXIST_MEMBER));
    }

    private void isExistFollowedMember(Long memberId, Long followedMemberId) {
        if (memberQueryRepository.isExistFollowedMember(memberId, followedMemberId)) {
            throw new AlreadyFollowedMemberException(ErrorEnum.ALREADY_FOLLOWED_MEMBER);
        }
    }

    private void isBlockedOrBlockingAboutAll(Long memberId, Long targetMemberId) {
        if (memberQueryRepository.isBlockedOrBlockingAndStatus(memberId, targetMemberId, BlockedMemberStatus.A)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private List<FollowedCollection> getFollowedCollectionsByFollowingMemberIdAndMemberId(
            Long memberId, Long followedMemberId) {
        return followedCollectionRepository
                .findAllIdByFollowingMemberIdAndMemberId(memberId, followedMemberId)
                .stream()
                .map(FollowedCollection::createFollowedCollection)
                .collect(Collectors.toList());
    }

    private List<FollowedMember> getFollowedMembersByFollowingMemberIdAndMemberId(Long memberId, Long followedMemberId) {
        return followedMemberRepository
                .findAllIdByFollowingMemberIdAndMemberId(memberId, followedMemberId)
                .stream()
                .map(FollowedMember::createFollowedMember)
                .collect(Collectors.toList());
    }
}
