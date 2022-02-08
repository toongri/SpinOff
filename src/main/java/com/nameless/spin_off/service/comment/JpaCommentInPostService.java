package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.comment.NotSearchCommentInPostException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaCommentInPostService implements CommentInPostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = false)
    public CommentInPost insertCommentInPostByCommentVO(CreateCommentInPostVO commentVO) throws NotSearchMemberException, NotSearchPostException, NotSearchCommentInPostException {

        Member member = getMemberById(commentVO.getMemberId());
        Post post = getPostById(commentVO.getPostId());
        CommentInPost parent = post.getParentCommentById(commentVO.getParentId());

        CommentInPost commentInPost = CommentInPost.createCommentInPost(member, commentVO.getContent(), parent);
        post.addCommentInPost(commentInPost);

        return commentInPost;
    }

    private Member getMemberById(Long memberId) throws NotSearchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NotSearchMemberException::new);
    }

    private Post getPostById(Long postId) throws NotSearchPostException {
        Optional<Post> optionalPost = postRepository.findOneByIdIncludeCommentInPost(postId);

        return optionalPost.orElseThrow(NotSearchPostException::new);
    }
}
