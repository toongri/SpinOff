package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.hashtag.PostedHashtag;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

public class HashtagDto {

    @Data
    @NoArgsConstructor
    public static class ContentHashtagDto {

        @ApiModelProperty(
                value = "해시태그 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "해시태그 id",
                example = "스프링부트와_aws로_혼자_구현하는_웹_서비스")
        private String content;

        @QueryProjection
        public ContentHashtagDto(Long id, String content) {
            this.id = id;
            this.content = content;
        }

        public ContentHashtagDto(PostedHashtag postedHashtag) {
            this.id = postedHashtag.getHashtag().getId();
            this.content = postedHashtag.getHashtag().getContent();;
        }
    }

    @Data
    @NoArgsConstructor
    public static class MostPopularHashtag {

        @ApiModelProperty(
                value = "해시태그 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "해시태그 id",
                example = "스프링부트와_aws로_혼자_구현하는_웹_서비스")
        private String content;

        @QueryProjection
        public MostPopularHashtag(Long id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    @Data
    @NoArgsConstructor
    public static class RelatedSearchHashtagDto {

        @ApiModelProperty(
                value = "해시태그 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "해시태그 id",
                example = "스프링부트와_aws로_혼자_구현하는_웹_서비스")
        private String content;

        @QueryProjection
        public RelatedSearchHashtagDto(Long id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    @Data
    @NoArgsConstructor
    public static class RelatedMostTaggedHashtagDto {

        @ApiModelProperty(
                value = "해시태그 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "해시태그 id",
                example = "스프링부트와_aws로_혼자_구현하는_웹_서비스")
        private String content;

        @QueryProjection
        public RelatedMostTaggedHashtagDto(Long id, String content) {
            this.id = id;
            this.content = content;
        }
        public RelatedMostTaggedHashtagDto(Hashtag hashtag) {
            this.id = hashtag.getId();
            this.content = hashtag.getContent();
        }
    }
}
