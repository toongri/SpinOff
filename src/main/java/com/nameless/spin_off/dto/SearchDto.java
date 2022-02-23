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
    public static class LastSearchDto {
        private Long id;
        private String content;

        @QueryProjection
        public LastSearchDto(Long id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedSearchAllDto {
        private List<RelatedSearchPostDto> posts;
        private List<RelatedSearchMovieDto> movies;
        private List<RelatedSearchHashtagDto> hashtags;
        private List<RelatedSearchMemberDto> members;
        private List<RelatedSearchCollectionDto> collections;
    }
}
