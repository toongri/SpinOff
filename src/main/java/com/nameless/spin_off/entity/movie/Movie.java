package com.nameless.spin_off.entity.movie;

import com.nameless.spin_off.StaticVariable;
import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.swing.text.View;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.nameless.spin_off.StaticVariable.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseTimeEntity {

    @Id
    @Column(name="movie_id")
    private Long id;

    private String title;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<FollowedMovie> followingMembers = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<Post> taggedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ViewedMovieByIp> viewedMovieByIps = new ArrayList<>();

    private Long postCount;
    private Long viewCount;
    private Long followCount;
    private Long popularity;

    //==연관관계 메소드==//
    public void addViewedMovieByIp(String ip) {
        ViewedMovieByIp viewedMovieByIp = ViewedMovieByIp.createViewedMovieByIp(ip);

        updateViewCount();
        this.viewedMovieByIps.add(viewedMovieByIp);
        viewedMovieByIp.updateMovie(this);
    }

    public void addTaggedPosts(Post post) {
        updatePostCount();
        this.taggedPosts.add(post);
        post.updateMovie(this);
    }

    public void addFollowingMembers(FollowedMovie followedMovie) {
        updateFollowCount();
        this.followingMembers.add(followedMovie);
    }

    //==생성 메소드==//
    public static Movie createMovie(Long id, String title, String imageUrl) {

        Movie movie = new Movie();
        movie.updateId(id);
        movie.updateTitle(title);
        movie.updateImageUrl(imageUrl);
        movie.updateCountToZero();

        return movie;
    }

    //==수정 메소드==//

    public void updateCountToZero() {
        this.viewCount = 0L;
        this.postCount = 0L;
        this.followCount = 0L;
        this.popularity = 0L;
    }

    public void updatePopularity() {
        this.popularity = viewCount + postCount + followCount;
    }

    public void updateFollowCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.followCount = followingMembers.stream()
                .filter(followingMember -> ChronoUnit.DAYS
                        .between(followingMember.getCreatedDate(), currentTime) < MOVIE_FOLLOW_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    public void updateViewCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.viewCount = viewedMovieByIps.stream()
                .filter(viewedMovieByIp -> ChronoUnit.DAYS
                        .between(viewedMovieByIp.getCreatedDate(), currentTime) < MOVIE_VIEW_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    public void updatePostCount() {
        LocalDateTime currentTime = LocalDateTime.now();

        this.postCount = taggedPosts.stream()
                .filter(taggedPost -> ChronoUnit.DAYS
                        .between(taggedPost.getCreatedDate(), currentTime) < MOVIE_POST_COUNT_DAYS)
                .count() + 1;

        updatePopularity();
    }

    private void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private void updateTitle(String title) {
        this.title = title;
    }

    private void updateId(Long id) {
        this.id = id;
    }

    //==비즈니스 로직==//
    public boolean insertViewedMovieByIp(String ip) {

        if (isNotAlreadyIpView(ip)) {
            addViewedMovieByIp(ip);
            return true;
        } else {
            return false;
        }
    }
    //==조회 로직==//

    public Boolean isNotAlreadyIpView(String ip) {

        LocalDateTime currentTime = LocalDateTime.now();

        return viewedMovieByIps.stream()
                .noneMatch(viewedMovieByIp -> viewedMovieByIp.getIp().equals(ip) &&
                        ChronoUnit.MINUTES.between(viewedMovieByIp.getCreatedDate(), currentTime)
                                < VIEWED_BY_IP_MINUTE);
    }
}
