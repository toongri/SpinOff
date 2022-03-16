package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.dto.MemberDto.MemberBuilder;
import com.nameless.spin_off.dto.MemberDto.MemberRegisterRequestDto;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.enums.help.ComplainStatus;
import com.nameless.spin_off.entity.enums.help.ContentTypeStatus;
import com.nameless.spin_off.entity.enums.member.AuthorityOfMemberStatus;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.help.Complain;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.movie.FollowedMovie;
import com.nameless.spin_off.entity.movie.Movie;
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

import static com.nameless.spin_off.entity.enums.member.MemberScoreEnum.MEMBER__FOLLOW;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String bio;
    private Long complainCount;
    private Long blockCount;
    private Double followScore;
    private Double popularity;
    private String googleEmail;
    private String naverEmail;
    private String kakaoEmail;
    private String refreshToken;
    private Boolean emailAuth;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @Column(name = "authority_of_member_status")
    @NotNull
    private Set<AuthorityOfMemberStatus> roles = new HashSet<>();

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<SearchedByMember> searches = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    //==연관관계 메소드==//
    public void addPost(Post post) {
        posts.add(post);
        post.updateMember(this);
    }

    public Long addFollowedHashtag(Hashtag hashtag) throws AlreadyFollowedHashtagException {
        FollowedHashtag followedHashtag = FollowedHashtag.createFollowedHashtag(this, hashtag);

        if (!this.followedHashtags.add(followedHashtag)) {
            throw new AlreadyFollowedHashtagException();
        }
        hashtag.addFollowingMembers(followedHashtag);

        return followedHashtag.getId();
    }

    public Long addFollowedMovie(Movie movie) throws AlreadyFollowedMovieException {
        FollowedMovie followedMovie = FollowedMovie.createFollowedMovie(this, movie);

        if (!this.followedMovies.add(followedMovie)) {
            throw new AlreadyFollowedMovieException();
        }
        movie.addFollowingMembers(followedMovie);

        return followedMovie.getId();
    }

    public Long addFollowedMember(Member followedMember) throws AlreadyFollowedMemberException {
        FollowedMember newFollowedMember = FollowedMember.createFollowedMember(this, followedMember);

        if (!this.followedMembers.add(newFollowedMember)) {
            throw new AlreadyFollowedMemberException();
        }
        followedMember.addFollowingMember(newFollowedMember);

        return newFollowedMember.getId();
    }

    public Long addBlockedMember(Member blockedMember, BlockedMemberStatus blockedMemberStatus) throws AlreadyBlockedMemberException {
        BlockedMember newBlockedMember = BlockedMember.createBlockedMember(this, blockedMember, blockedMemberStatus);

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

    public Long addComplain(Member member, Long contentId, ContentTypeStatus contentTypeStatus,
                            ComplainStatus complainStatus) throws AlreadyComplainException {
        Complain complain = Complain.createComplain(member, this, contentId, contentTypeStatus, complainStatus);

        if (!this.complains.add(complain)) {
            throw new AlreadyComplainException();
        }
        updateComplainCount();

        return complain.getId();
    }

    public Long addSearch(String search, SearchedByMemberStatus searchedByMemberStatus) {
        SearchedByMember searchedByMember = SearchedByMember.createMemberSearch(this, search, searchedByMemberStatus);

        searches.add(searchedByMember);

        return searchedByMember.getId();
    }

    //==생성 메소드==//
    public static Member createMember(String accountId, String accountPw, String nickname,
                                      String name, LocalDate birth, String phoneNumber, String email,
                                      String googleEmail, String naverEmail, String kakaoEmail, Boolean emailAuth) {

        Member member = new Member();
        member.updateAccountId(accountId);
        member.updateAccountPw(accountPw);
        member.updateName(name);
        member.updateBirth(birth);
        member.updatePhoneNumber(phoneNumber);
        member.updateEmail(email);
        member.updateNickname(nickname);
        member.updateCountToZero();
        member.addRolesToUser();
        member.updateGoogleEmail(googleEmail);
        member.updateNaverEmail(naverEmail);
        member.updateKakaoEmail(kakaoEmail);
        member.updateEmailAuth(emailAuth);

        return member;
    }


    public static Member createMemberByCreateVO(MemberRegisterRequestDto memberRegisterRequestDto) {

        return Member.buildMember()
                .setNickname(memberRegisterRequestDto.getNickname())
                .setEmail(memberRegisterRequestDto.getEmail())
                .setAccountId(memberRegisterRequestDto.getAccountId())
                .setAccountPw(memberRegisterRequestDto.getAccountPw())
                .setBirth(memberRegisterRequestDto.getBirth())
                .setName(memberRegisterRequestDto.getName())
                .build();
    }

    public static MemberBuilder buildMember() {
        return new MemberBuilder();
    }

    //==수정 메소드==//
    public void updateBio(String bio) {
        this.bio = bio;
    }
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

    public void addRole(AuthorityOfMemberStatus role) {
        this.roles.add(role);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateEmailAuth(Boolean emailAuth) {
        this.emailAuth = emailAuth;
    }

    public Member updateGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
        return this;
    }
    public Member updateNaverEmail(String naverEmail) {
        this.naverEmail = naverEmail;
        return this;
    }
    public Member updateKakaoEmail(String kakaoEmail) {
        this.kakaoEmail = kakaoEmail;
        return this;
    }

    public void addRolesToUser() {
        this.roles.add(AuthorityOfMemberStatus.C);
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
        double result = 0, total = MEMBER__FOLLOW.getLatestScore();

        while (i > -1) {
            followedMember = followingMembers.get(i);
            if (isInTimeFollowingMember(currentTime, followedMember, j)) {
                result += 1;
                i--;
            } else {
                if (j == MEMBER__FOLLOW.getScores().size() - 1) {
                    break;
                }
                total += MEMBER__FOLLOW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        followScore = total + MEMBER__FOLLOW.getScores().get(j) * result;

        updatePopularity();
    }

    //==조회 로직==//
    private boolean isInTimeFollowingMember(LocalDateTime currentTime, FollowedMember followingMember, int j) {
        return ChronoUnit.DAYS
                .between(followingMember.getCreatedDate(), currentTime) >= MEMBER__FOLLOW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(followingMember.getCreatedDate(), currentTime) < MEMBER__FOLLOW.getDays().get(j + 1);
    }
}
