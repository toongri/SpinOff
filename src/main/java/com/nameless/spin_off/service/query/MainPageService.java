package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;

public interface MainPageService {
    Slice<MainPagePostDto> getPostsOrderById(Pageable pageable, Long memberId);

    Slice<MainPagePostDto> getPostsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime, Long memberId);

    Slice<MainPageCollectionDto> getCollectionsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime, Long memberId);

    Slice<MainPagePostDto> getPostsByFollowedHashtagOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;

    Slice<MainPagePostDto> getPostsByFollowedMovieOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;

    Slice<MainPagePostDto> getPostsByFollowingMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;

    Slice<MainPageCollectionDto> getCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException;

    Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, Long memberId);
}
