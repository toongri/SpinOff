package com.nameless.spin_off.entity.movie;

import com.nameless.spin_off.entity.listener.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

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

    //==연관관계 메소드==//

    //==생성 메소드==//
    public static Movie createMovie(Long id, String title, String imageUrl) {

        Movie movie = new Movie();
        movie.updateId(id);
        movie.updateTitle(title);
        movie.updateImageUrl(imageUrl);

        return movie;

    }

    //==수정 메소드==//
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

    //==조회 로직==//

}
