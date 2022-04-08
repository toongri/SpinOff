package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto.CreateCollectionVO;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.entity.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.entity.enums.collection.CollectionScoreEnum.*;
import static com.nameless.spin_off.entity.enums.collection.PublicOfCollectionStatus.A;
import static com.nameless.spin_off.entity.enums.post.PostScoreEnum.POST_COLLECT;
import static com.nameless.spin_off.entity.enums.post.PostScoreEnum.POST_VIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CollectionServiceJpaTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired MemberService memberService;

    @Test
    public void 컬렉션_생성_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스 함수");
        Long aLong = collectionService
                .insertCollectionByCollectionVO(new CreateCollectionVO("", "", A), member.getId());
        System.out.println("컬렉션 조회 함수");
        Collection collection = collectionRepository.getById(aLong);

        //then
        assertThat(collection.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    public void 컬렉션_좋아요_업데이트() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        collectionService.insertLikedCollectionByMemberId(mem.getId(), col.getId());
        System.out.println("멤버");
        collectionService.insertViewedCollectionByIp("22", col.getId());
        collectionService.updateAllPopularity();
        em.flush();
        Collection collection = collectionRepository.getById(col.getId());
        Member member = memberRepository.getById(mem.getId());

        //then

        assertThat(collection.getPopularity())
                .isEqualTo(COLLECTION_LIKE.getLatestScore() * COLLECTION_LIKE.getRate() +
                        COLLECTION_VIEW.getRate() * COLLECTION_VIEW.getLatestScore());
        assertThat(collection.getLikedCollections().size()).isEqualTo(1);
        assertThat(collection.getLikedCollections().get(0).getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    public void 컬렉션_좋아요_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        Collection col2 = Collection.createDefaultCollection(mem2);
        collectionRepository.save(col2);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        collectionService.insertLikedCollectionByMemberId(mem.getId(), col.getId());

        System.out.println("멤버");
        Member member = memberRepository.getById(mem.getId());
        Collection collection = collectionRepository.getById(col.getId());

        //then

        assertThatThrownBy(() -> collectionService.insertLikedCollectionByMemberId(mem.getId(), -1L))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> collectionService.insertLikedCollectionByMemberId(mem.getId(), collection.getId()))
                .isInstanceOf(AlreadyLikedCollectionException.class);//.hasMessageContaining("")

        memberService.insertBlockedMemberByMemberId(mem.getId(), mem2.getId(), BlockedMemberStatus.A);
        em.flush();
        assertThatThrownBy(() -> collectionService.insertLikedCollectionByMemberId(mem.getId(), col2.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }

    @Test
    public void 컬렉션_팔로우_업데이트() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);
        Collection col = Collection.createCollection(mem2, "", "", PublicOfCollectionStatus.A);
        collectionRepository.save(col);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        collectionService.insertFollowedCollectionByMemberId(mem.getId(), col.getId());
        System.out.println("멤버");
        collectionService.insertViewedCollectionByIp("22", col.getId());
        collectionService.updateAllPopularity();
        em.flush();

        Member member = memberRepository.getById(mem.getId());
        Collection collection = collectionRepository.getById(col.getId());

        //then
        assertThat(collection.getPopularity())
                .isEqualTo(COLLECTION_FOLLOW.getLatestScore() * COLLECTION_FOLLOW.getRate() +
                        COLLECTION_VIEW.getRate() * COLLECTION_VIEW.getLatestScore());
        assertThat(collection.getFollowingMembers().size()).isEqualTo(1);
        assertThat(collection.getFollowingMembers().get(0).getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    public void 컬렉션_팔로우_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);
        Collection col = Collection.createCollection(mem, "", "", PublicOfCollectionStatus.A);
        collectionRepository.save(col);
        Collection col2 = Collection.createCollection(mem2, "", "", PublicOfCollectionStatus.A);
        collectionRepository.save(col2);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        collectionService.insertFollowedCollectionByMemberId(mem.getId(), col2.getId());

        System.out.println("멤버");
        Collection collection = collectionRepository.getById(col2.getId());
        Member member = memberRepository.findById(mem.getId()).get();

        //then
        assertThatThrownBy(() -> collectionService.insertFollowedCollectionByMemberId(mem.getId(), col.getId()))
                .isInstanceOf(CantFollowOwnCollectionException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> collectionService.insertFollowedCollectionByMemberId(mem.getId(), 0L))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> collectionService.insertFollowedCollectionByMemberId(mem.getId(), collection.getId()))
                .isInstanceOf(AlreadyFollowedCollectionException.class);//.hasMessageContaining("")

        memberService.insertBlockedMemberByMemberId(mem.getId(), mem2.getId(), BlockedMemberStatus.A);
        em.flush();
        assertThatThrownBy(() -> collectionService.insertFollowedCollectionByMemberId(mem.getId(), collection.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")

    }
    
    @Test
    public void 컬렉션_조회수_증가() throws Exception{

        //given
        LocalDateTime now;
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);

        em.flush();
        em.clear();

        //when
        now = LocalDateTime.now();
        System.out.println("서비스함수");
        collectionService.insertViewedCollectionByIp("00", col.getId());

        System.out.println("컬렉션셀렉");
        Collection collection = collectionRepository.getById(col.getId());

        //then
        assertThat(collection.getViewSize()).isEqualTo(collection.getViewedCollectionByIps().size());
        assertThat(collection.getViewSize()).isEqualTo(1);
    }
    
    @Test
    public void 컬렉션_조회수_중복_체크() throws Exception{

        //given
        LocalDateTime now;
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        now = LocalDateTime.now();
        collectionService.insertViewedCollectionByIp("00", col.getId());
        Collection collection1 = collectionRepository.getById(col.getId());

        em.flush();
        em.clear();

        //when
        now = LocalDateTime.now();
        System.out.println("서비스함수");
        collectionService.insertViewedCollectionByIp("00", col.getId());

        System.out.println("컬렉션셀렉");
        Collection collection2 = collectionRepository.getById(col.getId());

        //then
        assertThat(collection2.getViewSize()).isEqualTo(collection2.getViewedCollectionByIps().size());
        assertThat(collection2.getViewSize()).isEqualTo(1);

    }
    //테스트 불가
    public void 컬렉션_조회수_시간후_증가() throws Exception{

        //given
        LocalDateTime now;
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        now = LocalDateTime.now();
        collectionService.insertViewedCollectionByIp("00", col.getId());
        Collection collection1 = collectionRepository.getById(col.getId());

        em.flush();
        em.clear();

        //when
        now = LocalDateTime.now().plusHours(2);
        System.out.println("서비스함수");
        collectionService.insertViewedCollectionByIp("00", col.getId());
        Collection collection2 = collectionRepository.getById(col.getId());

        //then
        assertThat(collection2.getViewSize()).isEqualTo(collection2.getViewedCollectionByIps().size());
        assertThat(collection2.getViewSize()).isEqualTo(2);

    }
    
    @Test
    public void 글_컬렉션_등록() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(po);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);

        List<Collection> collectionList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            collectionList.add(Collection.createDefaultCollection(mem2));
        collectionRepository.saveAll(collectionList);

        List<Long> ids = collectionList.stream().map(Collection::getId).collect(Collectors.toList());

        em.flush();
        em.clear();

        //when

        System.out.println("서비스함수");
        postService.insertCollectedPosts(mem2.getId(), po.getId(), ids);

        System.out.println("포스트함수");
        em.flush();
        postService.insertViewedPostByIp("33", po.getId());
        postService.updateAllPopularity();
        em.flush();
        List<Collection> collections = collectionRepository.findAllByPostIdWithPost(po.getId());
        Post post = postRepository.findById(po.getId()).get();

        //then
        assertThat(collections.size()).isEqualTo(collectionList.size());
        assertThat(post.getPopularity())
                .isEqualTo(collections.size()* POST_COLLECT.getRate() * POST_COLLECT.getLatestScore() +
                        POST_VIEW.getLatestScore() * POST_VIEW.getRate());

    }

    @Test
    public void 글_컬렉션_수정() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(po);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);

        List<Collection> collectionList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            collectionList.add(Collection.createDefaultCollection(mem2));
        collectionRepository.saveAll(collectionList);
        postService.insertCollectedPosts(
                mem2.getId(), po.getId(),
                List.of(collectionList.get(0).getId(), collectionList.get(1).getId(), collectionList.get(2).getId()));

        List<Long> ids = List.of(
                collectionList.get(2).getId(), collectionList.get(3).getId(), collectionList.get(4).getId());

        em.flush();
        em.clear();

        //when

        System.out.println("서비스함수");
        postService.insertCollectedPosts(mem2.getId(), po.getId(), ids);

        System.out.println("포스트함수");
        em.flush();
        postService.insertViewedPostByIp("33", po.getId());
        postService.updateAllPopularity();
        em.flush();
        List<Collection> collections = collectionRepository.findAllByPostIdWithPost(po.getId());
        Post post = postRepository.findById(po.getId()).get();

        //then
        assertThat(collections.size()).isEqualTo(3);
        assertThat(collections.stream().map(Collection::getId).collect(Collectors.toList()))
                .containsOnly(collectionList.get(2).getId(), collectionList.get(3).getId(), collectionList.get(4).getId());
    }

    @Test
    public void 글_컬렉션_등록_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("").setContent("").setUrls(List.of()).setHashTags(List.of()).build();
        postRepository.save(po);
        Member mem2 = Member.buildMember().build();
        memberRepository.save(mem2);

        List<Collection> collectionList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            collectionList.add(Collection.createDefaultCollection(mem2));
        collectionRepository.saveAll(collectionList);

        po.insertCollectedPostByCollections(collectionList);

        List<Long> ids = collectionList.stream().map(Collection::getId).collect(Collectors.toList());

//        em.flush();
//        em.clear();

        //when
        //then
        assertThatThrownBy(() -> postService.insertCollectedPosts(-1L, po.getId(), ids))
                .isInstanceOf(NotMatchCollectionException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> postService.insertCollectedPosts(mem2.getId(), 0L, ids))
                .isInstanceOf(NotExistPostException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> postService.insertCollectedPosts(mem.getId(), po.getId(), ids))
                .isInstanceOf(NotMatchCollectionException.class);//.hasMessageContaining("")

        memberService.insertBlockedMemberByMemberId(mem2.getId(), mem.getId(), BlockedMemberStatus.A);
        em.flush();
        assertThatThrownBy(() -> postService.insertCollectedPosts(mem2.getId(), po.getId(), ids))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }
}
