package com.nameless.spin_off.service.post;

import com.nameless.spin_off.dto.PostDto;
import com.nameless.spin_off.entity.collections.CollectedPost;
import com.nameless.spin_off.entity.collections.Collection;
import com.nameless.spin_off.entity.collections.PublicOfCollectionStatus;
import com.nameless.spin_off.entity.member.Member;
import com.nameless.spin_off.entity.post.*;
import com.nameless.spin_off.repository.collections.CollectionRepository;
import com.nameless.spin_off.repository.member.MemberRepository;
import com.nameless.spin_off.repository.post.HashtagRepository;
import com.nameless.spin_off.repository.post.LikedPostRepository;
import com.nameless.spin_off.repository.post.PostRepository;
import com.nameless.spin_off.repository.post.PostedMediaRepository;
import com.nameless.spin_off.service.comment.CommentInPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//@Rollback(value = false)
@SpringBootTest
@Transactional
class JpaPostServiceTest {

    @Autowired PostService postService;
    @Autowired CommentInPostService commentInPostService;
    @Autowired PostRepository postRepository;
    @Autowired HashtagRepository hashtagRepository;
    @Autowired PostedMediaRepository postedMediaRepository;
    @Autowired CollectionRepository collectionRepository;
    @Autowired LikedPostRepository likedPostRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    public void 포스트_생성_테스트() throws Exception{
        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);

        collectionRepository.save(Collection.createDefaultCollection(member));
        collectionRepository.save(Collection.createCollection(member, "", "", PublicOfCollectionStatus.PUBLIC));
        collectionRepository.save(Collection.createCollection(member, "", "", PublicOfCollectionStatus.PUBLIC));

        List<Collection> collectionsByMember = collectionRepository.findAllByMember(member);

        List<Long> collectionIds = collectionsByMember.stream().map(Collection::getId).collect(Collectors.toList());

        PostDto.CreatePostVO createPostVO = new PostDto.CreatePostVO(member.getId(),
                "알라리숑", "얄라리얄라", PublicOfPostStatus.PUBLIC,
                List.of("abc", "bdc"), List.of("형윤이", "형윤이?"), List.of(), collectionIds);

        List<Post> preparePosts = postRepository.findAll();
        List<PostedMedia> preparePostedMedias = postedMediaRepository.findAll();
        List<Hashtag> prepareHashtags = hashtagRepository.findAll();
        List<Collection> prepareCollections = collectionRepository.findAllById(collectionIds);


        int preparePostsSize = preparePosts.size();
        int preparePostedMediasSize = preparePostedMedias.size();
        int prepareHashtagsSize = prepareHashtags.size();
        List<CollectedPost> prepareCollectedPosts1 = prepareCollections.get(0).getCollectedPosts();
        List<CollectedPost> prepareCollectedPosts2 = prepareCollections.get(1).getCollectedPosts();
        List<CollectedPost> prepareCollectedPosts3 = prepareCollections.get(2).getCollectedPosts();

        int prepareCollectedPosts1Size = prepareCollectedPosts1.size();
        int prepareCollectedPosts2Size = prepareCollectedPosts2.size();
        int prepareCollectedPosts3Size = prepareCollectedPosts3.size();
        //when
        Long aLong = postService.savePostByPostVO(createPostVO);
        Post post1 = postRepository.findById(aLong).orElseThrow(Exception::new);

        Long postCollectionCount = post1.getCollectionCount();
        List<PostedHashtag> postedHashtags = post1.getPostedHashtags();
        int postPostedHashtagSize = postedHashtags.size();

        List<Post> postPosts = postRepository.findAll();
        List<PostedMedia> postPostedMedias = postedMediaRepository.findAll();
        List<Hashtag> postHashtags = hashtagRepository.findAll();
        List<Collection> postCollections = collectionRepository.findAllById(collectionIds);

        int postPostsSize = postPosts.size();
        int postPostedMediasSize = postPostedMedias.size();
        int postHashtagsSize = postHashtags.size();
        List<CollectedPost> postCollectedPosts1 = postCollections.get(0).getCollectedPosts();
        List<CollectedPost> postCollectedPosts2 = postCollections.get(1).getCollectedPosts();
        List<CollectedPost> postCollectedPosts3 = postCollections.get(2).getCollectedPosts();

        int postCollectedPosts1Size = postCollectedPosts1.size();
        int postCollectedPosts2Size = postCollectedPosts2.size();
        int postCollectedPosts3Size = postCollectedPosts3.size();

        //then

        assertThat(preparePostsSize).isEqualTo(postPostsSize - 1);
        assertThat(preparePostedMediasSize).isEqualTo(postPostedMediasSize - 2);
        assertThat(prepareHashtagsSize).isEqualTo(postHashtagsSize - 2);
        assertThat(prepareCollectedPosts1Size).isEqualTo(postCollectedPosts1Size - 1);
        assertThat(prepareCollectedPosts2Size).isEqualTo(postCollectedPosts2Size - 1);
        assertThat(prepareCollectedPosts3Size).isEqualTo(postCollectedPosts3Size - 1);
        assertThat(postCollectionCount).isEqualTo(3);
        assertThat(postPostedHashtagSize).isEqualTo(2);

    }
    @Test
    public void 글_좋아요_체크() throws Exception{

        //given
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Post post = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(post);
        em.flush();
        em.clear();

        //when
        postService.updateLikedPostByMemberId(member.getId(), post.getId());
        //then

        List<LikedPost> likedPosts = post.getLikedPosts();
        Long likeCount = post.getLikeCount();
        assertThat(likeCount).isEqualTo(1);
        assertThat(likedPosts.size()).isEqualTo(1);
        assertThat(likedPosts.get(0).getMember()).isEqualTo(member);

    }
    
    @Test
    public void 글_조회수_체크() throws Exception{
        //given
        LocalDateTime now;
        Member member = Member.buildMember().build();
        memberRepository.save(member);
        Post post = Post.buildPost().setMember(member).setPostPublicStatus(PublicOfPostStatus.PUBLIC).build();
        postRepository.save(post);
        System.out.println(post.getId());
        now = LocalDateTime.now();
        Post post1 = postService.updateViewedPostByIp("00", post.getId(), now, 60L);
        em.flush();

        //when
        now = LocalDateTime.now();
        Post post2 = postService.updateViewedPostByIp("00", post.getId(), now, 60L);
        
        //then
        assertThat(post2.getViewCount()).isEqualTo(post2.getViewedPostByIps().size());
        assertThat(post2.getViewCount()).isEqualTo(1);

    }

}