package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
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
import java.time.LocalDate;
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
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(memberRepository.save(Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build()));
        }
        Post post = postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build());

        Post post2 = postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.B)
                .setTitle("").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build());
        List<Long> commentIds = new ArrayList<>();

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                null, "ddd"), member.getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                null, "ddd"), memberList.get(0).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                null, "ddd"), memberList.get(1).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                null, "ddd"), memberList.get(2).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                null, "ddd"), memberList.get(3).getId(), post.getId()));

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                commentIds.get(0), "ddd"), member.getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                commentIds.get(0), "ddd"), memberList.get(0).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                commentIds.get(0), "ddd"), memberList.get(1).getId(), post.getId()));

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                commentIds.get(2), "ddd"), member.getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                commentIds.get(2), "ddd"), memberList.get(0).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                commentIds.get(2), "ddd"), memberList.get(1).getId(), post.getId()));

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

    @Test
    public void 글_댓글_좋아요_멤버출력() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();

        memberRepository.save(member);

        Member member2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();

        memberRepository.save(member2);

        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memcnam"+i).build());
        }
        memberRepository.saveAll(memberList);

        List<Post> postList = new ArrayList<>();

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.B)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        List<Long> commentIds = new ArrayList<>();

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CreateCommentInPostVO(
                null, "ddd"), member.getId(), postList.get(0).getId()));

        memberService.insertFollowedMemberByMemberId(member2.getId(), member.getId());
        em.flush();
        memberService.insertBlockedMemberByMemberId(member2.getId(), memberList.get(5).getId(), BlockedMemberStatus.A);
        em.flush();

        commentInPostService.insertLikedCommentByMemberId(member.getId(), commentIds.get(0));
        em.flush();
        commentInPostService.insertLikedCommentByMemberId(memberList.get(5).getId(), commentIds.get(0));
        em.flush();
        commentInPostService.insertLikedCommentByMemberId(member2.getId(), commentIds.get(0));
        em.flush();
        commentInPostService.insertLikedCommentByMemberId(memberList.get(3).getId(), commentIds.get(0));
        em.flush();
        commentInPostService.insertLikedCommentByMemberId(memberList.get(7).getId(), commentIds.get(0));
        em.flush();
        commentInPostService.insertLikedCommentByMemberId(memberList.get(9).getId(), commentIds.get(0));
        em.flush();
        commentInPostService.insertLikedCommentByMemberId(memberList.get(4).getId(), commentIds.get(0));
        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        List<MembersByContentDto> members =
                commentInPostQueryService.getLikeCommentMembers(member2.getId(), commentIds.get(0));
        System.out.println("서비스함수끝");

        //then
        assertThat(members.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(member2.getId(), member.getId(), memberList.get(4).getId(), memberList.get(9).getId(),
                        memberList.get(7).getId(), memberList.get(3).getId());

        assertThat(members.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly(true, false, false, false, false, false);

        assertThat(members.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, true, false, false, false, false);

    }
}