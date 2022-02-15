package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CommentInPostServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired LikedPostRepository likedPostRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInPostService commentInPostService;
    @Autowired CommentInPostRepository commentInPostRepository;
    @Autowired EntityManager em;


    @Test
    public void saveCommentInPostByCommentVO() throws Exception{
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(po);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long commentId = commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(mem.getId(), po.getId(), null, "야스히로 라할살"));

        System.out.println("포스트업로드");
        Post post = postRepository.findById(po.getId()).get();

        //then
        assertThat(post.getCommentCount()).isEqualTo(post.getCommentInPosts().size());
        assertThat(post.getCommentInPosts().get(post.getCommentInPosts().size() - 1)).isEqualTo(commentInPostRepository.getById(commentId));
    }

    @Test
    public void 대댓글_테스트() throws Exception{
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(po);
        CommentInPost parent = CommentInPost.createCommentInPost(mem, "야스히로 라할살", null);
        po.addCommentInPost(parent);
        commentInPostRepository.save(parent);

        em.flush();
        em.clear();
        //when

        System.out.println("서비스함수1");
        CommentInPost childComment1 = commentInPostRepository.getById(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(mem.getId(), po.getId(), parent.getId(), "요지스타 라할살")));

        System.out.println("서비스함수2");
        CommentInPost childComment2 = commentInPostRepository.getById(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(mem.getId(), po.getId(), parent.getId(), "슈퍼스타검흰 라할살")));

        System.out.println("부모댓글업로드");
        CommentInPost parentComment = commentInPostRepository.findById(parent.getId()).get();

        System.out.println("포스트업로드");
        Post post = postRepository.findById(po.getId()).get();

        //then

        assertThat(post.getCommentCount()).isEqualTo(post.getCommentInPosts().size());
        assertThat(post.getCommentInPosts().size()).isEqualTo(3);
        assertThat(parentComment.getChildren().size()).isEqualTo(2);
        assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment1);
        assertThat(parentComment.getChildren().get(1)).isEqualTo(childComment2);
        assertThat(childComment1.getParent()).isEqualTo(parentComment);
        assertThat(childComment2.getParent()).isEqualTo(parentComment);
    }

    @Test
    public void 댓글_저장시_파라미터_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post post = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(post);

        CommentDto.CreateCommentInPostVO commentInPostVO1 =
                new CommentDto.CreateCommentInPostVO(0L, 0L, 0L, "");
        CommentDto.CreateCommentInPostVO commentInPostVO2 =
                new CommentDto.CreateCommentInPostVO(mem.getId(), 0L, 0L, "");
        CommentDto.CreateCommentInPostVO commentInPostVO3 =
                new CommentDto.CreateCommentInPostVO(mem.getId(), post.getId(), 0L, "");

        //when

        //then
        assertThatThrownBy(() -> commentInPostService.insertCommentInPostByCommentVO(commentInPostVO1))
                .isInstanceOf(NotExistMemberException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInPostService.insertCommentInPostByCommentVO(commentInPostVO2))
                .isInstanceOf(NotExistPostException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInPostService.insertCommentInPostByCommentVO(commentInPostVO3))
                .isInstanceOf(NotExistCommentInPostException.class);//.hasMessageContaining("")


    }
}