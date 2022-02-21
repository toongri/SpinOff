package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.*;
import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;

import java.util.List;

public interface SearchQueryRepository {
    List<RelatedSearchMemberDto> getMembersAboutKeyword(String keyword);
    List<RelatedSearchPostDto> getPostsAboutKeyword(String keyword);
    List<RelatedSearchHashtagDto> getHashtagsAboutKeyword(String keyword);
    List<RelatedSearchCollectionDto> getCollectionsAboutKeyword(String keyword);
    List<RelatedSearchMovieDto> getMoviesAboutKeyword(String keyword);
    List<MostPopularHashtag> getMostPopularHashtags();
}
