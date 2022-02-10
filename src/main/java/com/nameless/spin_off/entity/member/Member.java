package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Hashtag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String accountId;
    private String accountPw;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phoneNumber;
    private String email;
    private String profileImg;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FollowedHashtag> followedHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<FollowedMovie> followedMovies = new ArrayList<>();

    //==연관관계 메소드==//

    public void addFollowedHashtag(Hashtag hashtag) {
        FollowedHashtag followedHashtag = FollowedHashtag.createFollowedHashtag(hashtag);

        this.followedHashtags.add(followedHashtag);
        followedHashtag.updateMember(this);
    }

    public void addFollowedMovie(Movie movie) {
        FollowedMovie followedMovie = FollowedMovie.createFollowedMovie(movie);

        this.followedMovies.add(followedMovie);
        followedMovie.updateMember(this);
    }

    //==생성 메소드==//
    public static Member createMember(String accountId, String accountPw, String nickname, String profileImg,
                                      String name, LocalDate birth, String phoneNumber, String email) {

        Member member = new Member();
        member.updateAccountId(accountId);
        member.updateAccountPw(accountPw);
        member.updateName(name);
        member.updateBirth(birth);
        member.updatePhoneNumber(phoneNumber);
        member.updateEmail(email);
        member.updateNickname(nickname);
        member.updateProfileImg(profileImg);

        return member;

    }

    public static MemberDto.MemberBuilder buildMember() {
        return new MemberDto.MemberBuilder();
    }

    //==수정 메소드==//

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void updateAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void updateAccountPw(String accountPw) {
        this.accountPw = accountPw;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateBirth(LocalDate birth) {
        this.birth = birth;
    }

    public void updateName(String name) {
        this.name = name;
    }

    //==비즈니스 로직==//

    //==조회 로직==//


}
