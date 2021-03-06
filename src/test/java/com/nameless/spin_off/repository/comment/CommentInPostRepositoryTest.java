package com.nameless.spin_off.repository.comment;

import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentInPostRepositoryTest {
    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInPostRepository commentInPostRepository;
    @Autowired EntityManager em;

    @Test
    public void findParentByPostIncludeChildrenOrderByDesc() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);
        Post post = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of()).setHashTags(List.of()).build();
        postRepository.save(post);
        CommentInPost parentComment = CommentInPost.createCommentInPost(member, "야스히로 라할살", null, post);
        CommentInPost childComment1 = CommentInPost.createCommentInPost(member, "요지스타 라할살", parentComment, post);
        em.flush();
        CommentInPost childComment2 = CommentInPost.createCommentInPost(member, "슈스검흰 라할살", parentComment, post);
        em.flush();
        //when
        List<CommentInPost> comments = commentInPostRepository.findParentsByPostWithChildrenOrderByIdDESC(post.getId());

        //then
        for (CommentInPost comment : comments) {
            assertThat(comment.getParent()).isNull();
            assertThat(comment.getPost()).isEqualTo(post);
            assertThat(comment.getMember()).isEqualTo(member);
            assertThat(comment.getChildren().size()).isEqualTo(2);

            for (CommentInPost child : comment.getChildren()) {
                assertThat(child.getParent()).isEqualTo(comment);
                assertThat(child.getPost()).isEqualTo(post);
                assertThat(child.getMember()).isEqualTo(member);

            }
        }
    }
}