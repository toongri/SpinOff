package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.collection.CollectedPost;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.hashtag.PostedHashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.hashtag.IncorrectHashtagContentException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.AlreadyLikedPostException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nameless.spin_off.enums.post.PostScoreEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class PostServiceJpaTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;
    @Autowired MemberService memberService;

    @Test
    public void 해시태그_검열_테스트() throws Exception{
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
        CreatePostVO createPostVO = new CreatePostVO(
                "알라리숑", "얄라리얄라", null, PublicOfPostStatus.A,
                List.of("형윤이", "형윤이?"), List.of());

        //when
        System.out.println("서비스");
        assertThatThrownBy(() -> postService.insertPostByPostVO(createPostVO, member.getId(), Collections.emptyList()))
                .isInstanceOf(IncorrectHashtagContentException.class);

        createPostVO.setHashtagContents(List.of("_"));
        assertThatThrownBy(() -> postService.insertPostByPostVO(createPostVO, member.getId(), Collections.emptyList()))
                .isInstanceOf(IncorrectHashtagContentException.class);

        createPostVO.setHashtagContents(List.of("gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"));
        assertThatThrownBy(() -> postService.insertPostByPostVO(createPostVO, member.getId(), Collections.emptyList()))
                .isInstanceOf(IncorrectHashtagContentException.class);

        createPostVO.setHashtagContents(List.of("hashtag_what"));
        Long aLong = postService.insertPostByPostVO(createPostVO, member.getId(), Collections.emptyList());
        Post byId = postRepository.getById(aLong);


        createPostVO.setHashtagContents(List.of("hashtag_dft", "hashtag_dft"));
        Long aLong1 = postService.insertPostByPostVO(createPostVO, member.getId(), Collections.emptyList());
        Post byId1 = postRepository.getById(aLong1);
        //then

        assertThat(byId.getPostedHashtags().stream()
                .map(PostedHashtag::getHashtag)
                .map(Hashtag::getContent).
                collect(Collectors.toList())).containsExactly("hashtag_what");

        assertThat(byId1.getPostedHashtags().stream()
                .map(PostedHashtag::getHashtag)
                .map(Hashtag::getContent).
                collect(Collectors.toList())).containsExactly("hashtag_dft");

    }

    @Test
    public void 포스트_생성_테스트() throws Exception{
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

        collectionRepository.save(Collection.createDefaultCollection(member));
        collectionRepository.save(Collection.createCollection(member, "aaa", "", PublicOfCollectionStatus.A));
        collectionRepository.save(Collection.createCollection(member, "aaa", "", PublicOfCollectionStatus.A));

        List<Collection> collectionsByMember = collectionRepository.findAllByMember(member);

        List<Long> collectionIds = collectionsByMember.stream().map(Collection::getId).collect(Collectors.toList());

        List<Post> preparePosts = postRepository.findAll();
        List<Hashtag> prepareHashtags = hashtagRepository.findAll();
        List<Collection> prepareCollections = collectionRepository.findAllById(collectionIds);

        List<CollectedPost> prepareCollectedPosts1 = prepareCollections.get(0).getCollectedPosts();
        List<CollectedPost> prepareCollectedPosts2 = prepareCollections.get(1).getCollectedPosts();
        List<CollectedPost> prepareCollectedPosts3 = prepareCollections.get(2).getCollectedPosts();

        em.flush();
        em.clear();

        //when

        CreatePostVO createPostVO = new CreatePostVO("알라리숑", "얄라리얄라", null,
                PublicOfPostStatus.A, List.of("형윤이", "형윤이_"), collectionIds);
        System.out.println("서비스");
        Long aLong = postService.insertPostByPostVO(createPostVO, member.getId(), Collections.emptyList());
        em.flush();
        em.clear();

        System.out.println("포스트");
        postService.insertViewedPostByIp("22", aLong);
        em.flush();
        postService.updateAllPopularity();
        em.flush();

        Post post1 = postRepository.findById(aLong).orElseThrow(Exception::new);
        Set<PostedHashtag> postedHashtags = post1.getPostedHashtags();
        int postPostedHashtagSize = postedHashtags.size();

        List<Post> postPosts = postRepository.findAll();
        List<Hashtag> postHashtags = hashtagRepository.findAll();
        List<Collection> postCollections = collectionRepository.findAllById(collectionIds);

        List<CollectedPost> postCollectedPosts1 = postCollections.get(0).getCollectedPosts();
        List<CollectedPost> postCollectedPosts2 = postCollections.get(1).getCollectedPosts();
        List<CollectedPost> postCollectedPosts3 = postCollections.get(2).getCollectedPosts();

        //then

        assertThat(preparePosts.size()).isEqualTo(postPosts.size() - 1);
        assertThat(prepareHashtags.size()).isEqualTo(postHashtags.size() - 2);
        assertThat(prepareCollectedPosts1.size()).isEqualTo(postCollectedPosts1.size() - 1);
        assertThat(prepareCollectedPosts2.size()).isEqualTo(postCollectedPosts2.size() - 1);
        assertThat(prepareCollectedPosts3.size()).isEqualTo(postCollectedPosts3.size() - 1);
        assertThat(post1.getPopularity()).isEqualTo(POST_COLLECT.getLatestScore() * POST_COLLECT.getRate() * 3 +
                POST_VIEW.getRate() * POST_VIEW.getLatestScore());
        assertThat(postPostedHashtagSize).isEqualTo(2);
    }

    @Test
    public void 글_생성_파라미터_예외처리() throws Exception{

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

        CreatePostVO createPostVO1 = new CreatePostVO(
                "알라리숑", "얄라리얄라", null, PublicOfPostStatus.A,
                List.of(), List.of());

        CreatePostVO createPostVO2 = new CreatePostVO(
                "알라리숑", "얄라리얄라", 0L, PublicOfPostStatus.A, List.of(), List.of());

        CreatePostVO createPostVO3 = new CreatePostVO(
                "알라리숑", "얄라리얄라", null, PublicOfPostStatus.A, List.of(), List.of(-1L));
        //when

        //then

        assertThatThrownBy(() -> postService.insertPostByPostVO(createPostVO2, member.getId(), Collections.emptyList()))
                .isInstanceOf(NotExistMovieException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> postService.insertPostByPostVO(createPostVO3, member.getId(), Collections.emptyList()))
                .isInstanceOf(NotMatchCollectionException.class);//.hasMessageContaining("")
    }

    @Test
    public void 글_좋아요_체크() throws Exception{

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
                .setHashTags(List.of()).build();
        postRepository.save(po);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        postService.insertLikedPostByMemberId(mem.getId(), po.getId());

        System.out.println("포스트");
        postService.insertViewedPostByIp("aa", po.getId());
        em.flush();
        postService.updateAllPopularity();
        em.flush();
        Post post = postRepository.getById(po.getId());
        System.out.println("멤버");
        Member member = memberRepository.getById(mem.getId());

        //then
        assertThat(post.getPopularity())
                .isEqualTo(POST_LIKE.getLatestScore() * POST_LIKE.getRate() +
                        POST_VIEW.getRate() * POST_VIEW.getLatestScore());
        assertThat(post.getLikedPosts().size()).isEqualTo(1);
        assertThat(post.getLikedPosts().get(0).getMember().getId()).isEqualTo(member.getId());

    }

    @Test
    public void 글_좋아요_파라미터_예외처리() throws Exception{

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
        Post po2 = Post.buildPost().setMember(mem2).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(po2);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        postService.insertLikedPostByMemberId(mem.getId(), po.getId());

        System.out.println("포스트");
        Post post = postRepository.findById(po.getId()).get();
        System.out.println("멤버");
        Member member = memberRepository.findById(mem.getId()).get();

        //then
        assertThatThrownBy(() -> postService.insertLikedPostByMemberId(mem.getId(), 0L))
                .isInstanceOf(NotExistPostException.class);//.hasMessageContaining("")

        memberService.insertBlockedMemberByMemberId(mem.getId(), mem2.getId(), BlockedMemberStatus.A);
        em.flush();
        System.out.println("멤버차단");
        assertThatThrownBy(() -> postService.insertLikedPostByMemberId(mem.getId(), po2.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }

    @Test
    public void 글_좋아요_중복삽입_예외처리() throws Exception{

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
                .setHashTags(List.of()).build();
        postRepository.save(po);

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        postService.insertLikedPostByMemberId(mem.getId(), po.getId());

        //then
        System.out.println("서비스함수2");
        assertThatThrownBy(() -> postService.insertLikedPostByMemberId(mem.getId(), po.getId()))
                .isInstanceOf(AlreadyLikedPostException.class);//.hasMessageContaining("")

    }

    @Test
    public void 글_조회수_증가() throws Exception{
        //given
        LocalDateTime now;
        Member member = Member.buildMember()
                .setEmail("jhkimkkk0923@naver.com")
                .setAccountId("memberAccId2")
                .setName("memberName")
                .setBirth(LocalDate.now())
                .setPhoneNumber("01011111111")
                .setAccountPw("memberAccountPw")
                .setNickname("memcname").build();
        memberRepository.save(member);
        Post post = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(post);

        em.flush();
        em.clear();

        //when

        now = LocalDateTime.now();
        System.out.println("서비스함수");
        postService.insertViewedPostByIp("00", post.getId());
        em.flush();
        postService.updateAllPopularity();
        em.flush();
        System.out.println("포스트");
        Post post1 = postRepository.getById(post.getId());

        //then
        assertThat(post1.getPopularity())
                .isEqualTo(post1.getViewedPostByIps().size() * POST_VIEW.getRate() * POST_VIEW.getLatestScore());
        assertThat(post1.getViewSize()).isEqualTo(1);

    }
    
    @Test
    public void 글_조회수_중복_체크() throws Exception{
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
        Post post = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(post);
        postService.insertViewedPostByIp("00", post.getId());

        em.flush();
        postService.updateAllPopularity();

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        postService.insertViewedPostByIp("00", post.getId());
        System.out.println("포스트");
        em.flush();
        postService.updateAllPopularity();
        em.flush();
        Post post2 = postRepository.getById(post.getId());
        
        //then
        assertThat(post2.getPopularity())
                .isEqualTo(post2.getViewedPostByIps().size() * POST_VIEW.getRate() * POST_VIEW.getLatestScore());
        assertThat(post2.getViewSize()).isEqualTo(1);

    }

    //테스트불가능
    public void 글_조회수_시간후_증가() throws Exception{

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
        Post post = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build();
        postRepository.save(post);
        postService.insertViewedPostByIp("00", post.getId());

        em.flush();
        em.clear();

        //when
        System.out.println("서비스함수");
        postService.insertViewedPostByIp("00", post.getId());
        System.out.println("포스트");
        Post post2 = postRepository.getById(post.getId());

        //then
        assertThat(post2.getPopularity()).isEqualTo(post2.getViewedPostByIps().size());
        assertThat(post2.getPopularity()).isEqualTo(2);
    }

    @Test
    public void 글_삭제_테스트() throws Exception{
        //given

        //when

        //then

    }
}