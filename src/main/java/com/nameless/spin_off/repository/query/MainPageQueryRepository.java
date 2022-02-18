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
    Slice<Post> findPostsOrderByIdBySliced(
            Pageable pageable, Member user, List<Member> blockedMembers);
    Slice<Post> findPostsOrderByPopularityAfterLocalDateTimeSliced(
            Pageable pageable, Member user, List<Member> blockedMembers);
    Slice<Collection> findCollectionsOrderByPopularityAfterLocalDateTimeSliced(
            Pageable pageable, Member user, List<Member> blockedMembers);
    Slice<Post> findPostsByFollowingMemberOrderByIdSliced(
            Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers);
    Slice<Collection> findCollectionsByFollowedMemberOrderByIdSliced(
            Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers);
    Slice<Post> findPostsByFollowedHashtagsOrderByIdSliced(
            Pageable pageable, List<Hashtag> followedHashtags, List<Member> blockedMembers);
    Slice<Post> findPostsByFollowedMoviesOrderByIdSliced(
            Pageable pageable, List<Movie> followedMovies, List<Member> blockedMembers);
    Slice<Collection> findCollectionsByFollowedCollectionsOrderByIdSliced(
            Pageable pageable, List<Collection> collections, List<Member> blockedMembers);

}
