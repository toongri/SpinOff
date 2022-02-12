package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCommentInPostService implements CommentInPostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = false)
    public CommentInPost insertCommentInPostByCommentVO(CreateCommentInPostVO commentVO) throws NotExistMemberException, NotExistPostException, NotExistCommentInPostException {

        Member member = getMemberById(commentVO.getMemberId());
        Post post = getPostById(commentVO.getPostId());
        CommentInPost parent = post.getParentCommentById(commentVO.getParentId());

        CommentInPost commentInPost = CommentInPost.createCommentInPost(member, commentVO.getContent(), parent);
        post.addCommentInPost(commentInPost);

        return commentInPost;
    }

    private Member getMemberById(Long memberId) throws NotExistMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotExistMemberException::new);
    }

    private Post getPostById(Long postId) throws NotExistPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdIncludeCommentInPost(postId);

        return optionalPost.orElseThrow(NotExistPostException::new);
    }
}
