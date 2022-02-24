package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionOrderByCollectedDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MainPageQueryService {
    Slice<MainPagePostDto> getPostsOrderById(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsOrderByPopularityBySlicing(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsOrderByPopularityBySlicing(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowedHashtagOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowedMovieOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowingMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionOrderByCollectedDto> getCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
}
