package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaCommentInPostServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired LikedPostRepository likedPostRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInPostService commentInPostService;
    @Autowired CommentInPostRepository commentInPostRepository;


    @Test
    public void 글_댓글_체크() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Post post = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(post);

        //when
        CommentInPost comment = commentInPostService.saveCommentInPostByCommentVO(new CommentDto.CreateCommentVO(member.getId(), post.getId(), null, "야스히로 라할살"));
        //then
        assertThat(post.getCommentCount()).isEqualTo(post.getCommentInPosts().size());
        assertThat(post.getCommentInPosts().get(post.getCommentInPosts().size() - 1)).isEqualTo(comment);
    }

    @Test
    public void 대댓글_체크() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Post post = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(post);
        CommentInPost parentComment = CommentInPost.createCommentInPost(member, "야스히로 라할살", null);
        post.addCommentInPost(parentComment);
        commentInPostRepository.save(parentComment);
        //when

        CommentInPost childComment1 = commentInPostService.saveCommentInPostByCommentVO(new CommentDto.CreateCommentVO(member.getId(), post.getId(), parentComment.getId(), "요지스타 라할살"));
        CommentInPost childComment2 = commentInPostService.saveCommentInPostByCommentVO(new CommentDto.CreateCommentVO(member.getId(), post.getId(), parentComment.getId(), "슈퍼스타검흰 라할살"));

        //then

        assertThat(post.getCommentCount()).isEqualTo(post.getCommentInPosts().size());
        assertThat(post.getCommentInPosts().size()).isEqualTo(3);
        assertThat(parentComment.getChildren().size()).isEqualTo(2);
        assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment1);
        assertThat(parentComment.getChildren().get(1)).isEqualTo(childComment2);
        assertThat(childComment1.getParent()).isEqualTo(parentComment);
        assertThat(childComment2.getParent()).isEqualTo(parentComment);
    }
}