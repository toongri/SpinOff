package com.nameless.spin_off.entity.hashtag;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.movie.FollowedMovie;
import com.nameless.spin_off.entity.movie.ViewedMovieByIp;
import com.nameless.spin_off.entity.post.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.nameless.spin_off.StaticVariable.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="hashtag_id")
    private Long id;

    private String content;

    @OneToMany(mappedBy = "hashtag", fetch = FetchType.LAZY)
    private List<FollowedHashtag> followingMembers = new ArrayList<>();

    @OneToMany(mappedBy = "hashtag", fetch = FetchType.LAZY)
    private List<PostedHashtag> taggedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ViewedHashtagByIp> viewedHashtagByIps = new ArrayList<>();

    private Long postCount;
    private Long viewCount;
    private Long followCount;
    private Long popularity;

    //==연관관계 메소드==//
    public void addViewedHashtagByIp(String ip) {
        ViewedHashtagByIp viewedHashtagByIp = ViewedHashtagByIp.createViewedHashtagByIp(ip);

        updateViewCount();
        this.viewedHashtagByIps.add(viewedHashtagByIp);
        viewedHashtagByIp.updateHashtag(this);
    }

    public void addTaggedPosts(PostedHashtag postedHashtag) {
        updatePostCount();
        this.taggedPosts.add(postedHashtag);
    }

    public void addFollowingMembers(FollowedHashtag followedHashtag) {
        updateFollowCount();
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
        this.popularity = viewCount + postCount + followCount;
    }

    public void updateCountToZero() {
        this.viewCount = 0L;
        this.postCount = 0L;
        this.followCount = 0L;
        this.popularity = 0L;
    }

    public void updateFollowCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.followCount = followingMembers.stream()
                .filter(followingMember -> ChronoUnit.DAYS
                        .between(followingMember.getCreatedDate(), currentTime) < HASHTAG_FOLLOW_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    public void updateViewCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.viewCount = viewedHashtagByIps.stream()
                .filter(viewedHashtagByIp -> ChronoUnit.DAYS
                        .between(viewedHashtagByIp.getCreatedDate(), currentTime) < HASHTAG_VIEW_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    public void updatePostCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.postCount = taggedPosts.stream()
                .filter(taggedPost -> ChronoUnit.DAYS
                        .between(taggedPost.getCreatedDate(), currentTime) < HASHTAG_POST_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }
    //==비즈니스 로직==//

    //==조회 로직==//

}
