package com.nameless.spin_off.service.post;

import com.nameless.spin_off.config.member.MemberDetails;
import com.nameless.spin_off.dto.CommentDto;
import com.nameless.spin_off.dto.PostDto.CreatePostVO;
import com.nameless.spin_off.entity.collection.CollectedPost;
import com.nameless.spin_off.entity.collection.Collection;
import com.nameless.spin_off.entity.hashtag.Hashtag;
import com.nameless.spin_off.entity.hashtag.PostedHashtag;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.Post;
import com.nameless.spin_off.enums.ErrorEnum;
import com.nameless.spin_off.enums.collection.PublicOfCollectionStatus;
import com.nameless.spin_off.enums.member.AuthorityOfMemberStatus;
import com.nameless.spin_off.enums.member.BlockedMemberStatus;
import com.nameless.spin_off.enums.post.PublicOfPostStatus;
import com.nameless.spin_off.exception.collection.NotMatchCollectionException;
import com.nameless.spin_off.exception.hashtag.IncorrectHashtagContentException;
import com.nameless.spin_off.exception.movie.NotExistMovieException;
import com.nameless.spin_off.exception.post.AlreadyLikedPostException;
import com.nameless.spin_off.exception.post.NotExistPostException;
import com.nameless.spin_off.exception.security.DontHaveAuthorityException;
import com.nameless.spin_off.repository.collection.CollectionRepository;
import com.nameless.spin_off.repository.comment.CommentInPostRepository;
import com.nameless.spin_off.repository.comment.LikedCommentInPostRepository;
import com.nameless.spin_off.repository.hashtag.HashtagRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.service.comment.CommentInPostService;
import com.nameless.spin_off.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired CommentInPostService commentInPostService;
    @Autowired CommentInPostRepository commentInPostRepository;
    @Autowired LikedCommentInPostRepository likedCommentInPostRepository;

    @Test
    public void ????????????_??????_?????????() throws Exception{
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
                "????????????", "???????????????", null, PublicOfPostStatus.A,
                List.of("?????????", "??????????"), List.of());

        //when
        System.out.println("?????????");
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
    public void ?????????_??????_?????????() throws Exception{
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

        CreatePostVO createPostVO = new CreatePostVO("????????????", "???????????????", null,
                PublicOfPostStatus.A, List.of("?????????", "?????????_"), collectionIds);
        System.out.println("?????????");
        Long aLong = postService.insertPostByPostVO(createPostVO, member.getId(), Collections.emptyList());
        em.flush();
        em.clear();

        System.out.println("?????????");
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
    public void ???_??????_????????????_????????????() throws Exception{

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
                "????????????", "???????????????", null, PublicOfPostStatus.A,
                List.of(), List.of());

        CreatePostVO createPostVO2 = new CreatePostVO(
                "????????????", "???????????????", 0L, PublicOfPostStatus.A, List.of(), List.of());

        CreatePostVO createPostVO3 = new CreatePostVO(
                "????????????", "???????????????", null, PublicOfPostStatus.A, List.of(), List.of(-1L));
        //when

        //then

        assertThatThrownBy(() -> postService.insertPostByPostVO(createPostVO2, member.getId(), Collections.emptyList()))
                .isInstanceOf(NotExistMovieException.class);//.hasMessageContaining("")

        assertThatThrownBy(() -> postService.insertPostByPostVO(createPostVO3, member.getId(), Collections.emptyList()))
                .isInstanceOf(NotMatchCollectionException.class);//.hasMessageContaining("")
    }

    @Test
    public void ???_?????????_??????() throws Exception{

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
        System.out.println("???????????????");
        postService.insertLikedPostByMemberId(mem.getId(), po.getId());

        System.out.println("?????????");
        postService.insertViewedPostByIp("aa", po.getId());
        em.flush();
        postService.updateAllPopularity();
        em.flush();
        Post post = postRepository.getById(po.getId());
        System.out.println("??????");
        Member member = memberRepository.getById(mem.getId());

        //then
        assertThat(post.getPopularity())
                .isEqualTo(POST_LIKE.getLatestScore() * POST_LIKE.getRate() +
                        POST_VIEW.getRate() * POST_VIEW.getLatestScore());
        assertThat(post.getLikedPosts().size()).isEqualTo(1);
        assertThat(post.getLikedPosts().get(0).getMember().getId()).isEqualTo(member.getId());

    }

    @Test
    public void ???_?????????_????????????_????????????() throws Exception{

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
        System.out.println("???????????????");
        postService.insertLikedPostByMemberId(mem.getId(), po.getId());

        System.out.println("?????????");
        Post post = postRepository.findById(po.getId()).get();
        System.out.println("??????");
        Member member = memberRepository.findById(mem.getId()).get();

        //then
        assertThatThrownBy(() -> postService.insertLikedPostByMemberId(mem.getId(), 0L))
                .isInstanceOf(NotExistPostException.class);//.hasMessageContaining("")

        memberService.insertBlockedMemberByMemberId(mem.getId(), mem2.getId(), BlockedMemberStatus.A);
        em.flush();
        System.out.println("????????????");
        assertThatThrownBy(() -> postService.insertLikedPostByMemberId(mem.getId(), po2.getId()))
                .isInstanceOf(DontHaveAuthorityException.class);//.hasMessageContaining("")
    }

    @Test
    public void ???_?????????_????????????_????????????() throws Exception{

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
        System.out.println("???????????????");
        postService.insertLikedPostByMemberId(mem.getId(), po.getId());

        //then
        System.out.println("???????????????2");
        assertThatThrownBy(() -> postService.insertLikedPostByMemberId(mem.getId(), po.getId()))
                .isInstanceOf(AlreadyLikedPostException.class);//.hasMessageContaining("")

    }

    @Test
    public void ???_?????????_??????() throws Exception{
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
        System.out.println("???????????????");
        postService.insertViewedPostByIp("00", post.getId());
        em.flush();
        postService.updateAllPopularity();
        em.flush();
        System.out.println("?????????");
        Post post1 = postRepository.getById(post.getId());

        //then
        assertThat(post1.getPopularity())
                .isEqualTo(post1.getViewedPostByIps().size() * POST_VIEW.getRate() * POST_VIEW.getLatestScore());
        assertThat(post1.getViewSize()).isEqualTo(1);

    }
    
    @Test
    public void ???_?????????_??????_??????() throws Exception{
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
        System.out.println("???????????????");
        postService.insertViewedPostByIp("00", post.getId());
        System.out.println("?????????");
        em.flush();
        postService.updateAllPopularity();
        em.flush();
        Post post2 = postRepository.getById(post.getId());
        
        //then
        assertThat(post2.getPopularity())
                .isEqualTo(post2.getViewedPostByIps().size() * POST_VIEW.getRate() * POST_VIEW.getLatestScore());
        assertThat(post2.getViewSize()).isEqualTo(1);

    }

    //??????????????????
    public void ???_?????????_?????????_??????() throws Exception{

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
        System.out.println("???????????????");
        postService.insertViewedPostByIp("00", post.getId());
        System.out.println("?????????");
        Post post2 = postRepository.getById(post.getId());

        //then
        assertThat(post2.getPopularity()).isEqualTo(post2.getViewedPostByIps().size());
        assertThat(post2.getPopularity()).isEqualTo(2);
    }

    @Test
    public void ???_??????_?????????() throws Exception{
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
        Post post = postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build());

        List<Long> commentIds = new ArrayList<>();

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                null, "ddd"), member.getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                null, "ddd"), memberList.get(0).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                null, "ddd"), memberList.get(1).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                null, "ddd"), memberList.get(2).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                null, "ddd"), memberList.get(3).getId(), post.getId()));

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                commentIds.get(0), "ddd"), member.getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                commentIds.get(0), "ddd"), memberList.get(0).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                commentIds.get(0), "ddd"), memberList.get(1).getId(), post.getId()));

        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                commentIds.get(2), "ddd"), member.getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                commentIds.get(2), "ddd"), memberList.get(0).getId(), post.getId()));
        commentIds.add(commentInPostService.insertCommentInPostByCommentVO(new CommentDto.CommentInPostRequestDto(
                commentIds.get(2), "ddd"), memberList.get(1).getId(), post.getId()));

        commentInPostService.insertLikedCommentByMemberId(memberList.get(1).getId(), commentIds.get(1));
        commentInPostService.insertLikedCommentByMemberId(member.getId(), commentIds.get(0));
        commentInPostService.insertLikedCommentByMemberId(memberList.get(2).getId(), commentIds.get(0));
        commentInPostService.insertLikedCommentByMemberId(member.getId(), commentIds.get(7));

        em.flush();
        em.clear();

        //when
        System.out.println("???????????????");
        postService.deletePost(MemberDetails.builder()
                .id(member.getId())
                .accountId(member.getAccountId())
                .accountPw(member.getAccountPw())
                .authorities(member.getRoles()
                        .stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                        .collect(Collectors.toSet()))
                .build(), post.getId());
        em.flush();
        System.out.println("??????????????????");

        //then
        assertThatThrownBy(() -> postRepository.findById(post.getId())
                .orElseThrow(() -> new NotExistPostException(ErrorEnum.NOT_EXIST_POST)))
                .isInstanceOf(NotExistPostException.class);
        assertThat(commentInPostRepository.findAll().size()).isEqualTo(0);
        assertThat(likedCommentInPostRepository.findAll().size()).isEqualTo(0);

        Post post2 = postRepository.save(Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.A)
                .setTitle("aaa").setContent("").setUrls(List.of())
                .setHashTags(List.of()).build());

        postService.deletePost(MemberDetails.builder()
                .id(member2.getId())
                .accountId(member2.getAccountId())
                .accountPw(member2.getAccountPw())
                .authorities(member2.getRoles()
                        .stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getKey()))
                        .collect(Collectors.toSet()))
                .build(), post2.getId());
    }
}