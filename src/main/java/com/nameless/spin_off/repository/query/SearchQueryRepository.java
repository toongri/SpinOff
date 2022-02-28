package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;

import java.util.List;

public interface SearchQueryRepository {
    List<RelatedSearchMemberDto> findRelatedMembersAboutKeyword(String keyword, int length);
    List<RelatedSearchPostDto> findRelatedPostsAboutKeyword(String keyword, int length);
    List<RelatedSearchHashtagDto> findRelatedHashtagsAboutKeyword(String keyword, int length);
    List<RelatedSearchCollectionDto> findRelatedCollectionsAboutKeyword(String keyword, int length);
    List<RelatedSearchMovieDto> findRelatedMoviesAboutKeyword(String keyword, int length);
    List<MostPopularHashtag> findMostPopularHashtagsLimit(int length);
    List<LastSearchDto> findLastSearchesByMemberIdLimit(Long id, int length);

}
