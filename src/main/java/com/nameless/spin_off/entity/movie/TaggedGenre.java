package com.nameless.spin_off.entity.movie;

import com.nameless.spin_off.entity.enums.movie.GenreOfMovieStatus;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaggedGenre {

    @Id
    @GeneratedValue
    @Column(name="tagged_genre_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    @NotNull
    private Movie movie;

    @Enumerated(EnumType.STRING)
    @NotNull
    private GenreOfMovieStatus genreOfMovieStatus;
}
