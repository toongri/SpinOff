package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MainPageQueryRepository {
    Slice<MainPagePostDto> findPostsOrderByIdBySliced(
            Pageable pageable, Member user, List<Member> blockedMembers);
    Slice<MainPagePostDto> findPostsOrderByPopularitySliced(
            Pageable pageable, Member user, List<Member> blockedMembers);
    Slice<Collection> findCollectionsOrderByPopularitySliced(
            Pageable pageable, Member user, List<Member> blockedMembers);
    Slice<MainPagePostDto> findPostsByFollowingMemberOrderByIdSliced(
            Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers);
    Slice<Collection> findCollectionsByFollowedMemberOrderByIdSliced(
            Pageable pageable, List<Member> followedMembers, List<Member> blockedMembers);
    Slice<MainPagePostDto> findPostsByFollowedHashtagsOrderByIdSliced(
            Pageable pageable, List<Hashtag> followedHashtags, List<Member> blockedMembers);
    Slice<MainPagePostDto> findPostsByFollowedMoviesOrderByIdSliced(
            Pageable pageable, List<Movie> followedMovies, List<Member> blockedMembers);
    Slice<Collection> findCollectionsByFollowedCollectionsOrderByIdSliced(
            Pageable pageable, List<Collection> collections, List<Member> blockedMembers);

}
