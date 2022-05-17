package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto.PostIdAndOwnerIdAndPublicPostDto;
import com.nameless.spin_off.dto.PostDto.PostOwnerIdAndPublicPostDto;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.query.CommentInPostQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
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
public class CommentInPostQueryServiceJpa implements CommentInPostQueryService{

    private final CommentInPostQueryRepository commentInPostQueryRepository;
    private final PostQueryRepository postQueryRepository;
    private final MemberRepository memberRepository;
    private List<Long> blockedMemberIds;
    private Map<Long, List<ContentCommentDto>> children;
    private List<Long> likedCommentIds;

    @Override
    public List<ContentCommentDto> getCommentsByPostId(MemberDetails currentMember, Long postId) {
        Long memberId = getCurrentMemberId(currentMember);
        boolean hasAuth = isAdmin(currentMember);
        blockedMemberIds = getBlockingAllAndBlockedAllByIdAndBlockStatusA(memberId);
        PostOwnerIdAndPublicPostDto publicAndMemberIdByPostId = getPublicAndMemberIdByPostId(postId);
        hasAuthPost(memberId, postId, publicAndMemberIdByPostId.getPublicOfPostStatus(),
                publicAndMemberIdByPostId.getPostOwnerId());
        likedCommentIds = getAllLikedCommentIdByMemberId(memberId);

        List<ContentCommentDto> comments = commentInPostQueryRepository.findAllByCollectionId(postId, blockedMemberIds);

        comments.forEach(c -> c.setBoolean(
                memberId,
                hasAuth,
                isExistLikedCommentInPost(c.getCommentId())));

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
        PostIdAndOwnerIdAndPublicPostDto postInfo = getPostIdAndOwnerIdAndPublicPostDto(commentId);
        hasAuthPost(memberId, postInfo.getPostId(), postInfo.getPublicOfPostStatus(),
                postInfo.getPostOwnerId());

        return getMembersByContentDtos(getLikeMembersByCommentId(commentId), memberId);
    }

    private List<MembersByContentDto> getLikeMembersByCommentId(Long commentId) {
        return commentInPostQueryRepository.findAllLikeMemberByCommentId(commentId, blockedMemberIds);
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

    private Long getCurrentMemberId(MemberDetails currentMember) {
        if (currentMember != null) {
            return currentMember.getId();
        } else {
            return null;
        }
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

    private PostIdAndOwnerIdAndPublicPostDto getPostIdAndOwnerIdAndPublicPostDto(Long commentId) {
        return commentInPostQueryRepository.findPublicAndOwnerIdAndIdByCommentId(commentId)
                .orElseThrow(() -> new NotExistCollectionException(ErrorEnum.NOT_EXIST_POST));
    }

    private PostOwnerIdAndPublicPostDto getPublicAndMemberIdByPostId(Long postId) {
        return postQueryRepository.findOwnerIdAndPublicByPostId(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
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

    private PublicOfPostStatus getPublicOfPost(Long postId) {
        return postQueryRepository.findPublicByPostId(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }


    private List<Long> getAllLikedCommentIdByMemberId(Long memberId) {
        if (memberId != null) {
            return commentInPostQueryRepository.findAllLikedCommentIdByMemberId(memberId);
        } else {
            return Collections.emptyList();
        }
    }

    private Boolean isExistLikedCommentInPost(Long commentId) {
        return likedCommentIds.contains(commentId);
    }

    private List<Long> getBlockingAllAndBlockedAllByIdAndBlockStatusA(Long memberId) {
        if (memberId != null) {
            return memberRepository.findBlockingAllAndBlockedAllByIdAndBlockStatus(memberId, BlockedMemberStatus.A);
        } else{
            return Collections.emptyList();
        }
    }
}
