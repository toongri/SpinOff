package com.nameless.spin_off.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MovieDto {
    @Data
    @NoArgsConstructor
    public static class SearchPageAtAllPostDto {

        private Long id;
        private String title;

        private String imageUrl;
//
//        @QueryProjection
//        public SearchPageAtAllPostDto(Long postId, String title, Long memberId, String memberNickname,
//                                      String memberProfileImgUrl, String thumbnailUrl) {
//            this.postId = postId;
//            this.postTitle = title;
//            this.memberId = memberId;
//            this.memberNickname = memberNickname;
//            this.memberProfileImgUrl = memberProfileImgUrl;
//            this.thumbnailUrl = thumbnailUrl;
//        }
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
