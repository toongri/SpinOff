package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostQueryService {
    Slice<SearchPageAtAllPostDto> getPostsSlicedForSearchPagePostAtAll(String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowedHashtagSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowedMovieSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowingMemberSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
}
