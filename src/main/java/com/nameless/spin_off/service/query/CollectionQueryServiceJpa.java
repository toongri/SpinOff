package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.*;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto.CollectedPostDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
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
public class CollectionQueryServiceJpa implements CollectionQueryService {

    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberRepository memberRepository;
    private final HashtagQueryRepository hashtagQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final PostQueryRepository postQueryRepository;

    @Override
    public Slice<SearchAllCollectionDto> getSearchPageCollectionAtAllSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return collectionQueryRepository
                .findAllSlicedForSearchPageAtAll(keyword, pageable,
                        getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);
    }

    @Override
    public Slice<SearchCollectionDto> getSearchPageCollectionAtCollectionSliced(
            String keyword, Pageable pageable, Long memberId) throws NotExistMemberException {

        return collectionQueryRepository
                .findAllSlicedForSearchPageAtCollection(keyword, pageable, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);
    }

    @Override
    public SearchFirstDto<Slice<SearchCollectionDto>> getSearchPageCollectionAtCollectionSlicedFirst(
            String keyword, Pageable pageable, Long memberId, int length) throws NotExistMemberException {

        Slice<SearchCollectionDto> collections = collectionQueryRepository
                .findAllSlicedForSearchPageAtCollection(keyword, pageable, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId), memberId);

        return new SearchFirstDto<>(
                collections, hashtagQueryRepository.findAllByCollectionIds(length, collections.stream()
                .map(SearchCollectionDto::getCollectionId).collect(Collectors.toList())));
    }

    @Override
    public List<PostInCollectionDto> getCollectionsByMemberId(Long memberId) {
        return collectionQueryRepository.findAllByMemberIdOrderByCollectedPostDESC(memberId);
    }

    @Override
    public QuickPostInCollectionDto getCollectionNameByMemberId(Long memberId) {
        return collectionQueryRepository.findOneByMemberIdOrderByCollectedPostDESC(memberId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    @Override
    public Slice<MyPageCollectionDto> getCollectionsByMemberIdSliced(MemberDetails currentMember,
                                                                     Long targetMemberId, Pageable pageable) {
        if (currentMember != null) {
            Long currentMemberId = currentMember.getId();
            isExistBlockedMember(currentMemberId, targetMemberId);

            return collectionQueryRepository.findAllByMemberIdSliced(
                    targetMemberId,
                    pageable,
                    isExistFollowedMember(currentMemberId, targetMemberId),
                    hasAdminOfContent(currentMember, targetMemberId));
        } else {
            return collectionQueryRepository.findAllByMemberIdSliced(
                    targetMemberId, pageable, false, false);
        }
    }

    @Override
    public List<PostInCollectionDto> getCollectionsByMemberIdAndPostId(Long memberId, Long postId) {

        isExistPost(postId);

        List<Long> collectionsInPost = collectionQueryRepository.findAllIdByPostId(postId);

        List<PostInCollectionDto> collections =
                collectionQueryRepository.findAllByMemberIdOrderByCollectedPostDESC(memberId);

        collections.forEach(c -> c.setCollected(collectionsInPost.contains(c.getId())));

        return collections;
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsSlicedForMainPage(Pageable pageable, Long memberId)
            throws NotExistMemberException {

        return collectionQueryRepository.findAllSlicedForMainPage(pageable,
                memberId, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId));
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedMemberSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return collectionQueryRepository
                .findAllByFollowedMemberSlicedForMainPage(pageable, memberId);
    }

    @Override
    public Slice<MainPageCollectionDto> getCollectionsByFollowedCollectionsSlicedForMainPage(
            Pageable pageable, Long memberId) throws NotExistMemberException {

        return collectionQueryRepository
                .findAllByFollowedCollectionsSlicedForMainPage(pageable, memberId);
    }

    @Override
    public QuickPostInCollectionDto getCollectionNameByMemberIdAndPostId(Long memberId, Long postId) {
        isExistPost(postId);

        return collectionQueryRepository
                .findOneByMemberIdOrderByCollectedPostDESC(
                        memberId, collectionQueryRepository.findAllIdByPostId(postId))
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    @Override
    public ReadCollectionDto getCollectionForRead(MemberDetails currentMember, Long collectionId) {
        Long memberId = getCurrentMemberId(currentMember);
        return getReadCollectionDto(collectionId, memberId, getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId),
                isCurrentMemberAdmin(currentMember));
    }

    @Override
    public Slice<CollectedPostDto> getCollectedPostsSliced(MemberDetails currentMember, Long collectionId, Pageable pageable) {

        Long currentMemberId = getCurrentMemberId(currentMember);
        IdAndPublicCollectionDto publicAndMemberIdByCollectionId = getPublicAndMemberIdByCollectionId(collectionId);
        List<Long> blockedIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(currentMemberId);
        hasAuthCollection(currentMemberId, collectionId, publicAndMemberIdByCollectionId.getPublicOfCollectionStatus(),
                blockedIds.contains(publicAndMemberIdByCollectionId.getId()));
        if (currentMember != null) {
            return postQueryRepository.findAllCollectedPostByCollectionId(
                    pageable,
                    collectionId,
                    blockedIds,
                    isExistFollowedCollection(currentMemberId, collectionId),
                    hasAdminOfContent(currentMember, publicAndMemberIdByCollectionId.getId()));
        } else {
            return postQueryRepository.findAllCollectedPostByCollectionId(
                    pageable, collectionId, Collections.emptyList(), false, false);
        }
    }

    @Override
    public List<MembersByContentDto> getLikeCollectionMembers(Long memberId, Long collectionId) {
        List<Long> blockedIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        IdAndPublicCollectionDto publicAndMemberIdByCollectionId = getPublicAndMemberIdByCollectionId(collectionId);
        hasAuthCollection(memberId, collectionId, publicAndMemberIdByCollectionId.getPublicOfCollectionStatus(),
                blockedIds.contains(publicAndMemberIdByCollectionId.getId()));

        return getMembersByContentDtos(getLikeMembersByCollectionId(collectionId, blockedIds), memberId);
    }

    @Override
    public List<MembersByContentDto> getFollowCollectionMembers(Long memberId, Long collectionId) {
        List<Long> blockedIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        IdAndPublicCollectionDto publicAndMemberIdByCollectionId = getPublicAndMemberIdByCollectionId(collectionId);
        hasAuthCollection(memberId, collectionId, publicAndMemberIdByCollectionId.getPublicOfCollectionStatus(),
                blockedIds.contains(publicAndMemberIdByCollectionId.getId()));

        return getMembersByContentDtos(getFollowMembersByCollectionId(collectionId, blockedIds), memberId);
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

    private List<Long> getAllIdByFollowingMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else {
            return Collections.emptyList();
        }
    }

    private List<MembersByContentDto> getFollowMembersByCollectionId(Long collectionId, List<Long> blockedIds) {
        return collectionQueryRepository.findAllFollowMemberByCollectionId(collectionId, blockedIds);
    }

    private List<MembersByContentDto> getLikeMembersByCollectionId(Long collectionId, List<Long> blockedIds) {
        return collectionQueryRepository.findAllLikeMemberByCollectionId(collectionId, blockedIds);
    }

    private IdAndPublicCollectionDto getPublicAndMemberIdByCollectionId(Long collectionId) {
        return collectionQueryRepository.findCollectionOwnerIdAndPublic(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    private ReadCollectionDto getReadCollectionDto(Long collectionId, Long memberId,
                                               List<Long> blockedMemberIds, boolean isAdmin) {

        ReadCollectionDto collection = getOneByCollectionId(collectionId, blockedMemberIds);
        hasAuthCollection(memberId, collectionId, collection.getPublicOfCollectionStatus(),
                blockedMemberIds.contains(collection.getMember().getMemberId()));

        if (memberId != null) {
            collection.setAuth(memberId, isAdmin);
            collection.setLiked(isExistLikedCollection(memberId, collectionId));
            collection.setFollowed(isExistFollowedCollection(memberId, collectionId));
            collection.getMember().setFollowed(isExistFollowedMember(memberId, collection.getMember().getMemberId()));
        }
        return collection;
    }

    private void isExistPost(Long postId) {
        if (!postQueryRepository.isExist(postId)) {
            throw new NotExistPostException(ErrorEnum.NOT_EXIST_POST);
        }
    }

    private ReadCollectionDto getOneByCollectionId(Long collectionId, List<Long> blockedMemberIds) {
        return collectionQueryRepository.findByIdForRead(collectionId, blockedMemberIds)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
        } else{
            return Collections.emptyList();
        }
    }

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
    }

    private boolean isExistFollowedMember(Long memberId, Long followedMemberId) {
        if (memberId != null) {
            return memberQueryRepository.isExistFollowedMember(memberId, followedMemberId);
        } else{
            return false;
        }
    }

    private boolean isExistLikedCollection(Long memberId, Long collectionId) {
        return collectionQueryRepository.isExistLikedCollection(memberId, collectionId);
    }

    private boolean isExistFollowedCollection(Long memberId, Long collectionId) {
        return collectionQueryRepository.isExistFollowedCollection(memberId, collectionId);
    }

    private boolean isCurrentMemberAdmin(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.isAdmin();
        } else {
            return false;
        }
    }

    private void isExistBlockedMember(Long memberId, Long targetMemberId) {
        if (memberQueryRepository.isBlockedOrBlockingAndStatus(memberId, targetMemberId, BlockedMemberStatus.A)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private boolean hasAdminOfContent(MemberDetails currentMember, Long targetMemberId) {
        return currentMember.isAdmin() || currentMember.getId().equals(targetMemberId);
    }

    private void hasAuthCollection(Long memberId, Long collectionId, PublicOfCollectionStatus publicOfCollectionStatus,
                                   boolean isBlocked) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (memberId != null) {
                if (isBlocked) {
                    throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
                }
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (memberId == null) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            } else if (!collectionQueryRepository.isFollowMembersOrOwnerCollection(memberId, collectionId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (memberId == null) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            } else if (!memberId.equals(collectionQueryRepository.findOwnerIdByCollectionId(collectionId).orElseGet(() -> null))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }
}
