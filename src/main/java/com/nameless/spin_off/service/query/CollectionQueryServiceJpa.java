package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.*;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionQueryServiceJpa implements CollectionQueryService {

    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagQueryRepository hashtagQueryRepository;
    private final MemberQueryRepository memberQueryRepository;

    @Override
    public Slice<SearchAllCollectionDto> getSearchPageCollectionAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return collectionQueryRepository
                .findAllSlicedForSearchPageAtAll(keyword, pageable,
                        getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);
    }

    @Override
    public Slice<SearchCollectionDto> getSearchPageCollectionAtCollectionSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return collectionQueryRepository
                .findAllSlicedForSearchPageAtCollection(keyword, pageable, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);
    }

    @Override
    public SearchFirstDto<Slice<SearchCollectionDto>> getSearchPageCollectionAtCollectionSlicedFirst(
            String keyword, Pageable pageable, Long memberId, int length) throws NotExistMemberException {

        Slice<SearchCollectionDto> collections = collectionQueryRepository
                .findAllSlicedForSearchPageAtCollection(keyword, pageable, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);

        return new SearchFirstDto<>(
                collections, hashtagQueryRepository.findAllByCollectionIds(length, collections.stream()
                .map(SearchCollectionDto::getCollectionId).collect(Collectors.toList())));
    }

    @Override
    public List<PostInCollectionDto> getCollectionsById(Long memberId) {
        return collectionQueryRepository.findAllByMemberIdOrderByCollectedPostDESC(memberId);
    }

    @Override
    public QuickPostInCollectionDto getLatestCollectionNameById(Long memberId) {
        return collectionQueryRepository.findOneByMemberIdOrderByCollectedPostDESC(memberId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    @Override
    public Slice<MyPageCollectionDto> getCollectionsByMemberIdSliced(MemberDetails currentMember,
                                                                     Long targetMemberId, Pageable pageable) {
        if (currentMember != null) {
            Long currentMemberId = currentMember.getId();
            isExistBlockedMember(currentMemberId, targetMemberId);

            return collectionQueryRepository.findAllByMemberIdSliced(
                    targetMemberId,
                    pageable,
                    isExistFollowedMember(currentMemberId, targetMemberId),
                    isCurrentMemberAdminAtMyPage(currentMember, targetMemberId));
        } else {
            return collectionQueryRepository.findAllByMemberIdSliced(
                    targetMemberId, pageable, false, false);
        }
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsSlicedForMainPage(Pageable pageable, Long memberId)
            throws NotExistMemberException {

        return collectionQueryRepository.findAllSlicedForMainPage(pageable,
                memberId, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId));
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedMemberSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return collectionQueryRepository
                .findAllByFollowedMemberSlicedForMainPage(pageable, memberId);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return collectionQueryRepository
                .findAllByFollowedCollectionsSlicedForMainPage(pageable, memberId);
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
        } else{
            return Collections.emptyList();
        }
    }

    private boolean isExistFollowedMember(Long memberId, Long followedMemberId) {
        if (memberId != null) {
            return memberQueryRepository.isExistFollowedMember(memberId, followedMemberId);
        } else{
            return false;
        }
    }

    private void isExistBlockedMember(Long memberId, Long targetMemberId) {
        if (memberQueryRepository.isBlockedOrBlockingAndStatus(memberId, targetMemberId, BlockedMemberStatus.A)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private boolean isCurrentMemberAdminAtMyPage(MemberDetails currentMember, Long targetMemberId) {
        return currentMember.isAdmin() || currentMember.getId().equals(targetMemberId);
    }
}
