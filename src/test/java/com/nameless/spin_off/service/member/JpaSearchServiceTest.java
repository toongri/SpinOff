package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.*;
import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.member.SearchedByMember;
import com.nameless.spin_off.entity.member.SearchedByMemberStatus;
import com.nameless.spin_off.entity.movie.Movie;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaSearchServiceTest {

    @Autowired SearchService searchService;
    @Autowired MemberRepository memberRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired MovieRepository movieRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;

    @Test
    public void 검색_삽입() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long searchId = searchService.insertSearch(memberId, "dded", SearchedByMemberStatus.MEMBER);

        System.out.println("멤버함수");
        Member byId = memberRepository.getById(memberId);

        //then
        assertThat(byId.getSearches().size()).isEqualTo(1);
        assertThat(byId.getSearches().get(0).getContent()).isEqualTo("dded");
    }
    
    @Test
    public void 최근검색출력() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();

        for (int i = 0; i < 10; i++) {
            searchService.insertSearch(memberId, i+"", SearchedByMemberStatus.MEMBER);
        }

        em.flush();
        em.clear();
        //when
        System.out.println("서비스함수");
        List<SearchedByMember> lastSearchesByMember = searchService.getLastSearchesByMember(memberId);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchedByMember::getContent).collect(Collectors.toList()))
                .containsExactly("9", "8", "7", "6", "5");

    }

    @Test
    public void 최근검색출력_검색기록이_없으면() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();

//        for (int i = 0; i < 10; i++) {
//            searchService.insertSearch(memberId, i+"", SearchedByMemberStatus.MEMBER);
//        }

        em.flush();
        em.clear();
        //when

        System.out.println("서비스함수");
        List<SearchedByMember> lastSearchesByMember = searchService.getLastSearchesByMember(memberId);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchedByMember::getContent).collect(Collectors.toList()))
                .containsExactly();
    }

    @Test
    public void 최근검색출력_검색기록이_적으면() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();

        for (int i = 0; i < 3; i++) {
            searchService.insertSearch(memberId, i+"", SearchedByMemberStatus.MEMBER);
        }

        em.flush();
        em.clear();
        //when

        System.out.println("서비스함수");
        List<SearchedByMember> lastSearchesByMember = searchService.getLastSearchesByMember(memberId);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchedByMember::getContent).collect(Collectors.toList()))
                .containsExactly("2", "1", "0");

    }

    @Test
    public void 연관_검색_출력() throws Exception{

        //given
        String keyword = "아이유";
        Member member = Member.buildMember().setNickname(keyword+"23232").build();
        Long memberId = memberRepository.save(member).getId();
        Member member2 = Member.buildMember().setNickname(keyword+"dfdf").build();
        Long memberId2 = memberRepository.save(member2).getId();

        Movie mov = movieRepository.save(Movie.createMovie(0L, keyword+"asfd", ""));
        Movie mov2 = movieRepository.save(Movie.createMovie(1L, keyword+"h2tr", ""));

        Collection collection1 =
                collectionRepository.save(Collection.createCollection(member, keyword+"fgd", "", PublicOfCollectionStatus.PUBLIC));
        Collection collection2 =
                collectionRepository.save(Collection.createCollection(member, keyword+"215ww", "", PublicOfCollectionStatus.PUBLIC));

        Post po = Post.buildPost().setMember(member).setTitle(keyword+"fgd").setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        Post po2 = Post.buildPost().setMember(member).setTitle(keyword+"adfgf").setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(po);
        postRepository.save(po2);

        Hashtag hashtag = hashtagRepository.save(Hashtag.createHashtag("asdfa" + keyword));
        Hashtag hashtag2 = hashtagRepository.save(Hashtag.createHashtag("asdfa" + keyword));

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        RelatedSearchDto result = searchService.getRelatedSearchByKeyword(keyword);

        //then
        System.out.println("결과");
        assertThat(result.getCollections().stream()
                .map(RelatedSearchCollectionDto::getContent)
                .collect(Collectors.toList()))
                .containsExactly(keyword+"fgd", keyword+"215ww");
        assertThat(result.getHashtags().stream()
                .map(RelatedSearchHashtagDto::getContent)
                .collect(Collectors.toList()))
                .containsExactly("asdfa" + keyword, "asdfa" + keyword);
        assertThat(result.getMembers().stream()
                .map(RelatedSearchMemberDto::getNickname)
                .collect(Collectors.toList()))
                .containsExactly(keyword+"23232", keyword+"dfdf");
        assertThat(result.getMovies().stream()
                .map(RelatedSearchMovieDto::getTitle)
                .collect(Collectors.toList()))
                .containsExactly(keyword+"asfd", keyword+"h2tr");
        assertThat(result.getPosts().stream()
                .map(RelatedSearchPostDto::getTitle)
                .collect(Collectors.toList()))
                .containsExactly(keyword+"fgd", keyword+"adfgf");

    }
}