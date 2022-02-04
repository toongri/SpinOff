package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto.CreateCommentInPostVO;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.comment.NoSuchCommentInPostException;
import com.nameless.spin_off.exception.member.NoSuchMemberException;
import com.nameless.spin_off.exception.post.NoSuchPostException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
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
    private final MovieRepository movieRepository;
    private final HashtagRepository hashtagRepository;
    private final CollectionRepository collectionRepository;
    private final CommentInPostRepository commentInPostRepository;

    @Override
    @Transactional(readOnly = false)
    public CommentInPost saveCommentInPostByCommentVO(CreateCommentInPostVO commentVO) throws NoSuchMemberException, NoSuchPostException, NoSuchCommentInPostException {

        Member member = getMemberById(commentVO.getMemberId());
        Post post = getPostById(commentVO.getPostId());
        CommentInPost parent = getCommentInPostById(commentVO.getParentId());
        CommentInPost commentInPost = CommentInPost.createCommentInPost(member, commentVO.getContent(), parent);
        post.addCommentInPost(commentInPost);

        return commentInPost;
    }

    private CommentInPost getCommentInPostById(Long commentInPostId) throws NoSuchCommentInPostException {
        if (commentInPostId == null)
            return null;

        Optional<CommentInPost> optionalCommentInPost = commentInPostRepository.findById(commentInPostId);

        return optionalCommentInPost.orElseThrow(NoSuchCommentInPostException::new);
    }

    private Member getMemberById(Long memberId) throws NoSuchMemberException {
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        return optionalMember.orElseThrow(NoSuchMemberException::new);
    }

    private Post getPostById(Long postId) throws NoSuchPostException {
        Optional<Post> optionalPost = postRepository.findById(postId);

        return optionalPost.orElseThrow(NoSuchPostException::new);
    }
}
