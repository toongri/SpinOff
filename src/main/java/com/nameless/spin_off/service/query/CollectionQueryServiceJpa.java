package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.PostInCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchAllCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchCollectionDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
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
public class CollectionQueryServiceJpa implements CollectionQueryService {

    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagQueryRepository hashtagQueryRepository;

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
    public List<PostInCollectionDto> getCollectionNamesByMemberId(Long memberId) {
        return collectionQueryRepository.findAllCollectionNamesByMemberIdOrderByCollectedPostDESC(memberId);
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
            return new ArrayList<>();
        }
    }
}
