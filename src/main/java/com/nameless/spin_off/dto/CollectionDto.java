package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PostedMedia;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class CollectionDto {

    @Data
    @NoArgsConstructor
    public static class MainPageCollectionDto {

        private Long collectionId;
        private String collectionTitle;
        private Long memberId;
        private String memberNickname;
        private List<String> collectionImgUrls = new ArrayList<>();

        public MainPageCollectionDto(Collection collection) {
            this.collectionId = collection.getId();
            this.collectionTitle = collection.getTitle();
            this.memberId = collection.getMember().getId();
            this.memberNickname = collection.getMember().getNickname();

            for (CollectedPost collectedPost : collection.getCollectedPosts()) {
                List<PostedMedia> postedMedias = collectedPost.getPost().getPostedMedias();
                if (postedMedias.size() > 0) {
                    collectionImgUrls.add(postedMedias.get(0).getUrl());
                    if (collectionImgUrls.size() == 2) {
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
