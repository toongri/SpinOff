package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface MainPageQueryRepository {
    Slice<Post> findPostsOrderByIdBySliced(Pageable pageable, Long memberId);
    Slice<Post> findPostsOrderByPopularityAfterLocalDateTimeSliced(
            Pageable pageable, Long memberId);
    Slice<Collection> findCollectionsOrderByPopularityAfterLocalDateTimeSliced(Pageable pageable, Long memberId);
    Slice<Post> findPostsByFollowingMemberOrderByIdSliced(Pageable pageable, List<Member> followedMembers);
    Slice<Collection> findCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, List<Member> followedMembers);
    Slice<Post> findPostsByFollowedHashtagsOrderByIdSliced(Pageable pageable, List<Hashtag> followedHashtags);
    Slice<Post> findPostsByFollowedMoviesOrderByIdSliced(Pageable pageable, List<Movie> followedMovies);
    Slice<Collection> findCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, List<Collection> collections);

}
