package com.nameless.spin_off.entity.movie;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.ContentsTimeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.movie.MovieScoreEnum.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movie extends BaseTimeEntity {

    @Id
    @Column(name="movie_id")
    private Long id;

    private String title;
    private String thumbnail;
    private String directorName;
    private Integer year;
    private String naverUrl;
    private String nation;
    private Double popularity;
    private String actors;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<FollowedMovie> followingMembers = new ArrayList<>();

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<Post> taggedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ViewedMovieByIp> viewedMovieByIps = new ArrayList<>();

    private String firstGenreOfMovieStatus;

    private String secondGenreOfMovieStatus;

    private String thirdGenreOfMovieStatus;

    private String fourthGenreOfMovieStatus;

    //==연관관계 메소드==//
    private Long addViewedMovieByIp(String ip) {
        ViewedMovieByIp viewedMovieByIp = ViewedMovieByIp.createViewedMovieByIp(ip, this);

        this.viewedMovieByIps.add(viewedMovieByIp);

        return viewedMovieByIp.getId();
    }

    public void addTaggedPosts(Post post) {
        this.taggedPosts.add(post);
        post.updateMovie(this);
    }

    public void addFollowingMembers(FollowedMovie followedMovie) {
        this.followingMembers.add(followedMovie);
    }

    //==생성 메소드==//
    public static Movie createMovie(Long id, String title, Integer year, String directorName, String nation) {

        Movie movie = new Movie();
        movie.updateDirectorName(directorName);
        movie.updateYear(year);
        movie.updateNation(nation);
        movie.updateId(id);
        movie.updateTitle(title);
        movie.updatePopularityZero();
        return movie;
    }

    public static Movie createMovie(Long id) {
        Movie movie = new Movie();
        movie.updateId(id);

        return movie;
    }

    //==수정 메소드==//
    public void updateActors(String actors) {
        this.actors = actors;
    }
    public void updateNation(String nation) {
        this.nation = nation;
    }
    public void updateNaverUrl(String naverUrl) {
        this.naverUrl = naverUrl;
    }
    public void updateDirectorName(String directorName) {
        this.directorName = directorName;
    }
    public void updatePopularityZero() {
        popularity = 0.0;
    }
    public void updateYear(Integer year) {
        this.year = year;
    }

    public void updatePopularity() {
        popularity = executeViewScore() + executeFollowScore() + executePostScore();
    }
    public void updateImageUrl(String imageUrl) {
        this.thumbnail = imageUrl;
    }
    private void updateTitle(String title) {
        this.title = title;
    }
    private void updateId(Long id) {
        this.id = id;
    }

    public void updateGenres(List<String> genres) {
        if (genres.size() > 3) {
            updateFirstGenreOfMovieStatus(genres.get(0));
            updateSecondGenreOfMovieStatus(genres.get(1));
            updateThirdGenreOfMovieStatus(genres.get(2));
            updateFourthGenreOfMovieStatus(genres.get(3));
        } else if (genres.size() == 3) {
            updateFirstGenreOfMovieStatus(genres.get(0));
            updateSecondGenreOfMovieStatus(genres.get(1));
            updateThirdGenreOfMovieStatus(genres.get(2));
        } else if (genres.size() == 2) {
            updateFirstGenreOfMovieStatus(genres.get(0));
            updateSecondGenreOfMovieStatus(genres.get(1));
        } else if (genres.size() == 1) {
            updateFirstGenreOfMovieStatus(genres.get(0));
        }
    }
    private void updateFirstGenreOfMovieStatus(String firstGenreOfMovieStatus) {
        this.firstGenreOfMovieStatus = firstGenreOfMovieStatus;
    }
    private void updateSecondGenreOfMovieStatus(String secondGenreOfMovieStatus) {
        this.secondGenreOfMovieStatus = secondGenreOfMovieStatus;
    }
    private void updateThirdGenreOfMovieStatus(String thirdGenreOfMovieStatus) {
        this.thirdGenreOfMovieStatus = thirdGenreOfMovieStatus;
    }
    private void updateFourthGenreOfMovieStatus(String fourthGenreOfMovieStatus) {
        this.fourthGenreOfMovieStatus = fourthGenreOfMovieStatus;
    }

    //==비즈니스 로직==//
    public Long insertViewedMovieByIp(String ip) {

        if (isNotAlreadyIpView(ip)) {
            return addViewedMovieByIp(ip);
        } else {
            return -1L;
        }
    }

    public double executeFollowScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        FollowedMovie followedMovie ;
        int j = 0, i = followingMembers.size() - 1;
        double result = 0, total = 0;

        while (i > -1) {
            followedMovie = followingMembers.get(i);
            if (isInTimeFollowedMovie(currentTime, followedMovie, j)) {
                result += 1;
                i--;
            } else {
                if (j == MOVIE_FOLLOW.getScores().size() - 1) {
                    break;
                }
                total += MOVIE_FOLLOW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        return (total + MOVIE_FOLLOW.getScores().get(j) * result) * MOVIE_FOLLOW.getRate();
    }

    public double executeViewScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        ViewedMovieByIp viewedMovieByIp ;
        int j = 0, i = viewedMovieByIps.size() - 1;
        double result = 0, total = 0;

        while (i > -1) {
            viewedMovieByIp = viewedMovieByIps.get(i);
            if (isInTimeViewedMovie(currentTime, viewedMovieByIp, j)) {
                result += 1;
                i--;
            } else {
                if (j == MOVIE_VIEW.getScores().size() - 1) {
                    break;
                }
                total += MOVIE_VIEW.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        return (total + MOVIE_VIEW.getScores().get(j) * result) * MOVIE_VIEW.getRate();
    }

    public double executePostScore() {

        LocalDateTime currentTime = LocalDateTime.now();
        Post post ;
        int j = 0, i = taggedPosts.size() - 1;
        double result = 0, total = 0;

        while (i > -1) {
            post = taggedPosts.get(i);
            if (isInTimeTaggedPost(currentTime, post, j)) {
                result += 1;
                i--;
            } else {
                if (j == MOVIE_POST.getScores().size() - 1) {
                    break;
                }
                total += MOVIE_POST.getScores().get(j) * result;
                result = 0;
                j++;
            }
        }
        return (total + MOVIE_POST.getScores().get(j) * result) * MOVIE_POST.getRate();
    }

    //==조회 로직==//
    public Boolean isNotAlreadyIpView(String ip) {

        LocalDateTime currentTime = LocalDateTime.now();

        List<ViewedMovieByIp> views = viewedMovieByIps.stream()
                .filter(viewedMovieByIp -> viewedMovieByIp.getIp().equals(ip))
                .collect(Collectors.toList());

        if (views.isEmpty()) {
            return true;
        }
        return ChronoUnit.MINUTES
                .between(views.get(views.size() - 1).getCreatedDate(), currentTime) >= ContentsTimeEnum.VIEWED_BY_IP_MINUTE.getTime();
    }

    private boolean isInTimeFollowedMovie(LocalDateTime currentTime, FollowedMovie followedMovie, int j) {
        return ChronoUnit.DAYS
                .between(followedMovie.getCreatedDate(), currentTime) >= MOVIE_FOLLOW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(followedMovie.getCreatedDate(), currentTime) < MOVIE_FOLLOW.getDays().get(j + 1);
    }

    private boolean isInTimeViewedMovie(LocalDateTime currentTime, ViewedMovieByIp viewedMovieByIp, int j) {
        return ChronoUnit.DAYS
                .between(viewedMovieByIp.getCreatedDate(), currentTime) >= MOVIE_VIEW.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(viewedMovieByIp.getCreatedDate(), currentTime) < MOVIE_VIEW.getDays().get(j + 1);
    }

    private boolean isInTimeTaggedPost(LocalDateTime currentTime, Post post, int j) {
        return ChronoUnit.DAYS
                .between(post.getCreatedDate(), currentTime) >= MOVIE_POST.getDays().get(j) &&
                ChronoUnit.DAYS
                        .between(post.getCreatedDate(), currentTime) < MOVIE_POST.getDays().get(j + 1);
    }

}
