package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class SearchQueryServiceJpaTest {

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
    @Autowired PostService postService;
    @Autowired HashtagService hashtagService;

    @Test
    public void 검색_삽입() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Long searchId = memberService.insertSearch(memberId, "dded", SearchedByMemberStatus.D);

        System.out.println("멤버함수");
        Member byId = memberRepository.getById(memberId);

        //then
        assertThat(byId.getSearches().size()).isEqualTo(1);
        assertThat(byId.getSearches().get(0).getContent()).isEqualTo("dded");
    }

    @Test
    public void 연관_검색_출력() throws Exception{

        //given
        String keyword = "아이유";
        Member member = Member.buildMember().setNickname(keyword+"23232").build();
        Long memberId = memberRepository.save(member).getId();
        Member member2 = Member.buildMember().setNickname(keyword+"dfdf").build();
        Long memberId2 = memberRepository.save(member2).getId();

        memberService.insertFollowedMemberByMemberId(memberId2, memberId);

        Movie mov = movieRepository.save(Movie.createMovie(0L, keyword+"asfd", "",
                null, null, null, null));
        Movie mov2 = movieRepository.save(Movie.createMovie(1L, keyword+"h2tr", "",
                null, null, null, null));

        movieService.insertViewedMovieByIp("aa", mov.getId());

        Collection collection1 =
                collectionRepository.save(Collection.createCollection(member, keyword+"fgd", "", PublicOfCollectionStatus.A));
        Collection collection2 =
                collectionRepository.save(Collection.createCollection(member, keyword+"215ww", "", PublicOfCollectionStatus.A));

        collectionService.insertViewedCollectionByIp("aa", collection1.getId());

        Post po = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle(keyword+"fgd").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        Post po2 = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle(keyword+"adfgf").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();

        postRepository.save(po);
        postRepository.save(po2);

        postService.insertViewedPostByIp("aa", po.getId());

        Hashtag hashtag = hashtagRepository.save(Hashtag.createHashtag("asdfa" + keyword));
        Hashtag hashtag2 = hashtagRepository.save(Hashtag.createHashtag("asdfa" + keyword));

        hashtagService.insertViewedHashtagByIp("aa", hashtag.getId());

        em.flush();
        em.clear();
        movieService.updateAllPopularity();
        hashtagService.updateAllPopularity();
        memberService.updateAllPopularity();
        postService.updateAllPopularity();
        collectionService.updateAllPopularity();
        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        RelatedSearchAllDto result = searchQueryService.getRelatedSearchAllByKeyword(keyword, 5);

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
    
    @Test
    public void 인기_해시태그_출력() throws Exception{

        //given
        Hashtag hashtag1 = hashtagRepository.save(Hashtag.createHashtag("asdfa"));
        Hashtag hashtag2 = hashtagRepository.save(Hashtag.createHashtag("asdfa"));
        Hashtag hashtag3 = hashtagRepository.save(Hashtag.createHashtag("asdfabdf"));
        Hashtag hashtag4 = hashtagRepository.save(Hashtag.createHashtag("asdfaweq"));
        Hashtag hashtag5 = hashtagRepository.save(Hashtag.createHashtag("asdfabdffsd"));
        Hashtag hashtag6 = hashtagRepository.save(Hashtag.createHashtag("asdffdfaweq"));

        for (int i = 0; i < 10; i++) {
            hashtag4.insertViewedHashtagByIp(""+i);
            hashtag2.insertViewedHashtagByIp(""+i%8);
            hashtag6.insertViewedHashtagByIp(""+i%6);
            hashtag1.insertViewedHashtagByIp(""+i%4);
            hashtag5.insertViewedHashtagByIp(""+i%2);
            hashtag3.insertViewedHashtagByIp(""+ 0);
            em.flush();
        }
        em.clear();

        movieService.updateAllPopularity();
        hashtagService.updateAllPopularity();
        memberService.updateAllPopularity();
        postService.updateAllPopularity();
        collectionService.updateAllPopularity();
        em.flush();
        em.clear();
        //when
        System.out.println("서비스함수");
        List<MostPopularHashtag> mostPopularHashtags = searchQueryService.getMostPopularHashtagLimit(5);

        System.out.println("함수종료");
        //then
        assertThat(mostPopularHashtags.stream().map(MostPopularHashtag::getId).collect(Collectors.toList()))
                .containsExactly(hashtag4.getId(), hashtag2.getId(), hashtag6.getId(), hashtag1.getId(),
                        hashtag5.getId());
        
    }

    @Test
    public void 연관_해시태그_출력() throws Exception{

        //given
        Hashtag hashtag1 = hashtagRepository.save(Hashtag.createHashtag("asdfa"));
        Hashtag hashtag2 = hashtagRepository.save(Hashtag.createHashtag("asdfa"));
        Hashtag hashtag3 = hashtagRepository.save(Hashtag.createHashtag("asdfabdf"));
        Hashtag hashtag4 = hashtagRepository.save(Hashtag.createHashtag("asdfaweq"));
        Hashtag hashtag5 = hashtagRepository.save(Hashtag.createHashtag("asdfabdffsd"));
        Hashtag hashtag6 = hashtagRepository.save(Hashtag.createHashtag("asdffdfaweq"));
        Hashtag hashtag11 = hashtagRepository.save(Hashtag.createHashtag("asdfabdffsdbds"));
        Hashtag hashtag12 = hashtagRepository.save(Hashtag.createHashtag("asdffdfaweqqwe"));
        Hashtag hashtag13 = hashtagRepository.save(Hashtag.createHashtag("asdfabdffsdqwrqf"));
        Hashtag hashtag14 = hashtagRepository.save(Hashtag.createHashtag("asdffdfaweqbadf"));
        Hashtag hashtag15 = hashtagRepository.save(Hashtag.createHashtag("asdfabdffsdasdbf"));
        Hashtag hashtag16 = hashtagRepository.save(Hashtag.createHashtag("asdffdfaweqbasdf"));
        String keyword = "asdf";

        for (int i = 0; i < 12; i++) {
            hashtag4.insertViewedHashtagByIp(""+i%12);
            hashtag2.insertViewedHashtagByIp(""+i%10);
            hashtag6.insertViewedHashtagByIp(""+i%8);
            hashtag1.insertViewedHashtagByIp(""+i%6);
            hashtag5.insertViewedHashtagByIp(""+i%4);
            hashtag3.insertViewedHashtagByIp(""+i%2);
            hashtag14.insertViewedHashtagByIp(""+i%11);
            hashtag12.insertViewedHashtagByIp(""+i%9);
            hashtag16.insertViewedHashtagByIp(""+i%7);
            hashtag11.insertViewedHashtagByIp(""+i%5);
            hashtag15.insertViewedHashtagByIp(""+i%3);
            hashtag13.insertViewedHashtagByIp(""+ 0);
            em.flush();
        }
        em.clear();

        movieService.updateAllPopularity();
        hashtagService.updateAllPopularity();
        memberService.updateAllPopularity();
        postService.updateAllPopularity();
        collectionService.updateAllPopularity();
        em.flush();
        em.clear();
        //when
        System.out.println("서비스함수");
        List<RelatedSearchHashtagDto> searches = searchQueryService.getRelatedSearchHashtagByKeyword(keyword, 10);

        System.out.println("함수종료");
        //then
        assertThat(searches.stream().map(RelatedSearchHashtagDto::getId).collect(Collectors.toList()))
                .containsExactly(hashtag4.getId(), hashtag14.getId(), hashtag2.getId(), hashtag12.getId(),
                        hashtag6.getId(), hashtag16.getId(), hashtag1.getId(), hashtag11.getId(),
                        hashtag5.getId(), hashtag15.getId());

    }
}