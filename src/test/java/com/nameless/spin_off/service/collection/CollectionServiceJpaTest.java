package com.nameless.spin_off.service.collection;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CollectionDto.CollectionRequestDto;
import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.comment.CommentInCollection;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.enums.member.AuthorityOfMemberStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.*;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectedPostRepository;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInCollectionRepository;
import com.nameless.spin_off.repository.comment.LikedCommentInCollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.comment.CommentInCollectionService;
import com.nameless.spin_off.service.comment.CommentInPostService;
import com.nameless.spin_off.service.member.MemberService;
import com.nameless.spin_off.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.collection.CollectionScoreEnum.*;
import static com.nameless.spin_off.enums.collection.PublicOfCollectionStatus.A;
import static com.nameless.spin_off.enums.collection.PublicOfCollectionStatus.B;
import static com.nameless.spin_off.enums.post.PostScoreEnum.POST_COLLECT;
import static com.nameless.spin_off.enums.post.PostScoreEnum.POST_VIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class CollectionServiceJpaTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired CommentInCollectionRepository commentInCollectionRepository;
    @Autowired LikedCommentInCollectionRepository likedCommentInCollectionRepository;
    @Autowired CommentInCollectionService commentInCollectionService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired CollectionService collectionService;
    @Autowired MemberService memberService;
    @Autowired CollectedPostRepository collectedPostRepository;

    @Test
    public void 컬렉션_생성_테스트() throws Exception{

        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스 함수");
        Long aLong = collectionService
                .insertCollection(new CollectionRequestDto("aaa", "", A), member.getId());
        System.out.println("컬렉션 조회 함수");
        Collection collection = collectionRepository.getById(aLong);

        //then
        assertThat(collection.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    public void 컬렉션_좋아요_업데이트() throws Exception{

        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem2);
        Collection col = Collection.createCollection(mem2, "aaa", "", PublicOfCollectionStatus.A);
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem2);
        Collection col = Collection.createCollection(mem, "aaa", "", PublicOfCollectionStatus.A);
        collectionRepository.save(col);
        Collection col2 = Collection.createCollection(mem2, "aaa", "", PublicOfCollectionStatus.A);
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("dfdfdfd")
                .setHashTags(List.of()).build();
        postRepository.save(po);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        postService.updateCollectedPosts(mem2.getId(), po.getId(), ids);

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
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("dfdfdfd")
                .setHashTags(List.of()).build();
        postRepository.save(po);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem2);

        List<Collection> collectionList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            collectionList.add(Collection.createDefaultCollection(mem2));
        collectionRepository.saveAll(collectionList);
        postService.updateCollectedPosts(
                mem2.getId(), po.getId(),
                List.of(collectionList.get(0).getId(), collectionList.get(1).getId(), collectionList.get(2).getId()));

        List<Long> ids = List.of(
                collectionList.get(2).getId(), collectionList.get(3).getId(), collectionList.get(4).getId());

        em.flush();
        em.clear();

        //when

        System.out.println("서비스함수");
        postService.updateCollectedPosts(mem2.getId(), po.getId(), ids);

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
    public void 글_컬렉션_수정_썸네일() throws Exception{

        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem2);

        List<Collection> collectionList = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            collectionList.add(collectionRepository.save(Collection.createDefaultCollection(mem2)));

        List<Post> postList = new ArrayList<>();

        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("0")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("1")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("2")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("3")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("4")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("5")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("6")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("7")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("8")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("9")
                .setHashTags(List.of()).build()));

        postService.updateCollectedPosts(
                mem2.getId(), postList.get(0).getId(),
                List.of(collectionList.get(0).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(1).getId(),
                List.of(collectionList.get(1).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(2).getId(),
                List.of(collectionList.get(1).getId(), collectionList.get(2).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(3).getId(),
                List.of(collectionList.get(2).getId(), collectionList.get(3).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(4).getId(),
                List.of(collectionList.get(3).getId(), collectionList.get(4).getId(), collectionList.get(5).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(5).getId(),
                List.of(collectionList.get(4).getId(), collectionList.get(5).getId(), collectionList.get(6).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(6).getId(),
                List.of(collectionList.get(5).getId(), collectionList.get(6).getId(),
                        collectionList.get(7).getId(), collectionList.get(8).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(7).getId(),
                List.of(collectionList.get(6).getId(), collectionList.get(7).getId(),
                        collectionList.get(8).getId(), collectionList.get(9).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(8).getId(),
                List.of(collectionList.get(0).getId(), collectionList.get(1).getId(),
                        collectionList.get(2).getId(), collectionList.get(3).getId(),
                        collectionList.get(4).getId(), collectionList.get(5).getId()));
        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(9).getId(),
                List.of(collectionList.get(0).getId(), collectionList.get(1).getId(),
                        collectionList.get(2).getId(), collectionList.get(3).getId(),
                        collectionList.get(6).getId(), collectionList.get(7).getId(),
                        collectionList.get(8).getId(), collectionList.get(9).getId()));

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(0).getId(),
                List.of());

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(1).getId(),
                List.of(collectionList.get(0).getId(), collectionList.get(1).getId()));

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(2).getId(),
                List.of());

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(3).getId(),
                List.of(collectionList.get(2).getId(), collectionList.get(3).getId(), collectionList.get(4).getId()));

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(4).getId(),
                List.of(collectionList.get(3).getId(), collectionList.get(5).getId()));

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(5).getId(),
                List.of(collectionList.get(7).getId(), collectionList.get(8).getId(), collectionList.get(9).getId()));

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(6).getId(),
                List.of(collectionList.get(9).getId(), collectionList.get(2).getId(), collectionList.get(8).getId()));

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(7).getId(),
                List.of(collectionList.get(0).getId(), collectionList.get(1).getId(),
                        collectionList.get(2).getId(), collectionList.get(3).getId(),
                        collectionList.get(6).getId(), collectionList.get(7).getId(),
                        collectionList.get(8).getId(), collectionList.get(9).getId()));

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(8).getId(),
                List.of(collectionList.get(0).getId(), collectionList.get(1).getId(),
                        collectionList.get(2).getId()));

        em.flush();
        postService.updateCollectedPosts(
                mem2.getId(), postList.get(9).getId(),
                List.of(collectionList.get(6).getId(), collectionList.get(7).getId(),
                        collectionList.get(8).getId(), collectionList.get(9).getId()));

        em.flush();

        System.out.println("포스트함수");
        List<Collection> collections = collectionRepository.findAll();
        System.out.println(collections.size());
        //then

        assertThat(collections.get(0).getFirstThumbnail()).isEqualTo("7");
        assertThat(collections.get(0).getSecondThumbnail()).isEqualTo("1");
        assertThat(collections.get(0).getThirdThumbnail()).isEqualTo("8");
        assertThat(collections.get(0).getFourthThumbnail()).isEqualTo(null);

        assertThat(collections.get(1).getFirstThumbnail()).isEqualTo("7");
        assertThat(collections.get(1).getSecondThumbnail()).isEqualTo("8");
        assertThat(collections.get(1).getThirdThumbnail()).isEqualTo("1");
        assertThat(collections.get(1).getFourthThumbnail()).isEqualTo(null);

        assertThat(collections.get(2).getFirstThumbnail()).isEqualTo("7");
        assertThat(collections.get(2).getSecondThumbnail()).isEqualTo("6");
        assertThat(collections.get(2).getThirdThumbnail()).isEqualTo("8");
        assertThat(collections.get(2).getFourthThumbnail()).isEqualTo("3");

        assertThat(collections.get(3).getFirstThumbnail()).isEqualTo("7");
        assertThat(collections.get(3).getSecondThumbnail()).isEqualTo("4");
        assertThat(collections.get(3).getThirdThumbnail()).isEqualTo("3");
        assertThat(collections.get(3).getFourthThumbnail()).isEqualTo(null);

        assertThat(collections.get(4).getFirstThumbnail()).isEqualTo("3");
        assertThat(collections.get(4).getSecondThumbnail()).isEqualTo(null);
        assertThat(collections.get(4).getThirdThumbnail()).isEqualTo(null);
        assertThat(collections.get(4).getFourthThumbnail()).isEqualTo(null);

        assertThat(collections.get(5).getFirstThumbnail()).isEqualTo("4");
        assertThat(collections.get(5).getSecondThumbnail()).isEqualTo(null);
        assertThat(collections.get(5).getThirdThumbnail()).isEqualTo(null);
        assertThat(collections.get(5).getFourthThumbnail()).isEqualTo(null);

        assertThat(collections.get(6).getFirstThumbnail()).isEqualTo("9");
        assertThat(collections.get(6).getSecondThumbnail()).isEqualTo("7");
        assertThat(collections.get(6).getThirdThumbnail()).isEqualTo(null);
        assertThat(collections.get(6).getFourthThumbnail()).isEqualTo(null);

        assertThat(collections.get(7).getFirstThumbnail()).isEqualTo("5");
        assertThat(collections.get(7).getSecondThumbnail()).isEqualTo("9");
        assertThat(collections.get(7).getThirdThumbnail()).isEqualTo("7");
        assertThat(collections.get(7).getFourthThumbnail()).isEqualTo(null);

        assertThat(collections.get(8).getFirstThumbnail()).isEqualTo("5");
        assertThat(collections.get(8).getSecondThumbnail()).isEqualTo("9");
        assertThat(collections.get(8).getThirdThumbnail()).isEqualTo("7");
        assertThat(collections.get(8).getFourthThumbnail()).isEqualTo("6");

        assertThat(collections.get(9).getFirstThumbnail()).isEqualTo("6");
        assertThat(collections.get(9).getSecondThumbnail()).isEqualTo("5");
        assertThat(collections.get(9).getThirdThumbnail()).isEqualTo("9");
        assertThat(collections.get(9).getFourthThumbnail()).isEqualTo("7");

    }

    @Test
    public void 글_컬렉션_등록_예외처리() throws Exception{

        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Post po = Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of()).setHashTags(List.of()).build();
        postRepository.save(po);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
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
        assertThatThrownBy(() -> postService.updateCollectedPosts(-1L, po.getId(), ids))
                .isInstanceOf(NotMatchCollectionException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> postService.updateCollectedPosts(mem2.getId(), 0L, ids))
                .isInstanceOf(NotExistPostException.class);//.hasMessageContaining("")
        assertThatThrownBy(() -> postService.updateCollectedPosts(mem.getId(), po.getId(), ids))
                .isInstanceOf(NotMatchCollectionException.class);//.hasMessageContaining("")

        memberService.insertBlockedMemberByMemberId(mem2.getId(), mem.getId(), BlockedMemberStatus.A);
        em.flush();
        assertThatThrownBy(() -> postService.updateCollectedPosts(mem2.getId(), po.getId(), ids))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }

    @Test
    public void 컬렉션_글_삽입() throws Exception{
        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem2);
        Collection memberCollection = collectionRepository.save(Collection.createDefaultCollection(mem));

        List<Collection> collectionList = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            collectionList.add(collectionRepository.save(Collection.createDefaultCollection(mem2)));

        List<Post> postList = new ArrayList<>();

        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("0")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("1")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("2")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("3")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("4")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("5")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("6")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("7")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("8")
                .setHashTags(List.of()).build()));
        postList.add(postRepository.save(Post.buildPost().setMember(mem).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl("9")
                .setHashTags(List.of()).build()));
        em.flush();
        em.clear();
        //when
        System.out.println("서비스함수 시작");
        int i = collectionService.insertCollectedPost(mem.getId(), memberCollection.getId(), postList.stream()
                .map(Post::getId).collect(Collectors.toList()));
        System.out.println("서비스함수 끝");

        assertThat(postList.size()).isEqualTo(i);

        assertThatThrownBy(() -> collectionService.insertCollectedPost(mem.getId(), memberCollection.getId(), postList.stream()
                .map(Post::getId).collect(Collectors.toList()))).isInstanceOf(AlreadyCollectedPostException.class);
        assertThatThrownBy(() -> collectionService.insertCollectedPost(mem.getId(), collectionList.get(0).getId(), postList.stream()
                .map(Post::getId).collect(Collectors.toList()))).isInstanceOf(NotExistCollectionException.class);
    }

    @Test
    public void 컬렉션_수정() throws Exception{
        //given
        Member mem = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem);
        Member mem2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(mem2);
        Collection memberCollection1 = collectionRepository.save(Collection.createDefaultCollection(mem));

        Collection memberCollection2 = collectionRepository.save(
                Collection.createCollection(mem, "abc", "abcdd", A));

        em.flush();
        em.clear();
        //when
        System.out.println("서비스함수 시작");
        Long aLong = collectionService.updateCollection(new CollectionRequestDto("abc", "abcdd", A),
                memberCollection2.getId(), mem.getId());
        System.out.println("서비스함수 끝");

        em.flush();
        LocalDateTime lastModifiedDate = collectionRepository.findById(memberCollection2.getId()).get().getLastModifiedDate();

        assertThat(memberCollection2.getLastModifiedDate()).isEqualTo(lastModifiedDate);
        assertThat(aLong).isEqualTo(0L);

        Long aLong2 = collectionService.updateCollection(new CollectionRequestDto("abccd", "abcddaa", B),
                memberCollection2.getId(), mem.getId());
        Collection collection = collectionRepository.findById(memberCollection2.getId()).get();

        em.flush();
        //then

        assertThat(collection.getTitle()).isEqualTo("abccd");
        assertThat(collection.getContent()).isEqualTo("abcddaa");
        assertThat(collection.getPublicOfCollectionStatus()).isEqualTo(B);
        assertThat(aLong2).isEqualTo(3L);

        assertThatThrownBy(() -> collectionService.updateCollection(new CollectionRequestDto("abccd", "abcddaa", B),
                memberCollection2.getId(), mem2.getId())).isInstanceOf(DontHaveAuthorityException.class);
        assertThatThrownBy(() -> collectionService.updateCollection(new CollectionRequestDto("abccd", "abcddaa", B),
                memberCollection1.getId(), mem.getId())).isInstanceOf(DontHaveAuthorityException.class);
    }

    @Test
    public void 컬렉션_삭제_테스트() throws Exception{
        //given
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);
        Member member2 = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        member2.addRole(AuthorityOfMemberStatus.A);
        memberRepository.save(member2);
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            memberList.add(memberRepository.save(Member.buildMember()
                    .setEmail("jhkimkkk0923@naver.com")
                    .setAccountId("memberAccId2")
                    .setName("memberName")
                    .setBirth(LocalDate.now())
                    .setPhoneNumber("01011111111")
                    .setAccountPw("memberAccountPw")
                    .setNickname("memcname").build()));
        }
        Collection collection = collectionRepository.save(Collection.createCollection(member, "aaa", "", A));


        List<Post> postList = new ArrayList<>();

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.B)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.C)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        postList.add(postRepository.save(Post.buildPost().setMember(member2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setThumbnailUrl(member.getId() + "1")
                .setHashTags(List.of()).build()));

        em.flush();
        collectionService.insertCollectedPost(
                member.getId(), collection.getId(), postList.stream().map(Post::getId).collect(Collectors.toList()));

        em.flush();
        List<Long> commentIds = new ArrayList<>();

        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                null, "ddd"), member.getId(), collection.getId()));
        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                null, "ddd"), memberList.get(0).getId(), collection.getId()));
        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                null, "ddd"), memberList.get(1).getId(), collection.getId()));
        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                null, "ddd"), memberList.get(2).getId(), collection.getId()));
        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                null, "ddd"), memberList.get(3).getId(), collection.getId()));

        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                commentIds.get(0), "ddd"), member.getId(), collection.getId()));
        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                commentIds.get(0), "ddd"), memberList.get(0).getId(), collection.getId()));
        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                commentIds.get(0), "ddd"), memberList.get(1).getId(), collection.getId()));

        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                commentIds.get(2), "ddd"), member.getId(), collection.getId()));
        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                commentIds.get(2), "ddd"), memberList.get(0).getId(), collection.getId()));
        commentIds.add(commentInCollectionService.insertCommentInCollectionByCommentVO(new CommentDto.CommentInCollectionRequestDto(
                commentIds.get(2), "ddd"), memberList.get(1).getId(), collection.getId()));

        commentInCollectionService.insertLikedCommentByMemberId(memberList.get(1).getId(), commentIds.get(1));
        commentInCollectionService.insertLikedCommentByMemberId(member.getId(), commentIds.get(0));
        commentInCollectionService.insertLikedCommentByMemberId(memberList.get(2).getId(), commentIds.get(0));
        commentInCollectionService.insertLikedCommentByMemberId(member.getId(), commentIds.get(7));

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        collectionService.deleteCollection(MemberDetails.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .accountPw(member.getAccountPw())
                .authorities(member.getRoles()
                        .stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                        .collect(Collectors.toSet()))
                .build(), collection.getId());
        em.flush();
        System.out.println("서비스함수끝");

        //then
        assertThatThrownBy(() -> collectionRepository.findById(collection.getId())
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST)))
                .isInstanceOf(NotExistPostException.class);
        assertThat(commentInCollectionRepository.findAll().size()).isEqualTo(0);
        assertThat(likedCommentInCollectionRepository.findAll().size()).isEqualTo(0);
        assertThat(collectedPostRepository.findAll().size()).isEqualTo(0);

        Collection collection2 = collectionRepository.save(Collection.createCollection(member, "aaa", "", A));

        collectionService.deleteCollection(MemberDetails.builder()
                .id(member2.getId())
                .accountId(member2.getAccountId())
                .accountPw(member2.getAccountPw())
                .authorities(member2.getRoles()
                        .stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                        .collect(Collectors.toSet()))
                .build(), collection2.getId());
    }
}
