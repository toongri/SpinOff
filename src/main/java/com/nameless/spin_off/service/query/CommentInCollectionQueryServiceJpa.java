package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.CollectionIdAndOwnerIdAndPublicCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.OwnerIdAndPublicCollectionDto;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.CollectionQueryRepository;
import com.nameless.spin_off.repository.query.CommentInCollectionQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInCollectionQueryServiceJpa implements CommentInCollectionQueryService{

    private final CommentInCollectionQueryRepository commentInCollectionQueryRepository;
    private final CollectionQueryRepository collectionQueryRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final MemberRepository memberRepository;
    private List<Long> blockedMemberIds;
    private Map<Long, List<ContentCommentDto>> children;
    private List<Long> likedCommentIds;

    @Override
    public List<ContentCommentDto> getCommentsByCollectionId(MemberDetails currentMember, Long collectionId) {

        Long memberId = getCurrentMemberId(currentMember);
        boolean hasAuth = isAdmin(currentMember);
        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        OwnerIdAndPublicCollectionDto publicAndId = getPublicAndMemberIdByCollectionId(collectionId);
        hasAuthCollection(memberId, publicAndId.getPublicOfCollectionStatus(), publicAndId.getCollectionOwnerId());
        likedCommentIds = getAllLikedCommentIdByMemberId(memberId);

        List<ContentCommentDto> comments = commentInCollectionQueryRepository
                .findAllByCollectionId(collectionId, blockedMemberIds);

        comments.forEach(c -> c.setBoolean(
                                memberId,
                                hasAuth,
                                isExistLikedCommentInCollection(c.getCommentId())));

        children = comments.stream()
                .filter(c -> c.getParentId() != null && !blockedMemberIds.contains(c.getMember().getMemberId()))
                .collect(Collectors.groupingBy(ContentCommentDto::getParentId));

        List<ContentCommentDto> parents = comments.stream()
                .filter(this::isParentComment)
                .collect(Collectors.toList());

        parents.forEach(c -> c.setChildren(children.get(c.getCommentId())));

        return parents;
    }

    @Override
    public List<MembersByContentDto> getLikeCommentMembers(Long memberId, Long commentId) {
        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        CollectionIdAndOwnerIdAndPublicCollectionDto collectionInfo =
                getCollectionIdAndOwnerIdAndPublicCollectionDto(commentId);
        hasAuthCollection(memberId, collectionInfo.getPublicOfCollectionStatus(), collectionInfo.getCollectionOwnerId());

        return getMembersByContentDtos(getLikeMembersByCommentId(commentId), memberId);
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

    private List<MembersByContentDto> getLikeMembersByCommentId(Long commentId) {
        return commentInCollectionQueryRepository.findAllLikeMemberByCommentId(commentId, blockedMemberIds);
    }

    private List<Long> getAllIdByFollowingMemberId(Long memberId) {
        if (memberId != null) {
            return memberRepository.findAllIdByFollowingMemberId(memberId);
        } else {
            return Collections.emptyList();
        }
    }

    private List<Long> getAllLikedCommentIdByMemberId(Long memberId) {
        if (memberId != null) {
            return commentInCollectionQueryRepository.findAllLikedCommentIdByMemberId(memberId);
        } else {
            return Collections.emptyList();
        }
    }

    private Boolean isExistLikedCommentInCollection(Long commentId) {
        return likedCommentIds.contains(commentId);
    }

    private boolean isAdmin(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.isAdmin();
        }
        return false;
    }

    public boolean isParentComment(ContentCommentDto commentDto) {
        if (commentDto.getParentId() == null) {
            if (blockedMemberIds.contains(commentDto.getMember().getMemberId())) {
                if (children.get(commentDto.getCommentId()) != null) {
                    commentDto.setBlocked(true);
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private CollectionIdAndOwnerIdAndPublicCollectionDto getCollectionIdAndOwnerIdAndPublicCollectionDto(Long commentId) {
        return collectionQueryRepository.findPublicAndOwnerIdAndIdByCommentId(commentId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    private OwnerIdAndPublicCollectionDto getPublicAndMemberIdByCollectionId(Long collectionId) {
        return collectionQueryRepository.findCollectionOwnerIdAndPublic(collectionId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_COLLECTION));
    }

    private void hasAuthCollection(Long memberId, PublicOfCollectionStatus publicOfCollectionStatus, Long collectionOwnerId) {
        if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.A)) {
            if (memberId != null) {
                if (blockedMemberIds.contains(collectionOwnerId)) {
                    throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
                }
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.C)){
            if (memberId == null) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            } else if (!(memberId.equals(collectionOwnerId) || memberQueryRepository.isExistFollowedMember(memberId, collectionOwnerId))) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
        } else if (publicOfCollectionStatus.equals(PublicOfCollectionStatus.B)){
            if (memberId == null) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            } else if (!memberId.equals(collectionOwnerId)) {
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
