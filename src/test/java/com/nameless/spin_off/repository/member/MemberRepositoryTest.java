package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void findMembersByFollowingMemberId() throws Exception{

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
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember()
                    .setEmail("jhkimkkk0923@naver.com")
                    .setAccountId("memberAccId2")
                    .setName("memberName")
                    .setPhoneNumber("01011111111")
                    .setBirth(LocalDate.now())
                    .setAccountPw("memberAccountPw")
                    .setNickname("memcname").build());
        }

        List<Post> postList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
                    .setHashTags(List.of()).build());
        }
        postRepository.saveAll(postList);

        em.flush();
        em.clear();

        //when
        System.out.println("쿼리");
        List<Member> members = memberRepository.findAllByFollowingMemberId(member.getId());
        //then
        assertThat(members.size()).isEqualTo(memberList.size());
        for (Member member1 : members) {
            System.out.println("member1 = " + member1.getId());
        }
    }

    @Test
    public void findOneByIdIncludeFollowedMembers() throws Exception{
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
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember()
                    .setEmail("jhkimkkk0923@naver.com")
                    .setAccountId("memberAccId2")
                    .setName("memberName")
                    .setPhoneNumber("01011111111")
                    .setBirth(LocalDate.now())
                    .setAccountPw("memberAccountPw")
                    .setNickname("memcname").build());
        }

        List<Post> postList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
                    .setHashTags(List.of()).build());
        }
        postRepository.saveAll(postList);

        em.flush();
        em.clear();

        //when
        System.out.println("쿼리");
        Member member2 = memberRepository.findOneByIdWithFollowedMember(member.getId()).get();
        //then
        assertThat(member2.getFollowedMembers().size()).isEqualTo(memberList.size());

        for (FollowedMember member1 : member2.getFollowedMembers()) {
            System.out.println("member1 = " + member1.getMember().getId());
        }

    }
}
