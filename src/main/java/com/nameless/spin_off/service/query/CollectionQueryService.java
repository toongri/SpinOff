package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchPageAtAllCollectionDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CollectionQueryService {
    Slice<SearchPageAtAllCollectionDto> getSearchPageCollectionAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsByFollowedMemberSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<CollectionDto.SearchPageAtCollectionCollectionDto> getSearchPageCollectionAtCollectionSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
}
