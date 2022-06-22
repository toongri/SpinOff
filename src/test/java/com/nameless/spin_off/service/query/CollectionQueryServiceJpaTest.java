package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.*;
import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto.CollectedPostDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
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

import static com.nameless.spin_off.enums.collection.PublicOfCollectionStatus.A;
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
    @Autowired MovieRepository movieRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired CommentInCollectionService commentInCollectionService;

    @Test
    public void 전체검색_컬렉션_테스트_멤버_단일_팔로우() throws Exception{
        //given
        String keyword = "가나다라";
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
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollection(
                    new CollectionRequestDto(keyword + mem.getId(), "", A), mem.getId());
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
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollection(
                    new CollectionRequestDto(keyword + mem.getId(), "", A), mem.getId());
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
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollection(
                    new CollectionRequestDto(keyword + mem.getId(), "", A), mem.getId());
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
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollection(
                    new CollectionRequestDto(keyword + mem.getId(), "", A), mem.getId());
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
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollection(
                    new CollectionRequestDto("aaa", "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postList.add(build);
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
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
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollection(
                    new CollectionRequestDto("aaa", "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postList.add(build);
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collection));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
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
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            Long aLong = collectionService.insertCollection(
                    new CollectionRequestDto("aaa", "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            collectionService.insertFollowedCollectionByMemberId(member.getId(), aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("aaa").setContent("").setUrls(List.of())
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
                    .setTitle("aaa").setContent("").setUrls(List.of())
                    .setThumbnailUrl(collectionList.get(integer).getMember().getId() + "2")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(collectionList.get(integer)));

            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        Post build = Post.buildPost().setMember(collectionList.get(6).getMember())
                .setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
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
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();

        memberRepository.save(member);

        Member member2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();

        memberRepository.save(member2);

        Member member3 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();

        memberRepository.save(member3);

        Member member4 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();

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
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
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
    
    @Test
    public void 컬렉션_조회() throws Exception{
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

        Member member2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
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
                    .setPhoneNumber("01011111111")
                    .setNickname("memcnam"+i).build());
        }
        memberRepository.saveAll(memberList);
        Movie movie = Movie.createMovie(0L, "movietitle", null, null, null);

        movieRepository.save(movie);

        List<String> urls = List.of("a", "b", "c", "d", "e");

        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(i+""));
        }
        hashtagRepository.saveAll(hashtagList);

        Post post = Post.buildPost()
                .setMember(member)
                .setMovie(movie)
                .setTitle("postTitle")
                .setContent("postContent")
                .setPostPublicStatus(PublicOfPostStatus.A)
                .setUrls(urls)
                .setThumbnailUrl(urls.get(0))
                .setHashTags(hashtagList)
                .build();
        postRepository.save(post);

        List<Post> postList = new ArrayList<>();

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8))).build());

        postList.add(Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9))).build());
        postRepository.saveAll(postList);

        List<Collection> collectionList = new ArrayList<>();
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.A)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.B)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.C)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.A)));

        Long dfdd = commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(null, "dfdd"), memberList.get(3).getId(),
                collectionList.get(0).getId());
        commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(dfdd, "dfdd"), memberList.get(3).getId(),
                collectionList.get(0).getId());
        Long dfdd1 = commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(null, "dfdd"), memberList.get(5).getId(),
                collectionList.get(0).getId());
        commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(dfdd1, "dfdd"), memberList.get(0).getId(),
                collectionList.get(0).getId());
        commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(null, "dfdd"), memberList.get(8).getId(),
                collectionList.get(0).getId());

        memberService.insertFollowedMemberByMemberId(member2.getId(), member.getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), member2.getId());
        memberService.insertBlockedMemberByMemberId(member2.getId(), memberList.get(5).getId(), BlockedMemberStatus.A);

        collectionService.insertLikedCollectionByMemberId(member.getId(), collectionList.get(0).getId());
        collectionService.insertLikedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(0).getId());
        collectionService.insertLikedCollectionByMemberId(member2.getId(), collectionList.get(0).getId());

        collectionService.insertFollowedCollectionByMemberId(member2.getId(), collectionList.get(0).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(3).getId(), collectionList.get(0).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(0).getId());

        for (Post post1 : postList) {
            postService.updateCollectedPosts(member.getId(), post1.getId(),
                    List.of(collectionList.get(0).getId(), collectionList.get(1).getId(), collectionList.get(2).getId()));
        }

        em.flush();
        em.clear();
        
        //when
        System.out.println("서비스함수");
        ReadCollectionDto collectionForRead = collectionQueryService.getCollectionForRead(
                MemberDetails.builder()
                        .id(member2.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), collectionList.get(0).getId());
        System.out.println("서비스함수끝");

        Slice<CollectedPostDto> collectedPosts = collectionQueryService.getCollectedPostsSliced(
                MemberDetails.builder()
                        .id(member2.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), collectionList.get(0).getId(), PageRequest.of(0, 6, Sort.by("id").descending()));

        Slice<CollectedPostDto> collectedPosts2 = collectionQueryService.getCollectedPostsSliced(
                null, collectionList.get(0).getId(), PageRequest.of(0, 6, Sort.by("id").descending()));

        Slice<CollectedPostDto> collectedPosts3 = collectionQueryService.getCollectedPostsSliced(
                MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), collectionList.get(0).getId(), PageRequest.of(0, 10, Sort.by("id").descending()));

        collectionQueryService.getCollectionForRead(MemberDetails.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .accountPw(member.getAccountPw())
                .authorities(member.getRoles()
                        .stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                        .collect(Collectors.toSet()))
                .build(), collectionList.get(3).getId());

        ReadCollectionDto collectionForRead2 = collectionQueryService.getCollectionForRead(
                null, collectionList.get(0).getId());

        //then
        assertThat(collectionForRead.getCommentSize()).isEqualTo(4L);
        assertThat(collectionForRead.getLikedSize()).isEqualTo(2L);
        assertThat(collectionForRead.getFollowedSize()).isEqualTo(2L);
        assertThat(collectionForRead.getPostSize()).isEqualTo(9L);
        assertThat(collectionForRead.getMember().isFollowed()).isTrue();

        assertThat(collectionForRead2.getCommentSize()).isEqualTo(5L);
        assertThat(collectionForRead2.getLikedSize()).isEqualTo(3L);
        assertThat(collectionForRead2.getFollowedSize()).isEqualTo(3L);
        assertThat(collectionForRead2.getPostSize()).isEqualTo(10L);
        assertThat(collectionForRead2.getMember().isFollowed()).isFalse();

        assertThatThrownBy(() -> collectionQueryService.getCollectionForRead(MemberDetails.builder()
                .id(member2.getId())
                .accountId(member.getAccountId())
                .accountPw(member.getAccountPw())
                .authorities(member.getRoles()
                        .stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                        .collect(Collectors.toSet()))
                .build(), collectionList.get(1).getId())).isInstanceOf(DontHaveAuthorityException.class);

        List<CollectedPostDto> content = collectedPosts.getContent();
        List<CollectedPostDto> content2 = collectedPosts2.getContent();
        List<CollectedPostDto> content3 = collectedPosts3.getContent();

        assertThat(content.stream().map(CollectedPostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(postList.get(8).getId(), postList.get(7).getId(), postList.get(6).getId(),
                        postList.get(5).getId(), postList.get(4).getId(), postList.get(3).getId());

        assertThat(content2.stream().map(CollectedPostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(postList.get(9).getId(), postList.get(8).getId(), postList.get(7).getId(),
                        postList.get(6).getId(), postList.get(5).getId(), postList.get(4).getId());

        assertThat(content3.stream().map(CollectedPostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(postList.get(9).getId(), postList.get(8).getId(), postList.get(7).getId(),
                        postList.get(6).getId(), postList.get(5).getId(), postList.get(4).getId(),
                        postList.get(3).getId(), postList.get(2).getId(), postList.get(1).getId(),
                        postList.get(0).getId());


    }

    @Test
    public void 컬렉션_좋아요_멤버출력() throws Exception{
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

        Member member2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
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
                    .setPhoneNumber("01011111111")
                    .setNickname("memcnam"+i).build());
        }
        memberRepository.saveAll(memberList);

        List<Collection> collectionList = new ArrayList<>();
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.A)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.B)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.C)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member2, 0 + "", 0 + "", PublicOfCollectionStatus.A)));

        memberService.insertFollowedMemberByMemberId(member2.getId(), member.getId());
        em.flush();
        memberService.insertBlockedMemberByMemberId(member2.getId(), memberList.get(5).getId(), BlockedMemberStatus.A);
        em.flush();

        collectionService.insertLikedCollectionByMemberId(member.getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertLikedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertLikedCollectionByMemberId(member2.getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertLikedCollectionByMemberId(memberList.get(3).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertLikedCollectionByMemberId(memberList.get(7).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertLikedCollectionByMemberId(memberList.get(9).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertLikedCollectionByMemberId(memberList.get(4).getId(), collectionList.get(0).getId());
        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        List<MembersByContentDto> members =
                collectionQueryService.getLikeCollectionMembers(member2.getId(), collectionList.get(0).getId());
        System.out.println("서비스함수끝");

        assertThatThrownBy(() -> collectionQueryService
                .getLikeCollectionMembers(memberList.get(5).getId(), collectionList.get(3).getId()))
                .isInstanceOf(DontHaveAuthorityException.class);

        //then
        assertThat(members.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(member2.getId(), member.getId(), memberList.get(4).getId(), memberList.get(9).getId(),
                        memberList.get(7).getId(), memberList.get(3).getId());

        assertThat(members.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly(true, false, false, false, false, false);

        assertThat(members.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, true, false, false, false, false);

    }

    @Test
    public void 컬렉션_팔로우_멤버출력() throws Exception{
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

        Member member2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
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
                    .setPhoneNumber("01011111111")
                    .setNickname("memcnam"+i).build());
        }
        memberRepository.saveAll(memberList);

        List<Collection> collectionList = new ArrayList<>();
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.A)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.B)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member, 0 + "", 0 + "", PublicOfCollectionStatus.C)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member2, 0 + "", 0 + "", PublicOfCollectionStatus.A)));

        memberService.insertFollowedMemberByMemberId(member2.getId(), memberList.get(8).getId());
        em.flush();
        memberService.insertBlockedMemberByMemberId(member2.getId(), memberList.get(5).getId(), BlockedMemberStatus.A);
        em.flush();

        collectionService.insertFollowedCollectionByMemberId(memberList.get(8).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertFollowedCollectionByMemberId(member2.getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertFollowedCollectionByMemberId(memberList.get(3).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertFollowedCollectionByMemberId(memberList.get(7).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertFollowedCollectionByMemberId(memberList.get(9).getId(), collectionList.get(0).getId());
        em.flush();
        collectionService.insertFollowedCollectionByMemberId(memberList.get(4).getId(), collectionList.get(0).getId());
        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        List<MembersByContentDto> members =
                collectionQueryService.getFollowCollectionMembers(member2.getId(), collectionList.get(0).getId());
        System.out.println("서비스함수끝");

        assertThatThrownBy(() -> collectionQueryService
                .getFollowCollectionMembers(memberList.get(5).getId(), collectionList.get(3).getId()))
                .isInstanceOf(DontHaveAuthorityException.class);

        //then
        assertThat(members.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(member2.getId(), memberList.get(8).getId(), memberList.get(4).getId(), memberList.get(9).getId(),
                        memberList.get(7).getId(), memberList.get(3).getId());

        assertThat(members.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly(true, false, false, false, false, false);

        assertThat(members.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, true, false, false, false, false);

    }
}
