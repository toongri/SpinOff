package com.nameless.spin_off.entity.movie;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import com.nameless.spin_off.entity.member.Member;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowedMovie extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name="followed_movie_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movie movie;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static FollowedMovie createFollowedMovie(Movie movie) {

        FollowedMovie followedMovie = new FollowedMovie();
        followedMovie.updateMovie(movie);

        return followedMovie;

    }

    //==수정 메소드==//
    public void updateMovie(Movie movie) {
        this.movie = movie;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
