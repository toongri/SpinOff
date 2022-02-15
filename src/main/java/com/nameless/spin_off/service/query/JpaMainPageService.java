package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import com.nameless.spin_off.entity.movie.FollowedMovie;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.MainPageQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            Pageable pageable, Long memberId) {

        Slice<Post> postSlice = mainPageQueryRepository
                .findPostsOrderByPopularityAfterLocalDateTimeSliced(pageable, memberId);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsOrderByPopularityBySlicingAfterLocalDateTime(
            Pageable pageable, Long memberId) {

        Slice<Collection> collectionSlice = mainPageQueryRepository
                .findCollectionsOrderByPopularityAfterLocalDateTimeSliced(pageable, memberId);

        return collectionSlice.map(MainPageCollectionDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedHashtagOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdIncludeHashtags(memberId);

        List<Hashtag> hashtags =
                member.getFollowedHashtags().stream().map(FollowedHashtag::getHashtag).collect(Collectors.toList());

        Slice<Post> postsSlice = mainPageQueryRepository
                .findPostsByFollowedHashtagsOrderByIdSliced(pageable, hashtags);

        return postsSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedMovieOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {
        Member member = getMemberByIdIncludeMovie(memberId);

        List<Movie> movies =
                member.getFollowedMovies().stream().map(FollowedMovie::getMovie).collect(Collectors.toList());

        Slice<Post> moviesSlice = mainPageQueryRepository
                .findPostsByFollowedMoviesOrderByIdSliced(pageable, movies);

        return moviesSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowingMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        List<Member> members = memberRepository.findAllByFollowingMemberId(memberId);

        Slice<Post> membersSlice = mainPageQueryRepository.findPostsByFollowingMemberOrderByIdSliced(pageable, members);

        return membersSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        List<Member> members = memberRepository.findAllByFollowingMemberId(memberId);

        Slice<Collection> membersSlice = mainPageQueryRepository.findCollectionsByFollowedMemberOrderByIdSliced(pageable, members);

        return membersSlice.map(MainPageCollectionDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, Long memberId) {

        List<Collection> collections = collectionRepository.findAllByFollowingMemberId(memberId);

        Slice<Collection> slice = mainPageQueryRepository
                .findCollectionsByFollowedCollectionsOrderByIdSliced(pageable, collections);

        return slice.map(MainPageCollectionDto::new);

    }

    private Member getMemberByIdIncludeHashtags(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithHashtag(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdIncludeMovie(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithMovie(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

}
