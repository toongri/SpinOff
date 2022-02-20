package com.nameless.spin_off.dto;

import com.nameless.spin_off.StaticVariable;
import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.post.PostedMedia;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.nameless.spin_off.StaticVariable.COLLECTION_THUMBNAIL_NUMBER;

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

        public MainPageCollectionDto(Collection collection) {
            this.collectionId = collection.getId();
            this.collectionTitle = collection.getTitle();
            this.memberId = collection.getMember().getId();
            this.memberNickname = collection.getMember().getNickname();

            for (CollectedPost collectedPost : collection.getCollectedPosts()) {
                if (collectedPost.getPost().getThumbnailUrl() != null) {
                    thumbnailUrls.add(collectedPost.getPost().getThumbnailUrl());
                    if (thumbnailUrls.size() == COLLECTION_THUMBNAIL_NUMBER) {
                        break;
                    }
                }
            }
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
