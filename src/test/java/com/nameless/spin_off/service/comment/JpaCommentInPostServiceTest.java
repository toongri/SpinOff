package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.NotSearchCollectionException;
import com.nameless.spin_off.exception.comment.NotSearchCommentInCollectionException;
import com.nameless.spin_off.exception.comment.NotSearchCommentInPostException;
import com.nameless.spin_off.exception.member.NotSearchMemberException;
import com.nameless.spin_off.exception.post.NotSearchPostException;
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

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        CommentInPost comment = commentInPostService.saveCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(mem.getId(), po.getId(), null, "야스히로 라할살"));

        System.out.println("포스트업로드");
        Post post = postRepository.findById(po.getId()).get();

        //then
        assertThat(post.getCommentCount()).isEqualTo(post.getCommentInPosts().size());
        assertThat(post.getCommentInPosts().get(post.getCommentInPosts().size() - 1)).isEqualTo(comment);
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
        CommentInPost childComment1 = commentInPostService.saveCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(mem.getId(), po.getId(), parent.getId(), "요지스타 라할살"));

        System.out.println("서비스함수2");
        CommentInPost childComment2 = commentInPostService.saveCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(mem.getId(), po.getId(), parent.getId(), "슈퍼스타검흰 라할살"));

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
    public void 댓글_저장시_멤버미스_예외처리() throws Exception{

        //given
        CommentDto.CreateCommentInPostVO commentInPostVO =
                new CommentDto.CreateCommentInPostVO(0L, 0L, 0L, "");

        //when

        //then
        assertThatThrownBy(() -> commentInPostService.saveCommentInPostByCommentVO(commentInPostVO))
                .isInstanceOf(NotSearchMemberException.class);//.hasMessageContaining("")

    }

    @Test
    public void 댓글_저장시_컬렉션미스_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        CommentDto.CreateCommentInPostVO commentInPostVO =
                new CommentDto.CreateCommentInPostVO(mem.getId(), 0L, 0L, "");

        //when

        //then
        assertThatThrownBy(() -> commentInPostService.saveCommentInPostByCommentVO(commentInPostVO))
                .isInstanceOf(NotSearchPostException.class);//.hasMessageContaining("")

    }

    @Test
    public void 댓글_저장시_부모댓글미스_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post post = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(post);

        CommentDto.CreateCommentInPostVO commentInPostVO =
                new CommentDto.CreateCommentInPostVO(mem.getId(), post.getId(), 0L, "");

        //when

        //then
        assertThatThrownBy(() -> commentInPostService.saveCommentInPostByCommentVO(commentInPostVO))
                .isInstanceOf(NotSearchCommentInPostException.class);//.hasMessageContaining("")


    }
}