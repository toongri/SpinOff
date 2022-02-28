package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.CollectionDto.RelatedSearchCollectionDto;
import com.nameless.spin_off.dto.CollectionDto.SearchPageAtAllCollectionDto;
import com.nameless.spin_off.dto.HashtagDto.MostPopularHashtag;
import com.nameless.spin_off.dto.HashtagDto.RelatedSearchHashtagDto;
import com.nameless.spin_off.dto.MemberDto.RelatedSearchMemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.dto.MovieDto.RelatedSearchMovieDto;
import com.nameless.spin_off.dto.MovieDto.SearchPageAtAllMovieDto;
import com.nameless.spin_off.dto.PostDto.RelatedSearchPostDto;
import com.nameless.spin_off.dto.PostDto.SearchPageAtAllPostDto;
import com.nameless.spin_off.dto.SearchDto.LastSearchDto;
import com.nameless.spin_off.dto.SearchDto.RelatedSearchAllDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.entity.enums.movie.GenreOfMovieStatus;
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

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaSearchQueryServiceTest {

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
    public void 최근검색출력() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();

        for (int i = 0; i < 10; i++) {
            memberService.insertSearch(memberId, i+"", SearchedByMemberStatus.D);
        }

        em.flush();
        em.clear();
        //when
        System.out.println("서비스함수");
        List<LastSearchDto> lastSearchesByMember = searchQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(LastSearchDto::getContent).collect(Collectors.toList()))
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
        List<LastSearchDto> lastSearchesByMember = searchQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(LastSearchDto::getContent).collect(Collectors.toList()))
                .containsExactly();
    }

    @Test
    public void 최근검색출력_검색기록이_적으면() throws Exception{

        //given
        Member member = Member.buildMember().build();
        Long memberId = memberRepository.save(member).getId();

        for (int i = 0; i < 3; i++) {
            memberService.insertSearch(memberId, i+"", SearchedByMemberStatus.D);
        }

        em.flush();
        em.clear();
        //when

        System.out.println("서비스함수");
        List<LastSearchDto> lastSearchesByMember = searchQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(LastSearchDto::getContent).collect(Collectors.toList()))
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

        Movie mov = movieRepository.save(Movie.createMovie(0L, keyword+"asfd", "",
                null, null, null, null));
        Movie mov2 = movieRepository.save(Movie.createMovie(1L, keyword+"h2tr", "",
                null, null, null, null));

        Collection collection1 =
                collectionRepository.save(Collection.createCollection(member, keyword+"fgd", "", PublicOfCollectionStatus.A));
        Collection collection2 =
                collectionRepository.save(Collection.createCollection(member, keyword+"215ww", "", PublicOfCollectionStatus.A));

        Post po = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle(keyword+"fgd").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setHashTags(List.of()).build();
        Post po2 = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle(keyword+"adfgf").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setHashTags(List.of()).build();


        postRepository.save(po);
        postRepository.save(po2);

        Hashtag hashtag = hashtagRepository.save(Hashtag.createHashtag("asdfa" + keyword));
        Hashtag hashtag2 = hashtagRepository.save(Hashtag.createHashtag("asdfa" + keyword));

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

        //when
        System.out.println("서비스함수");
        List<MostPopularHashtag> mostPopularHashtags = searchQueryService.getMostPopularHashtagLimit(5);

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

        for (int i = 0; i < 10; i++) {
            hashtag4.insertViewedHashtagByIp(""+i);
            hashtag2.insertViewedHashtagByIp(""+i%8);
            hashtag6.insertViewedHashtagByIp(""+i%6);
            hashtag1.insertViewedHashtagByIp(""+i%4);
            hashtag5.insertViewedHashtagByIp(""+i%2);
            hashtag3.insertViewedHashtagByIp(""+ 0);
            hashtag14.insertViewedHashtagByIp(""+i);
            hashtag12.insertViewedHashtagByIp(""+i%8);
            hashtag16.insertViewedHashtagByIp(""+i%6);
            hashtag11.insertViewedHashtagByIp(""+i%4);
            hashtag15.insertViewedHashtagByIp(""+i%2);
            hashtag13.insertViewedHashtagByIp(""+ 0);
            em.flush();
        }
        em.clear();

        //when
        System.out.println("서비스함수");
        List<RelatedSearchHashtagDto> searches = searchQueryService.getRelatedSearchHashtagByKeyword(keyword, 5);

        //then
        assertThat(searches.stream().map(RelatedSearchHashtagDto::getId).collect(Collectors.toList()))
                .containsExactly(hashtag4.getId(), hashtag14.getId(), hashtag2.getId(), hashtag12.getId(),
                        hashtag6.getId(), hashtag16.getId(), hashtag11.getId(), hashtag1.getId(),
                        hashtag5.getId(), hashtag15.getId());

    }

    @Test
    public void 전체검색_컬렉션_테스트_멤버_단일_팔로우() throws Exception{
        //given
        String keyword = "가나다라";
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
                    new CollectionDto.CreateCollectionVO(mem.getId(), keyword + mem.getId(), "", A));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "1").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(8).getId());

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "2").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

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
        List<SearchPageAtAllCollectionDto> content = searchQueryService.getSearchPageCollectionAtAllSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchPageAtAllCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(SearchPageAtAllCollectionDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(5).getNickname(),
                        null,
                        null,
                        null,
                        null,
                        null);
        assertThat(content.stream().map(SearchPageAtAllCollectionDto::getFollowingNumber).collect(Collectors.toList()))
                .containsExactly(
                        1,
                        0,
                        0,
                        0,
                        0,
                        0);
    }

    @Test
    public void 전체검색_컬렉션_테스트_멤버_다중_팔로우() throws Exception{
        //given
        String keyword = "가나다라";
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
                    new CollectionDto.CreateCollectionVO(mem.getId(), keyword + mem.getId(), "", A));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
        }
        postRepository.saveAll(postList);

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "1").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(1).getId());

        collectionService.insertFollowedCollectionByMemberId(memberList.get(5).getId(), collectionList.get(8).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(3).getId(), collectionList.get(8).getId());
        collectionService.insertFollowedCollectionByMemberId(memberList.get(7).getId(), collectionList.get(8).getId());
        memberService.insertFollowedMemberByMemberId(memberList.get(5).getId(), memberList.get(7).getId());

        for (Collection collection : collectionList) {
            Post build = Post.buildPost().setMember(collection.getMember()).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + collection.getMember().getId() + "2").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "2")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

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
        List<SearchPageAtAllCollectionDto> content = searchQueryService.getSearchPageCollectionAtAllSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchPageAtAllCollectionDto::getCollectionId).collect(Collectors.toList()))
                .containsExactly(
                        collectionList.get(8).getId(),
                        collectionList.get(1).getId(),
                        collectionList.get(2).getId(),
                        collectionList.get(4).getId(),
                        collectionList.get(0).getId(),
                        collectionList.get(9).getId());

        assertThat(content.stream().map(SearchPageAtAllCollectionDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getNickname(),
                        memberList.get(5).getNickname(),
                        null,
                        null,
                        null,
                        null);
        assertThat(content.stream().map(SearchPageAtAllCollectionDto::getFollowingNumber).collect(Collectors.toList()))
                .containsExactly(
                        3,
                        1,
                        0,
                        0,
                        0,
                        0);
    }

    @Test
    public void 전체검색_멤버_테스트() throws Exception{
        //given
        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().setNickname(keyword+i).build());
        }

        List<Post> postList = new ArrayList<>();
        List<Collection> collectionList = new ArrayList<>();
        memberRepository.saveAll(memberList);
        for (Member mem : memberList) {
            member.addFollowedMember(mem);
            Long aLong = collectionService.insertCollectionByCollectionVO(
                    new CollectionDto.CreateCollectionVO(mem.getId(), keyword + mem.getId(), "", A));
            Collection byId = collectionRepository.getById(aLong);
            collectionList.add(byId);
            postList.add(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle(keyword + mem.getId() + "0").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(mem.getId()+"0")
                    .setHashTags(List.of()).setCollections(List.of(byId)).build());
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

        //when
        System.out.println("서비스");
        List<SearchPageAtAllMemberDto> content = searchQueryService.getSearchPageMemberAtAllSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending())).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchPageAtAllMemberDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getId(),
                        memberList.get(6).getId(),
                        memberList.get(5).getId(),
                        memberList.get(4).getId(),
                        memberList.get(3).getId(),
                        memberList.get(2).getId());
    }

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
        List<SearchPageAtAllMovieDto> content = searchQueryService.getSearchPageMovieAtAllSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending())).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchPageAtAllMovieDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        movieList.get(7).getId(),
                        movieList.get(6).getId(),
                        movieList.get(5).getId(),
                        movieList.get(4).getId(),
                        movieList.get(3).getId(),
                        movieList.get(2).getId());
        assertThat(content.stream().map(SearchPageAtAllMovieDto::getGenreOfMovieStatuses).collect(Collectors.toList()))
                .containsExactly(
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C),
                        List.of(GenreOfMovieStatus.A, GenreOfMovieStatus.C));
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

        List<Post> postList = new ArrayList<>();
        memberRepository.saveAll(memberList);

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
        List<SearchPageAtAllPostDto> content = searchQueryService.getSearchPagePostAtAllSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending())).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchPageAtAllPostDto::getPostId).collect(Collectors.toList()))
                .containsExactly(
                        postList.get(8).getId(),
                        postList.get(1).getId(),
                        postList.get(2).getId(),
                        postList.get(4).getId(),
                        postList.get(0).getId(),
                        postList.get(9).getId());
    }
}