package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.StaticVariable;
import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.FollowedCollection;
import com.nameless.spin_off.entity.collections.LikedCollection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.member.NotExistMemberException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
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

import static com.nameless.spin_off.StaticVariable.COLLECTION_LIKE_COUNT_SCORES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CollectionServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired LikedPostRepository likedPostRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired CommentInCollectionRepository commentInCollectionRepository;
    @Autowired CommentInCollectionService commentInCollectionService;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;

    @Test
    public void 컬렉션_생성_테스트() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);

        em.flush();
        em.clear();

        //when
        CollectionDto.CreateCollectionVO createCollectionVO = new CollectionDto
                .CreateCollectionVO(member.getId(), "", "", PublicOfCollectionStatus.PUBLIC);

        System.out.println("서비스 함수");
        Long aLong = collectionService.insertCollectionByCollectionVO(createCollectionVO);
        System.out.println("컬렉션 조회 함수");
        Collection collection = collectionRepository.getById(aLong);

        //then
        assertThat(collection.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    public void 컬렉션_생성_파라미터_예외처리() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);

        CollectionDto.CreateCollectionVO createCollectionVO1 = new CollectionDto
                .CreateCollectionVO(0L, "", "", PublicOfCollectionStatus.PUBLIC);
        //when

        //then
        assertThatThrownBy(() -> collectionService.insertCollectionByCollectionVO(createCollectionVO1))
                .isInstanceOf(NotExistMemberException.class);//.hasMessageContaining("")
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
        Collection collection = collectionRepository.getById(collectionService.insertLikedCollectionByMemberId(mem.getId(), col.getId()));

        System.out.println("멤버");
        Member member = memberRepository.findById(mem.getId()).get();

        //then

        List<LikedCollection> likedCollections = collection.getLikedCollections();
        Double likeCount = collection.getLikeScore();
        assertThat(likeCount).isEqualTo(COLLECTION_LIKE_COUNT_SCORES.get(0) * 1.0);
        assertThat(likedCollections.size()).isEqualTo(1);
        assertThat(likedCollections.get(0).getMember()).isEqualTo(member);

    }

    @Test
    public void 컬렉션_좋아요_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Collection collection = collectionRepository.getById(collectionService.insertLikedCollectionByMemberId(mem.getId(), col.getId()));

        System.out.println("멤버");
        Member member = memberRepository.findById(mem.getId()).get();

        //then
        assertThatThrownBy(() -> collectionService.insertLikedCollectionByMemberId(0L, collection.getId()))
                .isInstanceOf(NotExistMemberException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> collectionService.insertLikedCollectionByMemberId(mem.getId(), 0L))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> collectionService.insertLikedCollectionByMemberId(mem.getId(), collection.getId()))
                .isInstanceOf(AlreadyLikedCollectionException.class);//.hasMessageContaining("")
    }

    @Test
    public void 컬렉션_팔로우_업데이트() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Collection collection = collectionRepository.getById(collectionService.insertFollowedCollectionByMemberId(mem.getId(), col.getId()));

        System.out.println("멤버");
        Member member = memberRepository.findById(mem.getId()).get();

        //then

        List<FollowedCollection> followedCollections = collection.getFollowedCollections();
        Double followCount = collection.getFollowScore();
        assertThat(followCount).isEqualTo(StaticVariable.COLLECTION_FOLLOW_COUNT_SCORES.get(0) * 1.0);
        assertThat(followedCollections.size()).isEqualTo(1);
        assertThat(followedCollections.get(0).getMember()).isEqualTo(member);

    }

    @Test
    public void 컬렉션_팔로우_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        Collection collection = collectionRepository.getById(collectionService.insertFollowedCollectionByMemberId(mem.getId(), col.getId()));

        System.out.println("멤버");
        Member member = memberRepository.findById(mem.getId()).get();

        //then
        assertThatThrownBy(() -> collectionService.insertFollowedCollectionByMemberId(0L, collection.getId()))
                .isInstanceOf(NotExistMemberException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> collectionService.insertFollowedCollectionByMemberId(mem.getId(), 0L))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> collectionService.insertFollowedCollectionByMemberId(mem.getId(), collection.getId()))
                .isInstanceOf(AlreadyFollowedCollectionException.class);//.hasMessageContaining("")

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
        Collection collection = collectionRepository.getById(collectionService.insertViewedCollectionByIp("00", col.getId()));

        //then
        assertThat(collection.getViewCount()).isEqualTo(collection.getViewedCollectionByIps().size());
        assertThat(collection.getViewCount()).isEqualTo(1);

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
        Collection collection1 = collectionRepository.getById(collectionService.insertViewedCollectionByIp("00", col.getId()));

        em.flush();
        em.clear();

        //when
        now = LocalDateTime.now();
        System.out.println("서비스함수");
        Collection collection2 = collectionRepository.getById(collectionService.insertViewedCollectionByIp("00", col.getId()));

        //then
        assertThat(collection2.getViewCount()).isEqualTo(collection2.getViewedCollectionByIps().size());
        assertThat(collection2.getViewCount()).isEqualTo(1);

    }
    
    @Test
    public void 컬렉션_조회수_시간후_증가() throws Exception{

        //given
        LocalDateTime now;
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        now = LocalDateTime.now();
        Collection collection1 = collectionRepository.getById(collectionService.insertViewedCollectionByIp("00", col.getId()));

        em.flush();
        em.clear();

        //when
        now = LocalDateTime.now().plusHours(2);
        System.out.println("서비스함수");
        Collection collection2 = collectionRepository.getById(collectionService.insertViewedCollectionByIp("00", col.getId()));

        //then
        assertThat(collection2.getViewCount()).isEqualTo(collection2.getViewedCollectionByIps().size());
        assertThat(collection2.getViewCount()).isEqualTo(2);

    }

    @Test
    public void 글_조회수_파리미터_AND_복수데이터_예외처리() throws Exception{

        //given
        LocalDateTime now;
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Collection col = Collection.createDefaultCollection(mem);
        collectionRepository.save(col);
        now = LocalDateTime.now();
        col.insertViewedCollectionByIp("00");

        em.flush();
        em.clear();

        //when

        //then
        assertThatThrownBy(() -> collectionService.insertViewedCollectionByIp("00", 0L))
                .isInstanceOf(NotExistCollectionException.class);//.hasMessageContaining("")

    }
    
    @Test
    public void 글_컬렉션_등록() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
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
        Long postId = postService.insertCollectedPosts(mem2.getId(), po.getId(), ids);
        List<Collection> collections = collectionRepository.findAllByPostIdWithPost(postId);
        System.out.println("포스트함수");
        Post post = postRepository.findById(po.getId()).get();

        //then
        assertThat(collections.size()).isEqualTo(collectionList.size());
        assertThat(post.getCollectionScore()).isEqualTo(collections.size());

    }

    @Test
    public void 글_컬렉션_등록_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember().build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
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
        assertThatThrownBy(() -> postService.insertCollectedPosts(mem2.getId(), po.getId(), List.of(0L)))
                .isInstanceOf(NotMatchCollectionException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> postService.insertCollectedPosts(mem2.getId(), po.getId(), ids))
                .isInstanceOf(AlreadyCollectedPostException.class);//.hasMessageContaining("")

    }
}
