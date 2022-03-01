package com.nameless.spin_off.repository.movie;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.MovieDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.movie.GenreOfMovieStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.query.MemberQueryRepository;
import com.nameless.spin_off.repository.query.MovieQueryRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.movie.MovieService;
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

@SpringBootTest
@Transactional
public class MovieQueryRepositoryTest {

    @Autowired PostRepository postRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CollectionService collectionService;
    @Autowired MovieRepository movieRepository;
    @Autowired MovieService movieService;
    @Autowired MovieQueryRepository movieQueryRepository;
    @Autowired MemberService memberService;
    @Autowired MemberQueryRepository memberQueryRepository;
    @Autowired EntityManager em;


    @Test
    public void 전체검색_영화_테스트() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            movieList.add(Movie.createMovie((long) i, keyword + i, i + "",
                    GenreOfMovieStatus.A, GenreOfMovieStatus.C,
                    null, null));
            memberList.add(Member.buildMember().setNickname(keyword+i).build());
        }

        List<Post> postList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        movieRepository.saveAll(movieList);

        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(mem.getId(), keyword + mem.getId(), "", A));
            Collection byId = collectionRepository.getById(aLong);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
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

        em.flush();
        em.clear();

        //when
        System.out.println("서비스");
        List<MovieDto.SearchPageAtAllMovieDto> content = movieQueryRepository.findAllSlicedForSearchPageAtAll(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending())).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(MovieDto.SearchPageAtAllMovieDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        movieList.get(7).getId(),
                        movieList.get(6).getId(),
                        movieList.get(5).getId(),
                        movieList.get(4).getId(),
                        movieList.get(3).getId(),
                        movieList.get(2).getId());
        assertThat(content.stream().map(MovieDto.SearchPageAtAllMovieDto::getGenreOfMovieStatuses).collect(Collectors.toList()))
                .containsExactly(
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C));
    }
}