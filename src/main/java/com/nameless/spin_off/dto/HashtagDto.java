package com.nameless.spin_off.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

public class HashtagDto {

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
}
