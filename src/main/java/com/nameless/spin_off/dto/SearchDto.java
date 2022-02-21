package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.member.SearchedByMember;
import com.querydsl.core.annotations.QueryProjection;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

public class SearchDto {

    @Data
    @NoArgsConstructor
    public static class LastSearchDto {
        private Long id;
        private String content;

        public LastSearchDto(SearchedByMember searchedByMember) {
            this.id = searchedByMember.getId();
            this.content = searchedByMember.getContent();
        }
    }

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
