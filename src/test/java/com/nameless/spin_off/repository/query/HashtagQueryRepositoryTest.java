package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.HashtagDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import com.nameless.spin_off.service.query.CollectionQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class HashtagQueryRepositoryTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired CollectionQueryService collectionQueryService;
    @Autowired MemberService memberService;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired HashtagQueryRepository hashtagQueryRepository;

    @Test
    public void 글에_포함된_해시태그_카운트() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(i+""));
        }
        hashtagRepository.saveAll(hashtagList);

        List<Post> postList = new ArrayList<>();

        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0))).setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1))).setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9)))
                .setCollections(List.of()).build());

        em.flush();

        postRepository.saveAll(postList);
        em.flush();


        for (int i = 1; i < 12; i++) {
            postList.get(0).insertViewedPostByIp(""+i%6);
            postList.get(1).insertViewedPostByIp(""+i%9);
            postList.get(2).insertViewedPostByIp(""+i%8);
            postList.get(3).insertViewedPostByIp(""+i%2);
            postList.get(4).insertViewedPostByIp(""+i%7);
            postList.get(5).insertViewedPostByIp(""+i%3);
            postList.get(6).insertViewedPostByIp(""+i%2);
            postList.get(7).insertViewedPostByIp(""+i%4);
            postList.get(8).insertViewedPostByIp(""+i%10);
            postList.get(9).insertViewedPostByIp(""+i%5);
            em.flush();
        }

        em.clear();

        //when
        System.out.println("서비스");
        List<HashtagDto.RelatedMostTaggedHashtagDto> content = hashtagQueryRepository.findAllByPostIds(
                6, postList.stream().map(Post::getId).collect(Collectors.toList()));
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(RelatedMostTaggedHashtagDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        hashtagList.get(0).getId(),
                        hashtagList.get(1).getId(),
                        hashtagList.get(2).getId(),
                        hashtagList.get(3).getId(),
                        hashtagList.get(4).getId(),
                        hashtagList.get(5).getId());

    }

    @Test
    public void 멤버가_작성한_포스트가_포함된_해시태그_카운트() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(i+""));
        }
        hashtagRepository.saveAll(hashtagList);

        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().setNickname(keyword+i).build());
        }
        memberRepository.saveAll(memberList);

        List<Post> postList = new ArrayList<>();

        postList.add(Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0))).setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(4)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1))).setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(1)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(7)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(8)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8)))
                .setCollections(List.of()).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9)))
                .setCollections(List.of()).build());

        em.flush();

        postRepository.saveAll(postList);
        em.flush();


        for (int i = 1; i < 12; i++) {
            postList.get(0).insertViewedPostByIp(""+i%6);
            postList.get(1).insertViewedPostByIp(""+i%9);
            postList.get(2).insertViewedPostByIp(""+i%8);
            postList.get(3).insertViewedPostByIp(""+i%2);
            postList.get(4).insertViewedPostByIp(""+i%7);
            postList.get(5).insertViewedPostByIp(""+i%3);
            postList.get(6).insertViewedPostByIp(""+i%2);
            postList.get(7).insertViewedPostByIp(""+i%4);
            postList.get(8).insertViewedPostByIp(""+i%10);
            postList.get(9).insertViewedPostByIp(""+i%5);
            em.flush();
        }

        em.clear();

        //when
        System.out.println("서비스");
        List<RelatedMostTaggedHashtagDto> content = hashtagQueryRepository.findAllByMemberIds(
                6, memberList.stream().map(Member::getId).collect(Collectors.toList()));
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(HashtagDto.RelatedMostTaggedHashtagDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        hashtagList.get(0).getId(),
                        hashtagList.get(1).getId(),
                        hashtagList.get(2).getId(),
                        hashtagList.get(3).getId(),
                        hashtagList.get(4).getId(),
                        hashtagList.get(5).getId());


    }
}