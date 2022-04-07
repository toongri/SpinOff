package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MyPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchAllCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchCollectionDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus.A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                    new CollectionDto.CreateCollectionVO(keyword + mem.getId(), "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);//.
            Post post = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            post.addAllCollectedPost(List.of(byId));
            postList.add(post);
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "1").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(8).getId());

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "2").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
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
        collectionService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<SearchAllCollectionDto> content = collectionQueryService
                .getSearchPageCollectionAtAllSliced(
                        keyword,
                        PageRequest.of(0, 6, Sort.by("popularity").descending()),
                        member.getId())
                .getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchAllCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(SearchAllCollectionDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(5).getNickname(),
                        null,
                        null,
                        null,
                        null,
                        null);
        assertThat(content.stream().map(SearchAllCollectionDto::getFollowingCount).collect(Collectors.toList()))
                .containsExactly(
                        0L,
                        0L,
                        0L,
                        0L,
                        0L,
                        0L);
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
                    new CollectionDto.CreateCollectionVO(keyword + mem.getId(), "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postList.add(build);
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "1").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
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
                    .setTitle(keyword + collection.getMember().getId() + "2").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
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
        collectionService.updateAllPopularity();
        em.flush();

        em.clear();

        //when
        System.out.println("서비스");
        List<SearchAllCollectionDto> content = collectionQueryService
                .getSearchPageCollectionAtAllSliced(
                        keyword,
                        PageRequest.of(0, 6, Sort.by("popularity").descending()),
                        member.getId())
                .getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchAllCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(SearchAllCollectionDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getNickname(),
                        memberList.get(5).getNickname(),
                        null,
                        null,
                        null,
                        null);
        assertThat(content.stream().map(SearchAllCollectionDto::getFollowingCount).collect(Collectors.toList()))
                .containsExactly(
                        2L,
                        0L,
                        0L,
                        0L,
                        0L,
                        0L);
    }
    @Test
    public void 컬렉션_검색_컬렉션_테스트_멤버_단일_팔로우() throws Exception{
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
                    new CollectionDto.CreateCollectionVO(keyword + mem.getId(), "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postList.add(build);
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "1").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(8).getId());

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "2").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
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

        collectionService.updateAllPopularity();
        em.flush();
        em.clear();

        System.out.println("dfddfd"+ member.getId());

        //when
        System.out.println("서비스");
        List<SearchCollectionDto> content = collectionQueryService
                .getSearchPageCollectionAtCollectionSliced(
                        keyword,
                        PageRequest.of(0, 6, Sort.by("popularity").descending()),
                        member.getId())
                .getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(SearchCollectionDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(5).getNickname(),
                        null,
                        null,
                        null,
                        null,
                        null);
        assertThat(content.stream().map(SearchCollectionDto::getFollowingCount).collect(Collectors.toList()))
                .containsExactly(
                        0L,
                        0L,
                        0L,
                        0L,
                        0L,
                        0L);
    }

    @Test
    public void 컬렉션_검색_컬렉션_테스트_멤버_다중_팔로우() throws Exception{
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
                    new CollectionDto.CreateCollectionVO(keyword + mem.getId(), "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postList.add(build);
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "1").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
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
                    .setTitle(keyword + collection.getMember().getId() + "2").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
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

        collectionService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<SearchCollectionDto> content = collectionQueryService
                .getSearchPageCollectionAtCollectionSliced(
                        keyword,
                        PageRequest.of(0, 6, Sort.by("popularity").descending()),
                        member.getId())
                .getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(SearchCollectionDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getNickname(),
                        memberList.get(5).getNickname(),
                        null,
                        null,
                        null,
                        null);
        assertThat(content.stream().map(SearchCollectionDto::getFollowingCount).collect(Collectors.toList()))
                .containsExactly(
                        2L,
                        0L,
                        0L,
                        0L,
                        0L,
                        0L);
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
                    new CollectionDto.CreateCollectionVO("", "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postList.add(build);
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
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
        collectionService.updateAllPopularity();
        em.flush();

        em.clear();

        //when
        System.out.println("서비스");
        List<MainPageCollectionDto> content =
                collectionQueryService.getCollectionsSlicedForMainPage(
                        PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(MainPageCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(MainPageCollectionDto::getThumbnailUrls).collect(Collectors.toList()))
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
                    new CollectionDto.CreateCollectionVO("", "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postList.add(build);
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionService.updateAllPopularity();
        em.flush();
        collectionList = collectionRepository.findAll();

        em.clear();

        //when
        System.out.println("서비스");
        List<MainPageCollectionDto> content =
                collectionQueryService.getCollectionsByFollowedMemberSlicedForMainPage(
                        PageRequest.of(0, 6, Sort.by("lastModifiedDate").descending()), member.getId())
                        .getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(MainPageCollectionDto::getThumbnailUrls).collect(Collectors.toList()))
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
    public void 팔로잉_컬렉션_컬렉션_테스트() throws Exception {

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
                    new CollectionDto.CreateCollectionVO("", "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionService.insertFollowedCollectionByMemberId(member.getId(), aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        List<Integer> integers = List.of(5, 9, 8, 3, 2, 4, 7, 0, 1, 6);

        for (Integer integer : integers) {
            Post build = Post.buildPost().setMember(collectionList.get(integer).getMember())
                    .setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collectionList.get(integer).getMember().getId() + "2")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collectionList.get(integer)));

            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        Post build = Post.buildPost().setMember(collectionList.get(6).getMember())
                .setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(collectionList.get(6).getMember().getId() + "3")
                .setHashTags(List.of()).build();
        build.addAllCollectedPost(List.of(collectionList.get(6)));
        postRepository.save(build);
        postList.add(build);
        em.flush();

        collectionService.updateAllPopularity();
        em.flush();
        collectionList = collectionRepository.findAll();
        em.clear();

        //when
        System.out.println("서비스");
        List<MainPageCollectionDto> content =
                collectionQueryService.getCollectionsByFollowedCollectionsSlicedForMainPage(
                        PageRequest.of(0, 6, Sort.by("lastModifiedDate").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(MainPageCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(6).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(7).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(2).getId());

        assertThat(content.stream().map(MainPageCollectionDto::getThumbnailUrls).collect(Collectors.toList()))
                .containsExactly(
                        List.of(collectionList.get(6).getCollectedPosts().get(3).getPost().getThumbnailUrl(),
                                collectionList.get(6).getCollectedPosts().get(2).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(1).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(1).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(0).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(0).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(7).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(7).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(4).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(4).getCollectedPosts().get(1).getPost().getThumbnailUrl()),
                        List.of(collectionList.get(2).getCollectedPosts().get(2).getPost().getThumbnailUrl(),
                                collectionList.get(2).getCollectedPosts().get(1).getPost().getThumbnailUrl())
                );
    }
    
    @Test
    public void 마이페이지_컬렉션출력() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccountId")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memberNickname").build();

        memberRepository.save(member);

        Member member2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccountId")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memberNickname").build();

        memberRepository.save(member2);

        Member member3 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccountId")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memberNickname").build();

        memberRepository.save(member3);

        Member member4 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccountId")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setNickname("memberNickname").build();

        memberRepository.save(member4);

        List<Collection> collectionList = new ArrayList<>();
        Collection defaultCollection = Collection.createDefaultCollection(member);
        collectionRepository.save(defaultCollection);
        collectionList.add(defaultCollection);

        for (int i = 0; i < 4; i++) {
            collectionList.add(collectionRepository
                    .save(Collection.createCollection(member, i + "", i + "", PublicOfCollectionStatus.C)));
        }

        for (int i = 0; i < 5; i++) {
            collectionList.add(collectionRepository
                    .save(Collection.createCollection(member, i + "", i + "", PublicOfCollectionStatus.A)));
        }
        List<Post> postList = new ArrayList<>();

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.B)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
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

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));
        em.flush();

        memberService.insertFollowedMemberByMemberId(member2.getId(), member.getId());
        memberService.insertBlockedMemberByMemberId(member2.getId(), member4.getId(), BlockedMemberStatus.A);
        em.flush();

        em.clear();
        //when
        System.out.println("서비스시작");
        Slice<MyPageCollectionDto> collection = collectionQueryService.getCollectionsByMemberIdSliced(
                MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), member.getId(),
                PageRequest.of(0, 6, Sort.by("id").descending()));
        System.out.println("서비스끝");

        Slice<MyPageCollectionDto> collection2 = collectionQueryService.getCollectionsByMemberIdSliced(
                MemberDetails.builder()
                        .id(member2.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), member.getId(),
                PageRequest.of(0, 6, Sort.by("id").descending()));

        Slice<MyPageCollectionDto> collection3 = collectionQueryService.getCollectionsByMemberIdSliced(
                MemberDetails.builder()
                        .id(member3.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), member.getId(),
                PageRequest.of(0, 6, Sort.by("id").descending()));

        Slice<MyPageCollectionDto> collection4 = collectionQueryService.getCollectionsByMemberIdSliced(
                null, member.getId(),
                PageRequest.of(0, 6, Sort.by("id").descending()));

        //then

        assertThat(collection.getContent().stream().map(MyPageCollectionDto::getId)).containsExactly(
                collectionList.get(0).getId(),
                collectionList.get(9).getId(),
                collectionList.get(8).getId(),
                collectionList.get(7).getId(),
                collectionList.get(6).getId(),
                collectionList.get(5).getId());

        assertThat(collection2.getContent().stream().map(MyPageCollectionDto::getId)).containsExactly(
                collectionList.get(9).getId(),
                collectionList.get(8).getId(),
                collectionList.get(7).getId(),
                collectionList.get(6).getId(),
                collectionList.get(5).getId(),
                collectionList.get(4).getId());

        assertThat(collection3.getContent().stream().map(MyPageCollectionDto::getId)).containsExactly(
                collectionList.get(9).getId(),
                collectionList.get(8).getId(),
                collectionList.get(7).getId(),
                collectionList.get(6).getId(),
                collectionList.get(5).getId());

        assertThat(collection4.getContent().stream().map(MyPageCollectionDto::getId)).containsExactly(
                collectionList.get(9).getId(),
                collectionList.get(8).getId(),
                collectionList.get(7).getId(),
                collectionList.get(6).getId(),
                collectionList.get(5).getId());

        assertThatThrownBy(() -> collectionQueryService.getCollectionsByMemberIdSliced(
                MemberDetails.builder()
                        .id(member4.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), member2.getId(),
                PageRequest.of(0, 6, Sort.by("id").descending())))
                .isInstanceOf(DontHaveAuthorityException.class);
    }
}
