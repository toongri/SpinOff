package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.FollowedMovie;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
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
public class JpaMainPageQueryService implements MainPageQueryService {

    private final PostQueryRepository postQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberRepository memberRepository;

    @Override
    public Slice<MainPagePostDto> getPostsSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Optional<Member> optionalMember = getMemberByIdWithBlockedMember(memberId);
        List<Member> blockedMembers;
        Member member;

        if (optionalMember.isEmpty()) {
            member = null;
            blockedMembers = new ArrayList<>();
        } else {
            member = optionalMember.get();
            blockedMembers = member.getBlockedMembers().stream()
                    .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.A))
                    .map(BlockedMember::getMember).collect(Collectors.toList());
        }

        return postQueryRepository.findAllSlicedForMainPage(pageable, member, blockedMembers);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsSliced(
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
                    .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.A))
                    .map(BlockedMember::getMember).collect(Collectors.toList());
        }

        return collectionQueryRepository
                .findAllSlicedForMainPage(pageable, member, blockedMembers);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedHashtagSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedHashtagAndBlockedMember(memberId);

        List<Hashtag> hashtags =
                member.getFollowedHashtags().stream().map(FollowedHashtag::getHashtag).collect(Collectors.toList());
        List<Member> blockedMembers = member.getBlockedMembers().stream()
                        .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.A))
                        .map(BlockedMember::getMember).collect(Collectors.toList());

        return postQueryRepository
                .findAllByFollowedHashtagsSlicedForMainPage(pageable, hashtags, blockedMembers);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedMovieSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMovieAndBlockedMember(memberId);

        List<Movie> movies =
                member.getFollowedMovies().stream().map(FollowedMovie::getMovie).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        return postQueryRepository
                .findAllByFollowedMoviesSlicedForMainPage(pageable, movies, blockedMembers);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowingMemberSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);
        List<Member> followedMembers =
                member.getFollowedMembers().stream().map(FollowedMember::getMember).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        return postQueryRepository
                .findAllByFollowingMemberSlicedForMainPage(pageable, followedMembers, blockedMembers);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedMemberSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);

        List<Member> followedMembers =
                member.getFollowedMembers().stream().map(FollowedMember::getMember).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        return collectionQueryRepository
                .findAllByFollowedMemberSlicedForMainPage(pageable, followedMembers, blockedMembers);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsSliced(Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedCollectionAndBlockedMember(memberId);

        List<Collection> followedCollections =
                member.getFollowedCollections().stream().map(FollowedCollection::getCollection).collect(Collectors.toList());
        List<Member> blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());

        return collectionQueryRepository
                .findAllByFollowedCollectionsSlicedForMainPage(pageable, followedCollections, blockedMembers);
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
