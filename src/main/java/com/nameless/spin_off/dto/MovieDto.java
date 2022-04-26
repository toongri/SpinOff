package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.enums.movie.GenreOfMovieStatus;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;

import static com.nameless.spin_off.entity.enums.search.SearchEnum.MOVIE_SEARCH_THUMBNAIL_NUMBER;

public class MovieDto {

    @Data
    @NoArgsConstructor
    public static class FollowMovieDto {

        @ApiModelProperty(
                value = "영화 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "영화 제목",
                example = "스프링부트와_aws로_혼자_구현하는_웹_서비스")
        private String title;

        @ApiModelProperty(
                value = "영화 썸네일",
                example = "스프링부트와_aws로_혼자_구현하는_웹_서비스")
        private String thumbnail;

        @ApiModelProperty(
                value = "팔로우 여부",
                example = "false")
        private boolean isFollowed;

        @QueryProjection
        public FollowMovieDto(Long id, String title, String thumbnail) {
            this.id = id;
            this.title = title;
            this.thumbnail = thumbnail;
        }

    }

    @Data
    @AllArgsConstructor
    public static class MovieInReadPost {
        private String thumbnail;
        private String title;
        private String directorName;

        public MovieInReadPost(Movie movie) {
            this.thumbnail = movie.getThumbnail();
            this.title = movie.getTitle();
            this.directorName = movie.getDirectorName();
        }
    }


    @Data
    @AllArgsConstructor
    public static class SearchMovieFirstDto {

        SearchMovieAboutFirstMovieDto firstMovie;
        Slice<SearchMovieDto> movies;
    }

    @Data
    @NoArgsConstructor
    public static class SearchMovieAboutFirstMovieDto {

        private Long movieId;
        private String title;
        private String imageUrl;
        private List<String> thumbnails = new ArrayList<>();

        public SearchMovieAboutFirstMovieDto(Movie movie) {
            movieId = movie.getId();
            title = movie.getTitle();
            imageUrl = movie.getThumbnail();
            List<Post> taggedPosts = movie.getTaggedPosts();

            for (Post taggedPost : taggedPosts) {
                if (taggedPost.getThumbnailUrl() != null) {
                    thumbnails.add(taggedPost.getThumbnailUrl());
                }

                if (thumbnails.size() == MOVIE_SEARCH_THUMBNAIL_NUMBER.getValue()) {
                    break;
                }
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchMovieDto {

        private Long movieId;
        private String title;
        private String imageUrl;

        @QueryProjection
        public SearchMovieDto(Long movieId, String title, String imageUrl) {
            this.movieId = movieId;
            this.title = title;
            this.imageUrl = imageUrl;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchAllMovieDto {

        private Long movieId;
        private String title;
        private String imageUrl;
        private List<GenreOfMovieStatus> genreOfMovieStatuses = new ArrayList<>();

        @QueryProjection
        public SearchAllMovieDto(Long movieId, String title, String imageUrl,
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
