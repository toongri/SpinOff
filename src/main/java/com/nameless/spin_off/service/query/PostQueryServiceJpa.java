package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtHashtagPostDto;
import com.nameless.spin_off.dto.PostDto.visitPostDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.hashtag.FollowedHashtag;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.BlockedMember;
import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.FollowedMovie;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
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
public class PostQueryServiceJpa implements PostQueryService{

    private final PostQueryRepository postQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagRepository hashtagRepository;
    private List<Member> blockedMembers;
    private List<Member> followedMembers;
    private List<Hashtag> hashtags;
    private List<Movie> movies;

    @Override
    public Slice<SearchPageAtAllPostDto> getPostsSlicedForSearchPagePostAtAll(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        Optional<Member> optionalMember = getMemberByIdWithBlockedMember(memberId);

        blockedMembers = getBlockedMemberByMember(optionalMember.orElse(null));

        return postQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable, blockedMembers);
    }

    @Override
    public Slice<MainPagePostDto> getPostsSlicedForMainPage(Pageable pageable, Long memberId)
            throws NotExistMemberException {

        Optional<Member> optionalMember = getMemberByIdWithBlockedMember(memberId);

        Member member = optionalMember.orElse(null);
        blockedMembers = getBlockedMemberByMember(member);

        return postQueryRepository.findAllSlicedForMainPage(pageable, member, blockedMembers);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedHashtagSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedContentsAndBlockedMember(memberId);
        setMemberInfoByMember(member);
        movies = member.getFollowedMovies().stream().map(FollowedMovie::getMovie).collect(Collectors.toList());
        hashtags = member.getFollowedHashtags().stream().map(FollowedHashtag::getHashtag).collect(Collectors.toList());

        return postQueryRepository
                .findAllByFollowedHashtagsSlicedForMainPage(pageable,
                        movies, followedMembers, hashtags, blockedMembers);
    }


    @Override
    public Slice<MainPagePostDto> getPostsByFollowedMovieSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMovieAndBlockedAndFollowedMember(memberId);
        setMemberInfoByMember(member);

        movies = member.getFollowedMovies().stream().map(FollowedMovie::getMovie).collect(Collectors.toList());

        return postQueryRepository
                .findAllByFollowedMoviesSlicedForMainPage(pageable,
                        movies, followedMembers, blockedMembers);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowingMemberSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        Member member = getMemberByIdWithFollowedMemberAndBlockedMember(memberId);
        setMemberInfoByMember(member);

        return postQueryRepository
                .findAllByFollowingMemberSlicedForMainPage(pageable,
                        followedMembers, blockedMembers);
    }

    @Override
    public SearchFirstDto<Slice<SearchPageAtHashtagPostDto>> getPostsByHashtagsSlicedForSearchPageFirst(
            Pageable pageable, List<String> hashtagContent, Long memberId, int length) {

        hashtags = hashtagRepository.findAllByContentIn(hashtagContent);
        blockedMembers = memberRepository.findAllByBlockingMemberId(memberId);
        return new SearchFirstDto<>(
                postQueryRepository.findAllByHashtagsSlicedForSearchPage(pageable, hashtags, blockedMembers),
                hashtags.stream().map(RelatedMostTaggedHashtagDto::new).limit(length).collect(Collectors.toList()));
    }

    @Override
    public Slice<SearchPageAtHashtagPostDto> getPostsByHashtagsSlicedForSearchPage(
            Pageable pageable, List<String> hashtagContent, Long memberId) {

        hashtags = hashtagRepository.findAllByContentIn(hashtagContent);
        blockedMembers = memberRepository.findAllByBlockingMemberId(memberId);

        return postQueryRepository.findAllByHashtagsSlicedForSearchPage(pageable, hashtags, blockedMembers);
    }

    @Override
    public visitPostDto visitPost(MemberDetails currentMember, Long postId) {


        return null;
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

    private Member getMemberByIdWithFollowedMemberAndBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMemberAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdWithFollowedContentsAndBlockedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedContentsAndBlockedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Member getMemberByIdWithFollowedMovieAndBlockedAndFollowedMember(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMovieAndBlockedAndFollowedMember(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private void setMemberInfoByMember(Member member) {
        followedMembers =
                member.getFollowedMembers().stream().map(FollowedMember::getMember).collect(Collectors.toList());
        blockedMembers =
                member.getBlockedMembers().stream().map(BlockedMember::getMember).collect(Collectors.toList());
    }
}
