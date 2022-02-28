package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
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
class JpaMainPageQueryServiceTest {

    @Autowired MainPageQueryService mainPageQueryService;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectionService collectionService;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;
    @Autowired MovieRepository movieRepository;
    @Autowired MemberService memberService;

    @Test
    public void 발견_포스트_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Member member2 = Member.buildMember().build();
        memberRepository.save(member);
        memberRepository.save(member2);


        List<Post> postList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Post save = postRepository.save(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(List.of()).build());
            postList.add(save);
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

        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content = mainPageQueryService
                .getPostsSliced(
                        PageRequest.of(0, 6,Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());

        content = mainPageQueryService
                .getPostsSliced(
                        PageRequest.of(1, 6,Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(7).getId(),
                        postList.get(5).getId(),
                        postList.get(6).getId(),
                        postList.get(3).getId());
    }

    @Test
    public void 팔로잉_해시태그_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Member member2 = Member.buildMember().build();
        memberRepository.save(member);
        memberRepository.save(member2);

        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(""+i));
        }
        hashtagRepository.saveAll(hashtagList);

        List<Post> postList = new ArrayList<>();
        for (Hashtag hashtag : hashtagList) {
            member.addFollowedHashtag(hashtag);
            Post save = postRepository.save(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(hashtagList).build());
            postList.add(save);

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

        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content = mainPageQueryService
                .getPostsByFollowedHashtagSliced(
                        PageRequest.of(0, 6,Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());

        content = mainPageQueryService
                .getPostsByFollowedHashtagSliced(
                        PageRequest.of(1, 6,Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(7).getId(),
                        postList.get(5).getId(),
                        postList.get(6).getId(),
                        postList.get(3).getId());
    }

    @Test
    public void 팔로잉_무비_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Member member2 = Member.buildMember().build();
        memberRepository.save(member);
        memberRepository.save(member2);
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            movieList.add(Movie.createMovie((long) i, " ", " ",
                    null, null, null, null));
        }
        movieList = movieRepository.saveAll(movieList);

        List<Post> postList = new ArrayList<>();
        for (Movie movie : movieList) {
            member.addFollowedMovie(movie);
            Post save = postRepository.save(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(List.of()).setMovie(movie).build());
            postList.add(save);

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

        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content = mainPageQueryService.getPostsByFollowedMovieSliced(
                PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());

        content = mainPageQueryService.getPostsByFollowedMovieSliced(
                PageRequest.of(1, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(7).getId(),
                        postList.get(5).getId(),
                        postList.get(6).getId(),
                        postList.get(3).getId());
    }

    @Test
    public void 팔로잉_멤버_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }
        memberRepository.saveAll(memberList);

        List<Post> postList = new ArrayList<>();
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Post save = postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(List.of()).build());
            postList.add(save);
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

        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content = mainPageQueryService.getPostsByFollowingMemberSliced(
                PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());

        content = mainPageQueryService.getPostsByFollowingMemberSliced(
                PageRequest.of(1, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(7).getId(),
                        postList.get(5).getId(),
                        postList.get(6).getId(),
                        postList.get(3).getId());
    }

    @Test
    public void 멤버_차단_테스트() throws Exception{

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
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(List.of()).build());
        }
        postRepository.saveAll(postList);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(3).getId(), BlockedMemberStatus.A);


        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content =
                mainPageQueryService.getPostsByFollowingMemberSliced(PageRequest.of(0, 15), member.getId()).getContent();

        //then
        assertThat(content.size()).isEqualTo(postList.size() - 1);
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
        List<MainPageCollectionDto> content =
                mainPageQueryService.getCollectionsSliced(
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
        List<MainPageCollectionDto> content =
                mainPageQueryService.getCollectionsByFollowedMemberSliced(
                        PageRequest.of(0, 6, Sort.by("id").descending()), member.getId()).getContent();
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
        List<MainPageCollectionDto> content =
                mainPageQueryService.getCollectionsByFollowedCollectionsSliced(
                        PageRequest.of(0, 6, Sort.by("id").descending()), member.getId()).getContent();
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
}