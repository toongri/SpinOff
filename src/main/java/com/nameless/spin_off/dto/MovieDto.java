package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.enums.movie.GenreOfMovieStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class MovieDto {

    @Data
    @NoArgsConstructor
    public static class SearchPageAtMovieMovieDto {

        private Long id;
        private String title;
        private String imageUrl;

        @QueryProjection
        public SearchPageAtMovieMovieDto(Long id, String title, String imageUrl) {
            this.id = id;
            this.title = title;
            this.imageUrl = imageUrl;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchPageAtAllMovieDto {

        private Long id;
        private String title;
        private String imageUrl;
        private List<GenreOfMovieStatus> genreOfMovieStatuses = new ArrayList<>();

        @QueryProjection
        public SearchPageAtAllMovieDto(Long id, String title, String imageUrl,
                                       GenreOfMovieStatus firstGenre, GenreOfMovieStatus secondGenre) {
            this.id = id;
            this.title = title;
            this.imageUrl = imageUrl;
            if (firstGenre != null) {
                genreOfMovieStatuses.add(firstGenre);
                if (secondGenre != null) {
                    genreOfMovieStatuses.add(secondGenre);
                }
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class RelatedSearchMovieDto {

        private Long id;
        private String title;

        private String imageUrl;

        @QueryProjection
        public RelatedSearchMovieDto(Long id, String title, String imageUrl) {
            this.id = id;
            this.title = title;
        }
    }
}
