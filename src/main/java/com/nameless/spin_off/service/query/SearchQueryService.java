package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.SearchPageAtAllCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.search.OverLengthRelatedKeywordException;
import com.nameless.spin_off.exception.search.UnderLengthRelatedKeywordException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface SearchQueryService {

    RelatedSearchAllDto getRelatedSearchAllByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<RelatedSearchHashtagDto> getRelatedSearchHashtagByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<RelatedSearchMemberDto> getRelatedSearchMemberByKeyword(String keyword, int length)
            throws OverLengthRelatedKeywordException, UnderLengthRelatedKeywordException;
    List<MostPopularHashtag> getMostPopularHashtagLimit(int length);
    List<LastSearchDto> getLastSearchesByMemberLimit(Long memberId, int length) throws NotExistMemberException;
    Slice<SearchPageAtAllMemberDto> getSearchPageMemberAtAllSliced(String keyword, Pageable pageable);
    Slice<SearchPageAtAllMovieDto> getSearchPageMovieAtAllSliced(String keyword, Pageable pageable);
    Slice<SearchPageAtAllCollectionDto> getSearchPageCollectionAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException;
    Slice<SearchPageAtAllPostDto> getSearchPagePostAtAllSliced(String keyword, Pageable pageable);

}
