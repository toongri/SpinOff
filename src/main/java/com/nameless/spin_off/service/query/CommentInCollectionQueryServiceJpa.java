package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.entity.enums.ErrorEnum;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.CommentInCollectionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInCollectionQueryServiceJpa implements CommentInCollectionQueryService{

    private final CommentInCollectionQueryRepository commentInCollectionQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<ContentCommentDto> getCommentsByCollectionId(MemberDetails currentMember, Long collectionId) {

        Long memberId = getCurrentMemberId(currentMember);
        List<Long> blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        hasAuthCollection(memberId, collectionId, getPublicOfCollection(collectionId));
        List<ContentCommentDto> comments = commentInCollectionQueryRepository
                .findAllByCollectionId(collectionId, blockedMemberIds);



        Map<Long, List<ContentCommentDto>> children = comments.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(ContentCommentDto::getParentId));

        List<ContentCommentDto> parents = comments.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());

        parents.forEach(c -> c.setChildren(children.get(c.getCommentId())));


    }


    private PublicOfCollectionStatus getPublicOfCollection(Long collectionId) {
        return collectionQueryRepository.findPublicByCollectionId(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.DONT_HAVE_AUTHORITY));
    }

    private void hasAuthCollection(Long memberId, Long collectionId, PublicOfCollectionStatus publicOfCollectionStatus) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (memberId != null) {
                if (collectionQueryRepository.isBlockMembersCollection(memberId, collectionId)) {
                    throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
                }
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (memberId == null) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            } else if (!collectionQueryRepository.isFollowMembersCollection(memberId, collectionId)) {
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
}
