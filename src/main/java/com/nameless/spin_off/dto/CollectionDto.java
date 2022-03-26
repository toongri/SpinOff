package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
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
    public static class QuickPostInCollectionDto {
        private Long id;
        private String title;

        @QueryProjection
        public QuickPostInCollectionDto(Long id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    @Data
    @NoArgsConstructor
    public static class PostInCollectionDto {
        private Long id;
        private String title;
        private String thumbnail;
        private boolean isCollected;

        @QueryProjection
        public PostInCollectionDto(Long id, String title, String thumbnail) {
            this.id = id;
            this.title = title;
            this.thumbnail = thumbnail;
            this.isCollected = false;
        }
    }

    @Data
    public static class IdAndPublicCollectionDto {
        private Long id;
        private PublicOfCollectionStatus publicOfCollectionStatus;

        @QueryProjection
        public IdAndPublicCollectionDto(Long id, PublicOfCollectionStatus publicOfCollectionStatus) {
            this.id = id;
            this.publicOfCollectionStatus = publicOfCollectionStatus;
        }
    }

    @Data
    @NoArgsConstructor
    public static class FollowCollectionMemberDto {
        private Long collectionId;
        private Long memberId;
        private Double memberPopularity;
        private String memberNickname;

        @QueryProjection
        public FollowCollectionMemberDto(Long collectionId, Long memberId, Double memberPopularity, String memberNickname) {
            this.collectionId = collectionId;
            this.memberId = memberId;
            this.memberPopularity = memberPopularity;
            this.memberNickname = memberNickname;
        }
    }


    @Data
    @NoArgsConstructor
    public static class SearchCollectionDto {

        private Long collectionId;
        private String collectionTitle;
        private Long memberId;
        private String memberAccountId;
        private List<String> thumbnailUrls = new ArrayList<>();
        private String followingMemberNickname;
        private int followingCount;

        @QueryProjection
        public SearchCollectionDto(Long collectionId, String collectionTitle, Long memberId, String memberAccountId,
                                   String thumbnail1, String thumbnail2, String thumbnail3, String thumbnail4,
                                   Long followedMemberCount) {

            this.collectionId = collectionId;
            this.collectionTitle = collectionTitle;
            this.memberId = memberId;
            this.memberAccountId = memberAccountId;
            this.followingCount = followedMemberCount.intValue();
            if (thumbnail1 != null) {
                thumbnailUrls.add(thumbnail1);

                if (thumbnail2 != null) {
                    thumbnailUrls.add(thumbnail2);

                    if (thumbnail3 != null) {
                        thumbnailUrls.add(thumbnail3);

                        if (thumbnail4 != null) {
                            thumbnailUrls.add(thumbnail4);
                        }
                    }
                }
            }
        }

        public void setFollowingMember(List<FollowCollectionMemberDto> followedCollections) {
            if (followedCollections != null) {
                followedCollections.stream()
                        .max(Comparator.comparing(
                                c -> c.memberPopularity))
                        .ifPresent(c -> setFollowingMemberNickname(c.getMemberNickname()));
            }
        }

        public void setFollowingMemberNickname(String nickname) {
            this.followingMemberNickname = nickname;
            this.followingCount -= 1;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchAllCollectionDto {

        private Long collectionId;
        private String collectionTitle;
        private Long memberId;
        private String memberAccountId;
        private String thumbnailUrl;
        private String followingMemberNickname;
        private int followingCount;

        @QueryProjection
        public SearchAllCollectionDto(Long collectionId, String collectionTitle, Long memberId, String memberAccountId,
                                      String thumbnailUrl, Long followedMemberCount) {
            this.collectionId = collectionId;
            this.collectionTitle = collectionTitle;
            this.memberId = memberId;
            this.memberAccountId = memberAccountId;
            this.thumbnailUrl = thumbnailUrl;
            this.followingCount = followedMemberCount.intValue();
        }

        public void setFollowingMember(List<FollowCollectionMemberDto> followedCollections) {
            if (followedCollections != null) {
                followedCollections.stream()
                        .max(Comparator.comparing(
                                c -> c.memberPopularity))
                        .ifPresent(c -> setFollowingMemberNickname(c.getMemberNickname()));
            }
        }

        public void setFollowingMemberNickname(String nickname) {
            this.followingMemberNickname = nickname;
            this.followingCount -= 1;
        }

        public SearchAllCollectionDto(Collection collection, List<Long> followingMembers) {
            this.collectionId = collection.getId();
            this.collectionTitle = collection.getTitle();
            this.memberId = collection.getMember().getId();
            this.memberAccountId = collection.getMember().getAccountId();
            this.thumbnailUrl = collection.getFirstThumbnail();

            if (!followingMembers.isEmpty()) {
                findRelatedMember(followingMembers, collection.getFollowingMembers());
            }
        }

        private void findRelatedMember(List<Long> followingMembers, List<FollowedCollection> followedCollections) {
            followedCollections.stream()
                    .filter(followedCollection -> followingMembers.contains(followedCollection.getMember().getId()))
                    .max(Comparator.comparing(followedCollection -> followedCollection.getMember().getPopularity()))
                    .ifPresent(followedCollection -> setFollowingMemberNicknameAndNumber(
                            followedCollection.getMember().getNickname(), followedCollections.size()));
        }

        public void setFollowingMemberNicknameAndNumber(String nickname, int size) {
            this.followingMemberNickname = nickname;
            this.followingCount = size - 1;
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
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateCollectionVO {

        private String title;
        private String content;
        private PublicOfCollectionStatus publicOfCollectionStatus;
    }
}
