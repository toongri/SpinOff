package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.PostInCollectionDto;
import com.nameless.spin_off.dto.PostDto.*;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostQueryService {
    Slice<SearchPageAtAllPostDto> getPostsSlicedForSearchPagePostAtAll(String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowedHashtagSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowedMovieSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<MainPagePostDto> getPostsByFollowingMemberSlicedForMainPage(Pageable pageable, Long memberId) throws NotExistMemberException;
    SearchFirstDto<Slice<SearchPageAtHashtagPostDto>> getPostsByHashtagsSlicedForSearchPageFirst(
            Pageable pageable, List<String> hashtagContent, Long memberId, int length);
    Slice<SearchPageAtHashtagPostDto> getPostsByHashtagsSlicedForSearchPage(Pageable pageable, List<String> hashtagContent, Long memberId);

    RelatedPostFirstDto<VisitPostDto> visitPost(MemberDetails currentMember, Long postId, Pageable pageable);
    Slice<RelatedPostDto> RelatedPostsSliced(Long memberId, Long postId, Pageable pageable);
    List<PostInCollectionDto> getCollectionNamesByMemberIdAndPostId(Long memberId, Long postId);
}
