package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto.*;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
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

import java.util.Collections;
import java.util.Comparator;
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
    private List<Long> blockedMemberIds;

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
                        getFollowedMovieByMemberId(memberId), getAllIdByFollowingMemberId(memberId),
                        getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);
    }

    @Override
    public Slice<MainPagePostDto> getPostsByFollowedMovieSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return postQueryRepository
                .findAllByFollowedMoviesSlicedForMainPage(pageable,
                        getAllIdByFollowingMemberId(memberId), getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);
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
    public ReadPostDto getPostForRead(MemberDetails currentMember, Long postId) {

        Long memberId = getCurrentMemberId(currentMember);

        return getReadPostDto(postId, memberId, isCurrentMemberAdmin(currentMember));
    }

    @Override
    public Slice<RelatedPostDto> getRelatedPostsSliced(Long memberId, Long postId, Pageable pageable) {

        return postQueryRepository.findAllRelatedPostByPostId(pageable,
                getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), postId);
    }

    @Override
    public Slice<MyPagePostDto> getPostsByMemberIdSliced(MemberDetails currentMember, Long targetMemberId, Pageable pageable) {

        if (currentMember != null) {
            Long currentMemberId = currentMember.getId();
            isExistBlockedMember(currentMemberId, targetMemberId);

            return postQueryRepository.findAllByMemberIdSliced(
                    targetMemberId,
                    pageable,
                    isExistFollowedMember(currentMemberId, targetMemberId),
                    isCurrentMemberAdminAtMyPage(currentMember, targetMemberId));
        } else {
            return postQueryRepository.findAllByMemberIdSliced(
                    targetMemberId, pageable, false, false);
        }
    }

    @Override
    public List<MembersByContentDto> getLikePostMembers(Long memberId, Long postId) {
        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        PostOwnerIdAndPublicPostDto publicAndMemberIdByCollectionId = getPublicAndMemberIdByPostId(postId);
        hasAuthPost(memberId, postId, publicAndMemberIdByCollectionId.getPublicOfPostStatus(),
                publicAndMemberIdByCollectionId.getPostOwnerId());

        return getMembersByContentDtos(getLikeMembersByPostId(postId), memberId);
    }

    private List<MembersByContentDto> getLikeMembersByPostId(Long postId) {
        return postQueryRepository.findAllLikeMemberByPostId(postId, blockedMemberIds);
    }

    private List<MembersByContentDto> getMembersByContentDtos(List<MembersByContentDto> members, Long memberId) {
        List<Long> followingMemberIds = getAllIdByFollowingMemberId(memberId);
        members.forEach(m -> m.setFollowedAndOwn(followingMemberIds.contains(m.getMemberId()), memberId));
        return members.stream()
                .sorted(
                        Comparator.comparing(MembersByContentDto::isOwn)
                                .thenComparing(MembersByContentDto::isFollowed).reversed())
                .collect(Collectors.toList());
    }

    private ReadPostDto getOneByPostId(Long postId) {
        return postQueryRepository.findByIdForRead(postId, blockedMemberIds)
                .orElseGet(() -> postQueryRepository.findByIdForRead(postId)
                        .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST)));
    }

    private ReadPostDto getReadPostDto(Long postId, Long memberId, boolean isAdmin) {
        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        ReadPostDto post = getOneByPostId(postId);
        hasAuthPost(memberId, postId, post.getPublicOfPostStatus(), post.getMember().getMemberId());
        post.setHashtags(hashtagQueryRepository.findAllByPostId(postId));

        if (memberId != null) {
            post.setAuth(memberId, isAdmin);
            post.setLiked(isExistLikedPost(memberId, postId));
            post.getMember().setFollowed(isExistFollowedMember(memberId, post.getMember().getMemberId()));
        }
        return post;
    }

    private PostOwnerIdAndPublicPostDto getPublicAndMemberIdByPostId(Long postId) {
        return postQueryRepository.findOwnerIdAndPublicByPostId(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }

    private boolean isCurrentMemberAdminAtMyPage(MemberDetails currentMember, Long targetMemberId) {
        return currentMember.isAdmin() || currentMember.getId().equals(targetMemberId);
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
            return Collections.emptyList();
        }
    }

    private List<Long> getAllIdByFollowingMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else{
            return Collections.emptyList();
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
            return Collections.emptyList();
        }
    }

    private boolean isExistFollowedMember(Long memberId, Long followedMemberId) {
        return memberQueryRepository.isExistFollowedMember(memberId, followedMemberId);
    }

    private boolean isExistLikedPost(Long memberId, Long postId) {
        return postQueryRepository.isExistLikedPost(memberId, postId);
    }

    private void hasAuthPost(Long memberId, Long postId, PublicOfPostStatus publicOfPostStatus, Long postOwnerId) {
        if (publicOfPostStatus.equals(PublicOfPostStatus.A)) {
            if (memberId != null) {
                if (blockedMemberIds.contains(postOwnerId)) {
                    throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
                }
            }
        } else if (publicOfPostStatus.equals(PublicOfPostStatus.C)){
            if (memberId == null) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            } else if (!postQueryRepository.isFollowMembersOrOwnerPost(memberId, postId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfPostStatus.equals(PublicOfPostStatus.B)){
            if (memberId == null) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            } else if (!memberId.equals(postQueryRepository.findOwnerIdByPostId(postId).orElseGet(() -> null))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }
}
