package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

public class HashtagDto {

    @Data
    @NoArgsConstructor
    public static class MostPopularHashtag {

        private Long id;
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

        private Long id;
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

        private Long id;
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
