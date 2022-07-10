package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CommentInPostRequestDto;
import com.nameless.spin_off.dto.PostDto.PostIdAndOwnerIdAndPublicPostDto;
import com.nameless.spin_off.dto.PostDto.PostOwnerIdAndPublicPostDto;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.comment.LikedCommentInPost;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.comment.LikedCommentInPostRepository;
import com.nameless.spin_off.repository.query.CommentInPostQueryRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import com.nameless.spin_off.repository.query.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInPostServiceJpa implements CommentInPostService {

    private final CommentInPostRepository commentInPostRepository;
    private final PostQueryRepository postQueryRepository;
    private final CommentInPostQueryRepository commentInPostQueryRepository;
    private final LikedCommentInPostRepository likedCommentInPostRepository;
    private final MemberQueryRepository memberQueryRepository;

    @Transactional
    @Override
    public Long insertCommentInPostByCommentVO(CommentInPostRequestDto commentVO, Long memberId, Long postId)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {

        PostOwnerIdAndPublicPostDto publicAndMemberIdByPostId = getPublicAndMemberIdByPostId(postId);
        hasAuthPost(memberId, publicAndMemberIdByPostId.getPublicOfPostStatus(), publicAndMemberIdByPostId.getPostOwnerId());
        isExistCommentInPost(commentVO.getParentId(), postId);
        isBlockMembersComment(memberId, commentVO.getParentId());

        return commentInPostRepository.save(CommentInPost
                .createCommentInPost(
                        Member.createMember(memberId),
                        commentVO.getContent(),
                        getParentCommentById(commentVO.getParentId()),
                        Post.createPost(postId))).getId();
    }

    @Transactional
    @Override
    public Long insertLikedCommentByMemberId(Long memberId, Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        PostIdAndOwnerIdAndPublicPostDto postInfo = getPostIdAndOwnerIdAndPublicPostDto(commentId);
        hasAuthPost(memberId, postInfo.getPublicOfPostStatus(), postInfo.getPostOwnerId());
        isBlockMembersComment(memberId, commentId);
        isExistLikedCommentInPost(memberId, commentId);

        return likedCommentInPostRepository.save(
                LikedCommentInPost.createLikedCommentInPost(
                        Member.createMember(memberId),
                        CommentInPost.createCommentInPost(commentId))).getId();
    }

    private PostIdAndOwnerIdAndPublicPostDto getPostIdAndOwnerIdAndPublicPostDto(Long commentId) {
        return commentInPostQueryRepository.findPublicAndOwnerIdAndIdByCommentId(commentId)
                .orElseThrow(() -> new NotExistCommentInPostException(ErrorEnum.NOT_EXIST_COMMENT_IN_POST));
    }

    private PostOwnerIdAndPublicPostDto getPublicAndMemberIdByPostId(Long postId) {
        return postQueryRepository.findOwnerIdAndPublicByPostId(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }

    private PublicOfPostStatus getPublicOfPost(Long postId) {
        return postQueryRepository.findPublicByPostId(postId)
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST));
    }

    private void isBlockMembersComment(Long memberId, Long commentId) {
        if (commentId != null) {
            if (commentInPostQueryRepository.isBlockMembersComment(memberId, commentId)) {
                throw new DontHaveAuthorityException(ErrorEnum.DONT_HAVE_AUTHORITY);
            }
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

    private void isExistCommentInPost(Long commentId, Long postId) {
        if (commentId == null) {

        } else if (!commentInPostQueryRepository.isExistInPost(commentId, postId)) {
            throw new NotExistCommentInPostException(ErrorEnum.NOT_EXIST_COMMENT_IN_POST);
        }
    }

    private CommentInPost getParentCommentById(Long parentId) {
        return parentId == null ? null : CommentInPost.createCommentInPost(parentId);
    }

    private void isExistLikedCommentInPost(Long memberId, Long commentId) {
        if (commentInPostQueryRepository.isExistLikedCommentInPost(memberId, commentId)) {
            throw new AlreadyLikedCommentInPostException(ErrorEnum.ALREADY_LIKED_COMMENT_IN_POST);
        }
    }
}
