package com.nameless.spin_off.repository.member;

import com.nameless.spin_off.entity.member.FollowedMember;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired FollowedMemberRepository followedMemberRepository;
    @Autowired EntityManager em;

    @Test
    public void findMembersByFollowingMemberId() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }

        List<Post> postList = new ArrayList<>();
        List<FollowedMember> followedMembers = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            followedMembers.add(FollowedMember.createFollowedMember(member, mem));
            postList.add(Post.buildPost().setMember(mem).build());
        }
        followedMemberRepository.saveAll(followedMembers);
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
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }

        List<Post> postList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            postList.add(Post.buildPost().setMember(mem).build());
        }
        postRepository.saveAll(postList);

        em.flush();
        em.clear();

        //when
        System.out.println("쿼리");
        Member member2 = memberRepository.findOneByIdIncludeFollowedMember(member.getId()).get();
        //then
        assertThat(member2.getFollowedMembers().size()).isEqualTo(memberList.size());

        for (FollowedMember member1 : member2.getFollowedMembers()) {
            System.out.println("member1 = " + member1.getMember().getId());
        }

    }
}
