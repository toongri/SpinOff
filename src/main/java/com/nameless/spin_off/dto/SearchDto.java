package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class SearchDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedSearchDto {
        private List<RelatedSearchPostDto> posts;
        private List<RelatedSearchMovieDto> movies;
        private List<RelatedSearchHashtagDto> hashtags;
        private List<RelatedSearchMemberDto> members;
        private List<RelatedSearchCollectionDto> collections;
    }
}
