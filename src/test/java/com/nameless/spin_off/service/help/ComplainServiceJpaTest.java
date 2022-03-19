package com.nameless.spin_off.service.help;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.help.ComplainStatus;
import com.nameless.spin_off.entity.enums.help.ContentTypeStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.member.AlreadyComplainException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.nameless.spin_off.entity.enums.help.ContentTypeStatus.A;
import static com.nameless.spin_off.entity.enums.help.ContentTypeStatus.B;
import static com.nameless.spin_off.entity.enums.member.MemberScoreEnum.MEMBER_FOLLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class ComplainServiceJpaTest {

    @Autowired MemberService memberService;
    @Autowired ComplainService complainService;
    @Autowired MemberRepository memberRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired MovieRepository movieRepository;
    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired EntityManager em;
    @Autowired PostService postService;

    @Test
    public void 컴플레인_컬렉션_삽입() throws Exception {
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);
        Collection col = Collection.createDefaultCollection(mem2);
        collectionRepository.save(col);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        complainService.insertComplain(mem.getId(), col.getId(), ContentTypeStatus.B, ComplainStatus.A);
        em.flush();
        em.clear();
        System.out.println("멤버");
        memberService.insertFollowedMemberByMemberId(mem2.getId(), mem.getId());
        em.flush();
        em.clear();
        memberService.updateAllPopularity();
        Member member = memberRepository.getById(mem2.getId());
        //then

        assertThat(member.getPopularity()).isEqualTo(MEMBER_FOLLOW.getLatestScore() - 1);
        assertThat(member.getComplains().size()).isEqualTo(1);
    }

    @Test
    public void 컴플레인_포스트_삽입() throws Exception {
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);
        Post po = Post.buildPost().setMember(mem2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(po);


        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        complainService.insertComplain(mem.getId(), po.getId(), A, ComplainStatus.A);

        System.out.println("멤버");
        memberService.insertFollowedMemberByMemberId(mem2.getId(), mem.getId());
        em.flush();
        em.clear();
        memberService.updateAllPopularity();
        Member member = memberRepository.getById(mem2.getId());

        //then

        assertThat(member.getPopularity()).isEqualTo(MEMBER_FOLLOW.getLatestScore() - 1);
        assertThat(member.getComplains().size()).isEqualTo(1);
    }

    @Test
    public void 컴플레인_삽입_예외처리() throws Exception {
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(po);
        complainService.insertComplain(mem.getId(), po.getId(), A, ComplainStatus.A);

        em.flush();
        em.clear();

        //when

        //then
        assertThatThrownBy(() -> complainService.insertComplain(mem.getId(), po.getId(), A, ComplainStatus.A))
                        .isInstanceOf(AlreadyComplainException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> complainService.insertComplain(mem.getId(), -1L, A, ComplainStatus.A))
                .isInstanceOf(NotExistPostException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> complainService.insertComplain(mem.getId(), -1L, B, ComplainStatus.A))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")

    }
}