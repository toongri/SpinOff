package com.nameless.spin_off.dto;


import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MemberDto {

    @Data
    @NoArgsConstructor
    public static class SearchPageAtAllMemberDto {
        private Long id;
        private String profileImg;
        private String nickname;
        private String accountId;

        @QueryProjection
        public SearchPageAtAllMemberDto(Long id, String profileImg, String nickname, String accountId) {
            this.id = id;
            this.profileImg = profileImg;
            this.nickname = nickname;
            this.accountId = accountId;
        }
    }

    @Data
    @NoArgsConstructor
    public static class SearchPageAtMemberMemberDto {
        private Long id;
        private String profileImg;
        private String nickname;
        private String accountId;
        private String bio;
        private String followingMemberNickname;
        private int followingNumber;
        private List<String> thumbnailUrls = new ArrayList<>();

        public SearchPageAtMemberMemberDto(Member member) {
            this.id = member.getId();
            this.profileImg = member.getProfileImg();
            this.nickname = member.getNickname();
            this.accountId = member.getAccountId();
            this.bio = member.getBio();
        }

        public SearchPageAtMemberMemberDto(Member member, List<Member> followingMembers) {
            this.id = member.getId();
            this.profileImg = member.getProfileImg();
            this.nickname = member.getNickname();
            this.accountId = member.getAccountId();
            this.bio = member.getBio();

            List<FollowedMember> followingMembers2 = member.getFollowingMembers();
            followingMembers2.stream()
                    .filter(followingMember -> followingMembers.contains(followingMember.getFollowingMember()))
                    .max(Comparator.comparing(followingMember -> followingMember.getFollowingMember().getPopularity()))
                    .ifPresent(followingMember -> setFollowingMemberNicknameAndNumber(
                            followingMember.getFollowingMember().getNickname(), followingMembers2.size()));
        }

        public void setFollowingMemberNicknameAndNumber(String nickname, int size) {
            this.followingMemberNickname = nickname;
            this.followingNumber = size - 1;
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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateMemberVO {

        private String accountId;
        private String accountPw;
        private String name;
        private String nickname;
        private LocalDate birth;
        private String email;

        private String profileImg;
    }

    public static class MemberBuilder {

        private String accountId;
        private String accountPw;
        private String name;
        private String nickname = null;
        private LocalDate birth;
        private String phoneNumber;
        private String email;
        private String profileImg = null;

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

        public MemberBuilder setProfileImg(String profileImg) {
            this.profileImg = profileImg;
            return this;
        }

        public Member build() {
            return Member.createMember(accountId, accountPw, nickname, profileImg, name, birth, phoneNumber, email);
        }
    }
}
