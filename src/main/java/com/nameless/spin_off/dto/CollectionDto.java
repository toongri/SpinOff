package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class CollectionDto {

    @Data
    @NoArgsConstructor
    public static class RelatedSearchCollectionDto {

        private Long id;
        private String content;

        @QueryProjection
        public RelatedSearchCollectionDto(Long id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    @Data
    @NoArgsConstructor
    public static class MainPageCollectionDto {

        private Long collectionId;
        private String collectionTitle;
        private Long memberId;
        private String memberNickname;
        private List<String> thumbnailUrls = new ArrayList<>();

        @QueryProjection
        public MainPageCollectionDto(Long collectionId, String collectionTitle, Long memberId, String memberNickname,
                                     String thumbnailUrl1, String thumbnailUrl2) {

            this.collectionId = collectionId;
            this.collectionTitle = collectionTitle;
            this.memberId = memberId;
            this.memberNickname = memberNickname;
            this.thumbnailUrls.add(thumbnailUrl1);
            this.thumbnailUrls.add(thumbnailUrl2);
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCollectionVO {

        private Long memberId;
        private String title;
        private String content;
        private PublicOfCollectionStatus publicOfCollectionStatus;
    }
}
