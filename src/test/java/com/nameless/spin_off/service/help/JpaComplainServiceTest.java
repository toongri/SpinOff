package com.nameless.spin_off.service.help;

import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.help.ContentTypeStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.NotExistCollectionException;
import com.nameless.spin_off.exception.member.AlreadyComplainException;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.movie.MovieRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.member.MemberService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.nameless.spin_off.entity.help.ComplainStatus.TYPE1;
import static com.nameless.spin_off.entity.help.ContentTypeStatus.COLLECTION;
import static com.nameless.spin_off.entity.help.ContentTypeStatus.POST;
import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaComplainServiceTest {

    @Autowired MemberService memberService;
    @Autowired ComplainService complainService;
    @Autowired MemberRepository memberRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired MovieRepository movieRepository;
    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired EntityManager em;

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
        complainService.insertComplain(mem.getId(), col.getId(), ContentTypeStatus.COLLECTION, TYPE1);

        System.out.println("멤버");
        Member member = memberRepository.getById(mem2.getId());

        //then

        assertThat(member.getComplainCount()).isEqualTo(1);
        assertThat(member.getPopularity()).isEqualTo(-1.0);
        assertThat(member.getComplains().size()).isEqualTo(1);
    }

    @Test
    public void 컴플레인_포스트_삽입() throws Exception {
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);
        Post po = Post.buildPost().setMember(mem2).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(po);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        complainService.insertComplain(mem.getId(), po.getId(), null, TYPE1);

        System.out.println("멤버");
        Member member = memberRepository.getById(mem2.getId());

        //then

        assertThat(member.getComplainCount()).isEqualTo(1);
        assertThat(member.getPopularity()).isEqualTo(-1.0);
        assertThat(member.getComplains().size()).isEqualTo(1);
    }

    @Test
    public void 컴플레인_삽입_예외처리() throws Exception {
        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);
        Post po = Post.buildPost().setMember(mem2).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(po);
        complainService.insertComplain(mem.getId(), po.getId(), null, TYPE1);

        em.flush();
        em.clear();

        //when

        //then
        Assertions.assertThatThrownBy(() -> complainService.insertComplain(mem.getId(), po.getId(), POST, TYPE1))
                        .isInstanceOf(AlreadyComplainException.class);//.hasMessageContaining("")
        Assertions.assertThatThrownBy(() -> complainService.insertComplain(-1L, po.getId(), POST, TYPE1))
                .isInstanceOf(NotExistMemberException.class);//.hasMessageContaining("")
        Assertions.assertThatThrownBy(() -> complainService.insertComplain(mem.getId(), -1L, POST, TYPE1))
                .isInstanceOf(NotExistPostException.class);//.hasMessageContaining("")
        Assertions.assertThatThrownBy(() -> complainService.insertComplain(mem.getId(), -1L, COLLECTION, TYPE1))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")

    }
}