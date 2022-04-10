package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.CommentInCollectionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInCollectionQueryServiceJpa implements CommentInCollectionQueryService{

    private final CommentInCollectionQueryRepository commentInCollectionQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;

    @Override
    public List<ContentCommentDto> getCommentsByCollectionId(MemberDetails currentMember, Long collectionId) {

//        hasAuthCollection(memberId, collectionId, getPublicOfCollection(collectionId));
//        commentInCollectionQueryRepository.findAllByCollectionId()
        return null;
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
            if (!collectionQueryRepository.isFollowMembersCollection(memberId, collectionId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (!memberId.equals(collectionQueryRepository.findOwnerIdByCollectionId(collectionId).orElseGet(() -> null))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        }
    }
}
