package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.dto.CollectionDto.IdAndPublicCollectionDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.collection.FollowedCollection;
import com.nameless.spin_off.entity.collection.LikedCollection;
import com.nameless.spin_off.entity.collection.ViewedCollectionByIp;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.collection.CollectionScoreEnum;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.collection.FollowedCollectionRepository;
import com.nameless.spin_off.repository.collection.LikedCollectionRepository;
import com.nameless.spin_off.repository.collection.ViewedCollectionByIpRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nameless.spin_off.entity.enums.ContentsTimeEnum.VIEWED_BY_IP_MINUTE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CollectionServiceJpa implements CollectionService {
    private final CollectionRepository collectionRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final LikedCollectionRepository likedCollectionRepository;
    private final ViewedCollectionByIpRepository viewedCollectionByIpRepository;
    private final FollowedCollectionRepository followedCollectionRepository;

    @Transactional
    @Override
    public Long insertCollectionByCollectionVO(CreateCollectionVO collectionVO, Long memberId)
            throws NotExistMemberException, IncorrectTitleOfCollectionException, IncorrectContentOfCollectionException {

        return collectionRepository.save(Collection.createCollection(
                Member.createMember(memberId), collectionVO.getTitle(),
                collectionVO.getContent(), collectionVO.getPublicOfCollectionStatus())).getId();
    }

    @Transactional
    @Override
    public Long insertLikedCollectionByMemberId(Long memberId, Long collectionId)
            throws NotExistMemberException, NotExistCollectionException, AlreadyLikedCollectionException {

        hasAuthPost(memberId, collectionId, getPublicOfCollection(collectionId));
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

        hasAuthPost(memberId, collectionId, isCorrectIdAndGetPublic(memberId, collectionId));
        isExistFollowedCollection(memberId, collectionId);

        return followedCollectionRepository.save(
                FollowedCollection.createFollowedCollection(
                        Member.createMember(memberId), Collection.createCollection(collectionId))).getId();
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

    private PublicOfCollectionStatus getPublicOfCollection(Long collectionId) {
        return collectionQueryRepository.findPublicByCollectionId(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.DONT_HAVE_AUTHORITY));
    }

    private void hasAuthPost(Long memberId, Long collectionId, PublicOfCollectionStatus publicOfCollectionStatus) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (collectionQueryRepository.isBlockMembersCollection(memberId, collectionId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (!collectionQueryRepository.isFollowMembersCollection(memberId, collectionId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (!memberId.equals(collectionQueryRepository.findOwnerIdByCollectionId(collectionId).orElseGet(() -> null))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }

    private void isBlockedMemberCollection(Long memberId, Long collectionId) {
        if (collectionQueryRepository.isBlockMembersCollection(memberId, collectionId)) {
            throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
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
        IdAndPublicCollectionDto idAndPublic = collectionQueryRepository
                .findCollectionOwnerIdAndPublic(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));

        if (idAndPublic.getId().equals(memberId)) {
            throw new CantFollowOwnCollectionException(ErrorEnum.CANT_FOLLOW_OWN_COLLECTION);
        } else {
            return idAndPublic.getPublicOfCollectionStatus();
        }
    }

    private boolean isExistCollectionIp(Long collectionId, String ip) {
        return collectionQueryRepository.isExistIp(collectionId, ip, VIEWED_BY_IP_MINUTE.getDateTime());
    }

    private void isExistCollection(Long collectionId) {
        if (!collectionQueryRepository.isExist(collectionId)) {
            throw new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION);
        }
    }
}
