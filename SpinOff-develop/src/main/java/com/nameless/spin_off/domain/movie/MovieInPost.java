package com.nameless.spin_off.domain.movie;

import com.nameless.spin_off.domain.post.Post;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovieInPost {

    @Id
    @GeneratedValue
    @Column(name="movieinpost_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @NotNull
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movie movie;

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static MovieInPost createMovieInPost(Post post, Movie movie) {

        MovieInPost movieInPost = new MovieInPost();
        movieInPost.updatePost(post);
        movieInPost.updateMovie(movie);

        return movieInPost;

    }

    //==수정 메소드==//
    private void updateMovie(Movie movie) {
        this.movie = movie;
    }

    private void updatePost(Post post) {
        this.post = post;
    }

    //==비즈니스 로직==//

    //==조회 로직==//

}
