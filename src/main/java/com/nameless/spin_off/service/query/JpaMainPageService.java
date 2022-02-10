package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.member.FollowedHashtag;
import com.nameless.spin_off.entity.member.FollowedMovie;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.query.MainPageQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaMainPageService implements MainPageService{

    private final MainPageQueryRepository mainPageQueryRepository;
    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;

    @Override
    public Slice<MainPagePostDto> getPostsOrderById(Pageable pageable, Long memberId) {
        Slice<Post> postSlice = mainPageQueryRepository.findPostsOrderByIdBySliced(pageable, memberId);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime, Long memberId) {

        Slice<Post> postSlice = mainPageQueryRepository
                .findPostsOrderByPopularityAfterLocalDateTimeSliced(pageable, startDateTime, endDateTime, memberId);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, LocalDateTime startDateTime, LocalDateTime endDateTime, Long memberId) {

        Slice<Collection> collectionSlice = mainPageQueryRepository
                .findCollectionsOrderByPopularityAfterLocalDateTimeSliced(pageable, startDateTime, endDateTime, memberId);
        return collectionSlice.map(MainPageCollectionDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedHashtagOrderByIdSliced(Pageable pageable, Long memberId) throws NotSearchMemberException {

        Member member = getMemberByIdIncludeHashtags(memberId);

        List<Hashtag> hashtags =
                member.getFollowedHashtags().stream().map(FollowedHashtag::getHashtag).collect(Collectors.toList());

        Slice<Post> postsSlice = mainPageQueryRepository
                .findPostsByFollowedHashtagsOrderByIdSliced(pageable, hashtags);

        return postsSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedMovieOrderByIdSliced(Pageable pageable, Long memberId) throws NotSearchMemberException {
        Member member = getMemberByIdIncludeMovie(memberId);

        List<Movie> movies =
                member.getFollowedMovies().stream().map(FollowedMovie::getMovie).collect(Collectors.toList());

        Slice<Post> moviesSlice = mainPageQueryRepository
                .findPostsByFollowedMoviesOrderByIdSliced(pageable, movies);

        return moviesSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowingMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotSearchMemberException {

        List<Member> members = memberRepository.findMembersByFollowingMemberId(memberId);

        Slice<Post> membersSlice = mainPageQueryRepository.findPostsByFollowingMemberOrderByIdSliced(pageable, members);

        return membersSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotSearchMemberException {

        List<Member> members = memberRepository.findMembersByFollowingMemberId(memberId);

        Slice<Collection> membersSlice = mainPageQueryRepository.findCollectionsByFollowedMemberOrderByIdSliced(pageable, members);

        return membersSlice.map(MainPageCollectionDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, Long memberId) {

        List<Collection> collections = collectionRepository.findCollectionsByFollowingMemberId(memberId);

        Slice<Collection> slice = mainPageQueryRepository
                .findCollectionsByFollowedCollectionsOrderByIdSliced(pageable, collections);

        return slice.map(MainPageCollectionDto::new);

    }

    private Member getMemberByIdIncludeHashtags(Long memberId) throws NotSearchMemberException {
        Optional<Member> optionalMember = memberRepository.findMemberByIdIncludeHashtag(memberId);

        return optionalMember.orElseThrow(NotSearchMemberException::new);
    }

    private Member getMemberByIdIncludeMovie(Long memberId) throws NotSearchMemberException {
        Optional<Member> optionalMember = memberRepository.findMemberByIdIncludeMovie(memberId);

        return optionalMember.orElseThrow(NotSearchMemberException::new);
    }

}
