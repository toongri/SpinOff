package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CollectionDto {

    @Data
    @NoArgsConstructor
    public static class SearchPageAtAllCollectionDto {

        private Long collectionId;
        private String collectionTitle;
        private Long memberId;
        private String memberAccountId;
        private String thumbnailUrl;
        private String followingMemberNickname;
        private int followingNumber;

        public SearchPageAtAllCollectionDto(Collection collection) {

            this.collectionId = collection.getId();
            this.collectionTitle = collection.getTitle();
            this.memberId = collection.getMember().getId();
            this.memberAccountId = collection.getMember().getAccountId();
            this.thumbnailUrl = collection.getFirstThumbnail();
        }

        public SearchPageAtAllCollectionDto(Collection collection, List<Member> followingMembers) {
            this.collectionId = collection.getId();
            this.collectionTitle = collection.getTitle();
            this.memberId = collection.getMember().getId();
            this.memberAccountId = collection.getMember().getAccountId();
            this.thumbnailUrl = collection.getFirstThumbnail();

            List<FollowedCollection> followedCollections = collection.getFollowingMembers();

            followedCollections.stream()
                    .filter(followedCollection -> followingMembers.contains(followedCollection.getMember()))
                    .max(Comparator.comparing(followedCollection -> followedCollection.getMember().getPopularity()))
                    .ifPresent(followedCollection -> setFollowingMemberNicknameAndNumber(
                            followedCollection.getMember().getNickname(), followedCollections.size()));

        }

        public void setFollowingMemberNicknameAndNumber(String nickname, int size) {
            this.followingMemberNickname = nickname;
            this.followingNumber = size - 1;
        }
    }

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
            if (thumbnailUrl1 != null) {
                this.thumbnailUrls.add(thumbnailUrl1);
                if (thumbnailUrl2 != null) {
                    this.thumbnailUrls.add(thumbnailUrl2);
                }
            }
        }

        public MainPageCollectionDto(Collection collection) {

            this.collectionId = collection.getId();
            this.collectionTitle = collection.getTitle();
            this.memberId = collection.getMember().getId();
            this.memberNickname = collection.getMember().getNickname();
            if (collection.getFirstThumbnail() != null) {
                this.thumbnailUrls.add(collection.getFirstThumbnail());
                if (collection.getSecondThumbnail() != null) {
                    this.thumbnailUrls.add(collection.getSecondThumbnail());
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
