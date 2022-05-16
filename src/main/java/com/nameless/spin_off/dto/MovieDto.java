package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.MemberDto.MovieMemberDto;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nameless.spin_off.entity.enums.search.SearchEnum.MOVIE_SEARCH_THUMBNAIL_NUMBER;

public class MovieDto {

    @Data
    @NoArgsConstructor
    public static class ReadMovieDto {

        @ApiModelProperty(
                value = "영화 id",
                example = "123")
        private Long movieId;

        @ApiModelProperty(
                value = "영화 제목",
                example = "배트맨 다크나이트")
        private String title;

        @ApiModelProperty(
                value = "영화 썸네일",
                example = "www.naver.com")
        private String thumbnail;

        @ApiModelProperty(
                value = "네이버 영화 url",
                example = "www.naver.com")
        private String naverUrl;

        @ApiModelProperty(
                value = "영화 국가",
                example = "영국")
        private String nation;

        @ApiModelProperty(
                value = "영화 감독",
                example = "영국")
        private String director;

        @ApiModelProperty(
                value = "영화 배우",
                example = "영국")
        private String actors;

        @ApiModelProperty(
                value = "영화 장르",
                example = "로맨스")
        private List<String> genres;

        private List<MovieMemberDto> members;

        @ApiModelProperty(
                value = "태그 갯수",
                example = "123")
        private Long taggedSize;

        @ApiModelProperty(
                value = "팔로우 갯수",
                example = "123")
        private Long followedSize;

        @ApiModelProperty(
                value = "팔로우 여부",
                example = "false")
        private boolean isFollowed;


        @QueryProjection
        public ReadMovieDto(Long movieId, String title, String thumbnail, String naverUrl, String nation,
                            String director, String actors, String firstGenre, String secondGenre,
                            String thirdGenre, String fourthGenre, Long taggedSize, Long followedSize) {

            this.movieId = movieId;
            this.title = title;
            this.thumbnail = thumbnail;
            this.naverUrl = naverUrl;
            this.nation = nation;
            this.director = director;
            this.actors = actors;
            this.taggedSize = taggedSize;
            this.followedSize = followedSize;
        }
    }


    @Data
    public static class NaverMoviesResponseDto {

        private int display;
        private List<NaverMovie> items;
//        private Item[] items;
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NaverMovie {
            private String title;
            private String link;
            private String image;
            private String subtitle;
            private Date pubDate;
            private String director;
            private String actor;
            private float userRating;
        }
    }


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
        private List<String> genreOfMovieStatuses = new ArrayList<>();

        @QueryProjection
        public SearchAllMovieDto(Long movieId, String title, String imageUrl,
                                 String firstGenre, String secondGenre) {
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
            this.imageUrl = imageUrl;
        }
    }
}
