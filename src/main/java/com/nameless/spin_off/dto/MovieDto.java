package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.enums.movie.GenreOfMovieStatus;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class MovieDto {

    @Data
    @NoArgsConstructor
    public static class SearchPageAtMovieMovieFirstDto {

        private Long movieId;
        private String title;
        private String imageUrl;
        private List<String> thumbnails = new ArrayList<>();

        public SearchPageAtMovieMovieFirstDto(Movie movie) {
            movieId = movie.getId();
            title = movie.getTitle();
            imageUrl = movie.getImageUrl();
            List<Post> taggedPosts = movie.getTaggedPosts();

            for (Post taggedPost : taggedPosts) {
                if (taggedPost.getThumbnailUrl() != null) {
                    thumbnails.add(taggedPost.getThumbnailUrl());
                }

                if (thumbnails.size() == 10) {
                    break;
                }
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchPageAtMovieMovieDto {

        private Long movieId;
        private String title;
        private String imageUrl;

        @QueryProjection
        public SearchPageAtMovieMovieDto(Long movieId, String title, String imageUrl) {
            this.movieId = movieId;
            this.title = title;
            this.imageUrl = imageUrl;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchPageAtAllMovieDto {

        private Long movieId;
        private String title;
        private String imageUrl;
        private List<GenreOfMovieStatus> genreOfMovieStatuses = new ArrayList<>();

        @QueryProjection
        public SearchPageAtAllMovieDto(Long movieId, String title, String imageUrl,
                                       GenreOfMovieStatus firstGenre, GenreOfMovieStatus secondGenre) {
            this.movieId = movieId;
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

        private Long movieId;
        private String title;

        private String imageUrl;

        @QueryProjection
        public RelatedSearchMovieDto(Long movieId, String title, String imageUrl) {
            this.movieId = movieId;
            this.title = title;
        }
    }
}
