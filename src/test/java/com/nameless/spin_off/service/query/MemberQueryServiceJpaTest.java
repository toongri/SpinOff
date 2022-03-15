package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchMemberDto;
import com.nameless.spin_off.dto.SearchDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.member.SearchedByMemberStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
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
public class MemberQueryServiceJpaTest {

    @Autowired
    MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired CollectionRepository collectionRepository;
    @Autowired PostRepository postRepository;
    @Autowired MemberQueryService memberQueryService;

    @Test
    public void 유저검색() throws Exception{

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
                    new CollectionDto.CreateCollectionVO(keyword + mem.getId(), "", A), mem.getId());
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
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collection.getMember().getId() + "1")
                    .setHashTags(List.of()).setCollections(List.of(collection)).build();
            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        List<Integer> integers = List.of(5, 9, 8, 3, 2, 4, 7, 0, 1, 6);

        for (Integer integer : integers) {
            Post build = Post.buildPost().setMember(collectionList.get(integer).getMember())
                    .setPostPublicStatus(PublicOfPostStatus.A)
                    .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                    .setThumbnailUrl(collectionList.get(integer).getMember().getId() + "2")
                    .setHashTags(List.of()).setCollections(List.of(collectionList.get(integer))).build();

            postRepository.save(build);
            postList.add(build);
            em.flush();
        }

        Post build = Post.buildPost().setMember(collectionList.get(6).getMember())
                .setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setCollections(List.of()).setPostedMedias(List.of())
                .setThumbnailUrl(collectionList.get(6).getMember().getId() + "3")
                .setHashTags(List.of()).setCollections(List.of(collectionList.get(6))).build();

        postRepository.save(build);
        postList.add(build);
        em.flush();
        em.clear();

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
        List<SearchMemberDto> content = memberQueryService.getSearchPageMemberAtMemberSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(SearchMemberDto::getMemberId).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getId(),
                        memberList.get(6).getId(),
                        memberList.get(5).getId(),
                        memberList.get(4).getId(),
                        memberList.get(3).getId(),
                        memberList.get(2).getId());

        assertThat(content.stream().map(SearchMemberDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(6).getNickname(),
                        memberList.get(5).getNickname(),
                        memberList.get(4).getNickname(),
                        memberList.get(3).getNickname(),
                        memberList.get(2).getNickname(),
                        memberList.get(1).getNickname());

        assertThat(content.stream().map(SearchMemberDto::getFollowingNumber).collect(Collectors.toList()))
                .containsExactly(
                        6,
                        5,
                        4,
                        3,
                        2,
                        1);
        assertThat(content.stream().map(SearchMemberDto::getThumbnailUrls).collect(Collectors.toList()))
                .containsExactly(
                        List.of(memberList.get(7).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(7).getPosts().get(1).getThumbnailUrl()),
                        List.of(memberList.get(6).getPosts().get(3).getThumbnailUrl(),
                                memberList.get(6).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(6).getPosts().get(1).getThumbnailUrl(),
                                memberList.get(6).getPosts().get(0).getThumbnailUrl()),
                        List.of(memberList.get(5).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(5).getPosts().get(1).getThumbnailUrl()),
                        List.of(memberList.get(4).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(4).getPosts().get(1).getThumbnailUrl()),
                        List.of(memberList.get(3).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(3).getPosts().get(1).getThumbnailUrl()),
                        List.of(memberList.get(2).getPosts().get(2).getThumbnailUrl(),
                                memberList.get(2).getPosts().get(1).getThumbnailUrl()));

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
                    new CollectionDto.CreateCollectionVO(keyword + mem.getId(), "", A), mem.getId());
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
        List<MemberDto.SearchAllMemberDto> content = memberQueryService.getSearchPageMemberAtAllSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(MemberDto.SearchAllMemberDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getId(),
                        memberList.get(6).getId(),
                        memberList.get(5).getId(),
                        memberList.get(4).getId(),
                        memberList.get(3).getId(),
                        memberList.get(2).getId());
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
        List<SearchDto.LastSearchDto> lastSearchesByMember = memberQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchDto.LastSearchDto::getContent).collect(Collectors.toList()))
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
        List<SearchDto.LastSearchDto> lastSearchesByMember = memberQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchDto.LastSearchDto::getContent).collect(Collectors.toList()))
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
        List<SearchDto.LastSearchDto> lastSearchesByMember = memberQueryService.getLastSearchesByMemberLimit(memberId, 5);

        System.out.println("결과");
        //then
        assertThat(lastSearchesByMember.stream().map(SearchDto.LastSearchDto::getContent).collect(Collectors.toList()))
                .containsExactly("2", "1", "0");
    }
}
