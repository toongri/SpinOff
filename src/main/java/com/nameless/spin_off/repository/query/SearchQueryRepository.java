package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface SearchQueryRepository {
    List<RelatedSearchMemberDto> getRelatedMembersAboutKeyword(String keyword, int length);
    List<RelatedSearchPostDto> getRelatedPostsAboutKeyword(String keyword, int length);
    List<RelatedSearchHashtagDto> getRelatedHashtagsAboutKeyword(String keyword, int length);
    List<RelatedSearchCollectionDto> getRelatedCollectionsAboutKeyword(String keyword, int length);
    List<RelatedSearchMovieDto> getRelatedMoviesAboutKeyword(String keyword, int length);
    List<MostPopularHashtag> getMostPopularHashtags();
    List<LastSearchDto> getLastSearchesByMemberId(Long id);
    Slice<SearchPageAtAllMemberDto> getSearchPageMemberAtAll(String keyword, Pageable pageable);

}
