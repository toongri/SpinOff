package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.FollowedCollection;
import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.FollowedMember;
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

import java.util.ArrayList;
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
    public Slice<MainPagePostDto> getPostsOrderById(Pageable pageable, Long memberId) throws NotExistMemberException {

        Optional<Member> optionalMember = getMemberByIdWithBlockedMember(memberId);
        List<Member> blockedMembers;
        Member member;

        if (optionalMember.isEmpty()) {
            member = null;
            blockedMembers = new ArrayList<>();
        } else {
            member = optionalMember.get();
            blockedMembers = member.getBlockedMembers().stream()
                    .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.ALL))
                    .map(BlockedMember::getMember).collect(Collectors.toList());
        }

        Slice<Post> postSlice = mainPageQueryRepository.findPostsOrderByIdBySliced(pageable, member, blockedMembers);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsOrderByPopularityBySlicing(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        Optional<Member> optionalMember = getMemberByIdWithBlockedMember(memberId);
        List<Member> blockedMembers;
        Member member;

        if (optionalMember.isEmpty()) {
            member = null;
            blockedMembers = new ArrayList<>();
        } else {
            member = optionalMember.get();
            blockedMembers = member.getBlockedMembers().stream()
                    .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.ALL))
                    .map(BlockedMember::getMember).collect(Collectors.toList());
        }


        Slice<Post> postSlice = mainPageQueryRepository
                .findPostsOrderByPopularityAfterLocalDateTimeSliced(pageable, member, blockedMembers);

        return postSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsOrderByPopularityBySlicing(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        Optional<Member> optionalMember = getMemberByIdWithBlockedMember(memberId);
        List<Member> blockedMembers;
        Member member;

        if (optionalMember.isEmpty()) {
            member = null;
            blockedMembers = new ArrayList<>();
        } else {
            member = optionalMember.get();
            blockedMembers = member.getBlockedMembers().stream()
                    .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.ALL))
                    .map(BlockedMember::getMember).collect(Collectors.toList());
        }

        Slice<Collection> collectionSlice = mainPageQueryRepository
                .findCollectionsOrderByPopularityAfterLocalDateTimeSliced(pageable, member, blockedMembers);

        return collectionSlice.map(MainPageCollectionDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedHashtagOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedHashtagAndBlockedMember(memberId);

        List<Hashtag> hashtags =
                member.getFollowedHashtags().stream().map(FollowedHashtag::getHashtag).collect(Collectors.toList());
        List<Member> blockedMembers = member.getBlockedMembers().stream()
                        .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.ALL))
                        .map(BlockedMember::getMember).collect(Collectors.toList());

        Slice<Post> postsSlice = mainPageQueryRepository
                .findPostsByFollowedHashtagsOrderByIdSliced(pageable, hashtags, blockedMembers);

        return postsSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedMovieOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMovieAndBlockedMember(memberId);

        List<Movie> movies =
                member.getFollowedMovies().stream().map(FollowedMovie::getMovie).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        Slice<Post> moviesSlice = mainPageQueryRepository
                .findPostsByFollowedMoviesOrderByIdSliced(pageable, movies, blockedMembers);

        return moviesSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowingMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);
        List<Member> followedMembers =
                member.getFollowedMembers().stream().map(FollowedMember::getMember).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        Slice<Post> membersSlice =
                mainPageQueryRepository.findPostsByFollowingMemberOrderByIdSliced(pageable, followedMembers, blockedMembers);

        return membersSlice.map(MainPagePostDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedMemberOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);

        List<Member> followedMembers =
                member.getFollowedMembers().stream().map(FollowedMember::getMember).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        Slice<Collection> membersSlice =
                mainPageQueryRepository.findCollectionsByFollowedMemberOrderByIdSliced(pageable, followedMembers, blockedMembers);

        return membersSlice.map(MainPageCollectionDto::new);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsOrderByIdSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedCollectionAndBlockedMember(memberId);

        List<Collection> followedCollections =
                member.getFollowedCollections().stream().map(FollowedCollection::getCollection).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        Slice<Collection> slice = mainPageQueryRepository
                .findCollectionsByFollowedCollectionsOrderByIdSliced(pageable, followedCollections, blockedMembers);

        return slice.map(MainPageCollectionDto::new);

    }

    private Member getMemberByIdWithFollowedHashtagAndBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedHashtagAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdWithFollowedMovieAndBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMovieAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdWithFollowedMemberAndBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMemberAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdWithFollowedCollectionAndBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedCollectionAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Optional<Member> getMemberByIdWithBlockedMember(Long memberId) throws NotExistMemberException {
        if (memberId == null) {
            return Optional.empty();
        }
        Optional<Member> optionalMember = memberRepository.findOneByIdWithBlockedMember(memberId);

        return Optional.of(optionalMember.orElseThrow(NotExistMemberException::new));
    }
}
