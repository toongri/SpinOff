package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchPageAtAllCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.entity.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface SearchQueryRepository {
    List<RelatedSearchMemberDto> findRelatedMembersAboutKeyword(String keyword, int length);
    List<RelatedSearchPostDto> findRelatedPostsAboutKeyword(String keyword, int length);
    List<RelatedSearchHashtagDto> findRelatedHashtagsAboutKeyword(String keyword, int length);
    List<RelatedSearchCollectionDto> findRelatedCollectionsAboutKeyword(String keyword, int length);
    List<RelatedSearchMovieDto> findRelatedMoviesAboutKeyword(String keyword, int length);
    List<MostPopularHashtag> findMostPopularHashtags();
    List<LastSearchDto> findLastSearchesByMemberId(Long id);
    Slice<SearchPageAtAllMemberDto> findSearchPageMemberAtAllSliced(String keyword, Pageable pageable);
    Slice<SearchPageAtAllMovieDto> findSearchPageMovieAtAllSliced(String keyword, Pageable pageable);
    Slice<SearchPageAtAllCollectionDto> findSearchPageCollectionAtAllSliced(String keyword, Pageable pageable,
                                                                            List<Member> followedMembers);
    Slice<SearchPageAtAllPostDto> findSearchPagePostAtAllSliced(String keyword, Pageable pageable);

}
