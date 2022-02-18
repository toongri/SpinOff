package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.CreateMemberVO;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.FollowedCollection;
import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import com.nameless.spin_off.entity.help.Complain;
import com.nameless.spin_off.entity.help.ComplainStatus;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.movie.FollowedMovie;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.*;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.nameless.spin_off.StaticVariable.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    @NotNull
    private String accountId;
    @NotNull
    private String accountPw;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phoneNumber;
    private String email;
    private String profileImg;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<FollowedHashtag> followedHashtags = new HashSet<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<FollowedMovie> followedMovies = new HashSet<>();

    @OneToMany(mappedBy = "followingMember", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<FollowedMember> followedMembers = new HashSet<>();

    @OneToMany(mappedBy = "blockingMember", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<BlockedMember> blockedMembers = new HashSet<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<FollowedCollection> followedCollections = new HashSet<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<FollowedMember> followingMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private Set<BlockedMember> blockingMembers = new HashSet<>();

    @OneToMany(mappedBy = "complainedMember", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Complain> complains = new HashSet<>();

    private Long complainCount;
    private Long blockCount;
    private Double followScore;
    private Double popularity;

    //==연관관계 메소드==//
    public Long addFollowedHashtag(Hashtag hashtag) throws AlreadyFollowedHashtagException {
        FollowedHashtag followedHashtag = FollowedHashtag.createFollowedHashtag(hashtag);
        followedHashtag.updateMember(this);

        if (!this.followedHashtags.add(followedHashtag)) {
            throw new AlreadyFollowedHashtagException();
        }
        hashtag.addFollowingMembers(followedHashtag);

        return followedHashtag.getId();
    }

    public Long addFollowedMovie(Movie movie) throws AlreadyFollowedMovieException {
        FollowedMovie followedMovie = FollowedMovie.createFollowedMovie(movie);
        followedMovie.updateMember(this);

        if (!this.followedMovies.add(followedMovie)) {
            throw new AlreadyFollowedMovieException();
        }
        movie.addFollowingMembers(followedMovie);

        return followedMovie.getId();
    }

    public Long addFollowedMember(Member followedMember) throws AlreadyFollowedMemberException {
        FollowedMember newFollowedMember = FollowedMember.createFollowedMember(followedMember);
        newFollowedMember.updateFollowingMember(this);

        if (!this.followedMembers.add(newFollowedMember)) {
            throw new AlreadyFollowedMemberException();
        }
        followedMember.addFollowingMember(newFollowedMember);

        return newFollowedMember.getId();
    }

    public Long addBlockedMember(Member blockedMember, BlockedMemberStatus blockedMemberStatus) throws AlreadyBlockedMemberException {
        BlockedMember newBlockedMember = BlockedMember.createBlockedMember(blockedMember, blockedMemberStatus);
        newBlockedMember.updateBlockingMember(this);

        if (!this.blockedMembers.add(newBlockedMember)) {
            throw new AlreadyBlockedMemberException();
        }
        blockedMember.addBlockingMember(newBlockedMember);

        return newBlockedMember.getId();
    }

    public void addBlockingMember(BlockedMember blockingMember) {
        blockingMembers.add(blockingMember);
        updateBlockCount();
    }

    public void addFollowingMember(FollowedMember followingMember) {
        updateFollowScore();
        followingMembers.add(followingMember);
    }

    public void addFollowedCollection(FollowedCollection followedCollection) {
        followedCollections.add(followedCollection);
    }

    public Long addComplain(Member member, Post post, Collection collection, ComplainStatus complainStatus) throws AlreadyComplainException {
        Complain complain = Complain.createComplain(member, post, collection, complainStatus);

        complain.updateComplainedMember(this);

        if (!this.complains.add(complain)) {
            throw new AlreadyComplainException();
        }
        updateComplainCount();

        return complain.getId();
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
        member.updateCountToZero();

        return member;
    }

    public static Member createMemberByCreateVO(CreateMemberVO createMemberVO) {

        Member member = new Member();

        member.updateAccountId(createMemberVO.getAccountId());
        member.updateAccountPw(createMemberVO.getAccountPw());
        member.updateBirth(createMemberVO.getBirth());
        member.updateEmail(createMemberVO.getEmail());
        member.updateName(createMemberVO.getName());
        member.updateNickname(createMemberVO.getNickname());
        member.updateCountToZero();

        if (createMemberVO.getProfileImg() != null) {
            member.updateProfileImg(createMemberVO.getProfileImg());
        }
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

    public void updatePopularity() {
        this.popularity = followScore - complainCount - blockCount;
    }

    public void updateCountToZero() {
        complainCount = 0L;
        blockCount = 0L;
        followScore = 0.0;
        popularity = 0.0;
    }

    public void updateBlockCount() {
        blockCount = (long)blockingMembers.size();
        updatePopularity();
    }

    public void updateComplainCount() {
        complainCount = (long)complains.size();
        updatePopularity();
    }
    //==비즈니스 로직==//
    public void updateFollowScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        FollowedMember followedMember ;
        int j = 0, i = followingMembers.size() - 1;
        double result = 0, total = 1 * MEMBER_FOLLOW_COUNT_SCORES.get(0);

        while (i > -1) {
            followedMember = followingMembers.get(i);
            if (isInTimeFollowingMember(currentTime, followedMember, j)) {
                result += 1;
                i--;
            } else {
                if (j == MEMBER_FOLLOW_COUNT_SCORES.size() - 1) {
                    break;
                }
                total += MEMBER_FOLLOW_COUNT_SCORES.get(j) * result;
                result = 0;
                j++;
            }
        }
        followScore = total + MEMBER_FOLLOW_COUNT_SCORES.get(j) * result;

        updatePopularity();
    }

    //==조회 로직==//
    private boolean isInTimeFollowingMember(LocalDateTime currentTime, FollowedMember followingMember, int j) {
        return ChronoUnit.DAYS
                .between(followingMember.getCreatedDate(), currentTime) >= MEMBER_FOLLOW_COUNT_DAYS.get(j) &&
                ChronoUnit.DAYS
                        .between(followingMember.getCreatedDate(), currentTime) < MEMBER_FOLLOW_COUNT_DAYS.get(j + 1);
    }
}
