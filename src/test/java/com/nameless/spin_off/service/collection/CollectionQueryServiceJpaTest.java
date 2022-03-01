package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import com.nameless.spin_off.service.query.CollectionQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus.A;
import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
public class CollectionQueryServiceJpaTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired CollectionQueryService collectionQueryService;
    @Autowired MemberService memberService;

    @Test
    public void 전체검색_컬렉션_테스트_멤버_단일_팔로우() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(mem.getId(), keyword + mem.getId(), "", A));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "1").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(8).getId());

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "2").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionList = collectionRepository.findAll();

        for (int i = 1; i < 12; i++) {
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
        List<CollectionDto.SearchPageAtAllCollectionDto> content = collectionQueryService
                .getSearchPageCollectionAtAllSliced(
                        keyword,
                        PageRequest.of(0, 6, Sort.by("popularity").descending()),
                        member.getId())
                .getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(CollectionDto.SearchPageAtAllCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(CollectionDto.SearchPageAtAllCollectionDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(5).getNickname(),
                        null,
                        null,
                        null,
                        null,
                        null);
        assertThat(content.stream().map(CollectionDto.SearchPageAtAllCollectionDto::getFollowingNumber).collect(Collectors.toList()))
                .containsExactly(
                        0,
                        0,
                        0,
                        0,
                        0,
                        0);
    }

    @Test
    public void 전체검색_컬렉션_테스트_멤버_다중_팔로우() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(mem.getId(), keyword + mem.getId(), "", A));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "1").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(1).getId());

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(8).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(3).getId(), collectionList.get(8).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(7).getId(), collectionList.get(8).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(5).getId(), memberList.get(7).getId());

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "2").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionList = collectionRepository.findAll();

        for (int i = 1; i < 12; i++) {
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
        List<CollectionDto.SearchPageAtAllCollectionDto> content = collectionQueryService
                .getSearchPageCollectionAtAllSliced(
                        keyword,
                        PageRequest.of(0, 6, Sort.by("popularity").descending()),
                        member.getId())
                .getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(CollectionDto.SearchPageAtAllCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(CollectionDto.SearchPageAtAllCollectionDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getNickname(),
                        memberList.get(5).getNickname(),
                        null,
                        null,
                        null,
                        null);
        assertThat(content.stream().map(CollectionDto.SearchPageAtAllCollectionDto::getFollowingNumber).collect(Collectors.toList()))
                .containsExactly(
                        2,
                        0,
                        0,
                        0,
                        0,
                        0);
    }

    @Test
    public void 발견_컬렉션_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(mem.getId(), "", "", A));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

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
        List<CollectionDto.MainPageCollectionDto> content =
                collectionQueryService.getCollectionsSlicedForMainPage(
                        PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(CollectionDto.MainPageCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(CollectionDto.MainPageCollectionDto::getThumbnailUrls).collect(Collectors.toList()))
                .containsExactly(
                        List.of(collectionList.get(8).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(8).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(1).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(1).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(2).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(2).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(4).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(4).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(0).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(0).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(9).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(9).getCollectedPosts().get(1).getPost().getThumbnailUrl())
                );
    }

    @Test
    public void 팔로잉_유저_컬렉션_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(mem.getId(), "", "", A));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionList = collectionRepository.findAll();

        em.clear();

        //when
        System.out.println("서비스");
        List<CollectionDto.MainPageCollectionDto> content =
                collectionQueryService.getCollectionsByFollowedMemberSlicedForMainPage(
                        PageRequest.of(0, 6, Sort.by("id").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(CollectionDto.MainPageCollectionDto::getThumbnailUrls).collect(Collectors.toList()))
                .containsExactly(
                        List.of(collectionList.get(9).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(9).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(8).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(8).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(7).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(7).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(6).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(6).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(5).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(5).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(4).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(4).getCollectedPosts().get(1).getPost().getThumbnailUrl())
                );
    }

    @Test
    public void 팔로잉_컬렉션_컬렉션_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(mem.getId(), "", "", A));
            Collection byId = collectionRepository.getById(aLong);
            collectionService.insertFollowedCollectionByMemberId(member.getId(), aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionList = collectionRepository.findAll();

        em.clear();

        //when
        System.out.println("서비스");
        List<CollectionDto.MainPageCollectionDto> content =
                collectionQueryService.getCollectionsByFollowedCollectionsSlicedForMainPage(
                        PageRequest.of(0, 6, Sort.by("id").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(CollectionDto.MainPageCollectionDto::getThumbnailUrls).collect(Collectors.toList()))
                .containsExactly(
                        List.of(collectionList.get(9).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(9).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(8).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(8).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(7).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(7).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(6).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(6).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(5).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(5).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(4).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(4).getCollectedPosts().get(1).getPost().getThumbnailUrl())
                );
    }
}
