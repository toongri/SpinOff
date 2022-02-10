package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface MainPageQueryRepository {
    Slice<Post> findPostsOrderByIdBySliced(Pageable pageable);
    Slice<Post> findPostsOrderByPopularityAfterLocalDateTimeSliced(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime);
    Slice<Collection> findCollectionsOrderByPopularityAfterLocalDateTimeSliced(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime);
    Slice<Post> findPostsByFollowingMemberOrderByIdSliced(Pageable pageable, List<Long> followedMemberIds);
    Slice<Collection> findCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, List<Long> followedMemberIds);
    Slice<Post> findPostsByFollowedHashtagOrderByIdSliced(Pageable pageable, List<Hashtag> followedHashtags);
    Slice<Post> findPostsByFollowedMovieOrderByIdSliced(Pageable pageable, List<Movie> followedMovies);

}
