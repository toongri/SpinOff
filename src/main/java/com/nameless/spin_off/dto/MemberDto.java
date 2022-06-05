package com.nameless.spin_off.dto;


import com.nameless.spin_off.dto.PostDto.ThumbnailMemberDto;
import com.nameless.spin_off.entity.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MemberDto {

    @Data
    @AllArgsConstructor
    public static class MemberInfoDto {

        @ApiModelProperty(
                value = "멤버 닉네임",
                example = "toongri")
        private String nickname;

        @ApiModelProperty(
                value = "프로필 url",
                example = "fhjdklebneke")
        private String profileUrl;

        @ApiModelProperty(
                value = "프로필 id",
                example = "fhjdklebneke")
        private String accountId;

        @ApiModelProperty(
                value = "프로필 웹사이트url",
                example = "www.nvaer.com")
        private String website;

        @ApiModelProperty(
                value = "프로필 소개",
                example = "fhjdklebneke")
        private String bio;
    }


    @Data
    public static class MovieMemberDto {

        @ApiModelProperty(
                value = "멤버 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "멤버 닉네임",
                example = "toongri")
        private String nickname;

        @ApiModelProperty(
                value = "프로필 url",
                example = "fhjdklebneke")
        private String profileUrl;

        @QueryProjection
        public MovieMemberDto(Long id, String nickname, String profileUrl) {
            this.id = id;
            this.nickname = nickname;
            this.profileUrl = profileUrl;
        }
    }

    @Data
    public static class ReadMemberDto {

        @ApiModelProperty(
                value = "멤버 id",
                example = "123")
        private Long id;

        @ApiModelProperty(
                value = "멤버 닉네임",
                example = "toongri")
        private String nickname;

        @ApiModelProperty(
                value = "멤버 계정 아이디",
                example = "jhkim03284")
        private String accountId;

        @ApiModelProperty(
                value = "프로필 url",
                example = "fhjdklebneke")
        private String profileUrl;

        @ApiModelProperty(
                value = "프로필 설명",
                example = "스프링부트와 aws로 혼자 구현하는 웹 서비스")
        private String bio;

        @ApiModelProperty(
                value = "글 갯수",
                example = "123")
        private Long postSize;

        @ApiModelProperty(
                value = "팔로워 갯수",
                example = "123")
        private Long followerSize;

        @ApiModelProperty(
                value = "팔로잉 갯수",
                example = "123")
        private Long followingSize;

        @ApiModelProperty(
                value = "팔로우 여부",
                example = "false")
        private boolean isFollowed;

        @ApiModelProperty(
                value = "권한 여부",
                example = "false")
        private boolean isAdmin;

        @ApiModelProperty(
                value = "차단 여부",
                example = "false")
        private boolean isBlocking;

        @ApiModelProperty(
                value = "차단 당함 여부",
                example = "false")
        private boolean isBlocked;

        @QueryProjection
        public ReadMemberDto(Long id, String nickname, String accountId, String profileUrl, String bio,
                             Long postSize, Long followerSize, Long followingSize) {
            this.id = id;
            this.nickname = nickname;
            this.accountId = accountId;
            this.profileUrl = profileUrl;
            this.bio = bio;
            this.postSize = postSize;
            this.followerSize = followerSize;
            this.followingSize = followingSize;
        }

        @QueryProjection
        public ReadMemberDto(Long id, String nickname, String accountId, String profileUrl, String bio, Long postSize) {
            this.id = id;
            this.nickname = nickname;
            this.accountId = accountId;
            this.profileUrl = profileUrl;
            this.bio = bio;
            this.postSize = postSize;
            this.followerSize = 0L;
            this.followingSize = 0L;
        }

        public ReadMemberDto() {
            this.isBlocked = true;
        }

        public void setAdmin(Long memberId, boolean isAdmin) {
            this.isAdmin = this.id.equals(memberId) || isAdmin;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentMemberDto {
        private Long memberId;
        private String profile;
        private String nickname;

        public CommentMemberDto(Member member) {
            this.memberId = member.getId();
            this.profile = member.getProfileImg();
            this.nickname = member.getNickname();
        }
    }

    @Data
    @NoArgsConstructor
    public static class MembersByContentDto {

        @ApiModelProperty(
                value = "멤버 id",
                example = "123")
        private Long memberId;

        @ApiModelProperty(
                value = "멤버 프로필 이미지 주소",
                example = "www.naver.com")
        private String profile;

        @ApiModelProperty(
                value = "멤버 닉네임",
                example = "퉁그리")
        private String nickname;

        @ApiModelProperty(
                value = "멤버 아이디",
                example = "spinoff2322")
        private String accountId;

        @ApiModelProperty(
                value = "팔로우 여부",
                example = "false")
        private boolean isFollowed;

        @ApiModelProperty(
                value = "소유 여부",
                example = "false")
        private boolean isOwn;

        @QueryProjection
        public MembersByContentDto(Long memberId, String profile, String nickname, String accountId) {
            this.memberId = memberId;
            this.profile = profile;
            this.nickname = nickname;
            this.accountId = accountId;
        }

        public void setFollowedAndOwn(boolean isFollowed, Long memberId) {
            this.isFollowed = isFollowed;
            this.isOwn = this.memberId.equals(memberId);
        }
    }

    @Data
    @NoArgsConstructor
    public static class ContentMemberDto {

        @ApiModelProperty(
                value = "멤버 id",
                example = "123")
        private Long memberId;

        @ApiModelProperty(
                value = "멤버 프로필 이미지 주소",
                example = "www.naver.com")
        private String profile;

        @ApiModelProperty(
                value = "멤버 닉네임",
                example = "퉁그리")
        private String nickname;

        @ApiModelProperty(
                value = "멤버 아이디",
                example = "spinoff2322")
        private String accountId;

        @ApiModelProperty(
                value = "팔로우 여부",
                example = "false")
        private boolean isFollowed;

        public ContentMemberDto(Long memberId, String profile, String nickname, String accountId) {
            this.memberId = memberId;
            this.profile = profile;
            this.nickname = nickname;
            this.accountId = accountId;
        }

        public void setFollowed(boolean isFollowed) {
            this.isFollowed = isFollowed;
        }
    }

    @Data
    @NoArgsConstructor
    public static class EmailAuthRequestDto {

        @ApiModelProperty(
                value = "이메일 정보",
                required = true,
                example = "spinoff@naver.com",
                dataType = "string")
        private String email;

        @ApiModelProperty(
                value = "이메일 인증 토큰",
                required = true,
                example = "dsklsdk32930",
                dataType = "string")
        private String authToken;

        public EmailAuthRequestDto(String email, String authToken) {
            this.email = email;
            this.authToken = authToken;
        }
    }

    @Data
    @NoArgsConstructor
    public static class EmailLinkageUpdateRequestDto {
        private String email;
        private String accountId;

        public EmailLinkageUpdateRequestDto(String email, String accountId) {
            this.email = email;
            this.accountId = accountId;
        }
    }

    @Data
    @NoArgsConstructor
    public static class EmailLinkageCheckRequestDto {

        @ApiModelProperty(
                value = "이메일 정보",
                required = true,
                example = "spinoff@naver.com",
                dataType = "string")
        private String email;

        @ApiModelProperty(
                value = "계정 아이디",
                required = true,
                example = "spinof2321",
                dataType = "string")
        private String accountId;

        @ApiModelProperty(
                value = "이메일 인증 토큰 정보",
                required = true,
                example = "dfldkfbnk1232",
                dataType = "string")
        private String authToken;

        public EmailLinkageCheckRequestDto(String email, String accountId, String authToken) {
            this.email = email;
            this.accountId = accountId;
            this.authToken = authToken;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SocialMemberDto {
        private String email;
        private String name;
        private String picture;

        @Builder
        public SocialMemberDto(String email, String name, String picture) {
            this.email = email;
            this.name = name;
            this.picture = picture;
        }
    }

    @Data
    @AllArgsConstructor
    public static class TokenResponseDto {
        String accessToken;
        String refreshToken;
    }

    @Data
    @AllArgsConstructor
    public static class OauthResponseDto {
        Boolean isSuccess;
        String code;
        String message;
        TokenResponseDto data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenRequestDto {

        @ApiModelProperty(
                value = "엑세스토큰",
                required = true,
                example = "spinoff2323",
                dataType = "string")
        String accessToken;

        @ApiModelProperty(
                value = "리프레쉬토큰",
                required = true,
                example = "spinoff2323",
                dataType = "string")
        String refreshToken;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberLoginRequestDto {

        @ApiModelProperty(
                value = "계정아이디",
                required = true,
                example = "spinoff2323",
                dataType = "string")
        private String accountId;

        @ApiModelProperty(
                value = "계정비밀번호",
                required = true,
                example = "spino34223",
                dataType = "string")
        private String accountPw;
    }

    @Data
    @AllArgsConstructor
    public static class MemberLoginResponseDto {
        private Long id;
        private String token;
        private String refreshToken;
    }

    @Data
    @AllArgsConstructor
    public static class SocialLoginResponseDto {
        private boolean isRegister;
        private Long id;
        private String token;
        private String refreshToken;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class MemberRegisterResponseDto {
        private Long id;
        private String accountId;
    }

    @Data
    @NoArgsConstructor
    public static class SearchAllMemberDto {
        private Long id;
        private String profileImg;
        private String nickname;
        private String accountId;

        @QueryProjection
        public SearchAllMemberDto(Long id, String profileImg, String nickname, String accountId) {
            this.id = id;
            this.profileImg = profileImg;
            this.nickname = nickname;
            this.accountId = accountId;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchMemberDto {
        private Long memberId;
        private String profileImg;
        private String nickname;
        private String accountId;
        private String bio;
        private String followingMemberNickname;
        private int followingCount;
        private List<String> thumbnailUrls = new ArrayList<>();

        @QueryProjection
        public SearchMemberDto(Long memberId, String profileImg, String nickname, String accountId, String bio,
                               Long followingCount) {

            this.memberId = memberId;
            this.profileImg = profileImg;
            this.nickname = nickname;
            this.accountId = accountId;
            this.bio = bio;
            this.followingCount = followingCount.intValue();
        }

        public void setFollowingMemberAndThumbnails(List<FollowingMemberMemberDto> followingMembers,
                                                    List<ThumbnailMemberDto> thumbnails) {
            if (followingMembers != null) {
                followingMembers.stream()
                        .max(Comparator.comparing(
                                m -> m.followingMemberPopularity))
                        .ifPresent(m -> setFollowingMemberNickname(m.getFollowingMemberNickname()));
            }

            if (thumbnails != null) {
                setThumbnails(thumbnails);
            }
        }

        public void setFollowingMemberNickname(String nickname) {
            this.followingMemberNickname = nickname;
            this.followingCount -= 1;
        }

        private void setThumbnails(List<ThumbnailMemberDto> thumbnails) {
            int memberPostIndex = thumbnails.size() - 1;
            while (memberPostIndex >= 0) {
                thumbnailUrls.add(thumbnails.get(memberPostIndex).getThumbnail());
                if (thumbnailUrls.size() == 4) {
                    break;
                }
                memberPostIndex--;
            }
            if (thumbnailUrls.size() == 3) {
                thumbnailUrls.remove(2);
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class FollowingMemberMemberDto {
        private Long memberId;
        private Long followingMemberId;
        private Double followingMemberPopularity;
        private String followingMemberNickname;

        @QueryProjection
        public FollowingMemberMemberDto(Long memberId, Long followingMemberId,
                                        Double followingMemberPopularity, String followingMemberNickname) {
            this.memberId = memberId;
            this.followingMemberId = followingMemberId;
            this.followingMemberPopularity = followingMemberPopularity;
            this.followingMemberNickname = followingMemberNickname;
        }
    }

    @Data
    @NoArgsConstructor
    public static class RelatedSearchMemberDto {
        private Long id;
        private String profileImg;
        private String nickname;
        private String accountId;

        @QueryProjection
        public RelatedSearchMemberDto(Long id, String profileImg, String nickname, String accountId) {
            this.id = id;
            this.profileImg = profileImg;
            this.nickname = nickname;
            this.accountId = accountId;
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberRegisterRequestDto {

        @ApiModelProperty(
                value = "계정 id",
                required = true,
                example = "spinoff",
                dataType = "string")
        private String accountId;

        @ApiModelProperty(
                value = "계정 pw",
                required = true,
                example = "dfd9232",
                dataType = "string")
        private String accountPw;

        @ApiModelProperty(
                value = "이름",
                required = true,
                example = "김준형",
                dataType = "string")
        private String name;

        @ApiModelProperty(
                value = "닉네임",
                required = true,
                example = "퉁그리",
                dataType = "string")
        private String nickname;

        @ApiModelProperty(
                value = "생일",
                required = true,
                example = "2019-05-05",
                dataType = "string")
        private LocalDate birth;

        @ApiModelProperty(
                value = "이메일",
                required = true,
                example = "spinoff@naver.com",
                dataType = "string")
        private String email;

        @ApiModelProperty(
                value = "전화번호",
                required = true,
                example = "01011111111",
                dataType = "string")
        private String cellphone;

        @ApiModelProperty(
                value = "이메일 인증코드",
                required = true,
                example = "dfdfd0922",
                dataType = "string")
        private String authToken;
    }

    public static class MemberBuilder {

        private String accountId;
        private String accountPw;
        private String name;
        private String nickname;
        private LocalDate birth;
        private String phoneNumber;
        private String email;
        private String googleEmail;
        private String kakaoEmail;
        private String naverEmail;

        public MemberBuilder setAccountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public MemberBuilder setAccountPw(String accountPw) {
            this.accountPw = accountPw;
            return this;
        }

        public MemberBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MemberBuilder setNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public MemberBuilder setBirth(LocalDate birth) {
            this.birth = birth;
            return this;
        }

        public MemberBuilder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public MemberBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public MemberBuilder setGoogleEmail(String googleEmail) {
            this.googleEmail = googleEmail;
            return this;
        }

        public MemberBuilder setNaverEmail(String naverEmail) {
            this.naverEmail = naverEmail;
            return this;
        }

        public MemberBuilder setKakaoEmail(String kakaoEmail) {
            this.kakaoEmail = kakaoEmail;
            return this;
        }

        public Member build() {
            return Member.createMember(accountId, accountPw, nickname,name, birth, phoneNumber, email,
                    googleEmail, naverEmail, kakaoEmail);
        }
    }
}
