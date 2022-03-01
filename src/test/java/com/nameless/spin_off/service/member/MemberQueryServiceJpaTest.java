package com.nameless.spin_off.service.member;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.dto.MemberDto;
import com.nameless.spin_off.dto.MemberDto.SearchPageAtAllMemberDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.query.MemberQueryService;
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

    @Autowired MemberService memberService;
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
        List<MemberDto.SearchPageAtMemberMemberDto> content = memberQueryService.getSearchPageMemberAtMemberSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
        System.out.println("함수종료");
        //then
        assertThat(content.stream().map(MemberDto.SearchPageAtMemberMemberDto::getId).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(7).getId(),
                        memberList.get(6).getId(),
                        memberList.get(5).getId(),
                        memberList.get(4).getId(),
                        memberList.get(3).getId(),
                        memberList.get(2).getId());

        assertThat(content.stream().map(MemberDto.SearchPageAtMemberMemberDto::getFollowingMemberNickname).collect(Collectors.toList()))
                .containsExactly(
                        memberList.get(6).getNickname(),
                        memberList.get(5).getNickname(),
                        memberList.get(4).getNickname(),
                        memberList.get(3).getNickname(),
                        memberList.get(2).getNickname(),
                        memberList.get(1).getNickname());

        assertThat(content.stream().map(MemberDto.SearchPageAtMemberMemberDto::getFollowingNumber).collect(Collectors.toList()))
                .containsExactly(
                        6,
                        5,
                        4,
                        3,
                        2,
                        1);

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
        List<SearchPageAtAllMemberDto> content = memberQueryService.getSearchPageMemberAtAllSliced(
                keyword, PageRequest.of(0, 6, Sort.by("popularity").descending()), member.getId()).getContent();
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
}
