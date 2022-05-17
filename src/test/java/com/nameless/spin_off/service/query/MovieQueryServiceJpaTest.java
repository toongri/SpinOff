package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedMostTaggedHashtagDto;
import com.nameless.spin_off.dto.MemberDto.MovieMemberDto;
import com.nameless.spin_off.dto.MovieDto.ReadMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieAboutFirstMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchMovieFirstDto;
import com.nameless.spin_off.dto.SearchDto.SearchFirstDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
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

import static com.nameless.spin_off.enums.collection.PublicOfCollectionStatus.A;
import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class MovieQueryServiceJpaTest {

    @Autowired MemberQueryService memberQueryService;
    @Autowired SearchQueryService searchQueryService;
    @Autowired MovieService movieService;
    @Autowired MemberRepository memberRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired MovieRepository movieRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;
    @Autowired MemberService memberService;
    @Autowired CollectionService collectionService;
    @Autowired MovieQueryService movieQueryService;
    @Autowired HashtagService hashtagService;
    @Autowired PostService postService;
    
    @Test
    public void 영화_키워드_검색() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            movieList.add(Movie.createMovie((long) i, keyword + i, null, null, null));
            memberList.add(Member.buildMember().setNickname(keyword+i).build());
        }

        List<Post> postList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        movieRepository.saveAll(movieList);

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

        for (Movie movie : movieList) {
            movieService.insertViewedMovieByIp("22", movie.getId());
        }


        em.flush();
        em.clear();
        movieService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<SearchMovieDto> content = movieQueryService.getSearchPageMovieAtMovieSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending())).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchMovieDto::getMovieId).collect(Collectors.toList()))
                .containsExactly(
                        movieList.get(7).getId(),
                        movieList.get(6).getId(),
                        movieList.get(5).getId(),
                        movieList.get(4).getId(),
                        movieList.get(3).getId(),
                        movieList.get(2).getId());
    }

    @Test
    public void 영화_검색() throws Exception{
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
            movieList.add(Movie.createMovie((long) i, keyword + i, null, null, null));
        }
        memberRepository.saveAll(memberList);
        movieRepository.saveAll(movieList);

        em.flush();

        List<Post> postList = new ArrayList<>();


        em.flush();

        Post build = Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(3))
                .setHashTags(List.of(hashtagList.get(0))).build();
        postRepository.save(build);
        postList.add(build);

        em.flush();

        Post build1 = Post.buildPost().setMember(memberList.get(4)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(4))
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1))).build();
        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        Post build2 = Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2))).build();
        postRepository.save(build2);
        postList.add(build2);
        em.flush();

        Post build3 = Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setMovie(movieList.get(7))
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3)))
                .build();

        postRepository.save(build3);
        postList.add(build3);
        em.flush();

        Post build4 = Post.buildPost().setMember(memberList.get(1)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4)))
                .build();

        postRepository.save(build4);
        postList.add(build4);
        em.flush();

        Post build5 = Post.buildPost().setMember(memberList.get(7)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(7))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5)))
                .build();

        postRepository.save(build5);
        postList.add(build5);
        em.flush();

        Post build6 = Post.buildPost().setMember(memberList.get(8)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6)))
                .build();

        postRepository.save(build6);
        postList.add(build6);
        em.flush();

        Post build7 = Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(7))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7)))
                .build();

        postRepository.save(build7);
        postList.add(build7);
        em.flush();

        Post build8 = Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8)))
                .build();
        postRepository.save(build8);
        postList.add(build8);
        em.flush();

        Post build9 = Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(7))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9)))
                .build();
        postRepository.save(build9);
        postList.add(build9);
        em.flush();

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

        em.flush();
        em.clear();
        for (Movie movie : movieList) {
            movieService.insertViewedMovieByIp("22", movie.getId());
            hashtagService.insertViewedHashtagByIp(movie.getId()+"", hashtagList.get(2).getId());
            hashtagService.insertViewedHashtagByIp(movie.getId()+"", hashtagList.get(4).getId());
        }
        movieService.insertViewedMovieByIp("1", movieList.get(6).getId());
        movieService.insertViewedMovieByIp("2", movieList.get(6).getId());
        movieService.insertViewedMovieByIp("3", movieList.get(5).getId());
        movieService.insertViewedMovieByIp("1", movieList.get(5).getId());

        em.flush();
        em.clear();
        movieService.updateAllPopularity();
        postService.updateAllPopularity();
        collectionService.updateAllPopularity();
        hashtagService.updateAllPopularity();
        memberService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        SearchFirstDto<SearchMovieFirstDto> result = movieQueryService.getSearchPageMovieAtMovieSlicedFirst(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), 6);

        List<RelatedMostTaggedHashtagDto> hashtags = result.getHashtags();
        SearchMovieFirstDto data = result.getData();
        List<SearchMovieDto> content = data.getMovies().getContent();
        SearchMovieAboutFirstMovieDto firstMovie = data.getFirstMovie();


        System.out.println("함수종료");

        assertThat(content.stream().map(SearchMovieDto::getMovieId).collect(Collectors.toList()))
                .containsExactly(
                        movieList.get(7).getId(),
                        movieList.get(6).getId(),
                        movieList.get(5).getId(),
                        movieList.get(4).getId(),
                        movieList.get(3).getId(),
                        movieList.get(2).getId());

        //then
        assertThat(hashtags.stream().map(RelatedMostTaggedHashtagDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        hashtagList.get(0).getId(),
                        hashtagList.get(1).getId(),
                        hashtagList.get(2).getId(),
                        hashtagList.get(3).getId(),
                        hashtagList.get(4).getId(),
                        hashtagList.get(5).getId());

        assertThat(firstMovie.getMovieId()).isEqualTo(movieList.get(7).getId());

        assertThat(firstMovie.getThumbnails()).containsExactly(
                postList.get(9).getThumbnailUrl(),
                postList.get(7).getThumbnailUrl(),
                postList.get(5).getThumbnailUrl(),
                postList.get(3).getThumbnailUrl());
    }

    @Test
    public void 영화_조회() throws Exception{
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
            movieList.add(Movie.createMovie((long) i, keyword + i, null, null, null));
            movieList.get(i).updateGenres(List.of("" + i, "" + (i + 1), "" + (i + 2), "" + (i + 3)));
        }
        memberRepository.saveAll(memberList);
        movieRepository.saveAll(movieList);

        em.flush();

        List<Post> postList = new ArrayList<>();

        Post build = Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(3))
                .setHashTags(List.of(hashtagList.get(0))).build();
        postRepository.save(build);
        postList.add(build);

        em.flush();

        Post build1 = Post.buildPost().setMember(memberList.get(4)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(4))
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1))).build();
        postRepository.save(build1);
        postList.add(build1);
        em.flush();

        Post build2 = Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(hashtagList.get(0), hashtagList.get(1), hashtagList.get(2))).build();
        postRepository.save(build2);
        postList.add(build2);
        em.flush();

        Post build3 = Post.buildPost().setMember(memberList.get(2)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setMovie(movieList.get(7))
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3)))
                .build();

        postRepository.save(build3);
        postList.add(build3);
        em.flush();

        Post build4 = Post.buildPost().setMember(memberList.get(1)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4)))
                .build();

        postRepository.save(build4);
        postList.add(build4);
        em.flush();

        Post build5 = Post.buildPost().setMember(memberList.get(7)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(7))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5)))
                .build();

        postRepository.save(build5);
        postList.add(build5);
        em.flush();

        Post build6 = Post.buildPost().setMember(memberList.get(8)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6)))
                .build();

        postRepository.save(build6);
        postList.add(build6);
        em.flush();

        Post build7 = Post.buildPost().setMember(memberList.get(3)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(7))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7)))
                .build();

        postRepository.save(build7);
        postList.add(build7);
        em.flush();

        Post build8 = Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(7))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8)))
                .build();
        postRepository.save(build8);
        postList.add(build8);
        em.flush();

        Post build9 = Post.buildPost().setMember(memberList.get(5)).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setMovie(movieList.get(7))
                .setHashTags(List.of(
                        hashtagList.get(0), hashtagList.get(1), hashtagList.get(2), hashtagList.get(3),
                        hashtagList.get(4), hashtagList.get(5), hashtagList.get(6), hashtagList.get(7),
                        hashtagList.get(8), hashtagList.get(9)))
                .build();
        postRepository.save(build9);
        postList.add(build9);
        em.flush();

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

        memberService.insertBlockedMemberByMemberId
                (memberList.get(8).getId(), memberList.get(2).getId(), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId
                (memberList.get(8).getId(), memberList.get(3).getId(), BlockedMemberStatus.A);
        memberService.insertBlockedMemberByMemberId
                (memberList.get(8).getId(), memberList.get(4).getId(), BlockedMemberStatus.A);

        em.flush();
        em.clear();
        for (Movie movie : movieList) {
            movieService.insertViewedMovieByIp("22", movie.getId());
            hashtagService.insertViewedHashtagByIp(movie.getId()+"", hashtagList.get(2).getId());
            hashtagService.insertViewedHashtagByIp(movie.getId()+"", hashtagList.get(4).getId());
        }
        movieService.insertViewedMovieByIp("1", movieList.get(6).getId());
        movieService.insertViewedMovieByIp("2", movieList.get(6).getId());
        movieService.insertViewedMovieByIp("3", movieList.get(5).getId());
        movieService.insertViewedMovieByIp("1", movieList.get(5).getId());

        em.flush();
        em.clear();
        movieService.updateAllPopularity();
        postService.updateAllPopularity();
        collectionService.updateAllPopularity();
        hashtagService.updateAllPopularity();
        memberService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        ReadMovieDto movieForRead1 = movieQueryService.getMovieForRead(member.getId(), movieList.get(7).getId());
        System.out.println("서비스함수끝");

        ReadMovieDto movieForRead2 = movieQueryService.getMovieForRead(null, movieList.get(7).getId());

        ReadMovieDto movieForRead3 = movieQueryService.getMovieForRead(memberList.get(8).getId(), movieList.get(7).getId());

        ReadMovieDto movieForRead4 = movieQueryService.getMovieForRead(null, movieList.get(5).getId());
        ReadMovieDto movieForRead5 = movieQueryService.getMovieForRead(member.getId(), movieList.get(5).getId());

        //then

        assertThat(movieForRead1.getFollowedSize()).isEqualTo(6L);
        assertThat(movieForRead2.getFollowedSize()).isEqualTo(6L);
        assertThat(movieForRead3.getFollowedSize()).isEqualTo(3L);
        assertThat(movieForRead4.getFollowedSize()).isEqualTo(4L);
        assertThat(movieForRead5.getFollowedSize()).isEqualTo(4L);
        assertThat(movieForRead1.getTaggedSize()).isEqualTo(5L);
        assertThat(movieForRead2.getTaggedSize()).isEqualTo(5L);
        assertThat(movieForRead3.getTaggedSize()).isEqualTo(3L);
        assertThat(movieForRead4.getTaggedSize()).isEqualTo(0L);
        assertThat(movieForRead5.getTaggedSize()).isEqualTo(0L);
        assertThat(movieForRead1.getMembers().stream().map(MovieMemberDto::getId))
                .containsExactly(memberList.get(5).getId(), memberList.get(3).getId(), memberList.get(7).getId());
        assertThat(movieForRead2.getMembers().stream().map(MovieMemberDto::getId))
                .containsExactly(memberList.get(5).getId(), memberList.get(3).getId(), memberList.get(7).getId());
        assertThat(movieForRead3.getMembers().stream().map(MovieMemberDto::getId))
                .containsExactly(memberList.get(5).getId(), memberList.get(7).getId());
        assertThat(movieForRead4.getMembers().stream().map(MovieMemberDto::getId)).containsExactly();
        assertThat(movieForRead5.getMembers().stream().map(MovieMemberDto::getId)).containsExactly();
        assertThat(movieForRead1.getGenres()).containsExactly("7", "8", "9", "10");
        assertThat(movieForRead4.getGenres()).containsExactly("5", "6", "7", "8");


    }
}