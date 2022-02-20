package com.nameless.spin_off.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MovieDto {

    @Data
    @NoArgsConstructor
    public static class RelatedSearchMovieDto {

        private Long id;
        private String title;

        @QueryProjection
        public RelatedSearchMovieDto(Long id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
