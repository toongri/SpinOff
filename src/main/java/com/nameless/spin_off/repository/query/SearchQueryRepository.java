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
    List<RelatedSearchMemberDto> getMembersAboutKeyword(String keyword, int length);
    List<RelatedSearchPostDto> getPostsAboutKeyword(String keyword, int length);
    List<RelatedSearchHashtagDto> getHashtagsAboutKeyword(String keyword, int length);
    List<RelatedSearchCollectionDto> getCollectionsAboutKeyword(String keyword, int length);
    List<RelatedSearchMovieDto> getMoviesAboutKeyword(String keyword, int length);
    List<MostPopularHashtag> getMostPopularHashtags();

    List<LastSearchDto> getLastSearchesByMemberId(Long id);
}
