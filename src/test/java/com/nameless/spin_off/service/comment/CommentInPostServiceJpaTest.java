package com.nameless.spin_off.service.comment;

import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.entity.comment.CommentInPost;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.comment.AlreadyLikedCommentInPostException;
import com.nameless.spin_off.exception.comment.NotExistCommentInPostException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.nameless.spin_off.enums.post.PostScoreEnum.POST_COMMENT;
import static com.nameless.spin_off.enums.post.PostScoreEnum.POST_VIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CommentInPostServiceJpaTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInPostService commentInPostService;
    @Autowired CommentInPostRepository commentInPostRepository;
    @Autowired EntityManager em;
    @Autowired MemberService memberService;

    @Test
    public void saveCommentInPostByCommentVO() throws Exception{
        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(po);
        postService.insertViewedPostByIp("22", po.getId());

        em.flush();
        em.clear();

        //when
        System.out.println("???????????????");
        Long commentId = commentInPostService.insertCommentInPostByCommentVO(
                new CommentDto.CommentInPostRequestDto(null, "???????????? ?????????"), mem.getId(), po.getId());

        System.out.println("??????????????????");
        em.flush();
        em.clear();
        postService.updateAllPopularity();
        em.flush();
        Post post = postRepository.findById(po.getId()).get();
        //then
        assertThat(post.getPopularity())
                .isEqualTo(POST_VIEW.getRate() * POST_VIEW.getLatestScore() + +
                        post.getCommentInPosts().size()*POST_COMMENT.getLatestScore()*POST_COMMENT.getRate());
        assertThat(post.getCommentInPosts().get(post.getCommentInPosts().size() - 1))
                .isEqualTo(commentInPostRepository.getById(commentId));
    }

    @Test
    public void ?????????_?????????() throws Exception{
        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of()).setHashTags(List.of()).build();
        postRepository.save(po);
        CommentInPost parent = CommentInPost.createCommentInPost(mem, "???????????? ?????????", null, po);

        em.flush();
        em.clear();
        //when

        System.out.println("???????????????1");
        CommentInPost childComment1 = commentInPostRepository.getById(
                commentInPostService.insertCommentInPostByCommentVO(
                        new CommentDto.CommentInPostRequestDto(
                                parent.getId(), "???????????? ?????????"), mem.getId(), po.getId()));

        System.out.println("???????????????2");
        CommentInPost childComment2 = commentInPostRepository.getById(
                commentInPostService.insertCommentInPostByCommentVO(
                        new CommentDto.CommentInPostRequestDto(
                                parent.getId(), "?????????????????? ?????????"), mem.getId(), po.getId()));

        em.flush();
        em.clear();

        System.out.println("?????????????????????");
        postService.insertViewedPostByIp("22", po.getId());
        em.flush();
        em.clear();
        postService.updateAllPopularity();
        CommentInPost parentComment = commentInPostRepository.findById(parent.getId()).get();

        System.out.println("??????????????????");
        Post post = postRepository.findById(po.getId()).get();

        //then
        assertThat(post.getCommentInPosts().size()).isEqualTo(3);
        assertThat(parentComment.getChildren().size()).isEqualTo(2);
        assertThat(parentComment.getChildren().get(0).getId()).isEqualTo(childComment1.getId());
        assertThat(parentComment.getChildren().get(1).getId()).isEqualTo(childComment2.getId());
        assertThat(childComment1.getParent().getId()).isEqualTo(parentComment.getId());
        assertThat(childComment2.getParent().getId()).isEqualTo(parentComment.getId());
        assertThat(post.getPopularity())
                .isEqualTo(POST_VIEW.getRate() * POST_VIEW.getLatestScore() +
                        post.getCommentInPosts().size() * POST_COMMENT.getLatestScore() * POST_COMMENT.getRate());
    }

    @Test
    public void ??????_?????????_????????????_????????????() throws Exception{

        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Post post = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(post);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem2);
        Post post2 = Post.buildPost().setMember(mem2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(post2);

        CommentDto.CommentInPostRequestDto commentInPostVO1 =
                new CommentDto.CommentInPostRequestDto(0L, "");
        CommentDto.CommentInPostRequestDto commentInPostVO2 =
                new CommentDto.CommentInPostRequestDto(0L, "");
        CommentDto.CommentInPostRequestDto commentInPostVO3 =
                new CommentDto.CommentInPostRequestDto(0L, "");

        //when

        //then
        assertThatThrownBy(() -> commentInPostService.insertCommentInPostByCommentVO(commentInPostVO2, mem.getId(), 0L))
                .isInstanceOf(NotExistPostException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInPostService.insertCommentInPostByCommentVO(commentInPostVO3, mem.getId(), post.getId()))
                .isInstanceOf(NotExistCommentInPostException.class);//.hasMessageContaining("")

        Long aLong = commentInPostService.insertCommentInPostByCommentVO(
                new CommentDto.CommentInPostRequestDto(null, ""), mem2.getId(), post.getId());
        memberService.insertBlockedMemberByMemberId(mem.getId(), mem2.getId(), BlockedMemberStatus.A);
        em.flush();
        assertThatThrownBy(() -> commentInPostService.insertCommentInPostByCommentVO(
                new CommentDto.CommentInPostRequestDto(null, ""), mem.getId(), post2.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> commentInPostService.insertCommentInPostByCommentVO(
                new CommentDto.CommentInPostRequestDto(aLong, ""), mem.getId(), post.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }

    @Test
    public void ?????????_?????????() throws Exception{
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of()).setHashTags(List.of()).build();
        postRepository.save(po);
        CommentInPost parent = CommentInPost.createCommentInPost(mem, "???????????? ?????????", null, po);

        em.flush();
        em.clear();

        //when

        System.out.println("???????????????");
        commentInPostService.insertLikedCommentByMemberId(member.getId(), po.getCommentInPosts().get(0).getId());

        System.out.println("????????????");
        CommentInPost comment = commentInPostRepository.getById(po.getCommentInPosts().get(0).getId());

        //then
        assertThat(comment.getId()).isEqualTo(po.getCommentInPosts().get(0).getId());
        assertThat(comment.getPost().getId()).isEqualTo(po.getId());
        assertThat(comment.getLikedCommentInPosts().size()).isEqualTo(1);
        assertThat(comment.getLikedCommentInPosts().get(0).getMember().getId()).isEqualTo(member.getId());

    }

    @Test
    public void ?????????_?????????_????????????() throws Exception{
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of()).setHashTags(List.of()).build();
        postRepository.save(po);
        CommentInPost parent = CommentInPost.createCommentInPost(mem, "???????????? ?????????", null, po);

        em.flush();
        em.clear();

        //when

        System.out.println("???????????????");
        commentInPostService.insertLikedCommentByMemberId(member.getId(), po.getCommentInPosts().get(0).getId());

        System.out.println("????????????");
        CommentInPost comment = commentInPostRepository.getById(po.getCommentInPosts().get(0).getId());

        //then
        assertThatThrownBy(() -> commentInPostService.insertLikedCommentByMemberId(member.getId(), -1L))
                .isInstanceOf(NotExistCommentInPostException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> commentInPostService.insertLikedCommentByMemberId(member.getId(), po.getCommentInPosts().get(0).getId()))
                .isInstanceOf(AlreadyLikedCommentInPostException.class);//.hasMessageContaining("")

        memberService.insertBlockedMemberByMemberId(member.getId(), mem.getId(), BlockedMemberStatus.A);
        em.flush();
        assertThatThrownBy(() -> commentInPostService.insertLikedCommentByMemberId(member.getId(), po.getCommentInPosts().get(0).getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }
}