package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.PostDto.*;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.query.HashtagQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryServiceJpa implements PostQueryService{

    private final PostQueryRepository postQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagRepository hashtagRepository;
    private final MovieRepository movieRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final HashtagQueryRepository hashtagQueryRepository;

    @Override
    public Slice<SearchPageAtAllPostDto> getPostsSlicedForSearchPagePostAtAll(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository.findAllSlicedForSearchPageAtAll(keyword, pageable,
                getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId));
    }

    @Override
    public Slice<MainPagePostDto> getPostsSlicedForMainPage(Pageable pageable, Long memberId)
            throws NotExistMemberException {

        return postQueryRepository.findAllSlicedForMainPage(pageable, memberId, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId));
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedHashtagSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository
                .findAllByFollowedHashtagsSlicedForMainPage(pageable,
                        getFollowedMovieByMemberId(memberId), getFollowedMemberByMemberId(memberId),
                        getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);
    }


    @Override
    public Slice<MainPagePostDto> getPostsByFollowedMovieSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository
                .findAllByFollowedMoviesSlicedForMainPage(pageable,
                        getFollowedMemberByMemberId(memberId), getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowingMemberSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository
                .findAllByFollowingMemberSlicedForMainPage(pageable, memberId);
    }

    @Override
    public SearchFirstDto<Slice<SearchPageAtHashtagPostDto>> getPostsByHashtagsSlicedForSearchPageFirst(
            Pageable pageable, List<String> hashtagContent, Long memberId, int length) {

        List<Hashtag> hashtags = hashtagRepository.findAllByContentIn(hashtagContent);

        return new SearchFirstDto<>(
                postQueryRepository.findAllByHashtagsSlicedForSearchPage(
                        pageable,
                        hashtags.stream().map(Hashtag::getId).collect(Collectors.toList()),
                        getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId)),
                hashtags.stream().map(RelatedMostTaggedHashtagDto::new).limit(length).collect(Collectors.toList()));
    }

    @Override
    public Slice<SearchPageAtHashtagPostDto> getPostsByHashtagsSlicedForSearchPage(
            Pageable pageable, List<String> hashtagContent, Long memberId) {

        return postQueryRepository.findAllByHashtagsSlicedForSearchPage(
                pageable,
                hashtagRepository.findAllIdByContentIn(hashtagContent),
                getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId));
    }

    @Override
    public RelatedPostFirstDto<ReadPostDto> getPostForRead(MemberDetails currentMember, Long postId, Pageable pageable) {

        Long memberId = getCurrentMemberId(currentMember);
        List<Long> blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        ReadPostDto post = getReadPostDto(postId, memberId, blockedMemberIds, isCurrentMemberAdmin(currentMember));

        return new RelatedPostFirstDto<>(
                post, postQueryRepository.findAllRelatedPostByPostId(pageable, blockedMemberIds, postId));
    }

    @Override
    public Slice<RelatedPostDto> getRelatedPostsSliced(Long memberId, Long postId, Pageable pageable) {

        return postQueryRepository.findAllRelatedPostByPostId(pageable,
                getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), postId);
    }

    private ReadPostDto getOneByPostId(Long postId, List<Long> blockedMemberIds) {
        return postQueryRepository.findByIdForRead(postId, blockedMemberIds)
                .orElseGet(() -> postQueryRepository.findByIdForRead(postId)
                        .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST)));
    }

    private ReadPostDto getReadPostDto(Long postId, Long memberId,
                                       List<Long> blockedMemberIds, boolean isAdmin) {

        ReadPostDto post = getOneByPostId(postId, blockedMemberIds);
        post.setHashtags(hashtagQueryRepository.findAllByPostId(postId));

        if (memberId != null) {
            Long postMemberId = post.getMember().getMemberId();
            isExistBlockedMember(memberId, postMemberId);
            post.setHasAuth(memberId, isAdmin);
            post.setIsLiked(isExistLikedPost(memberId, postId));
            post.getMember().setIsFollowed(isExistFollowedMember(memberId, postMemberId));
        }
        return post;
    }

    private boolean isCurrentMemberAdmin(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.isAdmin();
        } else {
            return false;
        }
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
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

    private void isExistBlockedMember(Long memberId, Long targetMemberId) {
        if (memberQueryRepository.isBlockedOrBlockingAndStatus(memberId, targetMemberId, BlockedMemberStatus.A)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private List<Long> getFollowedMovieByMemberId(Long memberId) {
        if (memberId != null) {
            return movieRepository.findAllIdByFollowingMemberId(memberId);
        } else{
            return new ArrayList<>();
        }
    }

    private boolean isExistFollowedMember(Long memberId, Long followedMemberId) {
        return memberQueryRepository.isExistFollowedMember(memberId, followedMemberId);
    }

    private boolean isExistLikedPost(Long memberId, Long postId) {
        return postQueryRepository.isExistLikedPost(memberId, postId);
    }
}
