package com.nameless.spin_off.dto;

import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CollectionDto {

    @Data
    @NoArgsConstructor
    public static class MyPageCollectionDto {

        @ApiModelProperty(
                value = "컬렉션 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "컬렉션 제목",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
        private String title;

        @ApiModelProperty(
                value = "컬렉션 썸네일",
                example = "[\"www.naver.com\",\"www.kakao.com\",\"www.google.com\",\"www.nate.com\"]")
        private List<String> thumbnailUrls = new ArrayList<>();

        @QueryProjection
        public MyPageCollectionDto(Long id, String title,
                                   String thumbnail1, String thumbnail2, String thumbnail3, String thumbnail4) {

            this.id = id;
            this.title = title;
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
    }

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
    public static class PostThumbnailsCollectionDto {
        private Long collectionId;
        private String postThumbnail;

        @QueryProjection
        public PostThumbnailsCollectionDto(Long collectionId, String postThumbnail) {
            this.collectionId = collectionId;
            this.postThumbnail = postThumbnail;
        }
    }

    @Data
    @NoArgsConstructor
    public static class PostInCollectionDto {

        @ApiModelProperty(
                value = "글 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "글 제목",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
        private String title;

        @ApiModelProperty(
                value = "글 썸네일 주소",
                example = "www.naver.com")
        private String thumbnail;

        @ApiModelProperty(
                value = "글 공개 범위",
                example = "A")
        private PublicOfCollectionStatus publicOfCollectionStatus;

        @ApiModelProperty(
                value = "콜렉팅 유무",
                example = "false")
        private boolean isCollected;

        @QueryProjection
        public PostInCollectionDto(Long id, String title, PublicOfCollectionStatus publicOfCollectionStatus, String thumbnail) {
            this.id = id;
            this.title = title;
            this.thumbnail = thumbnail;
            this.publicOfCollectionStatus = publicOfCollectionStatus;
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
        private Long followingCount;

        @QueryProjection
        public SearchCollectionDto(Long collectionId, String collectionTitle, Long memberId, String memberAccountId,
                                   String thumbnail1, String thumbnail2, String thumbnail3, String thumbnail4,
                                   Long followedMemberCount) {

            this.collectionId = collectionId;
            this.collectionTitle = collectionTitle;
            this.memberId = memberId;
            this.memberAccountId = memberAccountId;
            this.followingCount = followedMemberCount;
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
        private Long followingCount;

        @QueryProjection
        public SearchAllCollectionDto(Long collectionId, String collectionTitle, Long memberId, String memberAccountId,
                                      String thumbnailUrl, Long followedMemberCount) {
            this.collectionId = collectionId;
            this.collectionTitle = collectionTitle;
            this.memberId = memberId;
            this.memberAccountId = memberAccountId;
            this.thumbnailUrl = thumbnailUrl;
            this.followingCount = followedMemberCount;
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

        @ApiModelProperty(
                value = "컬렉션 id",
                example = "123")
        private Long collectionId;
        @ApiModelProperty(
                value = "컬렉션 제목",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
        private String collectionTitle;
        @ApiModelProperty(
                value = "멤버 id",
                example = "123")
        private Long memberId;
        @ApiModelProperty(
                value = "멤버 닉네임",
                example = "퉁그리")
        private String memberNickname;
        @ApiModelProperty(
                value = "컬렉션 썸네일",
                example = "[\"www.naver.com\",\"www.kakao.com\",\"www.google.com\",\"www.nate.com\"]")
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

        @ApiModelProperty(
                value = "컬렉션 제목",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private String title;

        @ApiModelProperty(
                value = "컬렉션 본문",
                required = true,
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스",
                dataType = "String")
        private String content;

        @ApiModelProperty(
                value = "컬렉션 공개범위",
                required = true,
                example = "A",
                dataType = "PublicOfCollectionStatus")
        private PublicOfCollectionStatus publicOfCollectionStatus;
    }
}
