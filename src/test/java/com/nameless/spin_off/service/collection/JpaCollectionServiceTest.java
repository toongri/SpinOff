package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.dto.CollectionDto;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.LikedCollection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.LikedPost;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.entity.post.PublicOfPostStatus;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.post.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.engine.IterationStatusVar;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaCollectionServiceTest {

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
        Long aLong = collectionService.saveCollectionByCollectionVO(createCollectionVO);
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
        Collection collection = collectionService.updateLikedCollectionByMemberId(mem.getId(), col.getId());

        System.out.println("멤버");
        Member member = memberRepository.findById(mem.getId()).get();

        //then

        List<LikedCollection> likedCollections = collection.getLikedCollections();
        Long likeCount = collection.getLikeCount();
        assertThat(likeCount).isEqualTo(1);
        assertThat(likedCollections.size()).isEqualTo(1);
        assertThat(likedCollections.get(0).getMember()).isEqualTo(member);

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
        Collection collection = collectionService.updateLikedCollectionByMemberId(mem.getId(), col.getId());

        System.out.println("멤버");
        Member member = memberRepository.findById(mem.getId()).get();

        //then

        List<LikedCollection> likedCollections = collection.getLikedCollections();
        Long likeCount = collection.getLikeCount();
        assertThat(likeCount).isEqualTo(1);
        assertThat(likedCollections.size()).isEqualTo(1);
        assertThat(likedCollections.get(0).getMember()).isEqualTo(member);

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
        Collection collection = collectionService.updateViewedCollectionByIp("00", col.getId(), now, 60L);

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
        Collection collection1 = collectionService.updateViewedCollectionByIp("00", col.getId(), now, 60L);

        em.flush();
        em.clear();

        //when
        now = LocalDateTime.now();
        System.out.println("서비스함수");
        Collection collection2 = collectionService.updateViewedCollectionByIp("00", col.getId(), now, 60L);

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
        Collection collection1 = collectionService.updateViewedCollectionByIp("00", col.getId(), now, 60L);

        em.flush();
        em.clear();

        //when
        now = LocalDateTime.now().plusHours(2);
        System.out.println("서비스함수");
        Collection collection2 = collectionService.updateViewedCollectionByIp("00", col.getId(), now, 60L);

        //then
        assertThat(collection2.getViewCount()).isEqualTo(collection2.getViewedCollectionByIps().size());
        assertThat(collection2.getViewCount()).isEqualTo(2);

    }
}
