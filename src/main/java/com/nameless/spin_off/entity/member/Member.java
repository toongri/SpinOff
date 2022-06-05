package com.nameless.spin_off.entity.member;

import com.nameless.spin_off.dto.MemberDto.MemberBuilder;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.help.Complain;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.movie.FollowedMovie;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.help.ComplainStatus;
import com.nameless.spin_off.enums.help.ContentTypeStatus;
import com.nameless.spin_off.enums.member.AuthorityOfMemberStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.member.MemberCondition;
import com.nameless.spin_off.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.exception.member.*;
import com.nameless.spin_off.exception.sign.*;
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

import static com.nameless.spin_off.enums.member.MemberCondition.*;
import static com.nameless.spin_off.enums.member.MemberScoreEnum.MEMBER_FOLLOW;

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
    private String website;
    private String googleEmail;
    private String naverEmail;
    private String kakaoEmail;
    private String refreshToken;
    private Double popularity;

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
            throw new AlreadyFollowedHashtagException(ErrorEnum.ALREADY_FOLLOWED_HASHTAG);
        }
        hashtag.addFollowingMembers(followedHashtag);

        return followedHashtag.getId();
    }

    public Long addFollowedMovie(Movie movie) throws AlreadyFollowedMovieException {
        FollowedMovie followedMovie = FollowedMovie.createFollowedMovie(this, movie);

        if (!this.followedMovies.add(followedMovie)) {
            throw new AlreadyFollowedMovieException(ErrorEnum.ALREADY_FOLLOWED_MOVIE);
        }
        movie.addFollowingMembers(followedMovie);

        return followedMovie.getId();
    }

    public Long addFollowedMember(Member followedMember) throws AlreadyFollowedMemberException {
        FollowedMember newFollowedMember = FollowedMember.createFollowedMember(this, followedMember);

        if (!this.followedMembers.add(newFollowedMember)) {
            throw new AlreadyFollowedMemberException(ErrorEnum.ALREADY_FOLLOWED_MEMBER);
        }

        followedMember.addFollowingMember(newFollowedMember);

        return newFollowedMember.getId();
    }

    public Long addBlockedMember(Member blockedMember, BlockedMemberStatus blockedMemberStatus) throws AlreadyBlockedMemberException {
        BlockedMember newBlockedMember = BlockedMember.createBlockedMember(this, blockedMember, blockedMemberStatus);

        if (!this.blockedMembers.add(newBlockedMember)) {
            throw new AlreadyBlockedMemberException(ErrorEnum.ALREADY_BLOCKED_MEMBER);
        }
        blockedMember.addBlockingMember(newBlockedMember);

        return newBlockedMember.getId();
    }

    public void addBlockingMember(BlockedMember blockingMember) {
        blockingMembers.add(blockingMember);
    }

    public void addFollowingMember(FollowedMember followingMember) {
        followingMembers.add(followingMember);
    }

    public void addFollowedCollection(FollowedCollection followedCollection) {
        followedCollections.add(followedCollection);
    }

    public Long addComplain(Member member, Long contentId, ContentTypeStatus contentTypeStatus,
                            ComplainStatus complainStatus) throws AlreadyComplainException {
        Complain complain = Complain.createComplain(member, this, contentId, contentTypeStatus, complainStatus);

        if (!this.complains.add(complain)) {
            throw new AlreadyComplainException(ErrorEnum.ALREADY_COMPLAIN);
        }

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
                                      String googleEmail, String naverEmail, String kakaoEmail) {

        Member member = new Member();
        member.updateAccountId(accountId);
        member.updateAccountPw(accountPw);
        member.updateName(name);
        member.updateBirth(birth);
        member.updatePhoneNumber(phoneNumber);
        member.updateEmail(email);
        member.updateNickname(nickname);
        member.addRolesToUser();
        member.updateGoogleEmail(googleEmail);
        member.updateNaverEmail(naverEmail);
        member.updateKakaoEmail(kakaoEmail);
        member.updatePopularityZero();

        return member;
    }

    public static Member createMember(Long id) {
        Member member = new Member();
        member.updateId(id);

        return member;
    }

    public static MemberBuilder buildMember() {
        return new MemberBuilder();
    }

    //==수정 메소드==//
    public void updateBio(String bio) {
        this.bio = bio;
    }
    private void updateId(Long id) {
        this.id = id;
    }
    public void updateNickname(String nickname) {
        isCorrectNickname(nickname);
        this.nickname = nickname;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void updateWebsite(String website) {
        this.website = website;
    }

    public void updateAccountId(String accountId) {
        isCorrectAccountId(accountId);
        this.accountId = accountId;
    }

    public void updateAccountPw(String accountPw) {
        this.accountPw = accountPw;
    }

    public void updateEmail(String email) {
        isCorrectEmail(email);
        this.email = email;
    }

    public void updatePhoneNumber(String phoneNumber) {
        isCorrectPhoneNumber(phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    public void updateBirth(LocalDate birth) {
        this.birth = birth;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePopularityZero() {
        popularity = 0.0;
    }

    public void updatePopularity() {
        popularity = blockingMembers.size() + complains.size() + executeFollowScore();
    }

    public void addRole(AuthorityOfMemberStatus role) {
        this.roles.add(role);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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

    //==비즈니스 로직==//

    private void isCorrectEmail(String email) {
        if (EMAIL.isNotCorrect(email)) {
            throw new IncorrectEmailException(ErrorEnum.INCORRECT_EMAIL);
        }
    }

    private void isCorrectAccountId(String accountId) {
        if (MemberCondition.ACCOUNT_ID.isNotCorrect(accountId)) {
            throw new IncorrectAccountIdException(ErrorEnum.INCORRECT_ACCOUNT_ID);
        }
    }

    private void isCorrectAccountPw(String accountPw) {
        if (MemberCondition.ACCOUNT_PW.isNotCorrect(accountPw)) {
            throw new IncorrectAccountPwException(ErrorEnum.INCORRECT_ACCOUNT_PW);
        }
    }

    private void isCorrectNickname(String nickname) {
        if (NICKNAME.isNotCorrect(nickname)) {
            throw new IncorrectNicknameException(ErrorEnum.INCORRECT_NICKNAME);
        }
    }

    private void isCorrectPhoneNumber(String phoneNumber) {
        if (CELL_PHONE.isNotCorrect(phoneNumber)) {
            throw new IncorrectPhoneNumberException(ErrorEnum.INCORRECT_PHONE_NUMBER);
        }
    }

    public Double executeFollowScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        FollowedMember followedMember ;
        int j = 0, i = followingMembers.size() - 1;
        double result = 0, total = 0;

        while (i > -1) {
            followedMember = followingMembers.get(i);
            if (isInTimeFollowingMember(currentTime, followedMember, j)) {
                result += 1;
                i--;
            } else {
                if (j == MEMBER_FOLLOW.getScores().size() - 1) {
                    break;
                }
                total += MEMBER_FOLLOW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        return total + MEMBER_FOLLOW.getScores().get(j) * result;
    }

    //==조회 로직==//
    private boolean isInTimeFollowingMember(LocalDateTime currentTime, FollowedMember followingMember, int j) {
        return ChronoUnit.DAYS
                .between(followingMember.getCreatedDate(), currentTime) >= MEMBER_FOLLOW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(followingMember.getCreatedDate(), currentTime) < MEMBER_FOLLOW.getDays().get(j + 1);
    }
}
