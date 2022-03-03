package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchPageAtAllCollectionDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
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
public class CollectionQueryServiceJpa implements CollectionQueryService {

    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberRepository memberRepository;

    @Override
    public Slice<SearchPageAtAllCollectionDto> getSearchPageCollectionAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);

        List<Member> followedMembers = getFollowedMemberByMember(member);
        List<Member> blockedMembers = getBlockedMemberByMember(member);

        return collectionQueryRepository
                .findAllSlicedForSearchPageAtAll(keyword, pageable, followedMembers, blockedMembers);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsSlicedForMainPage(Pageable pageable,
                                                                        Long memberId) throws NotExistMemberException {

        Optional<Member> optionalMember = getMemberByIdWithBlockedMember(memberId);
        Member member = optionalMember.orElse(null);
        List<Member> blockedMembers = getBlockedMemberByMember(member);

        return collectionQueryRepository.findAllSlicedForMainPage(pageable, member, blockedMembers);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedMemberSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);

        List<Member> followedMembers =
                member.getFollowedMembers().stream().map(FollowedMember::getMember).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        return collectionQueryRepository
                .findAllByFollowedMemberSlicedForMainPage(pageable, followedMembers, blockedMembers);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedCollectionAndBlockedMember(memberId);

        List<Collection> followedCollections =
                member.getFollowedCollections().stream().map(FollowedCollection::getCollection).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        return collectionQueryRepository
                .findAllByFollowedCollectionsSlicedForMainPage(pageable, followedCollections, blockedMembers);
    }

    private Member getMemberByIdWithFollowedCollectionAndBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedCollectionAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Optional<Member> getMemberByIdWithBlockedMember(Long memberId) throws NotExistMemberException {
        if (memberId == null) {
            return Optional.empty();
        }
        Optional<Member> optionalMember = memberRepository.findOneByIdWithBlockedMember(memberId);

        return Optional.of(optionalMember.orElseThrow(NotExistMemberException::new));
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
