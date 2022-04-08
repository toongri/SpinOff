package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.*;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface CollectionQueryService {
    Slice<SearchAllCollectionDto> getSearchPageCollectionAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsByFollowedMemberSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<SearchCollectionDto> getSearchPageCollectionAtCollectionSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
    SearchFirstDto<Slice<SearchCollectionDto>> getSearchPageCollectionAtCollectionSlicedFirst(
            String keyword, Pageable pageable, Long memberId, int length) throws NotExistMemberException;
    List<PostInCollectionDto> getCollectionsByMemberId(Long memberId);
    QuickPostInCollectionDto getCollectionNameByMemberId(Long memberId);
    Slice<MyPageCollectionDto> getCollectionsByMemberIdSliced(MemberDetails currentMember, Long targetMemberId, Pageable pageable);
    List<PostInCollectionDto> getCollectionsByMemberIdAndPostId(Long memberId, Long postId);
    QuickPostInCollectionDto getCollectionNameByMemberIdAndPostId(Long memberId, Long postId);

}
