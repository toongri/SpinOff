package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.HashtagDto.ContentHashtagDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto.MembersByContentDto;
import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.dto.PostDto.MyPagePostDto;
import com.nameless.spin_off.dto.PostDto.ReadPostDto;
import com.nameless.spin_off.dto.PostDto.RelatedPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtHashtagPostDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.movie.GenreOfMovieStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
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
public class PostQueryServiceJpaTest {

    @Autowired MainPageQueryService mainPageQueryService;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectionService collectionService;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;
    @Autowired MovieRepository movieRepository;
    @Autowired MemberService memberService;
    @Autowired PostQueryService postQueryService;
    @Autowired PostService postService;
    @Autowired CommentInPostService commentInPostService;

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
                    .setTitle("").setContent("").setUrls(List.of())
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
        postService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<PostDto.MainPagePostDto> content = postQueryService
                .getPostsSlicedForMainPage(
                        PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(PostDto.MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());

        content = postQueryService
                .getPostsSlicedForMainPage(
                        PageRequest.of(1, 6,Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(PostDto.MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(7).getId(),
                        postList.get(5).getId(),
                        postList.get(6).getId(),
                        postList.get(3).getId());
    }

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
                    .setTitle("").setContent("").setUrls(List.of())
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
        List<PostDto.MainPagePostDto> content = postQueryService
                .getPostsByFollowedHashtagSlicedForMainPage(
                        PageRequest.of(0, 6,Sort.by("popularity").descending()), member.getId()).getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(PostDto.MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());

        content = postQueryService
                .getPostsByFollowedHashtagSlicedForMainPage(
                        PageRequest.of(1, 6,Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(PostDto.MainPagePostDto::getPostId).collect(Collectors.toList()))
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
                    .setTitle("").setContent("").setUrls(List.of())
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
        postService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<PostDto.MainPagePostDto> content = postQueryService.getPostsByFollowedMovieSlicedForMainPage(
                PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(PostDto.MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());

        content = postQueryService.getPostsByFollowedMovieSlicedForMainPage(
                PageRequest.of(1, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(PostDto.MainPagePostDto::getPostId).collect(Collectors.toList()))
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
                    .setTitle("").setContent("").setUrls(List.of())
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
        postService.updateAllPopularity();
        em.flush();
        em.clear();


        //when
        System.out.println("서비스");
        List<PostDto.MainPagePostDto> content = postQueryService.getPostsByFollowingMemberSlicedForMainPage(
                PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(PostDto.MainPagePostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());

        content = postQueryService.getPostsByFollowingMemberSlicedForMainPage(
                PageRequest.of(1, 6, Sort.by("popularity").descending()), member.getId()).getContent();

        //then
        assertThat(content.stream().map(PostDto.MainPagePostDto::getPostId).collect(Collectors.toList()))
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
                    .setTitle("").setContent("").setUrls(List.of())
                    .setHashTags(List.of()).build());
        }
        postRepository.saveAll(postList);
        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(3).getId(), BlockedMemberStatus.A);


        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<PostDto.MainPagePostDto> content =
                postQueryService.getPostsByFollowingMemberSlicedForMainPage(PageRequest.of(0, 15), member.getId()).getContent();

        System.out.println("함수종료");
        //then
        assertThat(content.size()).isEqualTo(postList.size() - 1);
    }

    @Test
    public void 전체검색_포스트_테스트() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().setNickname(keyword+i).build());
        }
        memberRepository.saveAll(memberList);

        List<Post> postList = new ArrayList<>();

        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(keyword + mem.getId(), "", A), mem.getId());
            Collection byId = collectionRepository.getById(aLong);
            Post build = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setUrls(List.of())
                    .setThumbnailUrl(mem.getId() + "0")
                    .setHashTags(List.of()).build();
            build.addAllCollectedPost(List.of(byId));
            postList.add(build);
        }
        postRepository.saveAll(postList);

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
        postService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<PostDto.SearchPageAtAllPostDto> content = postQueryService.getPostsSlicedForSearchPagePostAtAll(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(PostDto.SearchPageAtAllPostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());
    }

    //마리아디비에서만 함수사용가능
    public void 해시태그_검색() throws Exception{
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
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0))).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1))).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2))).build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8)))
                .build());

        em.flush();
        postList.add(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9))).build());

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
        SearchFirstDto<Slice<SearchPageAtHashtagPostDto>> result =
                postQueryService.getPostsByHashtagsSlicedForSearchPageFirst(
                PageRequest.of(0, 4, Sort.by("popularity").descending()),
                List.of("9", "6", "5"), member.getId(), 10);
        List<SearchPageAtHashtagPostDto> content = result.getData().getContent();
        List<RelatedMostTaggedHashtagDto> hashtags = result.getHashtags();
        System.out.println("함수종료");

        //then
        assertThat(content.stream().map(SearchPageAtHashtagPostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(9).getId(),
                        postList.get(7).getId(),
                        postList.get(5).getId());

        assertThat(hashtags.stream().map(RelatedMostTaggedHashtagDto::getContent).collect(Collectors.toList()))
                .containsOnly("9", "6", "5");
    }

    @Test
    public void 포스트_조회() throws Exception{
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

        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().setNickname(""+i).build());
        }
        memberRepository.saveAll(memberList);

        Movie movie = Movie.createMovie(0L, "movietitle", "moviethumbnail",
                GenreOfMovieStatus.A, null, null, null);

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

        Long dfdd = commentInPostService.insertCommentInPostByCommentVO(new CommentDto
                .CreateCommentInPostVO(null, "dfdd"), memberList.get(3).getId(), post.getId());
        commentInPostService.insertCommentInPostByCommentVO(new CommentDto
                .CreateCommentInPostVO(dfdd, "dfdd"), memberList.get(3).getId(), post.getId());
        Long dfdd1 = commentInPostService.insertCommentInPostByCommentVO(new CommentDto
                .CreateCommentInPostVO(null, "dfdd"), memberList.get(5).getId(), post.getId());
        commentInPostService.insertCommentInPostByCommentVO(new CommentDto
                .CreateCommentInPostVO(dfdd1, "dfdd"), memberList.get(0).getId(), post.getId());
        commentInPostService.insertCommentInPostByCommentVO(new CommentDto
                .CreateCommentInPostVO(null, "dfdd"), memberList.get(8).getId(), post.getId());

        postService.insertLikedPostByMemberId(memberList.get(4).getId(), post.getId());
        postService.insertLikedPostByMemberId(memberList.get(8).getId(), post.getId());
        postService.insertLikedPostByMemberId(memberList.get(7).getId(), post.getId());
        postService.insertLikedPostByMemberId(memberList.get(5).getId(), post.getId());
        postService.insertLikedPostByMemberId(memberList.get(9).getId(), post.getId());

        memberService.insertBlockedMemberByMemberId(member.getId(), memberList.get(5).getId(), BlockedMemberStatus.A);

        List<Post> postList = new ArrayList<>();

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8))).build());

        postList.add(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9))).build());
        postRepository.saveAll(postList);
        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        ReadPostDto data = postQueryService.getPostForRead(
                MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(),
                post.getId());

        System.out.println("서비스함수끝");

        Slice<RelatedPostDto> posts = postQueryService.getRelatedPostsSliced(
                member.getId(), post.getId(), PageRequest.of(0, 3, Sort.by("popularity").descending()));


        ReadPostDto data2 = postQueryService.getPostForRead(null, post.getId());

        Slice<RelatedPostDto> posts2 = postQueryService.getRelatedPostsSliced(
                null, post.getId(), PageRequest.of(0, 3, Sort.by("popularity").descending()));

        //then
        assertThat(data.getMember().getMemberId()).isEqualTo(member.getId());
        assertThat(data.getMember().getAccountId()).isEqualTo(member.getAccountId());
        assertThat(data.getMember().getNickname()).isEqualTo(member.getNickname());
        assertThat(data.getMember().getProfile()).isEqualTo(member.getProfileImg());

        assertThat(data.getCommentSize()).isEqualTo(4);
        assertThat(data.getLikedSize()).isEqualTo(4);
        assertThat(data.getPostId()).isEqualTo(post.getId());
        assertThat(data.getHashtags().stream().map(ContentHashtagDto::getId).collect(Collectors.toList()))
                .contains(hashtagList.get(0).getId(), hashtagList.get(1).getId(), hashtagList.get(2).getId(),
                        hashtagList.get(3).getId(), hashtagList.get(4).getId(), hashtagList.get(5).getId(),
                        hashtagList.get(6).getId(), hashtagList.get(7).getId(), hashtagList.get(8).getId(),
                        hashtagList.get(9).getId());
        assertThat(data.getMovie().getTitle()).isEqualTo(movie.getTitle());
        assertThat(data.getMovie().getThumbnail()).isEqualTo(movie.getThumbnail());
        assertThat(data.getMovie().getDirectorName()).isEqualTo(movie.getDirectorName());
        assertThat(data.getPublicOfPostStatus()).isEqualTo(post.getPublicOfPostStatus());

        assertThat(posts.getContent().get(0).getPostId()).isEqualTo(postList.get(9).getId());
        assertThat(posts.getContent().get(1).getPostId()).isEqualTo(postList.get(8).getId());
        assertThat(posts.getContent().get(2).getPostId()).isEqualTo(postList.get(7).getId());
        assertThat(posts.hasNext()).isTrue();
        assertThat(posts.getSize()).isEqualTo(3);

        assertThat(data2.getMember().getMemberId()).isEqualTo(member.getId());
        assertThat(data2.getMember().getAccountId()).isEqualTo(member.getAccountId());
        assertThat(data2.getMember().getNickname()).isEqualTo(member.getNickname());
        assertThat(data2.getMember().getProfile()).isEqualTo(member.getProfileImg());

        assertThat(data2.getCommentSize()).isEqualTo(5);
        assertThat(data2.getLikedSize()).isEqualTo(5);
        assertThat(data2.getPostId()).isEqualTo(post.getId());
        assertThat(data2.getHashtags().stream().map(ContentHashtagDto::getId).collect(Collectors.toList()))
                .contains(hashtagList.get(0).getId(), hashtagList.get(1).getId(), hashtagList.get(2).getId(),
                        hashtagList.get(3).getId(), hashtagList.get(4).getId(), hashtagList.get(5).getId(),
                        hashtagList.get(6).getId(), hashtagList.get(7).getId(), hashtagList.get(8).getId(),
                        hashtagList.get(9).getId());
        assertThat(data2.getMovie().getTitle()).isEqualTo(movie.getTitle());
        assertThat(data2.getMovie().getThumbnail()).isEqualTo(movie.getThumbnail());
        assertThat(data2.getMovie().getDirectorName()).isEqualTo(movie.getDirectorName());
        assertThat(data2.getPublicOfPostStatus()).isEqualTo(post.getPublicOfPostStatus());

        assertThat(posts2.getContent().get(0).getPostId()).isEqualTo(postList.get(9).getId());
        assertThat(posts2.getContent().get(1).getPostId()).isEqualTo(postList.get(8).getId());
        assertThat(posts2.getContent().get(2).getPostId()).isEqualTo(postList.get(7).getId());
        assertThat(posts2.hasNext()).isTrue();
        assertThat(posts2.getSize()).isEqualTo(3);
    }
    @Test
    public void 마이페이지_포스트출력() throws Exception{
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

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.B)
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
        Slice<MyPagePostDto> post = postQueryService.getPostsByMemberIdSliced(
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

        Slice<MyPagePostDto> post2 = postQueryService.getPostsByMemberIdSliced(
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

        Slice<MyPagePostDto> post3 = postQueryService.getPostsByMemberIdSliced(
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

        Slice<MyPagePostDto> post4 = postQueryService.getPostsByMemberIdSliced(
                null, member.getId(),
                PageRequest.of(0, 6, Sort.by("id").descending()));

        //then

        assertThat(post.getContent().stream().map(MyPagePostDto::getId)).containsExactly(
                postList.get(9).getId(),
                postList.get(8).getId(),
                postList.get(7).getId(),
                postList.get(6).getId(),
                postList.get(5).getId(),
                postList.get(4).getId());

        assertThat(post2.getContent().stream().map(MyPagePostDto::getId)).containsExactly(
                postList.get(8).getId(),
                postList.get(7).getId(),
                postList.get(6).getId(),
                postList.get(5).getId(),
                postList.get(4).getId(),
                postList.get(3).getId());

        assertThat(post3.getContent().stream().map(MyPagePostDto::getId)).containsExactly(
                postList.get(8).getId(),
                postList.get(7).getId(),
                postList.get(6).getId(),
                postList.get(5).getId(),
                postList.get(4).getId());

        assertThat(post4.getContent().stream().map(MyPagePostDto::getId)).containsExactly(
                postList.get(8).getId(),
                postList.get(7).getId(),
                postList.get(6).getId(),
                postList.get(5).getId(),
                postList.get(4).getId());

        assertThatThrownBy(() -> postQueryService.getPostsByMemberIdSliced(
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
    public void 포스트_좋아요_멤버출력() throws Exception{
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

        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().setNickname(""+i).build());
        }
        memberRepository.saveAll(memberList);

        List<Post> postList = new ArrayList<>();

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.B)
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

        memberService.insertFollowedMemberByMemberId(member2.getId(), member.getId());
        em.flush();
        memberService.insertBlockedMemberByMemberId(member2.getId(), memberList.get(5).getId(), BlockedMemberStatus.A);
        em.flush();

        postService.insertLikedPostByMemberId(member.getId(), postList.get(0).getId());
        em.flush();
        postService.insertLikedPostByMemberId(memberList.get(5).getId(), postList.get(0).getId());
        em.flush();
        postService.insertLikedPostByMemberId(member2.getId(), postList.get(0).getId());
        em.flush();
        postService.insertLikedPostByMemberId(memberList.get(3).getId(), postList.get(0).getId());
        em.flush();
        postService.insertLikedPostByMemberId(memberList.get(7).getId(), postList.get(0).getId());
        em.flush();
        postService.insertLikedPostByMemberId(memberList.get(9).getId(), postList.get(0).getId());
        em.flush();
        postService.insertLikedPostByMemberId(memberList.get(4).getId(), postList.get(0).getId());
        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        List<MembersByContentDto> members =
                postQueryService.getLikePostMembers(member2.getId(), postList.get(0).getId());
        System.out.println("서비스함수끝");

        //then
        assertThat(members.stream().map(MembersByContentDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(member2.getId(), member.getId(), memberList.get(4).getId(), memberList.get(9).getId(),
                        memberList.get(7).getId(), memberList.get(3).getId());

        assertThat(members.stream().map(MembersByContentDto::isOwn).collect(Collectors.toList()))
                .containsExactly(true, false, false, false, false, false);

        assertThat(members.stream().map(MembersByContentDto::isFollowed).collect(Collectors.toList()))
                .containsExactly(false, true, false, false, false, false);

    }
}
