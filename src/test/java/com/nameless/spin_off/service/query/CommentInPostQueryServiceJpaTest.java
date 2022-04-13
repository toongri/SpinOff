package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CommentInPostQueryServiceJpaTest {

    @Autowired CommentInCollectionService commentInCollectionService;
    @Autowired CommentInPostService commentInPostService;
    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired CollectionQueryService collectionQueryService;
    @Autowired MemberService memberService;
    @Autowired CommentInCollectionQueryService commentInCollectionQueryService;
    @Autowired CommentInPostQueryService commentInPostQueryService;

    @Test
    public void 댓글조회() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(memberRepository.save(Member.buildMember().build()));
        }
        Post post = postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build());

        Post post2 = postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.B)
                .setTitle("").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build());
        List<Long> commentIds = new ArrayList<>();

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), null, "ddd"), member.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), null, "ddd"), memberList.get(0).getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), null, "ddd"), memberList.get(1).getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), null, "ddd"), memberList.get(2).getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), null, "ddd"), memberList.get(3).getId()));

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), commentIds.get(0), "ddd"), member.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), commentIds.get(0), "ddd"), memberList.get(0).getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), commentIds.get(0), "ddd"), memberList.get(1).getId()));

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), commentIds.get(2), "ddd"), member.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), commentIds.get(2), "ddd"), memberList.get(0).getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                post.getId(), commentIds.get(2), "ddd"), memberList.get(1).getId()));

        commentInPostService.insertLikedCommentByMemberId(memberList.get(1).getId(), commentIds.get(1));
        commentInPostService.insertLikedCommentByMemberId(member.getId(), commentIds.get(0));
        commentInPostService.insertLikedCommentByMemberId(memberList.get(2).getId(), commentIds.get(0));
        commentInPostService.insertLikedCommentByMemberId(member.getId(), commentIds.get(7));

        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(1).getId(), BlockedMemberStatus.A);

        //when
        System.out.println("서비스함수");
        List<CommentDto.ContentCommentDto> comments =
                commentInPostQueryService.getCommentsByPostId(MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), post.getId());
        System.out.println("서비스함수끝");

        List<CommentDto.ContentCommentDto> comments2 =
                commentInPostQueryService.getCommentsByPostId(
                        null, post.getId());

        //then
        assertThat(comments.size()).isEqualTo(5);
        assertThat(comments.stream().map(CommentDto.ContentCommentDto::getCommentId)).containsExactly(
                commentIds.get(4), commentIds.get(3), commentIds.get(2), commentIds.get(1), commentIds.get(0));
        assertThat(comments.stream().map(CommentDto.ContentCommentDto::getLikeSize)).containsExactly(
                0L, 0L, 0L, 0L, 2L);
        assertThat(comments.get(4).isLiked()).isTrue();
        assertThat(comments.get(2).isBlocked()).isTrue();
        assertThat(comments.get(4).isHasAuth()).isTrue();
        assertThat(comments.get(4).getChildren().stream().map(CommentDto.ContentCommentDto::getCommentId)).containsExactly(
                commentIds.get(6), commentIds.get(5));

        assertThat(comments2.size()).isEqualTo(5);
        assertThat(comments2.stream().map(CommentDto.ContentCommentDto::getCommentId)).containsExactly(
                commentIds.get(4), commentIds.get(3), commentIds.get(2), commentIds.get(1), commentIds.get(0));
        assertThat(comments2.stream().map(CommentDto.ContentCommentDto::getLikeSize)).containsExactly(
                0L, 0L, 0L, 1L, 2L);
        assertThat(comments2.get(4).isLiked()).isFalse();
        assertThat(comments2.get(2).isBlocked()).isFalse();
        assertThat(comments2.get(4).isHasAuth()).isFalse();
        assertThat(comments2.get(4).getChildren().stream().map(CommentDto.ContentCommentDto::getCommentId)).containsExactly(
                commentIds.get(7), commentIds.get(6), commentIds.get(5));
    }
}