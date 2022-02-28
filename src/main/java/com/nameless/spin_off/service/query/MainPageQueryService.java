package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MainPageQueryService {
    Slice<MainPagePostDto> getPostsSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowedHashtagSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowedMovieSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowingMemberSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsByFollowedMemberSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsSliced(Pageable pageable, Long memberId) throws NotExistMemberException;
}
