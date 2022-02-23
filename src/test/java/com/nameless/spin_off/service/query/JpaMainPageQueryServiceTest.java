package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.FollowedMemberRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaMainPageQueryServiceTest {

    @Autowired
    MainPageQueryService mainPageQueryService;
    @Autowired MemberRepository memberRepository;
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
            postList.add(Post.buildPost().setMember(member2).setHashTags(List.of(hashtag)).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build());
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
            postList.add(Post.buildPost().setMember(member2).setMovie(movie).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build());
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
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build());
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
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build());
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