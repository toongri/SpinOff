package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.MainPageDto;
import com.nameless.spin_off.dto.MainPageDto.MainPageFollowDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collection.Collection;
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
import com.nameless.spin_off.service.hashtag.HashtagService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.movie.MovieService;
import com.nameless.spin_off.service.post.PostService;
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
class MainPageQueryServiceJpaTest {

    @Autowired MainPageQueryService mainPageQueryService;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectionService collectionService;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;
    @Autowired MovieRepository movieRepository;
    @Autowired MemberService memberService;
    @Autowired MovieService movieService;
    @Autowired HashtagService hashtagService;
    @Autowired PostService postService;

    @Test
    public void 발견_메인페이지() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);

        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }
        memberRepository.saveAll(memberList);

        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(i+""));
        }
        hashtagRepository.saveAll(hashtagList);

        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            movieList.add(Movie.createMovie((long) i, " ", " ",
                    null, null, null, null));
        }
        movieRepository.saveAll(movieList);

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        for (Member mem : memberList) {
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(keyword + mem.getId(), "", A) ,mem.getId());
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

        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(4).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(3).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(0).getId());

        postList.get(9).updateMovie(movieList.get(9));
        em.flush();
        postList.get(8).updateMovie(movieList.get(9));
        em.flush();
        postList.get(5).updateMovie(movieList.get(9));
        em.flush();
        postList.get(6).updateMovie(movieList.get(8));
        em.flush();

        postList.get(9).addPostedHashtagByHashtag(hashtagList.get(9));
        em.flush();
        postList.get(9).addPostedHashtagByHashtag(hashtagList.get(8));
        em.flush();
        postList.get(9).addPostedHashtagByHashtag(hashtagList.get(7));
        em.flush();
        postList.get(3).addPostedHashtagByHashtag(hashtagList.get(9));
        em.flush();
        postList.get(2).addPostedHashtagByHashtag(hashtagList.get(9));
        em.flush();

        movieService.insertFollowedMovieByMovieId(member.getId(), movieList.get(9).getId());
        movieService.insertFollowedMovieByMovieId(member.getId(), movieList.get(8).getId());
        hashtagService.insertFollowedHashtagByHashtagId(member.getId(), hashtagList.get(9).getId());
        hashtagService.insertFollowedHashtagByHashtagId(member.getId(), hashtagList.get(8).getId());

        collectionService.insertFollowedCollectionByMemberId(member.getId(), collectionList.get(1).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(1).getId());

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(8).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(3).getId(), collectionList.get(8).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(7).getId(), collectionList.get(8).getId());


        collectionList = collectionRepository.findAll();

        for (int i = 1; i < 12; i++) {
            collectionList.get(0).insertViewedCollectionByIp(""+i%6);
            collectionList.get(1).insertViewedCollectionByIp(""+i%9);
            collectionList.get(2).insertViewedCollectionByIp(""+i%8);
            collectionList.get(3).insertViewedCollectionByIp(""+0);
            collectionList.get(4).insertViewedCollectionByIp(""+i%7);
            collectionList.get(5).insertViewedCollectionByIp(""+i%3);
            collectionList.get(6).insertViewedCollectionByIp(""+i%2);
            collectionList.get(7).insertViewedCollectionByIp(""+i%4);
            collectionList.get(8).insertViewedCollectionByIp(""+i%10);
            collectionList.get(9).insertViewedCollectionByIp(""+i%5);
            em.flush();
        }

        for (int i = 1; i < 12; i++) {
            postList.get(0).insertViewedPostByIp(""+i%6);
            postList.get(1).insertViewedPostByIp(""+i%9);
            postList.get(2).insertViewedPostByIp(""+i%8);
            postList.get(3).insertViewedPostByIp(""+0);
            postList.get(4).insertViewedPostByIp(""+i%7);
            postList.get(5).insertViewedPostByIp(""+i%3);
            postList.get(6).insertViewedPostByIp(""+i%2);
            postList.get(7).insertViewedPostByIp(""+i%4);
            postList.get(8).insertViewedPostByIp(""+i%10);
            postList.get(9).insertViewedPostByIp(""+i%5);
            em.flush();
        }
        collectionService.updateAllPopularity();
        postService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        MainPageDto.MainPageDiscoveryDto discoveryData = mainPageQueryService.getDiscoveryData(
                PageRequest.of(0, 9, Sort.by("popularity").descending()),
                PageRequest.of(0, 8, Sort.by("id").descending()),
                PageRequest.of(0, 1, Sort.by("popularity").descending()),
                member.getId());
        System.out.println("함수종료");
        //then
        List<MainPagePostDto> ids = discoveryData.getPostsById().getContent();
        List<MainPagePostDto> popularities = discoveryData.getPostsByPopular().getContent();
        List<MainPageCollectionDto> content1 = discoveryData.getCollections().getContent();

        assertThat(ids.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(19).getId(), postList.get(18).getId(), postList.get(17).getId(),
                        postList.get(16).getId(), postList.get(15).getId(), postList.get(14).getId(),
                        postList.get(13).getId(), postList.get(12).getId());

        assertThat(popularities.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(), postList.get(1).getId(), postList.get(2).getId(),
                        postList.get(4).getId(), postList.get(0).getId(), postList.get(9).getId(),
                        postList.get(7).getId(), postList.get(5).getId(), postList.get(6).getId());

        assertThat(ids.size()).isEqualTo(8);
        assertThat(popularities.size()).isEqualTo(9);

        assertThat(discoveryData.getPostsByPopular().isFirst()).isTrue();
        assertThat(discoveryData.getPostsByPopular().isLast()).isFalse();
        assertThat(discoveryData.getPostsByPopular().hasNext()).isTrue();
        assertThat(content1.stream().map(MainPageCollectionDto::getCollectionId).findAny().get())
                .isEqualTo(collectionList.get(8).getId());

    }

    @Test
    public void 팔로우_메인페이지_게시글() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);

        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }
        memberRepository.saveAll(memberList);

        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(i+""));
        }
        hashtagRepository.saveAll(hashtagList);

        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            movieList.add(Movie.createMovie((long) i, " ", " ",
                    null, null, null, null));
        }
        movieRepository.saveAll(movieList);

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        for (Member mem : memberList) {
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

        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(5).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(4).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(3).getId());
        memberService.insertFollowedMemberByMemberId(member.getId(), memberList.get(0).getId());

        postList.get(9).updateMovie(movieList.get(9));
        em.flush();
        postList.get(8).updateMovie(movieList.get(9));
        em.flush();
        postList.get(5).updateMovie(movieList.get(9));
        em.flush();
        postList.get(6).updateMovie(movieList.get(8));
        em.flush();

        postList.get(9).addPostedHashtagByHashtag(hashtagList.get(9));
        em.flush();
        postList.get(9).addPostedHashtagByHashtag(hashtagList.get(8));
        em.flush();
        postList.get(9).addPostedHashtagByHashtag(hashtagList.get(7));
        em.flush();
        postList.get(3).addPostedHashtagByHashtag(hashtagList.get(9));
        em.flush();
        postList.get(2).addPostedHashtagByHashtag(hashtagList.get(9));
        em.flush();

        movieService.insertFollowedMovieByMovieId(member.getId(), movieList.get(9).getId());
        movieService.insertFollowedMovieByMovieId(member.getId(), movieList.get(8).getId());
        hashtagService.insertFollowedHashtagByHashtagId(member.getId(), hashtagList.get(9).getId());
        hashtagService.insertFollowedHashtagByHashtagId(member.getId(), hashtagList.get(8).getId());

        collectionService.insertFollowedCollectionByMemberId(member.getId(), collectionList.get(1).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(1).getId());

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(8).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(3).getId(), collectionList.get(8).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(7).getId(), collectionList.get(8).getId());


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
        MainPageFollowDto followData = mainPageQueryService.getFollowData(
                PageRequest.of(0, 5, Sort.by("id").descending()),
                PageRequest.of(0, 3, Sort.by("id").descending()),
                PageRequest.of(0, 3, Sort.by("id").descending()),
                PageRequest.of(0, 1, Sort.by("lastModifiedDate").descending()),
                member.getId());
        System.out.println("함수종료");

        //then
        List<MainPagePostDto> followedHashtag = followData.getPostsByFollowedHashtag().getContent();
        List<MainPagePostDto> followedMember = followData.getPostsByFollowedMember().getContent();
        List<MainPagePostDto> followedMovie = followData.getPostsByFollowedMovie().getContent();
        List<MainPageCollectionDto> content1 = followData.getCollections().getContent();

        assertThat(followedMember.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(15).getId(), postList.get(14).getId(), postList.get(13).getId(),
                        postList.get(10).getId(), postList.get(5).getId());

        assertThat(followedMovie.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(9).getId(), postList.get(8).getId(), postList.get(6).getId());

        assertThat(followedHashtag.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(2).getId());

        assertThat(followData.getPostsByFollowedMember().isFirst()).isTrue();
        assertThat(followData.getPostsByFollowedMember().isLast()).isFalse();
        assertThat(followData.getPostsByFollowedMember().hasNext()).isTrue();

        assertThat(followData.getPostsByFollowedHashtag().isFirst()).isTrue();
        assertThat(followData.getPostsByFollowedHashtag().isLast()).isTrue();
        assertThat(followData.getPostsByFollowedHashtag().hasNext()).isFalse();

        assertThat(content1.stream().map(MainPageCollectionDto::getCollectionId).findAny().get())
                .isEqualTo(collectionList.get(5).getId());

        followData = mainPageQueryService.getFollowData(
                PageRequest.of(1, 5, Sort.by("id").descending()),
                PageRequest.of(1, 3, Sort.by("id").descending()),
                PageRequest.of(1, 3, Sort.by("id").descending()),
                PageRequest.of(1, 1, Sort.by("lastModifiedDate").descending()),
                member.getId());
        content1 = followData.getCollections().getContent();

        assertThat(content1.stream().map(MainPageCollectionDto::getCollectionId).findAny().get())
                .isEqualTo(collectionList.get(1).getId());
    }
}