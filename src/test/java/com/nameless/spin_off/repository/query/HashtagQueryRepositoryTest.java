package com.nameless.spin_off.repository.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.movie.GenreOfMovieStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
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

import static com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus.A;
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
    @Autowired MovieRepository movieRepository;

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
        List<RelatedMostTaggedHashtagDto> content = hashtagQueryRepository.findAllByPostIds(
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
    public void 컬렉션에_포함된_해시태그_카운트() throws Exception{
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
        List<Collection> collectionList = new ArrayList<>();

        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(keyword, "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }

        postList.add(Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0)))
                .setCollections(List.of(collectionList.get(0))).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(4)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1)))
                .setCollections(List.of(collectionList.get(0), collectionList.get(1))).build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2)))
                .setCollections(List.of(collectionList.get(0), collectionList.get(1), collectionList.get(2)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(1)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(7)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(8)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5), collectionList.get(6)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5), collectionList.get(6), collectionList.get(7)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5), collectionList.get(6), collectionList.get(7),
                        collectionList.get(8)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5), collectionList.get(6), collectionList.get(7),
                        collectionList.get(8), collectionList.get(9)))
                .build());

        em.flush();

        postRepository.saveAll(postList);
        em.flush();


        collectionList = collectionRepository.findAll();

        for (int i = 1; i < 11; i++) {
            collectionList.get(0).insertViewedCollectionByIp(""+i%6);
            collectionList.get(1).insertViewedCollectionByIp(""+i%9);
            collectionList.get(2).insertViewedCollectionByIp(""+i%8);
            collectionList.get(3).insertViewedCollectionByIp(""+i%2);
            collectionList.get(4).insertViewedCollectionByIp(""+i%7);
            collectionList.get(5).insertViewedCollectionByIp(""+i%3);
            collectionList.get(6).insertViewedCollectionByIp(""+i%2);
            collectionList.get(7).insertViewedCollectionByIp(""+i%4);
            collectionList.get(8).insertViewedCollectionByIp(""+i%10);
            collectionList.get(9).insertViewedCollectionByIp(""+i%5);
            em.flush();
        }

        em.clear();

        //when
        System.out.println("서비스");
        List<RelatedMostTaggedHashtagDto> content = hashtagQueryRepository.findAllByCollectionIds(
                6, collectionList.stream().map(Collection::getId).collect(Collectors.toList()));
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
    public void 영화가_태그된_포스트가_태그한_해시태그_카운트() throws Exception{
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
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().setNickname(keyword+i).build());
            movieList.add(Movie.createMovie((long) i, keyword + i, i + "",
                    GenreOfMovieStatus.A, GenreOfMovieStatus.C,
                    null, null));
        }
        memberRepository.saveAll(memberList);
        movieRepository.saveAll(movieList);

        em.flush();

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();

        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(keyword, "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);
        collectionRepository.saveAll(collectionList);

        em.flush();

        Post build = Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(3))
                .setHashTags(List.of(hashtagList.get(0)))
                .setCollections(List.of(collectionList.get(0))).build();
        postRepository.save(build);
        postList.add(build);

        em.flush();

        Post build1 = Post.buildPost().setMember(memberList.get(4)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(4))
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1)))
                .setCollections(List.of(collectionList.get(0), collectionList.get(1))).build();
        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        build1 = Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2)))
                .setCollections(List.of(collectionList.get(0), collectionList.get(1), collectionList.get(2)))
                .build();
        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        build1 = Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3)))
                .build();

        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        build1 =Post.buildPost().setMember(memberList.get(1)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4)))
                .build();

        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        build1 = Post.buildPost().setMember(memberList.get(7)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(1))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5)))
                .build();

        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        build1 = Post.buildPost().setMember(memberList.get(8)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5), collectionList.get(6)))
                .build();

        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        build1 =Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(8))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5), collectionList.get(6), collectionList.get(7)))
                .build();

        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        build1 = Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5), collectionList.get(6), collectionList.get(7),
                        collectionList.get(8)))
                .build();
        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        build1 = Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(9))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9)))
                .setCollections(List.of(
                        collectionList.get(0), collectionList.get(1), collectionList.get(2), collectionList.get(3),
                        collectionList.get(4), collectionList.get(5), collectionList.get(6), collectionList.get(7),
                        collectionList.get(8), collectionList.get(9)))
                .build();
        postRepository.save(build1);
        postList.add(build1);
        em.flush();


        collectionList = collectionRepository.findAll();

        for (int i = 1; i < 11; i++) {
            collectionList.get(0).insertViewedCollectionByIp(""+i%6);
            collectionList.get(1).insertViewedCollectionByIp(""+i%9);
            collectionList.get(2).insertViewedCollectionByIp(""+i%8);
            collectionList.get(3).insertViewedCollectionByIp(""+i%2);
            collectionList.get(4).insertViewedCollectionByIp(""+i%7);
            collectionList.get(5).insertViewedCollectionByIp(""+i%3);
            collectionList.get(6).insertViewedCollectionByIp(""+i%2);
            collectionList.get(7).insertViewedCollectionByIp(""+i%4);
            collectionList.get(8).insertViewedCollectionByIp(""+i%10);
            collectionList.get(9).insertViewedCollectionByIp(""+i%5);
            em.flush();
        }

        em.clear();

        //when
        System.out.println("서비스");
        List<RelatedMostTaggedHashtagDto> content = hashtagQueryRepository.findAllByMovieIds(
                6, movieList.stream().map(Movie::getId).collect(Collectors.toList()));
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
}