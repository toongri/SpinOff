package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.comment.LikedCommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.comment.LikedCommentInPostRepository;
import com.nameless.spin_off.repository.query.CommentInPostQueryRepository;
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

    @Transactional
    @Override
    public Long insertCommentInPostByCommentVO(CreateCommentInPostVO commentVO, Long memberId)
            throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {

        isExistPost(commentVO.getPostId());
        isExistComment(commentVO.getParentId());
        return commentInPostRepository.save(CommentInPost
                .createCommentInPost(
                        Member.createMember(memberId),
                        commentVO.getContent(),
                        getParentCommentById(commentVO.getParentId()),
                        Post.createPost(commentVO.getPostId()))).getId();
    }

    @Transactional
    @Override
    public Long insertLikedCommentByMemberId(Long memberId, Long commentId)
            throws NotExistMemberException, NotExistCommentInPostException, AlreadyLikedCommentInPostException {

        isExistComment(commentId);
        isExistLikedCommentInPost(memberId, commentId);

        return likedCommentInPostRepository.save(
                LikedCommentInPost.createLikedCommentInPost(
                        Member.createMember(memberId),
                        CommentInPost.createCommentInPost(commentId))).getId();
    }

    private void isExistPost(Long postId) {
        if (!postQueryRepository.isExist(postId)) {
            throw new NotExistPostException();
        }
    }

    private void isExistComment(Long commentId) {
        if (commentId == null) {

        } else if (!commentInPostQueryRepository.isExist(commentId)) {
            throw new NotExistCommentInPostException();
        }
    }

    private CommentInPost getParentCommentById(Long parentId) {
        return parentId == null ? null : CommentInPost.createCommentInPost(parentId);
    }

    private void isExistLikedCommentInPost(Long memberId, Long commentId) {
        if (commentInPostQueryRepository.isExistLikedCommentInPost(memberId, commentId)) {
            throw new AlreadyLikedCommentInPostException();
        }
    }
}