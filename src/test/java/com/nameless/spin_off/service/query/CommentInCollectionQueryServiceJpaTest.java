package com.nameless.spin_off.service.query;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.CommentDto.ContentCommentDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.collection.CollectionService;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CommentInCollectionQueryServiceJpaTest {

    @Autowired CommentInCollectionService commentInCollectionService;
    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired CollectionQueryService collectionQueryService;
    @Autowired MemberService memberService;
    @Autowired CommentInCollectionQueryService commentInCollectionQueryService;

    @Test
    public void 댓글조회() throws Exception{
        //given

        String keyword = "가나다라";
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(Member.buildMember().build());
        }
        Collection collection = collectionRepository.save(Collection.createDefaultCollection(member));

        Long ddd = commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CreateCommentInCollectionVO(
                collection.getId(), null, "ddd"), member.getId());

        //when

        List<ContentCommentDto> commentsByCollectionId =
                commentInCollectionQueryService.getCommentsByCollectionId(MemberDetails.builder()
                        .id(member.getId())
                        .accountId(member.getAccountId())
                        .accountPw(member.getAccountPw())
                        .authorities(member.getRoles()
                                .stream()
                                .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                                .collect(Collectors.toSet()))
                        .build(), collection.getId());

        //then

    }
}