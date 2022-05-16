package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchAllCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchAllMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchAllMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

public class SearchDto {

    @Data
    @AllArgsConstructor
    public static class SearchFirstDto<T> {
        private T data;
        private List<RelatedMostTaggedHashtagDto> hashtags;
    }

    @Data
    @AllArgsConstructor
    public static class SearchAllFirstDto {
        private Slice<SearchPageAtAllPostDto> posts;
        private Slice<SearchAllCollectionDto> collections;
        private Slice<SearchAllMovieDto> movies;
        private Slice<SearchAllMemberDto> members;
    }

    @Data
    @AllArgsConstructor
    public static class SearchAllDto {
        private Slice<SearchPageAtAllPostDto> posts;
        private Slice<SearchAllMovieDto> movies;
        private Slice<SearchAllMemberDto> members;
    }

    @Data
    @NoArgsConstructor
    public static class LastSearchDto {

        @ApiModelProperty(
                value = "검색 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "검색 내용",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
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
