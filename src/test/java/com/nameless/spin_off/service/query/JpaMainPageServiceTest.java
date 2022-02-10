package com.nameless.spin_off.service.query;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.dto.PostDto.MainPagePostDto;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Hashtag;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaMainPageServiceTest {

    @Autowired MainPageService mainPageService;
    @Autowired MemberRepository memberRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostRepository postRepository;
    @Autowired EntityManager em;

    @Test
    public void 팔로잉_해시태그_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        List<Hashtag> hashtagList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            hashtagList.add(Hashtag.createHashtag(""+i));
        }

        List<Post> postList = new ArrayList<>();
        hashtagRepository.saveAll(hashtagList);
        for (Hashtag hashtag : hashtagList) {
            member.addFollowedHashtag(hashtag);
            postList.add(Post.buildPost().setMember(member).setHashTags(List.of(hashtag)).build());
        }
        postRepository.saveAll(postList);
        em.flush();
        em.clear();

        //when
        System.out.println("쿼리");
        List<MainPagePostDto> content = mainPageService
                .getPostsByFollowedHashtagOrderByIdSliced(PageRequest.of(0, 15), member.getId()).getContent();

        //then
        assertThat(content.size()).isEqualTo(postList.size());
    }

}