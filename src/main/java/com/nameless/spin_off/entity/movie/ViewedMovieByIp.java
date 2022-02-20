package com.nameless.spin_off.entity.movie;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.ViewedPostByIp;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViewedMovieByIp extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="viewed_movie_by_ip_id")
    private Long id;

    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movie movie;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static ViewedMovieByIp createViewedMovieByIp(String ip, Movie movie) {
        ViewedMovieByIp viewedMovieByIp = new ViewedMovieByIp();
        viewedMovieByIp.updateIp(ip);
        viewedMovieByIp.updateMovie(movie);

        return viewedMovieByIp;
    }

    //==수정 메소드==//
    public void updateMovie(Movie movie) {
        this.movie = movie;
    }

    private void updateIp(String ip) {
        this.ip = ip;
    }

    //==비즈니스 로직==//

    //==조회 로직==//
}
