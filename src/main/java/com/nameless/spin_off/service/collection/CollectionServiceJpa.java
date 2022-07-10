package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.CollectionRequestDto;
import com.nameless.spin_off.dto.CollectionDto.OwnerIdAndPublicCollectionDto;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.dto.PostDto.PostIdAndOwnerIdAndPublicPostDto;
import com.nameless.spin_off.dto.PostDto.PostOwnerIdAndPublicPostDto;
import com.nameless.spin_off.entity.collection.*;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.collection.CollectionScoreEnum;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.*;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionServiceJpa implements CollectionService {
    private final PostQueryRepository postQueryRepository;
    private final CollectionRepository collectionRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final LikedCollectionRepository likedCollectionRepository;
    private final ViewedCollectionByIpRepository viewedCollectionByIpRepository;
    private final FollowedCollectionRepository followedCollectionRepository;
    private final CollectedPostRepository collectedPostRepository;
    private final MemberQueryRepository memberQueryRepository;

    @Transactional
    @Override
    public Long insertCollection(CollectionRequestDto collectionVO, Long memberId)
            throws NotExistMemberException, IncorrectTitleOfCollectionException, IncorrectContentOfCollectionException {

        return collectionRepository.save(Collection.createCollection(
                Member.createMember(memberId), collectionVO.getTitle(),
                collectionVO.getContent(), collectionVO.getPublicOfCollectionStatus())).getId();
    }

    @Transactional
    @Override
    public Long updateCollection(CollectionRequestDto collectionRequestDto, Long collectionId, Long memberId) {
        Collection collection = getCollectionById(collectionId);
        hasAuthCollection(memberId, collection);
        return checkChangeValue(collectionRequestDto, collection);
    }

    @Transactional
    @Override
    public Long insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, AlreadyLikedCollectionException {
        OwnerIdAndPublicCollectionDto collectionInfo = getCollectionInfo(collectionId);
        hasAuthCollection(memberId, collectionInfo.getPublicOfCollectionStatus(), collectionInfo.getCollectionOwnerId());
        isExistLikedCollection(memberId, collectionId);

        return likedCollectionRepository.save(
                LikedCollection.createLikedCollection(
                        Member.createMember(memberId),
                        Collection.createCollection(collectionId))).getId();
    }

    @Transactional
    @Override
    public Long insertViewedCollectionByIp(String ip, Long collectionId)
            throws NotExistCollectionException {

        if (!isExistCollectionIp(collectionId, ip)) {
            return viewedCollectionByIpRepository.
                    save(ViewedCollectionByIp
                            .createViewedCollectionByIp(ip, Collection.createCollection(collectionId))).getId();
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public Long insertFollowedCollectionByMemberId(Long memberId, Long collectionId) {
        OwnerIdAndPublicCollectionDto collectionInfo = getCollectionInfo(collectionId);

        isCanFollowCollection(memberId, collectionInfo);

        hasAuthCollection(memberId, collectionInfo.getPublicOfCollectionStatus(), collectionInfo.getCollectionOwnerId());
        isExistFollowedCollection(memberId, collectionId);

        return followedCollectionRepository.save(
                FollowedCollection.createFollowedCollection(
                        Member.createMember(memberId), Collection.createCollection(collectionId))).getId();
    }

    @Transactional
    @Override
    public int insertCollectedPost(Long currentMemberId, Long collectionId, List<Long> postIds) {
        isExistPosts(currentMemberId, postIds);
        isExistCollection(currentMemberId, collectionId);
        isAlreadyCollectedPost(collectionId);

        return collectedPostRepository.saveAll(getCollectedPostsByCollectionIdAndPostIds(collectionId, postIds)).size();
    }

    @Transactional
    @Override
    public Long deleteCollection(MemberDetails currentMember, Long collectionId) {
        Collection collection = getCollectionById(collectionId);
        hasAuthCollection(currentMember, collection.getMember().getId(), collection.getIsDefault());
        deleteCollectedPosts(collection);
        collectionRepository.delete(collection);
        return 1L;
    }

    @Transactional
    @Override
    public int updateAllPopularity() {
        List<Collection> collections = collectionQueryRepository
                .findAllByViewAfterTime(CollectionScoreEnum.COLLECTION_VIEW.getOldestDate());

        for (Collection collection : collections) {
            collection.updatePopularity();
        }
        return collections.size();
    }

    private void isCanFollowCollection(Long memberId, OwnerIdAndPublicCollectionDto collectionInfo) {
        if (memberId.equals(collectionInfo.getCollectionOwnerId())) {
            throw new CantFollowOwnCollectionException(ErrorEnum.CANT_FOLLOW_OWN_COLLECTION);
        }
    }
    private List<CollectedPost> getCollectedPostsByCollectionIdAndPostIds(Long collectionId, List<Long> postIds) {
        return postIds.stream()
                .map(p -> CollectedPost.createCollectedPost(Collection.createCollection(collectionId), Post.createPost(p)))
                .collect(Collectors.toList());
    }

    private Long checkChangeValue(CollectionRequestDto collectionRequestDto, Collection collection) {
        long cnt = 0L;
        if (!collectionRequestDto.getTitle().equals(collection.getTitle())) {
            if (collection.getIsDefault()) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
            cnt++;
            collection.updateTitle(collectionRequestDto.getTitle());
        }
        if (!collectionRequestDto.getContent().equals(collection.getContent())) {
            cnt++;
            collection.updateContent(collectionRequestDto.getContent());
        }
        if (!collectionRequestDto.getPublicOfCollectionStatus().equals(collection.getPublicOfCollectionStatus())) {
            cnt++;
            collection.updatePublicOfCollectionStatus(collectionRequestDto.getPublicOfCollectionStatus());
        }
        if (cnt > 0) {
            collection.updateLastModifiedDate();
        }
        return cnt;
    }

    private void hasAuthCollection(Long memberId, Collection collection) {
        if (!collection.getMember().getId().equals(memberId)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private OwnerIdAndPublicCollectionDto getCollectionInfo(Long collectionId) {
        return collectionQueryRepository.findCollectionOwnerIdAndPublic(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    private void deleteCollectedPosts(Collection collection) {
        List<CollectedPost> collectedPosts = collectedPostRepository.findAllByCollection(collection);
        if (!collectedPosts.isEmpty()) {
            collectedPostRepository.deleteAll(collectedPosts);
        }
    }

    private void hasAuthCollection(MemberDetails currentMember, Long memberId, boolean isDefault) {
        if (!(currentMember.isAdmin() || currentMember.getId().equals(memberId)) || isDefault) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
    }

    private Collection getCollectionById(Long collectionId) {
        return collectionRepository.findById(collectionId).orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    private void isAlreadyCollectedPost(Long collectionId) {
        if (collectionQueryRepository.countCollectedPostsByCollectionId(collectionId) != 0L) {
            throw new AlreadyCollectedPostException(ErrorEnum.ALREADY_COLLECTED_POST);
        }
    }

    private void isExistCollection(Long currentMemberId, Long collectionId) {
        if (!collectionQueryRepository.isExistCollectionByIdAndMemberId(collectionId, currentMemberId)) {
            throw new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION);
        }
    }

    private void isExistPosts(Long currentMemberId, List<Long> postIds) {

        List<PostOwnerIdAndPublicPostDto> posts = postQueryRepository.findAllOwnerIdAndPublicByPostIds(postIds);
        if (postIds.size() != posts.size()) {
            throw new NotExistPostException(ErrorEnum.NOT_EXIST_POST);
        }
        posts.forEach(p -> hasAuthPost(currentMemberId, p.getPublicOfPostStatus(), p.getPostOwnerId()));
    }
    private void hasAuthCollection(Long memberId, PublicOfCollectionStatus publicOfCollectionStatus, Long collectionOwnerId) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (memberQueryRepository.isBlockedOrBlockingAndStatus(memberId, collectionOwnerId, BlockedMemberStatus.A)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (!(memberId.equals(collectionOwnerId) || memberQueryRepository.isExistFollowedMember(memberId, collectionOwnerId))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (!memberId.equals(collectionOwnerId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }

    private void isExistLikedCollection(Long memberId, Long collectionId) {
        if (collectionQueryRepository.isExistLikedCollection(memberId, collectionId)) {
            throw new AlreadyLikedCollectionException(ErrorEnum.ALREADY_LIKED_COLLECTION);
        }
    }

    private void isExistFollowedCollection(Long memberId, Long collectionId) {
        if (collectionQueryRepository.isExistFollowedCollection(memberId, collectionId)) {
            throw new AlreadyFollowedCollectionException(ErrorEnum.ALREADY_FOLLOWED_COLLECTION);
        }
    }

    private void hasAuthPost(Long memberId, PublicOfPostStatus publicOfPostStatus, Long postOwnerId) {
        if (publicOfPostStatus.equals(PublicOfPostStatus.A)) {
            if (memberQueryRepository.isBlockedOrBlockingAndStatus(memberId, postOwnerId, BlockedMemberStatus.A)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfPostStatus.equals(PublicOfPostStatus.C)){
            if (!(memberId.equals(postOwnerId) || memberQueryRepository.isExistFollowedMember(memberId, postOwnerId))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfPostStatus.equals(PublicOfPostStatus.B)){
            if (!memberId.equals(postOwnerId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }

    private boolean isExistCollectionIp(Long collectionId, String ip) {
        return collectionQueryRepository.isExistIp(collectionId, ip, VIEWED_BY_IP_MINUTE.getDateTimeMinusMinutes());
    }
}
