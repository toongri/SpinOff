package com.nameless.spin_off.dto;

import com.nameless.spin_off.dto.MemberDto.ContentMemberDto;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CollectionDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectedPostsRequestDto {

        @ApiModelProperty(
                value = "글 id",
                example = "[1,2,3,4]")
        List<Long> postIds;
    }

    @Data
    @NoArgsConstructor
    public static class FollowCollectionDto {

        @ApiModelProperty(
                value = "컬렉션 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "멤버 accountId",
                example = "toongri")
        private String accountId;

        @ApiModelProperty(
                value = "컬렉션 제목",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
        private String title;

        @ApiModelProperty(
                value = "컬렉션 썸네일",
                example = "[\"www.naver.com\",\"www.kakao.com\",\"www.google.com\",\"www.nate.com\"]")
        private List<String> thumbnailUrls = new ArrayList<>();

        @ApiModelProperty(
                value = "컬렉션 팔로우 여부",
                example = "false")
        private boolean isFollowed;

        @ApiModelProperty(
                value = "컬렉션 소유 여부",
                example = "false")
        private boolean isOwn;

        @QueryProjection
        public FollowCollectionDto(Long id, String title, String accountId,
                                   String thumbnail1, String thumbnail2, String thumbnail3, String thumbnail4) {

            this.id = id;
            this.accountId = accountId;
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

        public void setFollowedAndOwn(boolean isFollowed, String accountId) {
            this.isFollowed = isFollowed;
            this.isOwn = this.accountId.equals(accountId);
        }
    }


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
    public static class OwnerIdAndPublicCollectionDto {
        private Long collectionOwnerId;
        private PublicOfCollectionStatus publicOfCollectionStatus;

        @QueryProjection
        public OwnerIdAndPublicCollectionDto(Long collectionOwnerId, PublicOfCollectionStatus publicOfCollectionStatus) {
            this.collectionOwnerId = collectionOwnerId;
            this.publicOfCollectionStatus = publicOfCollectionStatus;
        }
    }

    @Data
    public static class CollectionIdAndPublicCollectionDto {
        private Long collectionId;
        private PublicOfCollectionStatus publicOfCollectionStatus;

        @QueryProjection
        public CollectionIdAndPublicCollectionDto(Long collectionId, PublicOfCollectionStatus publicOfCollectionStatus) {
            this.collectionId = collectionId;
            this.publicOfCollectionStatus = publicOfCollectionStatus;
        }
    }

    @Data
    public static class CollectionIdAndOwnerIdAndPublicCollectionDto {
        private Long collectionId;
        private Long collectionOwnerId;
        private PublicOfCollectionStatus publicOfCollectionStatus;

        @QueryProjection
        public CollectionIdAndOwnerIdAndPublicCollectionDto(Long collectionId, Long collectionOwnerId,
                                                            PublicOfCollectionStatus publicOfCollectionStatus) {
            this.collectionId = collectionId;
            this.collectionOwnerId = collectionOwnerId;
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
    public static class ReadCollectionDto {
        @ApiModelProperty(
                value = "컬렉션 id",
                example = "123")
        private Long collectionId;
        private ContentMemberDto member;

        @ApiModelProperty(
                value = "글 제목",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
        private String collectionTitle;

        @ApiModelProperty(
                value = "컬렉션 생성 시간",
                example = "2022-03-31T09:31:47.021Z")
        private LocalDateTime createTime;

        @ApiModelProperty(
                value = "컬렉션 수정 시간",
                example = "2022-03-31T09:31:47.021Z")
        private LocalDateTime getLastModifiedDate;

        @ApiModelProperty(
                value = "컬렉션 내용",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
        private String collectionContent;

        @ApiModelProperty(
                value = "컬렉션 공개범위",
                example = "A")
        private PublicOfCollectionStatus publicOfCollectionStatus;

        @ApiModelProperty(
                value = "권한 여부",
                example = "false")
        private boolean hasAuth;

        @ApiModelProperty(
                value = "좋아요 갯수",
                example = "123")
        private Long likedSize;

        @ApiModelProperty(
                value = "댓글 갯수",
                example = "123")
        private Long commentSize;

        @ApiModelProperty(
                value = "팔로우 갯수",
                example = "123")
        private Long followedSize;

        @ApiModelProperty(
                value = "글 갯수",
                example = "123")
        private Long postSize;

        @ApiModelProperty(
                value = "좋아요 여부",
                example = "false")
        private boolean isLiked;

        public void setLiked(boolean isLiked) {
            this.isLiked = isLiked;
        }

        public void setAuth(Long memberId, boolean isAdmin) {
            this.hasAuth = this.member.getMemberId().equals(memberId) || isAdmin;
        }

        @QueryProjection
        public ReadCollectionDto(Long collectionId, Long memberId, String profile, String nickname, String accountId,
                           String collectionTitle, LocalDateTime createTime, LocalDateTime getLastModifiedDate,
                           String collectionContent, PublicOfCollectionStatus publicOfCollectionStatus,
                           Long likedSize, Long commentSize, Long followedSize, Long postSize) {

            this.collectionId = collectionId;
            this.member = new ContentMemberDto(memberId, profile, nickname, accountId);
            this.collectionTitle = collectionTitle;
            this.createTime = createTime;
            this.publicOfCollectionStatus = publicOfCollectionStatus;
            this.getLastModifiedDate = getLastModifiedDate;
            this.collectionContent = collectionContent;
            this.likedSize = likedSize;
            this.commentSize = commentSize;
            this.followedSize = followedSize;
            this.postSize = postSize;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CollectionRequestDto {

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
