package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.PostInCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtHashtagPostDto;
import com.nameless.spin_off.dto.PostDto.visitPostDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.member.AuthorityOfMemberStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
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
    private final MovieRepository movieRepository;
    private List<Long> blockedMemberIds;
    private List<Long> followedMemberIds;
    private List<Hashtag> hashtagIds;
    private List<Long> movieIds;

    @Override
    public Slice<SearchPageAtAllPostDto> getPostsSlicedForSearchPagePostAtAll(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable,
                getBlockedMemberByMemberId(memberId));
    }

    @Override
    public Slice<MainPagePostDto> getPostsSlicedForMainPage(Pageable pageable, Long memberId)
            throws NotExistMemberException {

        return postQueryRepository.findAllSlicedForMainPage(pageable, memberId, getBlockedMemberByMemberId(memberId));
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedHashtagSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository
                .findAllByFollowedHashtagsSlicedForMainPage(pageable,
                        getFollowedMovieByMemberId(memberId), getFollowedMemberByMemberId(memberId),
                        getBlockedMemberByMemberId(memberId), memberId);
    }


    @Override
    public Slice<MainPagePostDto> getPostsByFollowedMovieSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository
                .findAllByFollowedMoviesSlicedForMainPage(pageable,
                        getFollowedMemberByMemberId(memberId), getBlockedMemberByMemberId(memberId), memberId);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowingMemberSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository
                .findAllByFollowingMemberSlicedForMainPage(pageable,
                        memberId, getBlockedMemberByMemberId(memberId));
    }

    @Override
    public SearchFirstDto<Slice<SearchPageAtHashtagPostDto>> getPostsByHashtagsSlicedForSearchPageFirst(
            Pageable pageable, List<String> hashtagContent, Long memberId, int length) {

        hashtagIds = hashtagRepository.findAllByContentIn(hashtagContent);
        blockedMemberIds = memberRepository.findAllByBlockingMemberId(memberId).stream().map(Member::getId).collect(Collectors.toList());
        return new SearchFirstDto<>(
                postQueryRepository.findAllByHashtagsSlicedForSearchPage(pageable, hashtagIds, blockedMemberIds),
                hashtagIds.stream().map(RelatedMostTaggedHashtagDto::new).limit(length).collect(Collectors.toList()));
    }

    @Override
    public Slice<SearchPageAtHashtagPostDto> getPostsByHashtagsSlicedForSearchPage(
            Pageable pageable, List<String> hashtagContent, Long memberId) {

        hashtagIds = hashtagRepository.findAllByContentIn(hashtagContent);
        blockedMemberIds = memberRepository.findAllByBlockingMemberId(memberId).stream().map(Member::getId).collect(Collectors.toList());

        return postQueryRepository.findAllByHashtagsSlicedForSearchPage(pageable, hashtagIds, blockedMemberIds);
    }

    @Override
    public visitPostDto visitPost(Long memberId, Long postId) {

        Member member = findOneByIdWithFollowedMemberAndBlockedMemberAndRoles(memberId);
        setMemberInfoByMember(member);

        Post post = getPostByIdWithHashtagAndMovieAndMember(postId);

        return new visitPostDto(post, memberId, isAdmin(member));
    }

    @Override
    public List<PostInCollectionDto> getCollectionNamesByMemberIdAndPostId(Long memberId, Long postId) {
        return null;
    }

    private boolean isAdmin(Member member) {
        return member.getRoles().stream().anyMatch(AuthorityOfMemberStatus.A::equals);
    }

    private Member findOneByIdWithFollowedMemberAndBlockedMemberAndRoles(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findOneByIdWithFollowedMemberAndBlockedMemberAndRoles(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private void setMemberInfoByMember(Member member) {
        followedMemberIds =
                member.getFollowedMembers().stream().map(followedMember -> followedMember.getMember().getId()).collect(Collectors.toList());
        blockedMemberIds =
                member.getBlockedMembers().stream().map(blockedMember -> blockedMember.getMember().getId()).collect(Collectors.toList());
    }

    private Post getPostByIdWithHashtagAndMovieAndMember(Long postId) {
        return postQueryRepository.findOneByIdWithHashtagAndMovieAndMember(postId).orElseThrow(NotExistPostException::new);
    }

    private List<Long> getBlockedMemberByMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByBlockingMemberId(memberId);
        } else{
            return new ArrayList<>();
        }
    }

    private List<Long> getFollowedMemberByMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else{
            return new ArrayList<>();
        }
    }

    private List<Long> getFollowedMovieByMemberId(Long memberId) {
        if (memberId != null) {
            return movieRepository.findAllIdByFollowingMemberId(memberId);
        } else{
            return new ArrayList<>();
        }
    }
}
