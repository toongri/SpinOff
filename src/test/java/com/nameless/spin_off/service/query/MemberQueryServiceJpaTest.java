package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.FollowCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.FollowHashtagDto;
import com.nameless.spin_off.dto.MemberDto.*;
import com.nameless.spin_off.dto.MovieDto.FollowMovieDto;
import com.nameless.spin_off.dto.SearchDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.hashtag.HashtagService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.movie.MovieService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
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
public class MemberQueryServiceJpaTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired CollectionRepository collectionRepository;
    @Autowired PostRepository postRepository;
    @Autowired MemberQueryService memberQueryService;
    @Autowired MovieService movieService;
    @Autowired MovieRepository movieRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired HashtagService hashtagService;
    @Autowired PostService postService;

    @Test
    public void ????????????() throws Exception{

        //given
        String keyword = "????????????";
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
                    .setAccountPw("memberAccountPw")
                    .setPhoneNumber("01011111111")
                    .setNickname(keyword+i).build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollection(
                    new CollectionDto.CollectionRequestDto(keyword + mem.getId(), "", A), mem.getId());
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
        em.clear();

        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(3).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(4).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(5).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(6).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(3).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(4).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(5).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(3).getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(4).getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(4).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(4).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(3).getId(), memberList.get(4).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(3).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(3).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(2).getId());

        em.flush();

        memberService.updateAllPopularity();

        em.flush();
        em.clear();

        //when
        System.out.println("?????????");
        List<SearchMemberDto> content = memberQueryService.getSearchPageMemberAtMemberSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("????????????");
        memberList = memberRepository.findAll();
        //then
        assertThat(content.stream().map(SearchMemberDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(8).getId(),
                        memberList.get(7).getId(),
                        memberList.get(6).getId(),
                        memberList.get(5).getId(),
                        memberList.get(4).getId(),
                        memberList.get(3).getId());

        assertThat(content.stream().map(SearchMemberDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getNickname(),
                        memberList.get(6).getNickname(),
                        memberList.get(5).getNickname(),
                        memberList.get(4).getNickname(),
                        memberList.get(3).getNickname(),
                        memberList.get(2).getNickname());

        assertThat(content.stream().map(SearchMemberDto::getFollowingCount).collect(Collectors.toList()))
                .containsExactly(
                        6,
                        5,
                        4,
                        3,
                        2,
                        1);

        assertThat(content.stream().map(SearchMemberDto::getThumbnailUrls).collect(Collectors.toList()))
                .containsExactly(
                        List.of(memberList.get(8).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(8).getPosts().get(1).getThumbnailUrl()),
                        List.of(memberList.get(7).getPosts().get(3).getThumbnailUrl(),
                                memberList.get(7).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(7).getPosts().get(1).getThumbnailUrl(),
                                memberList.get(7).getPosts().get(0).getThumbnailUrl()),
                        List.of(memberList.get(6).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(6).getPosts().get(1).getThumbnailUrl()),
                        List.of(memberList.get(5).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(5).getPosts().get(1).getThumbnailUrl()),
                        List.of(memberList.get(4).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(4).getPosts().get(1).getThumbnailUrl()),
                        List.of(memberList.get(3).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(3).getPosts().get(1).getThumbnailUrl()));

    }


    @Test
    public void ????????????_??????_?????????() throws Exception{
        //given
        String keyword = "????????????";
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
                    .setAccountPw("memberAccountPw")
                    .setPhoneNumber("01011111111")
                    .setNickname(keyword+i).build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollection(
                    new CollectionDto.CollectionRequestDto(keyword + mem.getId(), "", A), mem.getId());
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

        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(3).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(4).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(5).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(6).getId(), memberList.get(7).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(3).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(4).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(5).getId(), memberList.get(6).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(3).getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(4).getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(4).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(4).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(3).getId(), memberList.get(4).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(3).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), memberList.get(3).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(1).getId(), memberList.get(2).getId());

        em.flush();
        em.clear();

        memberService.updateAllPopularity();

        em.flush();
        em.clear();
        //when
        System.out.println("?????????");
        List<SearchAllMemberDto> content = memberQueryService.getSearchPageMemberAtAllSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("????????????");
        //then
        assertThat(content.stream().map(SearchAllMemberDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getId(),
                        memberList.get(6).getId(),
                        memberList.get(5).getId(),
                        memberList.get(4).getId(),
                        memberList.get(3).getId(),
                        memberList.get(2).getId());
    }

    @Test
    public void ??????????????????() throws Exception{

        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();

        for (int i = 0; i < 10; i++) {
            memberService.insertSearch(memberId, i+"", SearchedByMemberStatus.D);
        }

        em.flush();
        em.clear();
        //when
        System.out.println("???????????????");
        List<SearchDto.LastSearchDto> lastSearchesByMember = memberQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("??????");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchDto.LastSearchDto::getContent).collect(Collectors.toList()))
                .containsExactly("9", "8", "7", "6", "5");

    }

    @Test
    public void ??????????????????_???????????????_?????????() throws Exception{

        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();

//        for (int i = 0; i < 10; i++) {
//            searchService.insertSearch(memberId, i+"", SearchedByMemberStatus.MEMBER);
//        }

        em.flush();
        em.clear();
        //when

        System.out.println("???????????????");
        List<SearchDto.LastSearchDto> lastSearchesByMember = memberQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("??????");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchDto.LastSearchDto::getContent).collect(Collectors.toList()))
                .containsExactly();
    }

    @Test
    public void ??????????????????_???????????????_?????????() throws Exception{

        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();

        for (int i = 0; i < 3; i++) {
            memberService.insertSearch(memberId, i+"", SearchedByMemberStatus.D);
        }

        em.flush();
        em.clear();
        //when

        System.out.println("???????????????");
        List<SearchDto.LastSearchDto> lastSearchesByMember = memberQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("??????");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchDto.LastSearchDto::getContent).collect(Collectors.toList()))
                .containsExactly("2", "1", "0");
    }
    @Test
    public void ??????_??????() throws Exception{
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

        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(0).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(1).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(2).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(3).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(4).getId(), member.getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(5).getId(), member.getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(6).getId(), member.getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(7).getId(), member.getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(8).getId(), member.getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(9).getId(), member.getId());

        memberService.insertBlockedMemberByMemberId(memberList.get(4).getId(), memberList.get(5).getId(), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId(memberList.get(2).getId(), memberList.get(4).getId(), BlockedMemberStatus.A);

        //when
        System.out.println("???????????????");
        ReadMemberDto memberForRead1 = memberQueryService.getMemberForRead(
                MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                member.getId());
        System.out.println("??????????????????");

        ReadMemberDto memberForRead2 = memberQueryService.getMemberForRead(
                null,
                member.getId());

        ReadMemberDto memberForRead3 = memberQueryService.getMemberForRead(
                MemberDetails.builder()
                        .id(memberList.get(4).getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                member.getId());

        ReadMemberDto memberForRead6 = memberQueryService.getMemberForRead(
                MemberDetails.builder()
                        .id(memberList.get(4).getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                member2.getId());

        //then

        assertThat(memberForRead1.getId()).isEqualTo(member.getId());
        assertThat(memberForRead1.getBio()).isEqualTo(member.getBio());
        assertThat(memberForRead1.getFollowerSize()).isEqualTo(6);
        assertThat(memberForRead1.getFollowingSize()).isEqualTo(4);
        assertThat(memberForRead1.getProfileUrl()).isEqualTo(member.getProfileImg());
        assertThat(memberForRead1.isFollowed()).isEqualTo(false);
        assertThat(memberForRead1.isAdmin()).isEqualTo(true);

        assertThat(memberForRead2.getId()).isEqualTo(member.getId());
        assertThat(memberForRead2.getBio()).isEqualTo(member.getBio());
        assertThat(memberForRead2.getFollowerSize()).isEqualTo(6);
        assertThat(memberForRead2.getFollowingSize()).isEqualTo(4);
        assertThat(memberForRead2.getProfileUrl()).isEqualTo(member.getProfileImg());
        assertThat(memberForRead2.isFollowed()).isEqualTo(false);
        assertThat(memberForRead2.isAdmin()).isEqualTo(false);

        assertThat(memberForRead3.getId()).isEqualTo(member.getId());
        assertThat(memberForRead3.getBio()).isEqualTo(member.getBio());
        assertThat(memberForRead3.getFollowerSize()).isEqualTo(5);
        assertThat(memberForRead3.getFollowingSize()).isEqualTo(4);
        assertThat(memberForRead3.getProfileUrl()).isEqualTo(member.getProfileImg());
        assertThat(memberForRead3.isFollowed()).isEqualTo(true);
        assertThat(memberForRead3.isAdmin()).isEqualTo(false);

        assertThat(memberForRead6.getId()).isEqualTo(member2.getId());
        assertThat(memberForRead6.getBio()).isEqualTo(member2.getBio());
        assertThat(memberForRead6.getFollowerSize()).isEqualTo(0);
        assertThat(memberForRead6.getFollowingSize()).isEqualTo(0);
        assertThat(memberForRead6.getProfileUrl()).isEqualTo(member2.getProfileImg());
        assertThat(memberForRead6.isFollowed()).isEqualTo(false);
        assertThat(memberForRead6.isAdmin()).isEqualTo(false);

        ReadMemberDto memberForRead4 = memberQueryService.getMemberForRead(
                MemberDetails.builder()
                        .id(memberList.get(5).getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                memberList.get(4).getId());

        assertThat(memberForRead4.isBlocked()).isTrue();

        ReadMemberDto memberForRead5 = memberQueryService.getMemberForRead(
                MemberDetails.builder()
                        .id(memberList.get(4).getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                memberList.get(5).getId());

        assertThat(memberForRead5.isBlocking()).isTrue();
        assertThat(memberForRead3.isAdmin()).isEqualTo(false);
        assertThat(memberForRead5.getFollowerSize()).isEqualTo(0L);
        assertThat(memberForRead5.getFollowingSize()).isEqualTo(1L);
    }

    @Test
    public void ??????_?????????_??????() throws Exception{

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

        memberService.insertFollowedMemberByMemberId(member2.getId(), member.getId());
        em.flush();
        memberService.insertFollowedMemberByMemberId(member.getId(), member2.getId());
        em.flush();

        memberService.insertFollowedMemberByMemberId(member2.getId(), memberList.get(2).getId());
        em.flush();
        memberService.insertFollowedMemberByMemberId(member2.getId(), memberList.get(4).getId());
        em.flush();
        memberService.insertFollowedMemberByMemberId(member2.getId(), memberList.get(6).getId());
        em.flush();
        memberService.insertFollowedMemberByMemberId(member2.getId(), memberList.get(8).getId());
        em.flush();

        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(5).getId(), BlockedMemberStatus.A);
        em.flush();
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(3).getId(), BlockedMemberStatus.A);
        em.flush();
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(4).getId(), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(6).getId(), BlockedMemberStatus.A);

        em.flush();
        em.clear();
        //when
        System.out.println("???????????????");
        List<MembersByContentDto> members1 = memberQueryService
                .getFollowedMembersByMemberId(member2.getId(), member2.getId());
        System.out.println("??????????????????");
        List<MembersByContentDto> members2 = memberQueryService
                .getFollowedMembersByMemberId(member.getId(), member2.getId());
        List<MembersByContentDto> members3 = memberQueryService
                .getFollowedMembersByMemberId(member.getId(), memberList.get(4).getId());

        //then
        assertThatThrownBy(() -> memberQueryService
                .getFollowedMembersByMemberId(memberList.get(4).getId(), member.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);

        assertThat(members1.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(memberList.get(8).getId(), memberList.get(6).getId(), memberList.get(4).getId(),
                        memberList.get(2).getId(), member.getId());
        assertThat(members1.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly(false, false, false, false, false);
        assertThat(members1.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(true, true, true, true, true);

        assertThat(members2.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(member.getId(), memberList.get(8).getId(), memberList.get(2).getId());
        assertThat(members2.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly(true, false, false);
        assertThat(members2.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, false, false);

        assertThat(members3.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly();
        assertThat(members3.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly();
        assertThat(members3.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly();
    }

    @Test
    public void ??????_?????????_??????() throws Exception{

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

        memberService.insertFollowedMemberByMemberId(member2.getId(), member.getId());
        em.flush();
        memberService.insertFollowedMemberByMemberId(member.getId(), member2.getId());
        em.flush();

        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), member2.getId());
        em.flush();
        memberService.insertFollowedMemberByMemberId(memberList.get(4).getId(), member2.getId());
        em.flush();
        memberService.insertFollowedMemberByMemberId(memberList.get(6).getId(), member2.getId());
        em.flush();
        memberService.insertFollowedMemberByMemberId(memberList.get(8).getId(), member2.getId());
        em.flush();

        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(5).getId(), BlockedMemberStatus.A);
        em.flush();
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(3).getId(), BlockedMemberStatus.A);
        em.flush();
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(4).getId(), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(6).getId(), BlockedMemberStatus.A);

        em.flush();
        em.clear();
        //when
        System.out.println("???????????????");
        List<MembersByContentDto> members1 = memberQueryService
                .getFollowingMembersByMemberId(member2.getId(), member2.getId());
        System.out.println("??????????????????");
        List<MembersByContentDto> members2 = memberQueryService
                .getFollowingMembersByMemberId(member.getId(), member2.getId());
        List<MembersByContentDto> members3 = memberQueryService
                .getFollowingMembersByMemberId(member.getId(), memberList.get(4).getId());

        //then
        assertThatThrownBy(() -> memberQueryService
                .getFollowingMembersByMemberId(memberList.get(4).getId(), member.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);

        assertThat(members1.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(member.getId(), memberList.get(8).getId(), memberList.get(6).getId(), memberList.get(4).getId(),
                        memberList.get(2).getId());
        assertThat(members1.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly(false, false, false, false, false);
        assertThat(members1.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(true, false, false, false, false);

        assertThat(members2.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(member.getId(), memberList.get(8).getId(), memberList.get(2).getId());
        assertThat(members2.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly(true, false, false);
        assertThat(members2.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, false, false);

        assertThat(members3.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly();
        assertThat(members3.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly();
        assertThat(members3.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly();
    }
    
    @Test
    public void ??????_?????????_??????_?????????_??????() throws Exception{
        //given
        String keyword = "????????????";
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
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            movieList.add(Movie.createMovie((long) i, keyword + i, null, null, null));
            memberList.add(Member.buildMember()
                    .setEmail("jhkimkkk0923@naver.com")
                    .setAccountId("memberAccId2")
                    .setName("memberName")
                    .setBirth(LocalDate.now())
                    .setAccountPw("memberAccountPw")
                    .setPhoneNumber("01011111111")
                    .setNickname(keyword+i).build());
        }
        memberRepository.saveAll(memberList);
        movieRepository.saveAll(movieList);

        movieService.insertFollowedMovieByMovieId(memberList.get(1).getId(), movieList.get(7).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(2).getId(), movieList.get(7).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(3).getId(), movieList.get(7).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(4).getId(), movieList.get(7).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(5).getId(), movieList.get(7).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(6).getId(), movieList.get(7).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(1).getId(), movieList.get(6).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(2).getId(), movieList.get(6).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(3).getId(), movieList.get(6).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(4).getId(), movieList.get(6).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(5).getId(), movieList.get(6).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(1).getId(), movieList.get(5).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(2).getId(), movieList.get(5).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(3).getId(), movieList.get(5).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(4).getId(), movieList.get(5).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(1).getId(), movieList.get(4).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(2).getId(), movieList.get(4).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(3).getId(), movieList.get(4).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(1).getId(), movieList.get(3).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(2).getId(), movieList.get(3).getId());
        movieService.insertFollowedMovieByMovieId(memberList.get(1).getId(), movieList.get(2).getId());

        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(2).getId(), BlockedMemberStatus.A);
        em.flush();

        //when
        List<FollowMovieDto> movies1 = memberQueryService.getFollowMoviesByMemberId(null, memberList.get(1).getId());
        List<FollowMovieDto> movies2 = memberQueryService.getFollowMoviesByMemberId(memberList.get(2).getId(), memberList.get(1).getId());
        List<FollowMovieDto> movies3 = memberQueryService.getFollowMoviesByMemberId(member.getId(), memberList.get(2).getId());

        assertThatThrownBy(() -> memberQueryService.getFollowMoviesByMemberId(memberList.get(2).getId(), member.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);

        //then
        assertThat(movies1.stream().map(FollowMovieDto::getId).collect(Collectors.toList()))
                .containsExactly(movieList.get(2).getId(), movieList.get(3).getId(), movieList.get(4).getId(),
                        movieList.get(5).getId(), movieList.get(6).getId(), movieList.get(7).getId());

        assertThat(movies1.stream().map(FollowMovieDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, false, false, false, false, false);

        assertThat(movies2.stream().map(FollowMovieDto::getId).collect(Collectors.toList()))
                .containsExactly(movieList.get(3).getId(), movieList.get(4).getId(), movieList.get(5).getId(),
                        movieList.get(6).getId(), movieList.get(7).getId(), movieList.get(2).getId());

        assertThat(movies2.stream().map(FollowMovieDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(true, true, true, true, true, false);

        assertThat(movies3.stream().map(FollowMovieDto::getId).collect(Collectors.toList()))
                .containsExactly(movieList.get(3).getId(), movieList.get(4).getId(),
                        movieList.get(5).getId(), movieList.get(6).getId(), movieList.get(7).getId());

        assertThat(movies3.stream().map(FollowMovieDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, false, false, false, false);
    }

    @Test
    public void ??????_?????????_????????????_?????????_??????() throws Exception{
        //given
        String keyword = "????????????";
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
        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(keyword + i));
            memberList.add(Member.buildMember()
                    .setEmail("jhkimkkk0923@naver.com")
                    .setAccountId("memberAccId2")
                    .setName("memberName")
                    .setBirth(LocalDate.now())
                    .setPhoneNumber("01011111111")
                    .setAccountPw("memberAccountPw")
                    .setNickname(keyword+i).build());
        }
        memberRepository.saveAll(memberList);
        hashtagRepository.saveAll(hashtagList);

        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(1).getId(), hashtagList.get(7).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(2).getId(), hashtagList.get(7).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(3).getId(), hashtagList.get(7).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(4).getId(), hashtagList.get(7).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(5).getId(), hashtagList.get(7).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(6).getId(), hashtagList.get(7).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(1).getId(), hashtagList.get(6).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(2).getId(), hashtagList.get(6).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(3).getId(), hashtagList.get(6).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(4).getId(), hashtagList.get(6).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(5).getId(), hashtagList.get(6).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(1).getId(), hashtagList.get(5).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(2).getId(), hashtagList.get(5).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(3).getId(), hashtagList.get(5).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(4).getId(), hashtagList.get(5).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(1).getId(), hashtagList.get(4).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(2).getId(), hashtagList.get(4).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(3).getId(), hashtagList.get(4).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(1).getId(), hashtagList.get(3).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(2).getId(), hashtagList.get(3).getId());
        hashtagService.insertFollowedHashtagByHashtagId(memberList.get(1).getId(), hashtagList.get(2).getId());

        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(2).getId(), BlockedMemberStatus.A);
        em.flush();

        //when
        List<FollowHashtagDto> hashtags1 = memberQueryService.getFollowHashtagsByMemberId(null, memberList.get(1).getId());
        List<FollowHashtagDto> hashtags2 = memberQueryService.getFollowHashtagsByMemberId(memberList.get(2).getId(), memberList.get(1).getId());
        List<FollowHashtagDto> hashtags3 = memberQueryService.getFollowHashtagsByMemberId(member.getId(), memberList.get(2).getId());

        assertThatThrownBy(() -> memberQueryService.getFollowHashtagsByMemberId(memberList.get(2).getId(), member.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);

        //then
        assertThat(hashtags1.stream().map(FollowHashtagDto::getId).collect(Collectors.toList()))
                .containsExactly(hashtagList.get(2).getId(), hashtagList.get(3).getId(), hashtagList.get(4).getId(),
                        hashtagList.get(5).getId(), hashtagList.get(6).getId(), hashtagList.get(7).getId());

        assertThat(hashtags1.stream().map(FollowHashtagDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, false, false, false, false, false);

        assertThat(hashtags2.stream().map(FollowHashtagDto::getId).collect(Collectors.toList()))
                .containsExactly(hashtagList.get(3).getId(), hashtagList.get(4).getId(), hashtagList.get(5).getId(),
                        hashtagList.get(6).getId(), hashtagList.get(7).getId(), hashtagList.get(2).getId());

        assertThat(hashtags2.stream().map(FollowHashtagDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(true, true, true, true, true, false);

        assertThat(hashtags3.stream().map(FollowHashtagDto::getId).collect(Collectors.toList()))
                .containsExactly(hashtagList.get(3).getId(), hashtagList.get(4).getId(),
                        hashtagList.get(5).getId(), hashtagList.get(6).getId(), hashtagList.get(7).getId());

        assertThat(hashtags3.stream().map(FollowHashtagDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, false, false, false, false);
    }
    
    @Test
    public void ?????????_?????????_?????????_??????() throws Exception{
        //given
        String keyword = "abcddf";
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId1")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setAccountPw("memberAccountPw")
                .setPhoneNumber("01011111111")
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
                    .setAccountId(keyword+i)
                    .setName("memberName")
                    .setBirth(LocalDate.now())
                    .setAccountPw("memberAccountPw")
                    .setPhoneNumber("01011111111")
                    .setNickname("memcname").build());
        }
        memberRepository.saveAll(memberList);
        List<String> urls = List.of("a", "b", "c", "d", "e");

        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(i+""));
        }
        hashtagRepository.saveAll(hashtagList);

        Post post = Post.buildPost()
                .setMember(member)
                .setTitle("postTitle")
                .setContent("postContent")
                .setPostPublicStatus(PublicOfPostStatus.A)
                .setUrls(urls)
                .setThumbnailUrl(urls.get(0))
                .setHashTags(hashtagList)
                .build();
        postRepository.save(post);

        List<Post> postList = new ArrayList<>();

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
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
                .save(Collection.createCollection(memberList.get(5), 0 + "", 0 + "", PublicOfCollectionStatus.A)));
        collectionList.add(collectionRepository
                .save(Collection.createCollection(member2, 0 + "", 0 + "", PublicOfCollectionStatus.A)));

        memberService.insertFollowedMemberByMemberId(member2.getId(), member.getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(2).getId(), member.getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), member2.getId());
        memberService.insertBlockedMemberByMemberId(memberList.get(3).getId(), memberList.get(5).getId(), BlockedMemberStatus.A);

        collectionService.insertFollowedCollectionByMemberId(member.getId(), collectionList.get(3).getId());
        collectionService.insertFollowedCollectionByMemberId(member2.getId(), collectionList.get(0).getId());
        collectionService.insertFollowedCollectionByMemberId(member2.getId(), collectionList.get(2).getId());
        collectionService.insertFollowedCollectionByMemberId(member2.getId(), collectionList.get(3).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(2).getId(), collectionList.get(0).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(2).getId(), collectionList.get(2).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(2).getId(), collectionList.get(3).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(2).getId(), collectionList.get(4).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(3).getId(), collectionList.get(0).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(0).getId());

        for (Post post1 : postList) {
            postService.updateCollectedPosts(member.getId(), post1.getId(),
                    List.of(collectionList.get(0).getId(), collectionList.get(2).getId()));
            postService.updateCollectedPosts(member2.getId(), post1.getId(),
                    List.of(collectionList.get(4).getId()));
            postService.updateCollectedPosts(memberList.get(5).getId(), post1.getId(),
                    List.of(collectionList.get(3).getId()));
        }
        em.flush();
        em.clear();
        //when
        System.out.println("???????????????");
        List<FollowCollectionDto> collections1 = memberQueryService.getFollowCollectionsByMemberId(
                MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                memberList.get(2).getId());
        System.out.println("??????????????????");

        List<FollowCollectionDto> collections2 = memberQueryService.getFollowCollectionsByMemberId(
                null,
                memberList.get(2).getId());

        List<FollowCollectionDto> collections3 = memberQueryService.getFollowCollectionsByMemberId(
                MemberDetails.builder()
                        .id(memberList.get(2).getId())
                        .accountId(memberList.get(2).getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                memberList.get(2).getId());

        List<FollowCollectionDto> collections4 = memberQueryService.getFollowCollectionsByMemberId(
                MemberDetails.builder()
                        .id(memberList.get(3).getId())
                        .accountId(memberList.get(3).getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                memberList.get(2).getId());

        List<FollowCollectionDto> collections5 = memberQueryService.getFollowCollectionsByMemberId(
                MemberDetails.builder()
                        .id(memberList.get(3).getId())
                        .accountId(memberList.get(3).getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                memberList.get(5).getId());

        //then
        assertThat(collections1.stream().map(FollowCollectionDto::isOwn).collect(Collectors.toList()))
                .containsExactly(true, false, false);

        assertThat(collections1.stream().map(FollowCollectionDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, true, false);

        assertThat(collections1.stream().map(FollowCollectionDto::getId).collect(Collectors.toList()))
                .containsExactly(collectionList.get(0).getId(), collectionList.get(3).getId(),
                        collectionList.get(4).getId());

        assertThat(collections2.stream().map(FollowCollectionDto::isOwn).collect(Collectors.toList()))
                .containsExactly(false, false, false);

        assertThat(collections2.stream().map(FollowCollectionDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, false, false);

        assertThat(collections2.stream().map(FollowCollectionDto::getId).collect(Collectors.toList()))
                .containsExactly(collectionList.get(4).getId(), collectionList.get(3).getId(),
                        collectionList.get(0).getId());

        assertThat(collections3.stream().map(FollowCollectionDto::isOwn).collect(Collectors.toList()))
                .containsExactly(false, false, false);

        assertThat(collections3.stream().map(FollowCollectionDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(true, true, true);

        assertThat(collections3.stream().map(FollowCollectionDto::getId).collect(Collectors.toList()))
                .containsExactly(collectionList.get(4).getId(), collectionList.get(3).getId(),
                        collectionList.get(0).getId());

        assertThat(collections4.stream().map(FollowCollectionDto::isOwn).collect(Collectors.toList()))
                .containsExactly(false, false);

        assertThat(collections4.stream().map(FollowCollectionDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(true, false);

        assertThat(collections4.stream().map(FollowCollectionDto::getId).collect(Collectors.toList()))
                .containsExactly(collectionList.get(0).getId(), collectionList.get(4).getId());

        assertThat(collections5.stream().map(FollowCollectionDto::isOwn).collect(Collectors.toList()))
                .containsExactly(false);

        assertThat(collections5.stream().map(FollowCollectionDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(true);

        assertThat(collections5.stream().map(FollowCollectionDto::getId).collect(Collectors.toList()))
                .containsExactly(collectionList.get(0).getId());

        assertThatThrownBy(() -> memberQueryService.getFollowCollectionsByMemberId(
                MemberDetails.builder()
                        .id(memberList.get(5).getId())
                        .accountId(memberList.get(5).getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                memberList.get(3).getId())).isInstanceOf(DontHaveAuthorityException.class);
        
    }

    @Test
    public void ??????_??????_??????() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();
        em.flush();

        //when
        //then
        memberService.updateMemberProfile(memberId, new MemberProfileRequestDto(
                "mename",
                "memberAcc",
                "4",
                "5"), null);
        em.flush();
        MemberProfileResponseDto memberForInfo = memberQueryService.getMemberForProfile(memberId);
        assertThat(memberForInfo.getNickname()).isEqualTo("mename");
        assertThat(memberForInfo.getAccountId()).isEqualTo("memberAcc");
        assertThat(memberForInfo.getWebsite()).isEqualTo("4");
        assertThat(memberForInfo.getBio()).isEqualTo("5");

        memberService.updateMemberProfile(memberId, new MemberProfileRequestDto(
                "mename",
                "memberAccsds",
                "3",
                "5"), null);
        em.flush();
        memberForInfo = memberQueryService.getMemberForProfile(memberId);
        assertThat(memberForInfo.getNickname()).isEqualTo("mename");
        assertThat(memberForInfo.getAccountId()).isEqualTo("memberAccsds");
        assertThat(memberForInfo.getWebsite()).isEqualTo("3");
        assertThat(memberForInfo.getBio()).isEqualTo("5");

        memberService.updateMemberProfile(memberId, new MemberProfileRequestDto(
                "meme",
                "memberAccsds",
                "3",
                "5"), null);
        em.flush();
        memberForInfo = memberQueryService.getMemberForProfile(memberId);
        assertThat(memberForInfo.getNickname()).isEqualTo("meme");
        assertThat(memberForInfo.getAccountId()).isEqualTo("memberAccsds");
        assertThat(memberForInfo.getWebsite()).isEqualTo("3");
        assertThat(memberForInfo.getBio()).isEqualTo("5");
    }
    
    @Test
    public void ??????_??????_??????_??????() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        Long memberId = memberRepository.save(member).getId();


        List<Long> memberIds = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            memberIds.add(memberRepository.save(Member.buildMember()
                    .setEmail("jhkimkkk0923@naver.com")
                    .setAccountId("memberAccId2")
                    .setName("memberName")
                    .setBirth(LocalDate.now())
                    .setPhoneNumber("01011111111")
                    .setAccountPw("memberAccountPw")
                    .setNickname("memcname").build()).getId());
        }

        em.flush();

        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(0), BlockedMemberStatus.B);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(1), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(2), BlockedMemberStatus.B);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(3), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(4), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(5), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(6), BlockedMemberStatus.B);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(7), BlockedMemberStatus.B);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(8), BlockedMemberStatus.B);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberIds.get(9), BlockedMemberStatus.A);

        em.flush();
        em.clear();

        //when
        System.out.println("???????????????");
        List<BlockedMemberDto> blockedMembers = memberQueryService.getBlockedMembersByMemberId(memberId);
        em.flush();
        System.out.println("?????????????????????");
       //then

        assertThat(blockedMembers.stream().map(BlockedMemberDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(memberIds.get(9), memberIds.get(8), memberIds.get(7), memberIds.get(6),
                        memberIds.get(5), memberIds.get(4), memberIds.get(3), memberIds.get(2), memberIds.get(1),
                        memberIds.get(0));

        assertThat(blockedMembers.stream().map(BlockedMemberDto::getBlockedMemberStatus).collect(Collectors.toList()))
                .containsExactly(BlockedMemberStatus.A, BlockedMemberStatus.B, BlockedMemberStatus.B,
                        BlockedMemberStatus.B, BlockedMemberStatus.A, BlockedMemberStatus.A, BlockedMemberStatus.A,
                        BlockedMemberStatus.B, BlockedMemberStatus.A, BlockedMemberStatus.B);

        assertThat(blockedMembers.stream().map(BlockedMemberDto::getAccountId).collect(Collectors.toList()))
                .containsExactly("memberAccId2", "memberAccId2", "memberAccId2", "memberAccId2", "memberAccId2",
                        "memberAccId2", "memberAccId2", "memberAccId2", "memberAccId2", "memberAccId2");

        assertThat(blockedMembers.stream().map(BlockedMemberDto::getNickname).collect(Collectors.toList()))
                .containsExactly("memcname", "memcname", "memcname", "memcname", "memcname",
                        "memcname", "memcname", "memcname", "memcname", "memcname");


        assertThat(memberQueryService.getBlockedMembersByMemberId(memberIds.get(0)).size()).isEqualTo(0);
    }
}