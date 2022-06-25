package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.CollectionRequestDto;
import com.nameless.spin_off.dto.CollectionDto.OwnerIdAndPublicCollectionDto;
import com.nameless.spin_off.entity.collection.*;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.collection.CollectionScoreEnum;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.*;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;

@Service
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
        if (!collection.getMember().getId().equals(memberId)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
        }
        Long cnt = 0L;
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

    @Transactional
    @Override
    public Long insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, AlreadyLikedCollectionException {

        hasAuthCollection(memberId, collectionId, getPublicOfCollection(collectionId));
        isExistLikedCollection(memberId, collectionId);

        return likedCollectionRepository.save(
                LikedCollection.createLikedCollection(
                        Member.createMember(memberId), Collection.createCollection(collectionId))).getId();
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
    public Long insertFollowedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException,
            AlreadyFollowedCollectionException, CantFollowOwnCollectionException {

        hasAuthCollection(memberId, collectionId, isCorrectIdAndGetPublic(memberId, collectionId));
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

        List<CollectedPost> collectedPosts = postIds.stream()
                .map(p ->
                        CollectedPost.createCollectedPost(Collection.createCollection(collectionId), Post.createPost(p)))
                .collect(Collectors.toList());

        return collectedPostRepository.saveAll(collectedPosts).size();
    }

    @Transactional
    @Override
    public Long deleteCollection(MemberDetails currentMember, Long collectionId) {
        Collection collection = getCollectionById(collectionId);
        hasAuthCollection(currentMember, collection.getMember().getId(), collection.getIsDefault());
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
        if (postIds.size() != getCountExistPost(currentMemberId, postIds)) {
            throw new NotExistPostException(ErrorEnum.NOT_EXIST_POST);
        }
    }

    private Long getCountExistPost(Long currentMemberId, List<Long> postIds) {
        return postQueryRepository.countExistPostByPostIdAndMemberId(currentMemberId, postIds);
    }

    private PublicOfCollectionStatus getPublicOfCollection(Long collectionId) {
        return collectionQueryRepository.findPublicByCollectionId(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.DONT_HAVE_AUTHORITY));
    }

    private void hasAuthCollection(Long memberId, Long collectionId, PublicOfCollectionStatus publicOfCollectionStatus) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (collectionQueryRepository.isBlockMembersCollection(memberId, collectionId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (!collectionQueryRepository.isFollowMembersOrOwnerCollection(memberId, collectionId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (!memberId.equals(collectionQueryRepository.findOwnerIdByCollectionId(collectionId).orElseGet(() -> null))) {
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

    private PublicOfCollectionStatus isCorrectIdAndGetPublic(Long memberId, Long collectionId) {
        OwnerIdAndPublicCollectionDto idAndPublic = collectionQueryRepository
                .findCollectionOwnerIdAndPublic(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));

        if (idAndPublic.getCollectionOwnerId().equals(memberId)) {
            throw new CantFollowOwnCollectionException(ErrorEnum.CANT_FOLLOW_OWN_COLLECTION);
        } else {
            return idAndPublic.getPublicOfCollectionStatus();
        }
    }

    private boolean isExistCollectionIp(Long collectionId, String ip) {
        return collectionQueryRepository.isExistIp(collectionId, ip, VIEWED_BY_IP_MINUTE.getDateTimeMinusMinutes());
    }
}
