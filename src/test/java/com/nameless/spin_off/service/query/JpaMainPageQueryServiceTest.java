package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.MainPageCollectionOrderByCollectedDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.FollowedMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.collection.PublicOfCollectionStatus.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaMainPageQueryServiceTest {

    @Autowired
    MainPageQueryService mainPageQueryService;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectionService collectionService;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;
    @Autowired MovieRepository movieRepository;
    @Autowired FollowedMemberRepository followedMemberRepository;
    @Autowired MemberService memberService;

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

        List<Post> postList = new ArrayList<>();
        hashtagRepository.saveAll(hashtagList);
        for (Hashtag hashtag : hashtagList) {
            member.addFollowedHashtag(hashtag);
            postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(List.of(hashtag)).build());
        }
        postRepository.saveAll(postList);
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content = mainPageQueryService
                .getPostsByFollowedHashtagOrderByIdSliced(PageRequest.of(0, 15), member.getId()).getContent();

        //then
        assertThat(content.size()).isEqualTo(postList.size());
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
                    new CollectionDto.CreateCollectionVO(mem.getId(), "", "", PUBLIC));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
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
                mainPageQueryService.getCollectionsOrderByPopularityBySlicing(PageRequest.of(0, 6), member.getId()).getContent();
        System.out.println("함수종료");
        //then
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
                            new CollectionDto.CreateCollectionVO(mem.getId(), "", "", PUBLIC));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
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
                mainPageQueryService.getCollectionsByFollowedMemberOrderByIdSliced(PageRequest.of(0, 6), member.getId()).getContent();
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
                    new CollectionDto.CreateCollectionVO(mem.getId(), "", "", PUBLIC));
            Collection byId = collectionRepository.getById(aLong);
            collectionService.insertFollowedCollectionByMemberId(member.getId(), aLong);
            collectionList.add(byId);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
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
        List<MainPageCollectionOrderByCollectedDto> content =
                mainPageQueryService.getCollectionsByFollowedCollectionsOrderByIdSliced(PageRequest.of(0, 6), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(MainPageCollectionOrderByCollectedDto::getThumbnailUrls).collect(Collectors.toList()))
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
    public void 팔로잉_무비_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Member member2 = Member.buildMember().build();
        memberRepository.save(member);
        memberRepository.save(member2);
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            movieList.add(Movie.createMovie((long) i, " ", " "));
        }

        List<Post> postList = new ArrayList<>();
        movieList = movieRepository.saveAll(movieList);
        for (Movie movie : movieList) {
            member.addFollowedMovie(movie);
            postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(List.of()).setMovie(movie).build());
        }
        postRepository.saveAll(postList);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content = mainPageQueryService
                .getPostsByFollowedMovieOrderByIdSliced(PageRequest.of(0, 15), member.getId()).getContent();

        //then
        assertThat(content.size()).isEqualTo(postList.size());
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

        List<Post> postList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(List.of()).build());
        }
        postRepository.saveAll(postList);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content =
                mainPageQueryService.getPostsByFollowingMemberOrderByIdSliced(PageRequest.of(0, 15), member.getId()).getContent();

        //then
        assertThat(content.size()).isEqualTo(postList.size());
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
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setHashTags(List.of()).build());
        }
        postRepository.saveAll(postList);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(3).getId(), BlockedMemberStatus.ALL);


        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<MainPagePostDto> content =
                mainPageQueryService.getPostsByFollowingMemberOrderByIdSliced(PageRequest.of(0, 15), member.getId()).getContent();

        //then
        assertThat(content.size()).isEqualTo(postList.size() - 1);
    }

}