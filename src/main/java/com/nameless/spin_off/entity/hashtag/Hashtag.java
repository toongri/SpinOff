package com.nameless.spin_off.entity.hashtag;

import com.nameless.spin_off.entity.enums.ContentsTimeEnum;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.hashtag.HashtagScoreEnum.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hashtag_id")
    private Long id;

    private String content;

    @OneToMany(mappedBy = "hashtag", fetch = FetchType.LAZY)
    private List<FollowedHashtag> followingMembers = new ArrayList<>();

    @OneToMany(mappedBy = "hashtag", fetch = FetchType.LAZY)
    private List<PostedHashtag> taggedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ViewedHashtagByIp> viewedHashtagByIps = new ArrayList<>();

    private Double postScore;
    private Double viewScore;
    private Double followScore;
    private Double popularity;

    //==연관관계 메소드==//
    private Long addViewedHashtagByIp(String ip) {
        ViewedHashtagByIp viewedHashtagByIp = ViewedHashtagByIp.createViewedHashtagByIp(ip, this);

        updateViewScore();
        this.viewedHashtagByIps.add(viewedHashtagByIp);

        return viewedHashtagByIp.getId();
    }

    public void addTaggedPosts(PostedHashtag postedHashtag) {
        updatePostScore();
        this.taggedPosts.add(postedHashtag);
    }

    public void addFollowingMembers(FollowedHashtag followedHashtag) {
        updateFollowScore();
        this.followingMembers.add(followedHashtag);
    }

    //==생성 메소드==//
    public static Hashtag createHashtag(String content) {

        Hashtag hashtag = new Hashtag();
        hashtag.updateContent(content);
        hashtag.updateCountToZero();

        return hashtag;
    }

    //==수정 메소드==//
    public void updateContent(String content) {
        this.content = content;
    }

    public void updatePopularity() {
        this.popularity = viewScore + postScore + followScore;
    }

    public void updateCountToZero() {
        viewScore = 0.0;
        postScore = 0.0;
        followScore = 0.0;
        popularity = 0.0;
    }

    //==비즈니스 로직==//
    public Long insertViewedHashtagByIp(String ip) {

        if (isNotAlreadyIpView(ip)) {
            return addViewedHashtagByIp(ip);
        } else {
            return -1L;
        }
    }

    public void updateFollowScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        FollowedHashtag followedHashtag ;
        int j = 0, i = followingMembers.size() - 1;
        double result = 0, total = HASHTAG_FOLLOW.getLatestScore();

        while (i > -1) {
            followedHashtag = followingMembers.get(i);
            if (isInTimeFollowedHashtag(currentTime, followedHashtag, j)) {
                result += 1;
                i--;
            } else {
                if (j == HASHTAG_FOLLOW.getScores().size() - 1) {
                    break;
                }
                total += HASHTAG_FOLLOW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        followScore = (total + HASHTAG_FOLLOW.getScores().get(j) * result) * HASHTAG_FOLLOW.getRate();

        updatePopularity();
    }

    public void updateViewScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        ViewedHashtagByIp viewedHashtagByIp ;
        int j = 0, i = viewedHashtagByIps.size() - 1;
        double result = 0, total = HASHTAG_VIEW.getLatestScore();

        while (i > -1) {
            viewedHashtagByIp = viewedHashtagByIps.get(i);
            if (isInTimeViewedHashtag(currentTime, viewedHashtagByIp, j)) {
                result += 1;
                i--;
            } else {
                if (j == HASHTAG_VIEW.getScores().size() - 1) {
                    break;
                }
                total += HASHTAG_VIEW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        viewScore = (total + HASHTAG_VIEW.getScores().get(j) * result) * HASHTAG_VIEW.getRate();

        updatePopularity();
    }

    public void updatePostScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        PostedHashtag postedHashtag ;
        int j = 0, i = taggedPosts.size() - 1;
        double result = 0, total = HASHTAG_POST.getLatestScore();

        while (i > -1) {
            postedHashtag = taggedPosts.get(i);
            if (isInTimePostedHashtag(currentTime, postedHashtag, j)) {
                result += 1;
                i--;
            } else {
                if (j == HASHTAG_POST.getScores().size() - 1) {
                    break;
                }
                total += HASHTAG_POST.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        postScore = (total + HASHTAG_POST.getScores().get(j) * result) * HASHTAG_POST.getRate();

        updatePopularity();
    }

    //==조회 로직==//
    public Boolean isNotAlreadyIpView(String ip) {

        LocalDateTime currentTime = LocalDateTime.now();

        List<ViewedHashtagByIp> views = viewedHashtagByIps.stream()
                .filter(viewedHashtagByIp -> viewedHashtagByIp.getIp().equals(ip))
                .collect(Collectors.toList());

        if (views.isEmpty()) {
            return true;
        }
        return ChronoUnit.MINUTES
                .between(views.get(views.size() - 1).getCreatedDate(), currentTime) >= ContentsTimeEnum.VIEWED_BY_IP_MINUTE.getTime();

    }

    private boolean isInTimeFollowedHashtag(LocalDateTime currentTime, FollowedHashtag followedHashtag, int j) {
        return ChronoUnit.DAYS
                .between(followedHashtag.getCreatedDate(), currentTime) >= HASHTAG_FOLLOW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(followedHashtag.getCreatedDate(), currentTime) < HASHTAG_FOLLOW.getDays().get(j + 1);
    }

    private boolean isInTimeViewedHashtag(LocalDateTime currentTime, ViewedHashtagByIp viewedHashtagByIp, int j) {
        return ChronoUnit.DAYS
                .between(viewedHashtagByIp.getCreatedDate(), currentTime) >= HASHTAG_VIEW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(viewedHashtagByIp.getCreatedDate(), currentTime) < HASHTAG_VIEW.getDays().get(j + 1);
    }

    private boolean isInTimePostedHashtag(LocalDateTime currentTime, PostedHashtag postedHashtag, int j) {
        return ChronoUnit.DAYS
                .between(postedHashtag.getCreatedDate(), currentTime) >= HASHTAG_POST.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(postedHashtag.getCreatedDate(), currentTime) < HASHTAG_POST.getDays().get(j + 1);
    }

}
