package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageDiscoveryDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageFollowDto;
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
import org.springframework.data.domain.PageRequest;
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
public class MainPageQueryServiceJpa implements MainPageQueryService {

    private final PostQueryRepository postQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberRepository memberRepository;
    private List<Collection> followedCollections;
    private List<Member> followedMembers;
    private List<Member> blockedMembers;

    @Override
    public MainPageDiscoveryDto getDiscoveryData(Pageable popularPostPageable, Pageable latestPostPageable,
                                                 Pageable collectionPageable, Long memberId)
            throws NotExistMemberException {

        Optional<Member> optionalMember = getMemberByIdWithBlockedMember(memberId);
        Member member = optionalMember.orElse(null);

        blockedMembers = getBlockedMemberByMember(member);

        return new MainPageDiscoveryDto(
                postQueryRepository.findAllSlicedForMainPage(popularPostPageable, member, blockedMembers),
                postQueryRepository.findAllSlicedForMainPage(latestPostPageable, member, blockedMembers),
                collectionQueryRepository.findAllSlicedForMainPage(collectionPageable, member, blockedMembers));
    }

    @Override
    public MainPageFollowDto getFollowData(
            Pageable memberPageable, Pageable hashtagPageable,
            Pageable moviePageable, Pageable collectionPageable, Long memberId)
            throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedContentsAndBlockedMember(memberId);

        followedCollections = member.getFollowedCollections().stream()
                .map(FollowedCollection::getCollection).collect(Collectors.toList());
        followedMembers =
                member.getFollowedMembers().stream().map(FollowedMember::getMember).collect(Collectors.toList());
        List<Movie> movies =
                member.getFollowedMovies().stream().map(FollowedMovie::getMovie).collect(Collectors.toList());
        List<Hashtag> hashtags =
                member.getFollowedHashtags().stream().map(FollowedHashtag::getHashtag).collect(Collectors.toList());

        blockedMembers = member.getBlockedMembers().stream()
                .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.A))
                .map(BlockedMember::getMember).collect(Collectors.toList());

        Slice<MainPageCollectionDto> collections = getMainPageCollectionList(collectionPageable);

        return new MainPageFollowDto(
                postQueryRepository.findAllByFollowingMemberSlicedForMainPage(memberPageable,
                        followedMembers, blockedMembers),
                postQueryRepository.findAllByFollowedHashtagsSlicedForMainPage(hashtagPageable,
                        movies, followedMembers, hashtags, blockedMembers),
                postQueryRepository.findAllByFollowedMoviesSlicedForMainPage(moviePageable,
                         movies, followedMembers, blockedMembers),
                collections);
    }

    private Slice<MainPageCollectionDto> getMainPageCollectionList(Pageable pageable) {
        if (pageable.getPageNumber() % 2 == 0) {
            return collectionQueryRepository.findAllByFollowedMemberSlicedForMainPage(
                    PageRequest.of(pageable.getPageNumber() / 2, pageable.getPageSize(),
                            pageable.getSort()), followedMembers, blockedMembers);
        } else {
             return collectionQueryRepository.findAllByFollowedCollectionsSlicedForMainPage(
                    PageRequest.of(pageable.getPageNumber() / 2, pageable.getPageSize(),
                            pageable.getSort()), followedCollections, blockedMembers);
        }
    }

    private List<Member> getBlockedMemberByMember(Member member) {
        if (member != null) {
            return member.getBlockedMembers().stream()
                    .filter(blockedMember -> blockedMember.getBlockedMemberStatus().equals(BlockedMemberStatus.A))
                    .map(BlockedMember::getMember).collect(Collectors.toList());
        } else{
            return new ArrayList<>();
        }
    }

    private Optional<Member> getMemberByIdWithBlockedMember(Long memberId) throws NotExistMemberException {
        if (memberId == null) {
            return Optional.empty();
        }
        Optional<Member> optionalMember = memberRepository.findOneByIdWithBlockedMember(memberId);

        return Optional.of(optionalMember.orElseThrow(NotExistMemberException::new));
    }

    private Member getMemberByIdWithFollowedContentsAndBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedContentsAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }
}
